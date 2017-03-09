package org.cyanotic.cx10.api;

import java.io.Closeable;

/**
 * Created by cyanotic on 19/11/2016.
 */
public interface Controller extends Closeable {

    Command getCommand();

}
