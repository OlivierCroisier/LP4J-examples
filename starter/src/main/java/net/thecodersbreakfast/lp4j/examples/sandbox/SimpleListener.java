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

package net.thecodersbreakfast.lp4j.examples.sandbox;

import net.thecodersbreakfast.lp4j.api.*;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Simple listener that turns a light under pads and buttons when they are pressed, and turns the light off when
 * they are released.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
public class SimpleListener extends LaunchpadListenerAdapter {
    private LaunchpadClient client;
    private CountDownLatch stopLatch;

    public SimpleListener(LaunchpadClient client, CountDownLatch stopLatch) {
        this.client = client;
        this.stopLatch = stopLatch;
    }

    @Override
    public void onPadPressed(Pad pad, long timestamp) {
        // Turn on the light under the pad with a random non-black color
        Random random = new Random();
        Color color = Color.BLACK;
        while (color == Color.BLACK) {
            color = Color.of(random.nextInt(4), random.nextInt(4));
        }
        client.setPadLight(pad, color, BackBufferOperation.NONE);
    }

    @Override
    public void onPadReleased(Pad pad, long timestamp) {
        // Turn off the light under the pad
        client.setPadLight(pad, Color.BLACK, BackBufferOperation.NONE);
    }

    @Override
    public void onButtonPressed(Button button, long timestamp) {
        // Display a red light under the button
        client.setButtonLight(button, Color.RED, BackBufferOperation.NONE);
    }

    @Override
    public void onButtonReleased(Button button, long timestamp) {
        // Turn off the light under the button
        client.setButtonLight(button, Color.BLACK, BackBufferOperation.NONE);
        switch (button) {
            // The STOP button shuts the emulator down.
            case STOP:
                stopLatch.countDown();
                break;
        }
    }
}
