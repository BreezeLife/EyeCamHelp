package com.weiqilab.hackathon.eyecanhelp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.weiqilab.hackathon.eyecanhelp.R;

import tools.stio.atlas.Dt;


/**
 * Created by Enuviel on 8/27/16.
 */
public class ShareActivity extends Activity {
    private static final String TAG = ShareActivity.class.getSimpleName();

    TextView callButton;
    TextView emailButton;
    TextView shareButton;
    String telNumber = "4156236129";

    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate() state: " + Dt.toString(savedInstanceState));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        initOnCreate();
    }

    @Override
    public void finish() {
        Log.w(TAG, "finish() called from: " + Dt.printStackTrace());
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult() requestCode: " + requestCode + ", resultCode: " + resultCode + ", intent: " + Dt.toString(data.getExtras()));
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void phonecall() {
        String uri = "tel:" + telNumber;
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
        try {
            startActivity(dialIntent);
        } catch (Exception e) {

        }

    }

    public void sendEmail() {
        final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { "abc@gmail.com" });

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Email Subject");

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Email Body");

        startActivity(Intent.createChooser(
                emailIntent, "Send mail..."));

    }

    public void shareFacebook() {

    }


    private void initOnCreate() {
        callButton = (TextView) findViewById(R.id.screen_rename_me_now_btn_call);
        emailButton = (TextView) findViewById(R.id.screen_rename_me_now_btn_email);
        shareButton = (TextView) findViewById(R.id.screen_rename_me_now_btn_share);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonecall();
            }

        });
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }

        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareFacebook();
            }

        });
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                //  Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                // Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                //Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    //     Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }


}
