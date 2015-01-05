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

import fr.computing.arca.launchpad.draugths.game.Coordinates;
import fr.computing.arca.launchpad.draugths.game.Draughts;
import fr.computing.arca.launchpad.draugths.game.DraughtsListener;
import fr.computing.arca.launchpad.draugths.game.Piece;
import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.emulator.EmulatorLaunchpad;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static fr.computing.arca.launchpad.draugths.game.Coordinates.coord;
import static net.thecodersbreakfast.lp4j.api.Button.STOP;

public class DraughtsPad {

    public static final Buffer VISIBLE = Buffer.BUFFER_0;
    public static final Buffer WRITE = Buffer.BUFFER_0;
    public static final Color SELECTED = Color.GREEN;
    public static final Color UNSELECTED = Color.BLACK;
    public static final int TEXT_SPEED = 5;
    public static final String RED_WINS = "Red Wins !!!";
    public static final String YELLOW_WINS = "Yellow Wins !!!";

    private LaunchpadClient client;
    private Draughts draughts;
    private DraughtsLaunchpadListener draugthsLaunchpadListener = new DraughtsLaunchpadListener();

    private ColoredPad selectedPad;
    private static CountDownLatch stop = new CountDownLatch(1);
    private boolean gameEnded = false;

    public static void main(String[] args) throws MidiUnavailableException, InterruptedException, IOException {
        new DraughtsPad();
        stop.await();
    }

    public DraughtsPad() throws MidiUnavailableException, InterruptedException, IOException {
        initPad(draugthsLaunchpadListener);
        draughts = new Draughts(draugthsLaunchpadListener);
    }

    // for test
    DraughtsPad(LaunchpadClient client) {
        this.client = client;
        draughts = new Draughts(draugthsLaunchpadListener);
    }

    private void initPad(DraughtsLaunchpadListener draugthsLaunchpadListener) throws MidiUnavailableException, InterruptedException, IOException {
        //Launchpad launchpad = new MidiLaunchpad(MidiDeviceConfiguration.autodetect());
        Launchpad launchpad = new EmulatorLaunchpad(9000);

        Thread.sleep(5000);

        client = launchpad.getClient();
        launchpad.setListener(draugthsLaunchpadListener);
        client.setBuffers(VISIBLE, WRITE, false, false);
        client.setButtonLight(STOP, Color.RED, BackBufferOperation.COPY);
    }

    class DraughtsLaunchpadListener extends LaunchpadListenerAdapter implements DraughtsListener {

        @Override
        public void onPadPressed(Pad pad, long timestamp) {
            if (gameEnded) {
                resetGame();
            } else {
                selectOrMove(pad.getX(), pad.getY());
            }
        }

        @Override
        public void onButtonPressed(Button button, long timestamp) {
            if (gameEnded) {
                resetGame();
            }
            if (button == STOP) {
                stop.countDown();
            }
        }


        @Override
        public void onPieceAdded(Coordinates coord, Piece piece) {
            client.setPadLight(Pad.at(coord.x, coord.y), pieceToColor(piece), BackBufferOperation.COPY);
        }

        @Override
        public void onPieceMoved(Coordinates from, Coordinates to, Piece piece) {
            client.setPadLight(Pad.at(from.x, from.y), UNSELECTED, BackBufferOperation.COPY);
            client.setPadLight(Pad.at(to.x, to.y), pieceToColor(piece), BackBufferOperation.COPY);
            resetSelectedPad();
        }

        @Override
        public void onPieceCatched(Coordinates coord, Piece piece) {
            client.setPadLight(Pad.at(coord.x, coord.y), UNSELECTED, BackBufferOperation.COPY);
        }

        @Override
        public void onWinner(Piece winner) {
            gameEnded = true;
            if (Piece.PLAYER_1 == winner) {
                client.scrollText(RED_WINS, pieceToColor(winner), ScrollSpeed.of(TEXT_SPEED), true, BackBufferOperation.COPY);
            } else {
                client.scrollText(YELLOW_WINS, pieceToColor(winner), ScrollSpeed.of(TEXT_SPEED), true, BackBufferOperation.COPY);
            }
        }

        private void selectOrMove(int x, int y) {
            if (!draughts.isAPieceAt(coord(x, y))) {
                if (aPadIsSelected()) {
                    draughts.move(coord(selectedPad.coord.x, selectedPad.coord.y), coord(x, y));
                }
            } else {
                selectPad(x, y);
            }
        }

        private boolean aPadIsSelected() {
            return selectedPad != null;
        }

        private void selectPad(int x, int y) {
            if (draughts.isAPieceAt(coord(x, y))) {
                unselectPad();
                selectedPad = new ColoredPad(coord(x, y), pieceToColor(draughts.getPieceAt(coord(x, y))));
                client.setPadLight(Pad.at(x, y), SELECTED, BackBufferOperation.COPY);
            }
        }

        private void unselectPad() {
            if (aPadIsSelected()) {
                client.setPadLight(Pad.at(selectedPad.coord.x, selectedPad.coord.y), selectedPad.previousColor, BackBufferOperation.COPY);
                resetSelectedPad();
            }
        }

        private void resetSelectedPad() {
            selectedPad = null;
        }

        private void resetGame() {
            client.reset();
            gameEnded = false;
            draughts = new Draughts(draugthsLaunchpadListener);
        }

        private Color pieceToColor(Piece piece) {
            return piece == Piece.PLAYER_1 ? Color.RED : Color.YELLOW;
        }
    }

    public DraughtsLaunchpadListener getDraugthsLaunchpadListener() {
        return draugthsLaunchpadListener;
    }
}
