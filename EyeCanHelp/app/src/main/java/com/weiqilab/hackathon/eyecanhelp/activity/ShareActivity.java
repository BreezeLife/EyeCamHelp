package com.weiqilab.hackathon.eyecanhelp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.weiqilab.hackathon.eyecanhelp.R;
import com.weiqilab.hackathon.eyecanhelp.pojo.Kid;
import com.weiqilab.hackathon.eyecanhelp.pojo.Report;

import tools.stio.atlas.Dt;


/**
 * Created by Enuviel on 8/27/16.
 */
public class ShareActivity extends Activity {
    private static final String TAG = ShareActivity.class.getSimpleName();

    TextView callButton;
    TextView emailButton;
    TextView shareButton;
    TextView currentLocation;
    TextView body;
    Report report;
    Bitmap basephoto;
    Kid kid;
    String telNumber = "4156236129";
    String email = "enuviel13@gmail.com";
    ImageView kidPhoto;

    private ShareLinkContent shareLinkContent;
    private ShareDialog shareDialog;

    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate() state: " + Dt.toString(savedInstanceState));
        super.onCreate(savedInstanceState);
        String kidUUId= getIntent().getExtras().getString("kidUUId");
        kid= new Kid();
        kid.setAge("18");
        kid.setKidName("Biwei Tao");
        kid.setMissingDate("27 Aug 2016");
        kid.setLocation("1 Hacker Way, Menlo Park CA 94025");
        kid.setContactEmail("enuviel13@gmail.com");
        kid.setContactCallNubmer("4156236129");


        setContentView(R.layout.share_activity);

        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        kidPhoto = (ImageView)findViewById(R.id.screen_rename_me_now_body_img);
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
                new String[] { email });

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Missing girl "+kid.getKidName()+" was seen at " +currentLocation.toString() );

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Missing girl "+kid.getKidName()+" was seen at " +currentLocation.toString() );

        startActivity(Intent.createChooser(
                emailIntent, "Send mail..."));

    }

    public void shareFacebook() {

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Missing girl "+kid.getKidName()+" was seen at " +currentLocation.toString() )
                    .setContentDescription("Missing girl "+kid.getKidName()+" was seen at " +currentLocation.toString() )
                    .setContentUrl(Uri.parse("http://www.google.com"))
                    .setImageUrl(Uri.parse("http://placehold.it/350x150"))
                    .build();
            shareDialog.show(shareLinkContent);
        }
//
//        genSharedPostOnFacebook();
//        ShareDialog.show(ShareActivity.this, shareLinkContent);
    }


    private void initOnCreate() {
        callButton = (TextView) findViewById(R.id.screen_rename_me_now_btn_call);
        emailButton = (TextView) findViewById(R.id.screen_rename_me_now_btn_email);
        shareButton = (TextView) findViewById(R.id.screen_rename_me_now_btn_share);
        currentLocation=(TextView) findViewById(R.id.screen_rename_me_now_title_text);
        body=(TextView)findViewById(R.id.screen_rename_me_now_body_text);
        String textBody=kid.getKidName()+"/n"+kid.getAge()+" years "+"/n"+kid.getMissingDate()
                +"/n"+kid.getLocation();
        body.setText(textBody);
        String textTitle=report.getKidData()+"/n"+report.getLocation_Lat();
        currentLocation.setText(textTitle);
        telNumber=kid.getContactCallNubmer();
        email=kid.getContactEmail();



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

        shareDialog = new ShareDialog(this);
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

    // updated by Weiqi Zhao
    private void genSharedPostOnFacebook () {
         shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle("Your Title")
                .setContentDescription("Your Description")
                .setContentUrl(Uri.parse("URL[will open website or app]"))
                .setImageUrl(Uri.parse("image or logo [if playstore or app store url then no need of this image url]"))
                .build();

    }

}
