package com.mact.B2SCam;

import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

/* loaded from: classes4.dex */
public class GraphicFaceTracker extends Tracker<Face> {
    private static final float CLOSE_THRESHOLD = 0.4f;
    private static final float OPEN_THRESHOLD = 0.85f;
    private final CameraActivity cameraActivity;
    private int state = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GraphicFaceTracker(CameraActivity cameraActivity) {
        this.cameraActivity = cameraActivity;
    }

    private void blink(float value) {
        switch (this.state) {
            case 0:
                if (value > OPEN_THRESHOLD) {
                    this.state = 1;
                    return;
                }
                return;
            case 1:
                if (value < CLOSE_THRESHOLD) {
                    this.state = 2;
                    return;
                }
                return;
            case 2:
                if (value > OPEN_THRESHOLD) {
                    Log.i("Camera Demo", "blink has occurred!");
                    this.state = 0;
                    this.cameraActivity.captureImage();
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override // com.google.android.gms.vision.Tracker
    public void onUpdate(Detector.Detections<Face> detectionResults, Face face) {
        float left = face.getIsLeftEyeOpenProbability();
        float right = face.getIsRightEyeOpenProbability();
        if (left == -1.0f || right == -1.0f) {
            return;
        }
        float value = Math.min(left, right);
        blink(value);
    }
}
