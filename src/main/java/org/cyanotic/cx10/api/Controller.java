package org.cyanotic.cx10.api;

/**
 * Created by cyanotic on 19/11/2016.
 */
public interface Controller extends AutoCloseable {

    Command getCommand();

}
