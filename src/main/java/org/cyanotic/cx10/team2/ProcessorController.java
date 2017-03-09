package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

import java.awt.*;
import java.io.IOException;

/**
 * Created by gerard on 9-3-17.
 */
public class ProcessorController implements Controller {

    private final Processor processor;
    private Command command = new Command(0, 0, 0, 0, true, false);

    public ProcessorController(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Command getCommand() {
        Point delta = processor.getDelta();
        // todo: zet de delta om in een beweging
        return command;
    }
}
