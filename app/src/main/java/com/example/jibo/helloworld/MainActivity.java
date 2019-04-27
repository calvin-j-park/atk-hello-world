package com.example.jibo.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

// import app toolkit packages
import com.jibo.atk.OnConnectionListener;
import com.jibo.atk.ROMCommander;
import com.jibo.atk.model.EventMessage;
import com.jibo.rom.sdk.JiboRemoteControl;
import com.jibo.rom.sdk.model.api.Robot;

// import java packages
import java.io.InputStream;
import java.util.ArrayList;

// Main class that implements OnConnectionListener and OnCommandResponseListener
public class MainActivity extends AppCompatActivity implements OnConnectionListener,
        ROMCommander.OnCommandResponseListener{

    // Variable for using the command library
    private ROMCommander mRomCommander;

    // List of robots associated with a user's account
    private ArrayList<Robot> mRobots = new ArrayList<>();

    // UI buttons
    private Button mAuth;
    private Button mConnect;
    private Button mDisconnect;
    private Button mLogOut;
    private Button mSay;

    // Authentication
    private JiboRemoteControl.OnAuthenticationListener onAuthenticationListener = new JiboRemoteControl.OnAuthenticationListener() {

        // OnAuthenticationListener overrides

        // If authentication is successful
        @Override
        public void onSuccess(ArrayList<Robot> robots) {

            // Grab all robot from the user's authenticated account
            mRobots.clear();
            mRobots.addAll(robots);

            // Print a list of all robots associated with the account
            // so we can choose the one we want when we click Connect
            int i = 0;
            while (i < mRobots.size()) {
                Toast.makeText(MainActivity.this, i + ": " + mRobots.get(i).getRobotName(), Toast.LENGTH_SHORT).show();
                i++;
            }

            // Enable Connect and Log Out buttons when authenticated
            mConnect.setEnabled(true);
            mLogOut.setEnabled(true);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });

        }

        // If there's an authentication error
        @Override
        public void onError(Throwable throwable) {

            // Log the error to the app
            Toast.makeText(MainActivity.this,"API onError:" + throwable.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }

        // If there's an authentication cancellation
        @Override
        public void onCancel() {

            // Log the cancellation to the app
            Toast.makeText(MainActivity.this,"Authentication canceled" ,Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // User taps the Authenticate button
        mAuth = ((Button)findViewById(R.id.authButton));
        mAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Disable the Authenticate button while we're waiting to authenticate
                mAuth.setEnabled(false);

                // Open the Sign In window for the user to authenticate their account
                authenticate();
            }
        });

        // User taps the Connect button
        mConnect = ((Button)findViewById(R.id.connectButton));
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Disable the Connect button while we're waiting to connect
                mConnect.setEnabled(false);

                // Get the first robot returned in the list.
                // If you have multiple robots, change the digit
                // below until you get the robot you want
                connect(mRobots.get(0));
            }
        });

        // User taps the Disconnect button
        mDisconnect = ((Button)findViewById(R.id.disconnectButton));
        mDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Disable the Disconnect button while we're waiting to disconnect
                mDisconnect.setEnabled(false);

                // Call the disconnect function
                disconnect();
            }
        });

        // User taps the LogOut button
        mLogOut = ((Button)findViewById(R.id.logoutButton));
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Disable the LogOut button while we're waiting to log out
                mLogOut.setEnabled(false);

                // Call the logOut function
                logOut();
            }
        });

        // User taps the Say button
        mSay = ((Button)findViewById(R.id.sayButton));
        mSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRomCommander != null) {

                    // Make Jibo say Hello World!
                    mRomCommander.say("Hello world", MainActivity.this);
                }
            }
        });

        // Start with only the Authenticate button enabled
        mAuth.setEnabled(true);
        mConnect.setEnabled(false);
        mDisconnect.setEnabled(false);
        mLogOut.setEnabled(false);
        mSay.setEnabled(false);
    }

    // Our connectivity functions
    public void authenticate() {
        JiboRemoteControl.getInstance().signIn(this, onAuthenticationListener);
    }

    public void connect(Robot robot){
        JiboRemoteControl.getInstance().connect(robot, this);
    }

    public void disconnect() {
        JiboRemoteControl.getInstance().disconnect();
    }

    public void logOut() {
        JiboRemoteControl.getInstance().logOut();

        // Once we're logged out, only enable Authenticate button
        mAuth.setEnabled(true);
        mLogOut.setEnabled(false);
        mConnect.setEnabled(false);
        mDisconnect.setEnabled(false);
        mSay.setEnabled(false);

        // Log that we've logged out to the app
        Toast.makeText(MainActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
    }

    // onConnectionListener overrides

    @Override
    public void onConnected() {
    }

    @Override
    public void onSessionStarted(ROMCommander romCommander) {
        mRomCommander = romCommander;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Once we're connected and ready for commands,
                // enable the Disconnect button
                mDisconnect.setEnabled(true);
                mSay.setEnabled(true);

                // Log that we're connected to the app
                Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(Throwable throwable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // If connection fails, re-enable the Connect button so we can try again
                mConnect.setEnabled(true);

                // Log the error to the app
                Toast.makeText(MainActivity.this,"Connection failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDisconnected(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Re-enable Connnect when we're disconnected
                mConnect.setEnabled(true);
                mSay.setEnabled(false);

                // Log that we've disconnected to the app
                Toast.makeText(MainActivity.this,"Disconnected",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // onCommandResponseListener overrides
    @Override
    public void onSuccess(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void onError(final String s,final String s1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Log the error to the app
                Toast.makeText(MainActivity.this,"error : " + s + " " + s1,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEventError(final String s, final EventMessage.ErrorEvent.ErrorData errorData) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Log the error to the app
                Toast.makeText(MainActivity.this,"error : " + s + " " + errorData.getErrorString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSocketError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Log the error to the app
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
