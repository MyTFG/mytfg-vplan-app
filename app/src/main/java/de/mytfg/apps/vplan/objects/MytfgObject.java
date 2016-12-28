package de.mytfg.apps.vplan.objects;

import de.mytfg.apps.vplan.api.SuccessCallback;

/**
 * Abstract class for all MyTFG API callbacks
 */

public abstract class MytfgObject {
    /**
     * Loads this object from the MyTFG Server
     * @param callback Called when loading finished.
     */

    public abstract void load(SuccessCallback callback);
}
