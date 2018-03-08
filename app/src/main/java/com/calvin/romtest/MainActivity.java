package com.calvin.romtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jibo.atk.OnConnectionListener;
import com.jibo.atk.ROMCommander;
import com.jibo.atk.model.EventMessage;
import com.jibo.rom.sdk.JiboRemoteControl;
import com.jibo.rom.sdk.model.api.Robot;


import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnConnectionListener,
        ROMCommander.OnCommandResponseListener{

    private ArrayList<Robot> mRobots = new ArrayList<>();

    private ROMCommander mRomCommander;
    private ProgressBar mBar;
    private Button mSignIn;
    private Button mConnect;
    private Button mHelloWorld;
    private Button mSignOut;

    private JiboRemoteControl.OnAuthenticationListener onAuthenticationListener = new JiboRemoteControl.OnAuthenticationListener() {
        @Override
        public void onSuccess(ArrayList<Robot> robots) {
            mRobots.clear();
            mRobots.addAll(robots);
            Toast.makeText(MainActivity.this,"Authenticated",Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSignIn.setEnabled(false);
                    mConnect.setEnabled(true);
                }
            });

        }

        @Override
        public void onError(Throwable throwable) {
            Toast.makeText(MainActivity.this,"API onError:" + throwable.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this,"API onCancel",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBar = (ProgressBar) findViewById(R.id.progressBarOne);
        mBar.setVisibility(View.INVISIBLE);

        mSignIn = ((Button)findViewById(R.id.button));
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                siginInMethod();
            }
        });

        mConnect = ((Button)findViewById(R.id.button2));
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBar.setVisibility(View.VISIBLE);
                mConnect.setEnabled(false);
                connectToRobot(mRobots.get(1));
            }
        });

        mSignOut = ((Button)findViewById(R.id.button3));
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singOutMethod();
            }
        });

        mHelloWorld = ((Button)findViewById(R.id.button4));
        mHelloWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRomCommander != null) {
                    mRomCommander.say("Hello world", MainActivity.this);
                }
            }
        });
    }

    //login with webview oauth
    public void siginInMethod() {
        JiboRemoteControl.getInstance().signIn(this, onAuthenticationListener);
    }

    public void singOutMethod() {
        JiboRemoteControl.getInstance().logOut();
    }

    public void connectToRobot(Robot robot){
        JiboRemoteControl.getInstance().connect(robot, this);
    }


    @Override
    public void onConnected() {
    }

    @Override
    public void onSessionStarted(ROMCommander romCommander) {

        mRomCommander = romCommander;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBar.setVisibility(View.INVISIBLE);
                mConnect.setEnabled(false);
                mHelloWorld.setEnabled(true);
                mSignOut.setEnabled(true);
                Toast.makeText(MainActivity.this,"session started",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(Throwable throwable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBar.setVisibility(View.INVISIBLE);
                mConnect.setEnabled(true);
                Toast.makeText(MainActivity.this,"connection failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDisconnected(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBar.setVisibility(View.INVISIBLE);
                mSignIn.setEnabled(true);
                mConnect.setEnabled(true);
                mHelloWorld.setEnabled(false);
                Toast.makeText(MainActivity.this,"on disconnected",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onSuccess(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"success + " + s,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onError(final String s,final String s1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"error : " + s + " " + s1,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEventError(final String s, final EventMessage.ErrorEvent.ErrorData errorData) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"error : " + s + " " + errorData.getErrorString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSocketError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"socket error",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onEvent(String s, EventMessage.BaseEvent baseEvent) {

    }

    @Override
    public void onPhoto(String s, EventMessage.TakePhotoEvent takePhotoEvent, InputStream inputStream) {

    }

    @Override
    public void onVideo(String s, EventMessage.VideoReadyEvent videoReadyEvent, InputStream inputStream) {

    }

    @Override
    public void onListen(String s, String s1) {

    }

    @Override
    public void onParseError() {

    }
}
