package com.example.rog.mcpix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;


public class CameraActivity extends AppCompatActivity {
    private static String imgName = null;
    private static String path = null;
    private Camera mCamera;
    private CameraTest mTest;
    private int lastOrientation = 90;
    private OrientationEventListener mOrientationListener;
    private ImageButton s;
    private ImageButton f;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private ImageButton capture_button;
    private boolean isPicture = false;
    private boolean flashStatus = false;
    private static boolean longClickActive = false;
    private static boolean isLongclick = false;
    private static long startClickTime;
    private String idfrom ="";
    public static int maxZoom= 0;
    public static int zoom = 0;
    public static boolean sizePictureCam=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        s = (ImageButton) findViewById(R.id.gbr2txt);
        f = (ImageButton) findViewById(R.id.flashButton);

        mCamera = mTest.getCameraInstance();
        mTest = new CameraTest(this, mCamera);

        idfrom = getIntent().getStringExtra("idfrom");

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPicture){
                    isPicture=false;
                    s.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.text_btn));
                }else{
                    isPicture=true;
                    s.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gbr_btn));
                }
            }
        });

        if (hasFlash()){
            f.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flashStatus){
                        flashStatus=false;
                        f.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flash_off));
                    }else{
                        flashStatus=true;
                        f.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flash_on));
                    }
                }
            });
        }

        final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mTest);
        capture_button = (ImageButton) findViewById(R.id.button_capture);

        final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
            }
        };


        capture_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        capture_button.setImageDrawable(getDrawable(R.drawable.camera_btn));
                        if (isLongclick){
                        }else{
                            mTest.setHasCaptured();
                            mOrientationListener.disable();
                            capture_button.setEnabled(false);
                            capture_button.setClickable(false);
                            if (flashStatus){
                                Thread thread1 = new Thread() {
                                    public void run() {
                                        mCamera.takePicture(shutterCallback,null,mPicture);
                                    }
                                };
                                Thread thread2 = new Thread() {
                                    public void run() {
                                        mTest.blinkFlash();
                                    }
                                };
                                thread2.run();
                                thread1.run();
                                try {
                                    thread1.join();
                                    thread2.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Log.d("Gabung", "Gagal bro");
                                }
                            }else{
                                mCamera.takePicture(shutterCallback,null,mPicture);
                            }
                        }
                        longClickActive = false;
                        startClickTime = 0;
                        isLongclick = false;
                        break;
                    case MotionEvent.ACTION_DOWN:
                            longClickActive = true;
                           startClickTime = Calendar.getInstance().getTimeInMillis();
                        capture_button.setImageDrawable(getDrawable(R.drawable.camera_btn_pressed));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (longClickActive == true) {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if (clickDuration > 150) {
                                isLongclick = true;
                            }
                        }
                        break;
                }

                return true;
            }
        });

