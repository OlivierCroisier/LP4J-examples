/*
 * Copyright 2015 ARCA Computing (http://arca-computing.fr)
 *
 * Licensed under the Creative Commons Zero (CC0) 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.computing.arca.launchpad.draugths.launchpad;

import net.thecodersbreakfast.lp4j.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MockedDraughtsPad {

    private char[][] pads = initBoard();

    public static void main(String[] args) throws IOException {
        new MockedDraughtsPad();
    }

    public MockedDraughtsPad() throws IOException {

        DraughtsPad draughtsPad = new DraughtsPad(new LaunchpadClient() {
            @Override
            public void reset() {
                initBoard();
            }

            @Override
            public void testLights(LightIntensity lightIntensity) {
            }

            @Override
            public void setLights(Color[] colors, BackBufferOperation backBufferOperation) {
            }

            @Override
            public void setPadLight(Pad pad, Color color, BackBufferOperation backBufferOperation) {
                pads[pad.getX()][pad.getY()] = colorToChar(color);
            }

            @Override
            public void setButtonLight(Button button, Color color, BackBufferOperation backBufferOperation) {
            }

            @Override
            public void setBrightness(Brightness brightness) {
            }

            @Override
            public void setBuffers(Buffer buffer, Buffer buffer2, boolean b, boolean b2) {
            }

            @Override
            public void scrollText(String s, Color color, ScrollSpeed speed, boolean b, BackBufferOperation backBufferOperation) {
                System.out.println(s);
            }
        });

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            displayBoard();
            String input = br.readLine();
            draughtsPad.getDraugthsLaunchpadListener().onPadPressed(Pad.at(Integer.valueOf(input.charAt(0)) - 48, Integer.valueOf(input.charAt(1)) - 48), 42);
        }
    }

    private char[][] initBoard() {
        char[][] board = new char[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[x][y] = '.';
            }
        }
        return board;
    }

    private void displayBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                System.out.print(pads[x][y]);
            }
            System.out.print("\n");
        }
    }

    private char colorToChar(Color color) {
        if (Color.RED == color) {
            return 'R';
        }
        if (Color.YELLOW == color) {
            return 'Y';
        }
        if (Color.GREEN == color) {
            return 'G';
        }
        return '.';
    }

}
