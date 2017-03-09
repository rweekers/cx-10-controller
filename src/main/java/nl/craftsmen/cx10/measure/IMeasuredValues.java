package nl.craftsmen.cx10.measure;

/**
 * Created by gerlo on 09/03/2017.
 */
public interface IMeasuredValues {

    default boolean measurementAvailable() {return false;};

    int getY();
    int getX();
    int getHoogteL();
    int getHoogteR();
    int getBreedte();

    default int getHoogte() {
        return (getHoogteL() + getHoogteR())/2;
    }


    default int getVerhouding() {
        return 1000*getHoogte()/getBreedte();
    }
}
