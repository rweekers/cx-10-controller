package nl.craftsmen.cx10.measure;

/**
 * Created by gerlo on 09/03/2017.
 */
public interface IMeasuredValues {

    int getY();
    int getX();
    int getHoogteL();
    int getHoogteR();
    int getBreedte();

    default int getHoogte() {
        return (getHoogteL() + getHoogteR())/2;
    }

}
