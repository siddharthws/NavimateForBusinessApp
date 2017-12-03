package com.biz.navimate.interfaces;

import com.biz.navimate.objects.Route;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class IfaceDialog {
    // Confirm dialog interface
    public interface Confirm {
        void onConfirmYesClick();
        void onConfirmNoClick();
    }

    // Confirm dialog interface
    public interface RouteBuilder {
        void onRouteBuilt(Route.Way route);
    }

    // Confirm dialog interface
    public interface MapSettings {
        void onSettingsUpdated();
    }
}
