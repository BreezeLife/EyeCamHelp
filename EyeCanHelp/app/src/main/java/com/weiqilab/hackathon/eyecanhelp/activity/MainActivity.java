package com.weiqilab.hackathon.eyecanhelp.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.SimilarPersistedFace;
import com.weiqilab.hackathon.eyecanhelp.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;

    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;

    private Button btnEyeCanHelp;
    private boolean isEyeHelping;
    private boolean isCameraInUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isEyeHelping = false;

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // call for the projection manager
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

//        // start projection
//        Button startButton = (Button)findViewById(R.id.startButton);
//        startButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startProjection();
//            }
//        });
//
//        // stop projection
//        Button stopButton = (Button)findViewById(R.id.stopButton);
//        stopButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                stopProjection();
//            }
//        });

        btnEyeCanHelp = (Button) findViewById(R.id.eyecanhelpButton);

        if (!isEyeHelping) {
            btnEyeCanHelp.setText(R.string.start_recording_button);
        } else {
            btnEyeCanHelp.setText(R.string.end_recording_button);
        }
        btnEyeCanHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEyeHelping) {
                    startProjection();
                } else {
                    stopProjection();
                }
            }
        });

        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }.start();

        showNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;
                Log.d(TAG, "camera: " + checkCameraOn());
                try {
                    image = mImageReader.acquireLatestImage();
                    if (image != null) {
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * mWidth;

                        // create bitmap
                        bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);

                        // write bitmap to a file
                        fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
                        Log.d(TAG, "Screenshot saved:" + STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        IMAGES_PRODUCED++;
                        Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos!=null) {
                        try {
                            fos.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }

                    if (bitmap!=null) {
                        bitmap.recycle();
                    }

                    if (image!=null) {
                        image.close();
                    }
                }
            }
    }

    private class OrientationChangeCallback extends OrientationEventListener {
        public OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            synchronized (this) {
                final int rotation = mDisplay.getRotation();
                if (rotation != mRotation) {
                    mRotation = rotation;
                    try {
                        // clean up
                        if(mVirtualDisplay != null) mVirtualDisplay.release();
                        if(mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                        // re-create virtual display depending on device width / height
                        createVirtualDisplay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mVirtualDisplay != null) mVirtualDisplay.release();
                    if(mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if(mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE) {
                btnEyeCanHelp.setText(R.string.end_recording_button);
                sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

                if (sMediaProjection != null) {

                    // create file storage directory
                    File externalFilesDir = getExternalFilesDir(null);
                    if (externalFilesDir != null) {
                        STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots/";
                        File storeDirectory = new File(STORE_DIRECTORY);
                        if (!storeDirectory.exists()) {
                            boolean success = storeDirectory.mkdirs();
                            if (!success) {
                                Log.e(TAG, "failed to create file storage directory.");
                                return;
                            }
                        }
                    } else {
                        Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                        return;
                    }

                    // display metrics
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    mDensity = metrics.densityDpi;
                    mDisplay = getWindowManager().getDefaultDisplay();

                    // create virtual display depending on device width / height
                    createVirtualDisplay();

                    // register orientation change callback
                    mOrientationChangeCallback = new OrientationChangeCallback(this);
                    if (mOrientationChangeCallback.canDetectOrientation()) {
                        mOrientationChangeCallback.enable();
                    }

                    // register media projection stop callback
                    sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
                }
            }
    }


    /****************************************** UI Widget Callbacks *******************************/
    private void startProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
        isEyeHelping = true;
    }

    private void stopProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                    isEyeHelping = false;
                }
            }
        });
        btnEyeCanHelp.setText(R.string.start_recording_button);
    }

    /****************************************** Factoring Virtual Display creation ****************/
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    private boolean checkCameraOn() {
        // within constructor
        // Figure out if Camera is Available or Not
        CameraManager cam_manager = (CameraManager) getBaseContext().getSystemService(Context.CAMERA_SERVICE);

        CameraManager.AvailabilityCallback camAvailCallback = new CameraManager.AvailabilityCallback() {
            public void onCameraAvailable(String cameraId) {

                isCameraInUse=false;
                Log.d(TAG, "notified that camera is not in use.");

            }

            public void onCameraUnavailable(String cameraId) {

                isCameraInUse=true;
                Log.d(TAG, "notified that camera is in use.");

            }
        };
        cam_manager.registerAvailabilityCallback(camAvailCallback, mHandler);
        return isCameraInUse;
    }


    private void startProjectionTask() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkCameraOn()) {
                    startProjection();
                    isEyeHelping = true;
                } else {
                    stopProjection();
                    isEyeHelping = false;
                }
            }
        });
    }

    /**
     * notification
     */
    public void showNotification() {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, ShareActivity.class), 0);
        Resources r = getResources();

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.notification_title))
                .setContentTitle(r.getString(R.string.notification_title))
                .setContentText(r.getString(R.string.notification_text))
                .setContentIntent(pi)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


    /**
     * Utility functions for dectecting and finding similar faces
     */

    // servive client of Microsoft Face API
    private FaceServiceClient faceServiceClient;
    // similar faces found
    private List<UUID> similarFacesDetected;


    // main function for detection and finding similar faces
    private void detectAndFindSimilarFaces(final Bitmap imageBitmap)
    {
        faceServiceClient =
                new FaceServiceRestClient(getString(R.string.subscription_key));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        new DetectionTask().execute(inputStream);
    }
    // Background task for face detection
    class DetectionTask extends AsyncTask<InputStream, String, Face[]> {

        @Override
        protected Face[] doInBackground(InputStream... params) {
            try {
                publishProgress("Detecting...");
                Face[] result = faceServiceClient.detect(
                        params[0],
                        true,         // returnFaceId
                        false,        // returnFaceLandmarks
                        null           // returnFaceAttributes: a string like "age, gender"
                );
                if (result == null)
                {
                    publishProgress("Detection Finished. Nothing detected");
                    return null;
                }
                publishProgress(
                        String.format("Detection Finished. %d face(s) detected",
                                result.length));
                return result;
            } catch (Exception e) {
                publishProgress("Detection failed");
                return null;
            }
        }
        @Override
        protected void onPostExecute(Face[] result) {
            if (result != null) {
                // Only return 1 face detected
                UUID faceDetected = result[0].faceId;
                new FindSimilarFaceTask().execute(faceDetected);

            }

        }

    }
    // Background task for finding similar faces.
    private class FindSimilarFaceTask extends AsyncTask<UUID, String, SimilarPersistedFace[]> {

        @Override
        protected SimilarPersistedFace[] doInBackground(UUID... params) {
            // Get an instance of face service client to detect faces in image.
            try {
                publishProgress("Finding Similar Faces...");
                String faceListId = getString(R.string.face_list_id);

                SimilarPersistedFace[] result = faceServiceClient.findSimilar(
                        params[0],    /* The first face ID to verify */
                        faceListId,      /* The face list ID to find match */
                        1); /* max number of match returned*/
                if (result == null) {
                    publishProgress("Finding Similar Faces Finished. Nothing detected");
                    return null;
                }
                publishProgress(
                        String.format("Finding Similar Faces Finished. %d face(s) detected",
                                result.length));
                return result;
            } catch (Exception e) {
                publishProgress("Finding Similar Faces failed");
                return null;
            }
        }

        @Override
        protected void onPostExecute(SimilarPersistedFace[] result) {
            if (result != null) {
                similarFacesDetected = new ArrayList<>();
                for (SimilarPersistedFace face : result) {
                    similarFacesDetected.add(face.persistedFaceId);
                }
            }

        }
    }
}
