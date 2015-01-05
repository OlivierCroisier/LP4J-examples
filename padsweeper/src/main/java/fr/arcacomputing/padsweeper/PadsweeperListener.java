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
package fr.arcacomputing.padsweeper;

import net.thecodersbreakfast.lp4j.api.*;

import java.util.concurrent.CountDownLatch;

import static net.thecodersbreakfast.lp4j.api.BackBufferOperation.COPY;
import static net.thecodersbreakfast.lp4j.api.BackBufferOperation.NONE;

public class PadsweeperListener extends LaunchpadListenerAdapter {

    public static Buffer visible = Buffer.BUFFER_0;
    public static Buffer write = Buffer.BUFFER_0;

    private final LaunchpadClient launchpad;
    private final CountDownLatch stop;
    private Grille grille;

    public PadsweeperListener(final LaunchpadClient launchpad, final CountDownLatch stop) {
        this.launchpad = launchpad;
        this.stop = stop;
        restart();
    }

    @Override
    public void onPadPressed(Pad pad, long timestamp) {
        int x = pad.getX();
        int y = pad.getY();
        if (grille.isDead(x, y)) {
            showMeDeath();
        } else if (grille.isNearDeath(x, y)) {
            launchpad.setPadLight(pad, Color.ORANGE, COPY);
        } else {
            launchpad.setPadLight(pad, Color.GREEN, COPY);
        }
    }

    @Override
    public void onButtonPressed(Button button, long timestamp) {
        switch (button) {
            case STOP:
                stop.countDown();
                break;
            case SESSION:
                restart();
                break;
            case ARM:
                setKillersVisibility(true);
                break;
        }
    }

    @Override
    public void onButtonReleased(Button button, long timestamp) {
        if (button != Button.STOP && button != Button.SESSION) {
            launchpad.setButtonLight(button, Color.BLACK, COPY);
        }
        if (button == Button.ARM) {
            setKillersVisibility(false);
        }
    }

    private void restart() {
        launchpad.setBuffers(visible, write, false, false);
        launchpad.reset();
        grille = new Grille();
        launchpad.setButtonLight(Button.STOP, Color.RED, COPY);
        launchpad.setButtonLight(Button.SESSION, Color.GREEN, COPY);
    }

    private void showMeDeath() {
        for (int x = 0; x < Grille.GRID_SIZE; x++) {
            for (int y = 0; y < Grille.GRID_SIZE; y++) {
                launchpad.setPadLight(Pad.at(x, y), Color.RED, COPY);
            }
        }
        blink(Button.SESSION, Color.GREEN);
    }

    private void setKillersVisibility(boolean visibleMines) {
        if (visibleMines) {
            launchpad.setBuffers(Buffer.BUFFER_1, Buffer.BUFFER_1, false, false);
            for (int x = 0; x < Grille.GRID_SIZE; x++) {
                for (int y = 0; y < Grille.GRID_SIZE; y++) {
                    if (grille.isDead(x, y)) {
                        launchpad.setPadLight(Pad.at(x, y), Color.RED, NONE);
                    }
                }
            }
        } else {
            launchpad.setBuffers(Buffer.BUFFER_0, Buffer.BUFFER_0, false, false);
        }
    }

    private void blink(Button button, Color color) {
        for (int i = 0; i < 10; i++) {
            try {
                launchpad.setButtonLight(button, color, COPY);
                Thread.sleep(200);
                launchpad.setButtonLight(button, Color.BLACK, COPY);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        launchpad.setButtonLight(button, color, COPY);
    }

}
