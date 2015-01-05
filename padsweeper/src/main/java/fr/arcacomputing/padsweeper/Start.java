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

import net.thecodersbreakfast.lp4j.api.Launchpad;
import net.thecodersbreakfast.lp4j.api.LaunchpadClient;
import net.thecodersbreakfast.lp4j.emulator.EmulatorLaunchpad;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;

import java.util.concurrent.CountDownLatch;

public class Start {

    private static CountDownLatch stop = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        MidiDeviceConfiguration configuration = null;
        Launchpad launchpad = null;
        try {
            launchpad = new EmulatorLaunchpad(9000);
            LaunchpadClient client = launchpad.getClient();
            launchpad.setListener(new PadsweeperListener(client, stop));
            stop.await();
            client.reset();
        } finally {
            if (launchpad != null) {
                launchpad.close();
            }
        }

    }

}
