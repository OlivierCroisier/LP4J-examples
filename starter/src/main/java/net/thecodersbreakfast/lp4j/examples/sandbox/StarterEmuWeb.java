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

import net.thecodersbreakfast.lp4j.api.Launchpad;
import net.thecodersbreakfast.lp4j.api.LaunchpadClient;
import net.thecodersbreakfast.lp4j.emulator.EmulatorLaunchpad;

import java.util.concurrent.CountDownLatch;

/**
 * Basic usage of the EmulatorLaunchpad implementation.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
public class StarterEmuWeb {

    public static void main(String[] args) throws Exception {

        CountDownLatch stop = new CountDownLatch(1);
        Launchpad launchpad = null;
        try {
            launchpad = new EmulatorLaunchpad(9000);

            LaunchpadClient client = launchpad.getClient();
            launchpad.setListener(new SimpleListener(client, stop));

            stop.await();
            client.reset();
        } finally {
            if (launchpad != null) {
                launchpad.close();
            }
        }

    }

}
