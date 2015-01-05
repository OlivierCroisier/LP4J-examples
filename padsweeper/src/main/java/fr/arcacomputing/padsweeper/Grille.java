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

import java.util.Random;

public class Grille {

    public final static int GRID_SIZE = 8;
    private int[][] data = new int[GRID_SIZE][GRID_SIZE];
    private int killers = 8;

    public Grille() {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                data[x][y] = 0;
            }
        }

        Random rnd = new Random();

        for (int counter = 0; counter < killers; counter++) {
            int killerX = rnd.nextInt(GRID_SIZE);
            int killerY = rnd.nextInt(GRID_SIZE);
            data[killerX][killerY] = -1;
        }
    }

    public boolean isDead(int x, int y) {
        return data[x][y] == -1;
    }

    public boolean isNearDeath(int x, int y) {
        boolean isNear = false;

        for (int checkX = x - 1; checkX <= x + 1; checkX++) {
            for (int checkY = y - 1; checkY <= y + 1; checkY++) {
                if (isInBound(checkX, checkY)) {
                    isNear = isNear || isDead(checkX, checkY);
                }
            }
        }

        return isNear;
    }

    public boolean isInBound(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

}
