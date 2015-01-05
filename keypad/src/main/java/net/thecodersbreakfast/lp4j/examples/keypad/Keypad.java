/*
 * Copyright 2015 Olivier Croisier (thecodersbreakfast.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.thecodersbreakfast.lp4j.examples.keypad;

import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

/**
 * When a pad is pressed, the related String (defined in config.xml) is pasted.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
public class Keypad {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage : Keypad <config_file.properties>");
            return;
        }

        new Keypad(args[0]);
    }

    private static final Pattern PATTERN_COMMA = Pattern.compile(",");
    private static final Pattern PATTERN_SHARP = Pattern.compile("#");

    private final CountDownLatch stop = new CountDownLatch(1);

    private final String configFile;
    private final Map<String, PadConfig> boardConfig = new HashMap<String, PadConfig>();

    private Robot robot;
    private final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private LaunchpadClient client;

    private Keypad(String configFile) throws Exception {
        Launchpad launchpad = null;
        try {
            robot = new Robot();

            launchpad = new MidiLaunchpad(MidiDeviceConfiguration.autodetect());
            launchpad.setListener(new KeypadListener());
            client = launchpad.getClient();

            this.configFile = configFile;
            loadConfig();
            initBoard();

            stop.await();
        } finally {
            if (client != null) {
                client.reset();
            }
            if (launchpad != null) {
                launchpad.close();
            }
        }
    }

    private void loadConfig() throws IOException {
        Properties config = new Properties();
        config.loadFromXML(new FileInputStream(configFile));

        boardConfig.clear();
        Set<String> keys = config.stringPropertyNames();
        for (String key : keys) {
            String value = config.getProperty(key);
            PadConfig padConfig = PadConfig.parse(key, value);
            boardConfig.put(padConfig.getCoords(), padConfig);
        }
    }

    private void initBoard() {
        client.reset();
        client.setButtonLight(Button.STOP, Color.RED, BackBufferOperation.COPY);
        client.setButtonLight(Button.ARM, Color.ORANGE, BackBufferOperation.COPY);
        for (PadConfig padConfig : boardConfig.values()) {
            client.setPadLight(Pad.at(padConfig.x, padConfig.y), padConfig.color, BackBufferOperation.NONE);
        }
    }


    private class KeypadListener extends LaunchpadListenerAdapter {

        @Override
        public void onPadReleased(Pad pad, long timestamp) {
            String key = pad.getX() + "," + pad.getY();
            PadConfig padConfig = boardConfig.get(key);
            if (padConfig != null) {
                paste(padConfig.value);
            }
        }

        @Override
        public void onButtonReleased(Button button, long timestamp) {
            // Turn off the light under the button
            client.setButtonLight(button, Color.BLACK, BackBufferOperation.NONE);
            switch (button) {
                // The STOP button shuts the emulator down.
                case STOP:
                    stop.countDown();
                    break;
                // The ARM button reloads the configuration file.
                case ARM:
                    try {
                        loadConfig();
                        initBoard();
                    } catch (IOException e) {
                        client.scrollText("Invalid config : " + e.getMessage(), Color.RED, ScrollSpeed.SPEED_MAX, false, BackBufferOperation.NONE);
                    }
            }
        }

        private void paste(String value) {
            systemClipboard.setContents(new StringSelection(value), null);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.delay(20);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }
    }


    public static class PadConfig {
        public final int x;
        public final int y;
        public final String value;
        public final Color color;

        private PadConfig(int x, int y, Color color, String value) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.value = value;
        }

        public static PadConfig parse(String config, String value) {

            String coordsConfig = config;
            String colorsConfig = null;

            if (config.contains("#")) {
                String[] keyParts = PATTERN_SHARP.split(config);
                coordsConfig = keyParts[0];
                colorsConfig = keyParts[1];
            }

            String[] coordsParts = PATTERN_COMMA.split(coordsConfig);
            int x = Integer.parseInt(coordsParts[0]);
            int y = Integer.parseInt(coordsParts[1]);

            Color color = Color.GREEN;
            if (colorsConfig != null) {
                String[] colorParts = PATTERN_COMMA.split(colorsConfig);
                int r = Integer.parseInt(colorParts[0]);
                int g = Integer.parseInt(colorParts[1]);
                color = Color.of(r, g);
            }

            return new PadConfig(x, y, color, value);
        }

        public String getCoords() {
            return x + "," + y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PadConfig padConfig = (PadConfig) o;
            return x == padConfig.x && y == padConfig.y;
        }

        @Override
        public int hashCode() {
            return x + 8 * y;
        }
    }

}