//        capture_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOrientationListener.disable();
//                if (flashStatus){
//                    Thread thread1 = new Thread() {
//                        public void run() {
//                            mCamera.takePicture(shutterCallback,null,mPicture);
//                        }
//                    };
//                    Thread thread2 = new Thread() {
//                        public void run() {
//                            mTest.blinkFlash();
//                        }
//                    };
//                    thread1.run();
//                    thread2.run();
//                    try {
//                        thread1.join();
//                        thread2.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Log.d("Gabung", "Gagal bro");
//                    }
//                }else{
//                    mCamera.takePicture(shutterCallback,null,mPicture);
//                }
//
//            }
//        });

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                Log.v("Orientation : ","Orientation changed to " + orientation);
                if(orientation>60&&orientation<=120){
                    s.setRotation(270);
                    f.setRotation(270);
                    lastOrientation = 180;
                }else if(orientation>120&&orientation<=240){
                    s.setRotation(180);
                    f.setRotation(180);
                    lastOrientation = 270;
                }else if(orientation>240&&orientation<=300){
                    s.setRotation(90);
                    f.setRotation(90);
                    lastOrientation = 0;
                }else{
                    s.setRotation(0);
                    f.setRotation(0);
                    lastOrientation = 90;
                }
                mTest.setCameraParameters(lastOrientation);
            }
        };

        if (mOrientationListener.canDetectOrientation() == true) {
            Log.v("OR", "Can detect orientation");
            mOrientationListener.enable();
        } else {
            Log.v("GG", "Cannot detect orientation");
            mOrientationListener.disable();
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mCamera.stopPreview();
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            Matrix matrix = new Matrix();
            matrix.postRotate(lastOrientation);
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

            if (pictureFile == null){
                Log.d("Error", "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos;
                fos = new FileOutputStream(pictureFile);
                bmp.compress(Bitmap.CompressFormat.JPEG,70 , fos);
                fos.write(data);
                fos.flush();
                fos.close();
//                bmp.recycle();
//                ExifInterface exifInterface = new ExifInterface(path+File.separator+imgName);
//                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(lastOrientation));
//                exifInterface.saveAttributes();
                Intent intent = new Intent(getApplicationContext(),PhotoPreview.class).putExtra("imgPath",path).putExtra("imgName",imgName).putExtra("lastOrientation",lastOrientation+"").putExtra("idfrom",idfrom);
                startActivity(intent);
                finish();
            } catch (FileNotFoundException e) {
                Log.d("Error", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Error", "Error accessing file: " + e.getMessage());
            }
        }
    };

    public boolean hasFlash() {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters = mCamera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            Log.d("Flashlight : ","Tidak Punya Kamera");
            return false;
        }else{
            Log.d("Flashlight : ","Punya Kamera");
        }

        return true;
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Pixature");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Pixature", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        DateFormat df = new java.text.SimpleDateFormat("ddMMyyHHmmss");
        String date = df.format(java.util.Calendar.getInstance().getTime());
        File mediaFile;
        if (isPicture){
            imgName = "IMG_"+ date + ".jpg";
        }else{
            imgName = "TXT_"+ date + ".jpg";
        }

        path = getIntent().getStringExtra("datapath");

        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(path + File.separator + imgName);
        }  else {
            return null;
        }

        return mediaFile;
    }


    public static class CameraTest extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private android.hardware.Camera.Parameters cameraParameters;
        float mDist = 0;
        private boolean isCaptured = false;

        public CameraTest(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public static Camera getCameraInstance(){
            Camera c = null;
            try {
                c = Camera.open(); // attempt to get a Camera instance
            }
            catch (Exception e){
                // Camera is not available (in use or does not exist)
            }
            return c; // returns null if camera is unavailable
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                initiateParams();
                mCamera.setParameters(cameraParameters);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d("SurfaceCreated Fail", "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            requestLayout();
            mCamera.setParameters(cameraParameters);
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                // Call stopPreview() to stop updating the preview surface.
                mCamera.stopPreview();
                Log.d("CAMERA DITUTUP","CAMERA BERHASIL DITUTUP");
            }
        }

        public void initiateParams(){
            if (mCamera==null){
                getCameraInstance();
            }
            cameraParameters = mCamera.getParameters();
//        cameraParameters.set("orientation", "potrait");
//        cameraParameters.setRotation(90);
//        cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            List<Camera.Size> supportedSizes = cameraParameters.getSupportedPictureSizes();
            Camera.Size sizePicture = supportedSizes.get(0);
            for(int i = 1; i < supportedSizes.size(); i++){
                if((supportedSizes.get(i).width * supportedSizes.get(i).height) > (sizePicture.width * sizePicture.height)){
                    sizePicture = supportedSizes.get(i);
                }
//                if((supportedSizes.get(i).width * supportedSizes.get(i).height) == (3264 * 1836)){
//                    sizePicture = supportedSizes.get(i);
//                }
            }
            Log.d("SIZE",sizePictureCam+"");
            cameraParameters.setPictureSize(sizePicture.width, sizePicture.height);
            mCamera.setDisplayOrientation(90);
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cameraParameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
        }

        public void changeSize(){
            if (mCamera==null){
                getCameraInstance();
            }
            cameraParameters = mCamera.getParameters();

            if (sizePictureCam){
                List<Camera.Size> supportedSizes = cameraParameters.getSupportedPictureSizes();
                Camera.Size sizePicture = supportedSizes.get(0);
                for(int i = 1; i < supportedSizes.size(); i++){
                    if((supportedSizes.get(i).width * supportedSizes.get(i).height) > (sizePicture.width * sizePicture.height)){
                        sizePicture = supportedSizes.get(i);
                    }
                }
                cameraParameters.setPictureSize(sizePicture.width,sizePicture.height);
            }else{
                cameraParameters.setPictureSize(3264,1836);
            }
        }

        public void setCameraParameters(int orientation){
//        Camera.Parameters params = mCamera.getParameters();
//        params.setRotation(orientation);
//        mCamera.setParameters(params);
            Log.d("Orientasi Dirubah",orientation+"");
        }


//    public static void setCameraDisplayOrientation(Activity activity,
//                                                   int cameraId, android.hardware.Camera camera) {
//        android.hardware.Camera.CameraInfo info =
//                new android.hardware.Camera.CameraInfo();
//        android.hardware.Camera.getCameraInfo(cameraId, info);
//        int rotation = activity.getWindowManager().getDefaultDisplay()
//                .getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0: degrees = 0; break;
//            case Surface.ROTATION_90: degrees = 90; break;
//            case Surface.ROTATION_180: degrees = 180; break;
//            case Surface.ROTATION_270: degrees = 270; break;
//        }
//
//        int result;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - degrees + 360) % 360;
//        }
//        camera.setDisplayOrientation(result);
//    }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // Get the pointer ID
            Camera.Parameters params = mCamera.getParameters();
            int action = event.getAction();


            if (event.getPointerCount() > 1) {
                // handle multi-touch events
                if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    mDist = getFingerSpacing(event);
                } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                    mCamera.cancelAutoFocus();
                    handleZoom(event, params);
                }
            } else {
                // handle single touch events
                if (action == MotionEvent.ACTION_UP) {
                    handleFocus(event, params);
                }
            }
            return true;
        }

        public void setHasCaptured(){
            isCaptured = true;
        }

        private void handleZoom(MotionEvent event, Camera.Parameters params) {
            if (!isCaptured){
                maxZoom = params.getMaxZoom();
                zoom = params.getZoom();
                float newDist = getFingerSpacing(event);
                if (newDist > mDist) {
                    //zoom in
                    if (zoom < maxZoom)
                        zoom++;
                } else if (newDist < mDist) {
                    //zoom out
                    if (zoom > 0)
                        zoom--;
                }
                mDist = newDist;
                params.setZoom(zoom);
                mCamera.setParameters(params);
            }
        }

        public void handleFocus(MotionEvent event, Camera.Parameters params) {
            if (!isCaptured){
                int pointerId = event.getPointerId(0);
                int pointerIndex = event.findPointerIndex(pointerId);
                // Get the pointer's current position
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);

                List<String> supportedFocusModes = params.getSupportedFocusModes();
                if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean b, Camera camera) {
                            // currently set to auto-focus on single touch
                        }
                    });
                }
            }
        }

        /** Determine the space between the first two fingers */
        private float getFingerSpacing(MotionEvent event) {
            // ...
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float)Math.sqrt(x * x + y * y);
        }

        public void blinkFlash(){
            String myString = "11100110";
            long blinkDelay = 120;
            for (int i = 0; i < myString.length(); i++) {
                if (myString.charAt(i) == '1') {
                    cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(cameraParameters);
//                mCamera.startPreview();
                } else {
                    cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(cameraParameters);
//                mCamera.startPreview();
                }
                try {
                    Thread.sleep(blinkDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (idfrom!=null){
            Intent intent = new Intent(getApplicationContext(),Gallery.class);
            intent.putExtra("path",path);
            startActivity(intent);
            capture_button.setEnabled(false);
            Runtime.getRuntime().freeMemory();
            Runtime.getRuntime().gc();
            finish();
        }else{
            Intent intent = new Intent(getApplicationContext(),ChooseFolder.class);
            startActivity(intent);
            capture_button.setEnabled(false);
            Runtime.getRuntime().freeMemory();
            Runtime.getRuntime().gc();
            finish();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mOrientationListener.disable();
        capture_button.setEnabled(false);
    }
    @Override
    protected void onResume(){
        super.onResume();
        mOrientationListener.enable();
        capture_button.setEnabled(true);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mOrientationListener.disable();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
    }
}
