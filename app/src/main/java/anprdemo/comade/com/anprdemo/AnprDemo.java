package anprdemo.comade.com.anprdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Handheld.AnprEngineLib.AnprEngine;
import com.Handheld.AnprEngineLib.AnprEngineListener;
import com.Handheld.AnprEngineLib.LicenseCheckFailedException;
import com.Handheld.AnprEngineLib.PreviewAnprImage;


public class AnprDemo extends Activity implements SurfaceHolder.Callback, AnprEngineListener, Camera.PreviewCallback {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private final String tag = "AnprDemo";
    private Button start, stop;
    private AnprEngine anprEngine;
    private ImageView patchImage;
    private TextView vrmText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anpr_demo);
        setUpImageAndTextViews();
        setUpButtons();
        setUpSurfaceView();
        startAnprEngine();
    }

    private void setUpImageAndTextViews(){
        patchImage = (ImageView) findViewById(R.id.patch);
        vrmText = (TextView) findViewById(R.id.vrm_text);
    }

    private void setUpButtons(){
        start = (Button) findViewById(R.id.btn_start);
        start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                startCamera();
            }
        });

        stop = (Button) findViewById(R.id.btn_stop);
        stop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                stopCamera();
            }
        });
    }

    private void setUpSurfaceView(){
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    private void startAnprEngine(){
        try {
            final AnprDemo thisDemoActivity = this;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    anprEngine = new AnprEngine(thisDemoActivity);
                    anprEngine.checkLicense();
                    try {
                        anprEngine.initialise(thisDemoActivity);
                    } catch (LicenseCheckFailedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            }.execute();
        } catch (Exception e) {
            Log.e(tag, "license error", e);
        }
    }

    private void startCamera() {
        openCamera();
        setUpCameraParameters();
        setUpPreview();

    }

    private void setUpPreview(){
        try{
            camera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            Log.e(tag, "init_camera: " + e);
            return;
        }
        camera.startPreview();
        camera.setOneShotPreviewCallback(this);
    }

    private void openCamera(){
        camera = Camera.open();
        camera.setDisplayOrientation(90);
    }

    private void setUpCameraParameters(){
        Camera.Parameters params;
        params = camera.getParameters();
        Camera.Size previewSize = camera.new Size(320, 240);
        params.setPreviewSize(previewSize.width, previewSize.height);
        params.setPreviewFpsRange(20, 20);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Size imageSize = camera.getParameters().getPreviewSize();
        try {
            anprEngine.processImage(new PreviewAnprImage(imageSize.width, imageSize.height, data.clone()));
        } catch (Exception e) {
            Log.e(tag, "IMAGE SEND FAILED: ", e);
        }
        camera.setOneShotPreviewCallback(this);

    }

    private void stopCamera() {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onVrmFound(Bitmap bitmap, String vrm) {
        vrmText.setText(vrm);
        patchImage.setImageBitmap(bitmap);
    }

    @Override
    public void onProgressUpdate(String s) {
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }




}
