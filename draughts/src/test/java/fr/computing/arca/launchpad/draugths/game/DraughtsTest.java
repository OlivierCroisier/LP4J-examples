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

package fr.computing.arca.launchpad.draugths.game;

import fr.computing.arca.launchpad.draugths.game.Coordinates;
import fr.computing.arca.launchpad.draugths.game.Draughts;
import fr.computing.arca.launchpad.draugths.game.DraughtsListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static fr.computing.arca.launchpad.draugths.game.Coordinates.coord;
import static fr.computing.arca.launchpad.draugths.game.Piece.PLAYER_1;
import static fr.computing.arca.launchpad.draugths.game.Piece.PLAYER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DraughtsTest {

    @Mock
    private DraughtsListener draughtsListener;

    private Draughts draughts;

    @Before
    public void before() {
        draughts = new Draughts(draughtsListener);
    }

    @Test
    public void should_init_pieces_player1() {
        assertThat(draughts.getPieceAt(coord(0, 0))).isEqualTo(PLAYER_1);
        assertThat(draughts.getPieceAt(coord(2, 0))).isEqualTo(PLAYER_1);
        assertThat(draughts.getPieceAt(coord(1, 1))).isEqualTo(PLAYER_1);
        assertThat(draughts.getPieceAt(coord(5, 1))).isEqualTo(PLAYER_1);
        assertThat(draughts.getPieceAt(coord(2, 2))).isEqualTo(PLAYER_1);
        assertThat(draughts.getPieceAt(coord(6, 2))).isEqualTo(PLAYER_1);

        assertThat(draughts.getPieceAt(coord(0, 1))).isNull();
        assertThat(draughts.getPieceAt(coord(3, 0))).isNull();
        assertThat(draughts.getPieceAt(coord(0, 1))).isNull();
        assertThat(draughts.getPieceAt(coord(4, 1))).isNull();
        assertThat(draughts.getPieceAt(coord(2, 4))).isNull();
        assertThat(draughts.getPieceAt(coord(7, 3))).isNull();
    }

    @Test
    public void should_init_pieces_player2() {
        assertThat(draughts.getPieceAt(coord(1, 7))).isEqualTo(PLAYER_2);
        assertThat(draughts.getPieceAt(coord(3, 7))).isEqualTo(PLAYER_2);
        assertThat(draughts.getPieceAt(coord(0, 6))).isEqualTo(PLAYER_2);
        assertThat(draughts.getPieceAt(coord(4, 6))).isEqualTo(PLAYER_2);
        assertThat(draughts.getPieceAt(coord(1, 5))).isEqualTo(PLAYER_2);
        assertThat(draughts.getPieceAt(coord(7, 5))).isEqualTo(PLAYER_2);

        assertThat(draughts.getPieceAt(coord(0, 7))).isNull();
        assertThat(draughts.getPieceAt(coord(2, 7))).isNull();
        assertThat(draughts.getPieceAt(coord(0, 4))).isNull();
        assertThat(draughts.getPieceAt(coord(4, 4))).isNull();
        assertThat(draughts.getPieceAt(coord(2, 5))).isNull();
        assertThat(draughts.getPieceAt(coord(6, 5))).isNull();
    }

    @Test
    public void should_call_on_piece_added() {
        verify(draughtsListener).onPieceAdded(coord(0, 0), PLAYER_1);
        verify(draughtsListener).onPieceAdded(coord(3, 7), PLAYER_2);
    }

    @Test
    public void player_can_move_diagonal_with_range_1() {
        boolean moved = draughts.move(coord(0, 2), coord(1, 3));

        assertThat(moved).isTrue();
        assertThat(draughts.getPieceAt(coord(0, 2))).isNull();
        assertThat(draughts.getPieceAt(coord(1, 3))).isEqualTo(PLAYER_1);
        verify(draughtsListener).onPieceMoved(coord(0, 2), coord(1, 3), PLAYER_1);
    }

    @Test
    public void player_cant_move_forward_with_range_1() {
        boolean moved = draughts.move(coord(0, 2), coord(0, 3));

        assertThat(moved).isFalse();
    }

    @Test
    public void player_can_move_if_catch_square() {

        draughts.move(coord(0, 2), coord(1, 1));
        draughts.move(coord(0, 2), coord(1, 1));

        boolean moved = draughts.move(coord(0, 2), coord(1, 1));

        assertThat(moved).isFalse();
    }

    @Test
    public void player_can_catch_if_dest_square_is_empty_and_there_is_another_piece_between() {
        draughts.move(coord(0, 2), coord(1, 3));
        draughts.move(coord(3, 5), coord(2, 4));

        boolean moved = draughts.move(coord(1, 3), coord(3, 5));

        assertThat(moved).isTrue();
        assertThat(draughts.getPieceAt(coord(2, 4))).isNull();
        verify(draughtsListener).onPieceCatched(coord(2, 4), PLAYER_2);
    }

    @Test
    public void player_can_catch_twice() {
        draughts.move(coord(2, 2), coord(3, 3));
        draughts.move(coord(1, 5), coord(0, 4));
        draughts.move(coord(3, 3), coord(2, 2));
        draughts.move(coord(0, 4), coord(1, 3));
        draughts.move(coord(2, 2), coord(3, 3));
        draughts.move(coord(0, 6), coord(1, 5));

        boolean moved = true;
        moved &= draughts.move(coord(0, 2), coord(2, 4));
        moved &= draughts.move(coord(2, 4), coord(0, 6));

        assertThat(moved).isTrue();
        assertThat(draughts.getPieceAt(coord(1, 3))).isNull();
        assertThat(draughts.getPieceAt(coord(1, 5))).isNull();
        assertThat(draughts.move(coord(7, 5), coord(6, 4))).isTrue();
    }

    @Test
    public void player_cant_catch_if_dest_square_is_not_empty_and_there_is_another_piece_between() {
        draughts.move(coord(0, 2), coord(1, 3));
        draughts.move(coord(3, 5), coord(2, 4));
        draughts.move(coord(2, 2), coord(3, 3));
        draughts.move(coord(4, 6), coord(3, 5));
        boolean moved = draughts.move(coord(1, 3), coord(3, 5));

        assertThat(moved).isFalse();
    }

    @Test
    public void player_cant_catch_if_dest_square_is_empty_and_there_is_same_piece_between() {
        draughts.move(coord(0, 2), coord(1, 3));
        draughts.move(coord(3, 5), coord(4, 4));
        draughts.move(coord(2, 2), coord(3, 3));
        draughts.move(coord(4, 4), coord(3, 5));
        draughts.move(coord(3, 3), coord(2, 4));
        draughts.move(coord(3, 5), coord(4, 4));
        boolean moved = draughts.move(coord(1, 3), coord(3, 5));

        assertThat(moved).isFalse();
    }

    @Test
    public void player_cant_catch_if_dest_square_is_not_empty_and_there_is_space_between() {
        boolean moved = draughts.move(coord(0, 2), coord(2, 4));

        assertThat(moved).isFalse();
    }

    @Test
    public void should_piece_exist_at() {
        assertThat(draughts.isAPieceAt(coord(0, 2))).isTrue();
    }

    @Test
    public void should_not_piece_exist_at() {
        assertThat(draughts.isAPieceAt(coord(0, 1))).isFalse();
    }

    @Test
    public void player_2_should_not_start() {
        boolean moved = draughts.move(coord(1, 5), coord(2, 4));

        assertThat(moved).isFalse();
    }

    @Test
    public void player_should_not_move_twice() {
        draughts.move(coord(0, 2), coord(1, 3));
        boolean moved = draughts.move(coord(1, 3), coord(2, 4));

        assertThat(moved).isFalse();
    }

    @Test
    public void player_should_win() {
        boolean moved = true;
        moved = moveAndDisplay(moved, coord(0, 2), coord(1, 3));
        moved = moveAndDisplay(moved, coord(1, 5), coord(0, 4));
        moved = moveAndDisplay(moved, coord(2, 2), coord(3, 3));
        moved = moveAndDisplay(moved, coord(3, 5), coord(2, 4));
        moved = moveAndDisplay(moved, coord(4, 2), coord(5, 3));
        moved = moveAndDisplay(moved, coord(5, 5), coord(4, 4));
        moved = moveAndDisplay(moved, coord(6, 2), coord(7, 3));
        moved = moveAndDisplay(moved, coord(7, 5), coord(6, 4));
        moved = moveAndDisplay(moved, coord(1, 3), coord(3, 5));
        moved = moveAndDisplay(moved, coord(0, 4), coord(1, 3));
        moved = moveAndDisplay(moved, coord(3, 3), coord(5, 5));
        moved = moveAndDisplay(moved, coord(0, 6), coord(1, 5));
        moved = moveAndDisplay(moved, coord(5, 3), coord(7, 5));
        moved = moveAndDisplay(moved, coord(1, 7), coord(0, 6));
        moved = moveAndDisplay(moved, coord(3, 5), coord(1, 7));
        moved = moveAndDisplay(moved, coord(3, 7), coord(2, 6));
        moved = moveAndDisplay(moved, coord(1, 7), coord(3, 5));
        moved = moveAndDisplay(moved, coord(6, 6), coord(4, 4));
        moved = moveAndDisplay(moved, coord(3, 5), coord(2, 6));
        moved = moveAndDisplay(moved, coord(5, 7), coord(6, 6));
        moved = moveAndDisplay(moved, coord(2, 6), coord(0, 4));
        moved = moveAndDisplay(moved, coord(0, 4), coord(2, 2));
        moved = moveAndDisplay(moved, coord(0, 6), coord(1, 5));
        moved = moveAndDisplay(moved, coord(2, 2), coord(3, 3));
        moved = moveAndDisplay(moved, coord(1, 5), coord(2, 6));
        moved = moveAndDisplay(moved, coord(7, 5), coord(5, 7));
        moved = moveAndDisplay(moved, coord(5, 7), coord(3, 5));
        moved = moveAndDisplay(moved, coord(3, 5), coord(1, 7));
        moved = moveAndDisplay(moved, coord(7, 7), coord(6, 6));
        moved = moveAndDisplay(moved, coord(3, 3), coord(5, 5));
        moved = moveAndDisplay(moved, coord(5, 5), coord(7, 7));

        assertThat(moved).isTrue();
        verify(draughtsListener).onWinner(PLAYER_1);
    }

    private boolean moveAndDisplay(boolean moved, Coordinates from, Coordinates to) {
        moved &= draughts.move(from, to);
        //System.out.println(draughts.toString());
        return moved;
    }

}
