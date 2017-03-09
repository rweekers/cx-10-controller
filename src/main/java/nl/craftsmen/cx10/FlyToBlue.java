package nl.craftsmen.cx10;

import com.sun.org.apache.xpath.internal.operations.Mod;
import nl.craftsmen.cx10.measure.IMeasuredValues;
import nl.craftsmen.cx10.measure.MeasuredValuesStub;
import nl.craftsmen.cx10.pid.PIDController;
import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by gerlo on 09/03/2017.
 */
public class FlyToBlue implements Controller {

    Logger LOGGER = Logger.getLogger("flytoblue");

    private final Command command = new Command();

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);


    int gewensteY = 500;
    int gewensteX = 500;


    IMeasuredValues measuredValues = new MeasuredValuesStub();
    PIDController yPIDController = new PIDController(gewensteY, 0);
    PIDController xPIDController = new PIDController(gewensteX, 0);
    private boolean initialized = false;


    public int controlY() {

        measuredValues.getY();
        return yPIDController.doPID(gewensteY);

    }


    private int controlX() {
        measuredValues.getX();
        return xPIDController.doPID(gewensteX);
    }


    @Override
    public Command getCommand() {
        LOGGER.info("getCommand");

        if (!initialized) {
            initialized = true;
            LOGGER.info("takeoff!!");
            return TAKEOFF_COMMAND;
        }


        int throttle = controlY();
        LOGGER.info("throttle:" + throttle);

        int yaw = controlX();
        LOGGER.info("yaw:" + yaw);


        return new Command(0, yaw, 0, throttle, false, false);


    }


    @Override
    public void close() throws IOException {

    }
}
