package com.example.jibo.helloworld;

import android.app.Application;

import com.jibo.rom.sdk.JiboRemoteControl;

/**
 * Created by Jibo, Inc. on 3/8/18.
 */

public class HelloWorldApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JiboRemoteControl.init(this, "id-here",
                "secret-here");
    }
}