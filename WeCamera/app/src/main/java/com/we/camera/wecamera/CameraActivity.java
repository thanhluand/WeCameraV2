package com.we.camera.wecamera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    private boolean isRecording = false;
    private boolean isFlash = false;

    private Button captureButton;
    private Button videoButton;
    public Button flashButton;

    private CameraPreview mCPreview;
    FrameLayout flPreview;

    public ArrayAdapter<String> adapter;

    String sCapture;
    public Chronometer chronometer;
    public String mCurrent;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static  final String TAG = "YOUR-TAG-NAME";

    protected static final int h = 0, m = 0, s = 0;
    public static int orientation;

    public Button tripButton;
    public static int CameraID = 0;

    public String fileVideoPath;

    /* HorizontalScrollView - Effect */
    // GUI controls
    ViewGroup scrollViewgroup;

    // each frame in the HorizontalScrollView has [icon, caption]
    ImageView icon;
    TextView caption;

    // frame captions
    String[] items = {"None", "Autofix", "Brightness", "BW", "Contrast",
            "Documentary", "Duotone", "Fill Light", "Grain", "Grayscale", "Lomoish",
            "Negative", "Posterize", "Rotate", "Saturate", "Scross Process", "Sepia",
            "Sharpen", "Temperature", "Tint", "Vignette", "Fliphor", "Flipvert"};

    // frame-icons ( 100x100 thumbnails )
    Integer[] thumbnails = { R.drawable.seolhyun,
            R.drawable.documentary,
            R.drawable.negative,
            R.drawable.brightness,
            R.drawable.posterize,
            R.drawable.sepia,
            R.drawable.constrast,
            R.drawable.doutone,
            R.drawable.filllight,
            R.drawable.grain,
            R.drawable.grayscale,
            R.drawable.lomoish,
            R.drawable.rotate,
            R.drawable.saturate,
            R.drawable.autofix,
            R.drawable.scrossprocess,
            R.drawable.sharpen,
            R.drawable.temperature,
            R.drawable.tint,
            R.drawable.bw,
            R.drawable.vignette,
            R.drawable.fliphor,
            R.drawable.flipvert };

    Integer[] itemsEffect = { R.id.none, R.id.autofix, R.id.brightness, R.id.bw, R.id.contrast, R.id.documentary,
            R.id.duotone, R.id.filllight, R.id.grain, R.id.grayscale, R.id.lomoish, R.id.negative, R.id.posterize,
            R.id.rotate, R.id.saturate, R.id.crossprocess, R.id.sepia, R.id.sharpen, R.id.temperature, R.id.tint,
            R.id.vignette, R.id.fliphor, R.id.flipvert };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        flPreview  = (FrameLayout)findViewById(R.id.camera_preview);

        if (Camera.getNumberOfCameras() < 2)
            tripButton.setVisibility(View.GONE);
        CameraID = Camera.CameraInfo.CAMERA_FACING_BACK;


        mCamera = getCameraInstance();

        tripButton = (Button) findViewById(R.id.btnSwitch);
        tripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        mCPreview = new CameraPreview(this, mCamera);
        flPreview.addView(mCPreview);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.setVisibility(View.GONE);

        // Check effect
        Parameters params = mCamera.getParameters();
        List<String> list = params.getSupportedColorEffects();

        // Horizontal Scroll effect
        scrollViewgroup = (ViewGroup)findViewById(R.id.viewgroup);

        // populate the ScrollView
        int numEffect;
        if (list.size() > 9)
            numEffect = 9;
        else
            numEffect = list.size();

        for (int i = 0; i < numEffect; i++) {

            final View singleFrame = getLayoutInflater().inflate(
                    R.layout.frame_icon_caption, null);

            // frame: 0, frame: 1, frame: 2, ... and so on
            singleFrame.setId(i);

            // internal plumbing to reach elements inside single frame
            TextView caption = (TextView) singleFrame.findViewById(R.id.caption);
            ImageView icon = (ImageView) singleFrame.findViewById(R.id.icon);

            // put data [icon, caption] in each frame
            icon.setImageResource(thumbnails[i]);
            caption.setText(list.get(i).toString());
            //caption.setBackgroundColor(Color.YELLOW);

            // add frame to the scrollView
            scrollViewgroup.addView(singleFrame);

            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //setCurrentEffect(itemsEffect[singleFrame.getId()]);
                    //mEffectView.requestRender();
                    Parameters params = mCamera.getParameters();
                    List<String> list = params.getSupportedColorEffects();
                    switch (list.get(singleFrame.getId())){
                        case "none":
                            params.setColorEffect(Parameters.EFFECT_NONE);
                            break;
                        case "mono":
                            params.setColorEffect(Parameters.EFFECT_MONO);
                            break;
                        case "negative":
                            params.setColorEffect(Parameters.EFFECT_NEGATIVE);
                            break;
                        case "solarize":
                            params.setColorEffect(Parameters.EFFECT_SOLARIZE);
                            break;
                        case "posterize":
                            params.setColorEffect(Parameters.EFFECT_POSTERIZE);
                            break;
                        case "sepia":
                            params.setColorEffect(Parameters.EFFECT_SEPIA);
                            break;
                        case "blackboard":
                            params.setColorEffect(Parameters.EFFECT_BLACKBOARD);
                            break;
                        case "whiteboard":
                            params.setColorEffect(Parameters.EFFECT_WHITEBOARD);
                            break;
                        case "aqua":
                            params.setColorEffect(Parameters.EFFECT_AQUA);
                            break;
                    }

                    mCamera.setParameters(params);
                }
            });
        }
        // ---------------

        flashButton = (Button)findViewById(R.id.btnFlash);
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasFlash = CameraActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                if (hasFlash) {
                    Parameters p = mCamera.getParameters();

                    // TODO Auto-generated method stub
                    if (p.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_ON)) {
                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flashButton.setText("Off");
                        Log.e("Torch", "MODE OFF");

                    } else if (p.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_OFF)) {
                        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flashButton.setText("TORCH");
                        Log.e("Torch", "MODE TORCH");

                    } else if (p.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_AUTO)) {
                        p.setFlashMode(Parameters.FLASH_MODE_ON);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flashButton.setText("ON");
                        Log.e("Torch", "MODE ON");

                    } else if (p.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)) {
                        p.setFlashMode(Parameters.FLASH_MODE_AUTO);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flashButton.setText("AUTO");
                        Log.e("Torch", "MODE AUTO");

                    } else {
                        p.setFlashMode(Parameters.FLASH_MODE_AUTO);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flashButton.setText(p.getFlashMode().toString());
                        Log.e("Torch", "MODE AUTO");
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"device does not contain flash", Toast.LENGTH_LONG).show();
                }
            }
        });

        videoButton = (Button)findViewById(R.id.btnVideo);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    // stop recording and release camera
                    File saveVideo = getOutputMediaFile(MEDIA_TYPE_IMAGE);

                    chronometer.stop();
                    chronometer.setVisibility(View.GONE);

                    mMediaRecorder.stop();  // stop the recording
                    releaseMediaRecorder(); // release the MediaRecorder object
                    mCamera.lock();         // take camera access back from MediaRecorder

                    // inform the user that recording has stopped
                    Context context = getApplicationContext();
                    CharSequence text = "Hello toast!";
                    int duration = Toast.LENGTH_SHORT;
                    videoButton.setText("Start");

                    galleryAddPic(saveVideo.getAbsolutePath());
                    galleryAddPic(fileVideoPath);

                    isRecording = false;
                } else {
                    videoButton.setText("Stop");
                    // initialize video camera
                    if (prepareVideoRecorder()) {
                        // Camera is available and unlocked, MediaRecorder is prepared
                        // now you can start recording

                        // inform the user that recording has started
                        mMediaRecorder.start();

                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        chronometer.setVisibility(View.VISIBLE);

                        isRecording = true;
                    } else {
                        // prepare didn't work, release the camera
                        releaseMediaRecorder();
                        // inform user
                    }
                }
            }
        });

        captureButton = (Button)findViewById(R.id.btnCapture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(shutterCallback, null, jpegCallback);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String d = "m";
                restartCamera();
            }
        });
    }

    public void switchCamera(){
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        if(CameraID == Camera.CameraInfo.CAMERA_FACING_BACK){
            CameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else{
            CameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera = Camera.open(CameraID);

        mCPreview.setCameraDisplayOrientation(CameraActivity.this, CameraID, mCamera);
        try {

            mCamera.setPreviewDisplay(mCPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    public void restartCamera() {
        mCamera.startPreview();
    }

    public void startCamera() {
        mCamera = getCameraInstance();
        mCPreview = new CameraPreview(this, mCamera);
        flPreview.addView(mCPreview);
    }

    public void previewPicture() {
        flPreview.addView(mCPreview);
    }

    private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    private final Camera.PictureCallback jpegCallback  = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap oldBitmap = bitmap;
            Matrix matrix = new Matrix();

            if(CameraID == Camera.CameraInfo.CAMERA_FACING_BACK){
                matrix.postRotate(orientation);
            }else{
                matrix.postRotate(-orientation);
            }

            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
            oldBitmap.recycle();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            data = stream.toByteArray();
            String mCurrentFile =  pictureFile.getAbsolutePath();
            mCurrent = mCurrentFile;
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: " ); // thêm Exeption  //
                return;
            }
            try{
                FileOutputStream fos  = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            }catch (FileNotFoundException e){

            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            galleryAddPic(mCurrentFile);
        }
    };

    private void galleryAddPic() {}

    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(CameraID); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /* Create a File for saving an image or video */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled

        // Create the storage diractory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    // VIDEO capture
    private boolean prepareVideoRecorder() {
        mCPreview.setCameraDisplayOrientation(this, CameraID, mCamera);
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        fileVideoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        if (CameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mMediaRecorder.setOutputFile(fileVideoPath);
            mMediaRecorder.setOrientationHint(90);
        }

        if (CameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setOutputFile(fileVideoPath);
            mMediaRecorder.setOrientationHint(270);
        }

        // Step 4: Set output file
        galleryAddPic(fileVideoPath);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mCPreview.getHolder().getSurface());
        int  m = CameraActivity.orientation;

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();     // clear recorder configuration
            mMediaRecorder.release();   // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();             // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();      // release the camera for other applications
            mCamera = null;
        }
    }

    protected void onPause() {
        super.onPause();
        mCPreview.getHolder().removeCallback(mCPreview);
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }


}
