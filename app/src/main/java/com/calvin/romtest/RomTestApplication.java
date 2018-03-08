package com.calvin.romtest;

import android.app.Application;

import com.jibo.atk.utils.Commons;
import com.jibo.rom.sdk.JiboRemoteControl;

/**
 * Created by calvinator on 1/9/18.
 */

public class RomTestApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Commons.ROOT_ENDPOINT = Commons.STG_ENDPOINT;
        JiboRemoteControl.init(this, "rom_mobile_test",
                "xf4us9bcHAfxMAqEZ3LQLJ927K9vKUMiquQNm6fwHV4uGRzmrhgFRdRAFzXE8izF");
    }
}
