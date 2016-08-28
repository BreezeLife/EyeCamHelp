package com.weiqilab.hackathon.eyecanhelp.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.SimilarPersistedFace;
import com.weiqilab.hackathon.eyecanhelp.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceFaceDetection extends Service {

    private FaceServiceClient faceServiceClient;

    @Override
    public void onCreate() {
        super.onCreate();
        faceServiceClient =
                new FaceServiceRestClient(getString(R.string.subscription_key));
    }
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    // main function for detection and finding similar faces
    private void detectAndFindSimilarFaces(final Bitmap imageBitmap)
    {
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
                if (result == null)
                {
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
                List<UUID> similarFacesDetected = new ArrayList<>();
                for (SimilarPersistedFace face: result) {
                    similarFacesDetected.add(face.persistedFaceId);
                }
                // Persist similarFacesDetected?
            }

        }

    }


}
