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

import fr.computing.arca.launchpad.draugths.game.Draughts;
import fr.computing.arca.launchpad.draugths.game.Piece;
import net.thecodersbreakfast.lp4j.api.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static fr.computing.arca.launchpad.draugths.launchpad.DraughtsPad.*;
import static net.thecodersbreakfast.lp4j.api.Color.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DraughtsPadTest {

    public static final int CONST = 42;
    @Mock
    private LaunchpadClient client;

    @Mock
    private Draughts draughts;

    private DraughtsPad draughtsPad;

    private DraughtsPad.DraughtsLaunchpadListener draugthsLaunchpadListener;

    @Before
    public void before() {
        draughtsPad = new DraughtsPad(client);
        draugthsLaunchpadListener = draughtsPad.getDraugthsLaunchpadListener();
    }

    @Test
    public void should_select_square_if_there_is_a_piece() {
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 2), CONST);

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), GREEN, BackBufferOperation.COPY);
    }

    @Test
    public void should_not_select_square_if_there_is_no_piece() {

        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 1), CONST);

        //verify(client, times(0)).setPadLight(Pad.at(eq(0), eq(1)), any(Color.class));
    }

    @Test
    public void should_unselect_before_select_another_square() {
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 0), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 2), CONST);

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).setPadLight(Pad.at(0, 0), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 0), GREEN, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 0), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), GREEN, BackBufferOperation.COPY);
    }

    @Test
    public void should_not_unselect_if_move_is_not_allowed() {

        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 2), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 3), CONST);

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), GREEN, BackBufferOperation.COPY);
    }

    @Test
    public void should_move_piece() {
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 2), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(1, 3), CONST);

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), GREEN, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), BLACK, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(1, 3), RED, BackBufferOperation.COPY);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_catch_piece() {
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 2), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(1, 3), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(3, 5), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(2, 4), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(1, 3), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(3, 5), CONST);

        InOrder inOrder = inOrder(client);

        inOrder.verify(client).setPadLight(Pad.at(0, 2), GREEN, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), BLACK, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(1, 3), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(3, 5), GREEN, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(3, 5), BLACK, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(2, 4), YELLOW, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(1, 3), GREEN, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(1, 3), BLACK, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(3, 5), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(2, 4), BLACK, BackBufferOperation.COPY);
    }

    @Test
    public void should_scroll_text_if_player_1_has_won() {
        draugthsLaunchpadListener.onWinner(Piece.PLAYER_1);

        verify(client).scrollText(RED_WINS, RED, ScrollSpeed.of(TEXT_SPEED), true, BackBufferOperation.COPY);
    }

    @Test
    public void should_scroll_text_if_player_2_has_won() {
        draugthsLaunchpadListener.onWinner(Piece.PLAYER_2);

        verify(client).scrollText(YELLOW_WINS, YELLOW, ScrollSpeed.of(TEXT_SPEED), true, BackBufferOperation.COPY);
    }

    @Test
    public void should_reset_game_after_win_when_pad_is_pressed() {
        draugthsLaunchpadListener.onWinner(Piece.PLAYER_2);

        draugthsLaunchpadListener.onPadPressed(Pad.at(1, 0), CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 2), CONST);

        InOrder inOrder = inOrder(client);
        verify(client).reset();
        inOrder.verify(client).setPadLight(Pad.at(0, 2), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), GREEN, BackBufferOperation.COPY);
    }

    @Test
    public void should_reset_game_after_win_when_a_button_is_pressed() {
        draugthsLaunchpadListener.onWinner(Piece.PLAYER_2);

        draugthsLaunchpadListener.onButtonPressed(Button.DOWN, CONST);
        draugthsLaunchpadListener.onPadPressed(Pad.at(0, 2), CONST);

        InOrder inOrder = inOrder(client);
        verify(client).reset();
        inOrder.verify(client).setPadLight(Pad.at(0, 2), RED, BackBufferOperation.COPY);
        inOrder.verify(client).setPadLight(Pad.at(0, 2), GREEN, BackBufferOperation.COPY);
    }

}
