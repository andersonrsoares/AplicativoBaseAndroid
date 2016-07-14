package br.com.anderson.aplicativobase;

import android.app.Application;


/**
 * Created by DevMaker on 7/12/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
