/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.gardena.internal.model.command;

/**
 * Command to manually override the mower timer.
 *
 * @author Gerhard Riegler - Initial contribution
 */

public class MowerStartOverrideTimerCommand extends Command {
    private static final String COMMAND = "start_override_timer";

    public MowerStartOverrideTimerCommand(String durationInMinutes) {
        super(COMMAND);
        parameters = new CommandParameters();
        parameters.setDuration(durationInMinutes);
    }
}
