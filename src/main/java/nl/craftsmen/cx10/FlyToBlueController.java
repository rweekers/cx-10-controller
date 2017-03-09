package nl.craftsmen.cx10;

import java.io.IOException;
import java.util.logging.Logger;

import nl.craftsmen.cx10.measure.IMeasuredValues;
import nl.craftsmen.cx10.pid.PIDController;
import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

/**
 * Created by gerlo on 09/03/2017.
 */
public class FlyToBlueController implements Controller {

    Logger LOGGER = Logger.getLogger("flytoblue");

    private final Command command = new Command();

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true);
    private static final Command TURN_COMMAND = new Command(0, 50, 0, 0, false, false);


    int gewensteY = 576 / 2;
    int gewensteX = 720 / 2;
    int gewensteAfstand = 250 * 100;
    int gewensteVerhouding = 707; //0.707
    long flytime = 0; //flytime in milleseconds
    long startTime = System.currentTimeMillis();


    private IMeasuredValues measuredValues;
    private PIDController yPIDController = new PIDController(gewensteY, 0);
    private PIDController xPIDController = new PIDController(gewensteX, 0);
    private PIDController afstandPidController = new PIDController(gewensteAfstand, 0);
    private PIDController rechtErvoorPidController = new PIDController(gewensteVerhouding, 0);
    private boolean initialized = false;
    private int initCounter = 0;
    private boolean geland = false;
    private int vorigeVerhouding;

    public FlyToBlueController(IMeasuredValues measureValuesCache) {
        measuredValues = measureValuesCache;
    }


    public int controlY() {
        return -1 * yPIDController.doPID(measuredValues.getY());
    }


    private int controlX() {
        return -1 * xPIDController.doPID(measuredValues.getX());
    }

    private int controlAfstand() {
        int afstand = measuredValues.getBreedte() * measuredValues.getHoogte();
        LOGGER.info("afstand: " + afstand);
        return -1 * afstandPidController.doPID(afstand);
    }

    private int controlRechtErvoor() {
        if (vorigeVerhouding > measuredValues.getVerhouding()) {
            return xPIDController.doPID(measuredValues.getVerhouding());

        } else {
            return -1 * xPIDController.doPID(measuredValues.getVerhouding());
        }

    }


    private boolean targetInHetmidden() {
        return ((Math.abs(gewensteX - measuredValues.getX()) < 10) &&
                (Math.abs(gewensteY - measuredValues.getY()) < 10));
    }

    private boolean gewensteAfsstandBereikt() {
        return gewensteAfstand - measuredValues.getBreedte() * measuredValues.getHoogte() < 10;
    }

    @Override
    public Command getCommand() {
        if (geland) {
            System.exit(0);
        }
        flytime = System.currentTimeMillis() - startTime;

        if (flytime > 100000) {
            LOGGER.info("object niet gevonden... helaas landen");
            geland = true;
            return LAND_COMMAND;

        }

        if (!initialized) {
            if (initCounter++ < 4) {
                LOGGER.info("takeoff!!");
                return TAKEOFF_COMMAND;
            } else {
                initialized = true;
            }

        }

        if (measuredValues.measurementAvailable()) {
            if (targetInHetmidden()) {
                LOGGER.info(" target in midden!!");
                if (gewensteAfsstandBereikt()) {
                    if ((measuredValues.getVerhouding() - gewensteVerhouding) < 100 ) {
                        int pitch = rechtErvoorPidController.doPID(measuredValues.getVerhouding());
                        LOGGER.info("pitch:" + pitch);
                        return new Command(pitch, 0, 0, 0, false, false);
                    }

                    LOGGER.info(" gewenste afstand bereikt en recht ervoor !! foto maken en landen");
                    //TODO bewaar foto
                    geland = true;
                    return LAND_COMMAND;
                }

                int roll = controlAfstand();
                LOGGER.info("roll:" + roll);
                return new Command(0, 0, roll, 0, false, false);


            }
            int throttle = controlY();

            int yaw = controlX();
            LOGGER.info("yaw:" + yaw + "throttle:" + throttle);
            return new Command(0, yaw, 0, throttle, false, false);


        }

        if (geland) {
            return null;
        }

        //rotate...
        LOGGER.info("rotate");
        return TURN_COMMAND;


    }


    @Override
    public void close() throws IOException {

    }
}
