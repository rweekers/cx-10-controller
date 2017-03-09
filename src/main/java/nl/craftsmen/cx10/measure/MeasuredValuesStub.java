package nl.craftsmen.cx10.measure;

/**
 * Created by gerlo on 09/03/2017.
 */
public class MeasuredValuesStub implements IMeasuredValues {
    @Override
    public int getY() {
        return 500;
    }

    @Override
    public int getX() {
        return 500;
    }

    @Override
    public int getHoogteL() {
        return 500;
    }

    @Override
    public int getHoogteR() {
        return 500;
    }

    @Override
    public int getBreedte() {
        return 500;
    }

    @Override
    public boolean measurementAvailable() {
        return true;
    }
}
