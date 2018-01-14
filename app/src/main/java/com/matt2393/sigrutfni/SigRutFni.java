package com.matt2393.sigrutfni;

import android.app.Application;

/**
 * Created by matt2393 on 14-01-18.
 * Clase de la aplicacion
 */

public class SigRutFni extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseInit.init();
    }
}
