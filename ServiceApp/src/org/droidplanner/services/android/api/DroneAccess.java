package org.droidplanner.services.android.api;

import android.os.Binder;

import org.droidplanner.services.android.drone.DroneManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
* Created by fhuya on 11/3/14.
*/
public final class DroneAccess extends Binder {

    private final WeakReference<DroidPlannerService> serviceRef;

    DroneAccess(DroidPlannerService service) {
        serviceRef = new WeakReference<DroidPlannerService>(service);
    }

    private DroidPlannerService getService() {
        final DroidPlannerService service = serviceRef.get();
        if (service == null)
            throw new IllegalStateException("Lost reference to parent service.");

        return service;
    }

    public List<DroneApi> getDroneApiList() {
        return new ArrayList<DroneApi>(getService().droneApiStore);
    }
}
