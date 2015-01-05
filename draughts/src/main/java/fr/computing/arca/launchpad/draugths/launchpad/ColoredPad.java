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
import net.thecodersbreakfast.lp4j.api.Color;

public class ColoredPad {

    public final Coordinates coord;

    public final Color previousColor;

    public ColoredPad(Coordinates coord, Color previousColor) {
        this.coord = coord;
        this.previousColor = previousColor;
    }
}
