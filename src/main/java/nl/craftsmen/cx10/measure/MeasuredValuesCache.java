package nl.craftsmen.cx10.measure;

/**
 * Created by gerlo on 09/03/2017.
 */
public class MeasuredValuesCache implements IMeasuredValues {

    public int x;
    public int y;
    public int hoogteL ;
    public int hoogteR ;
    public int breedte;
    public boolean measurementAvailable ;


    @Override
    public int getY() {
       return y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getHoogteL() {
        return  hoogteL;
    }

    @Override
    public int getHoogteR() {
        return hoogteR;
    }

    @Override
    public int getBreedte() {
        return breedte;
    }

    @Override
    public boolean measurementAvailable() {
        return measurementAvailable;
    }
}
