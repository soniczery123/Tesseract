package com.test.epassportreadertesseract;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceView cameraView, transparentView;
    private SurfaceHolder holder, holderTransparent;
    boolean play = false;
    private Camera camera;
    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private TessBaseAPI tessBaseAPI;
    private Bitmap bitmapCrop;
    private int widthPic, heightPic;
    final int RequestPermissionCode = 1;
    private int RectLeft, RectTop, RectRight, RectBottom;
    int deviceHeight, deviceWidth;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_camera);

        int permissionCheck1 = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA);
        int permissionCheck2 = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck3 = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck1 == PackageManager.PERMISSION_DENIED || permissionCheck2 == PackageManager.PERMISSION_DENIED
                 || permissionCheck3 == PackageManager.PERMISSION_DENIED)
            RequestRuntimePermission();

        final at.markushi.ui.CircleButton btn = (at.markushi.ui.CircleButton) findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!play){
                    startTakePic();
                    btn.setImageResource(R.mipmap.ic_launcher_pause);
                }else{
                    refreshCamera();
                    timer.cancel();
                    btn.setImageResource(R.mipmap.ic_launcher_camera_black);
                }
                play = !play;
                //camera.takePicture(null, null, mPicture);
            }
        });

        // Create first surface with his holder(holder)
        cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // Create second surface with another holder (holderTransparent)
        transparentView = (SurfaceView) findViewById(R.id.TransparentView);
        holder = cameraView.getHolder();
        holder.addCallback((SurfaceHolder.Callback) this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.setSecure(true);

        // Create second surface with another holder (holderTransparent)

        transparentView = (SurfaceView) findViewById(R.id.TransparentView);
        holderTransparent = transparentView.getHolder();
        holderTransparent.addCallback((SurfaceHolder.Callback) this);
        holderTransparent.setFormat(PixelFormat.TRANSLUCENT);
        transparentView.setZOrderMediaOverlay(true);

        //getting the device heigth and width

        deviceWidth = getScreenWidth();
        deviceHeight = getScreenHeight();

    }

    private void startTakePic() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                            camera.takePicture(null, null, mPicture);
            }

        }, 0, 2500);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmapCrop = bitmap;
            System.out.println("Screen : "+getScreenWidth() + " " + getScreenHeight());
            System.out.println("Bitmap : " + bitmapCrop.getWidth() + " " + bitmapCrop.getHeight());
            prepareTessData();
            startOCR();
        }
    };

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private void Draw()

    {
        Canvas canvas = holderTransparent.lockCanvas(null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        RectLeft = widthPic - (widthPic - 100);
        RectTop = heightPic - (heightPic - 300);
        RectRight = widthPic -100;
        RectBottom = RectTop + 200;
        Rect rec = new Rect((int) RectLeft, (int) RectTop, (int) RectRight, (int) RectBottom);
        canvas.drawRect(rec, paint);
        holderTransparent.unlockCanvasAndPost(canvas);
    }

    @Override

    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(); //open a camera
        try {
            //  Toast.makeText(this, "surfaceCreated", Toast.LENGTH_SHORT).show();
            synchronized (holder) {
                setPicSize();
                Draw();
            }   //call a draw method
        } catch (Exception e) {
            Log.i("Exception", e.toString());
            return;
        }
        setParam();
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            return;
        }
    }

    void setParam() {
        Camera.Parameters param;
        param = camera.getParameters();
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
        }
        param.setPictureSize(widthPic, heightPic);
        param.setPreviewSize(widthPic, heightPic);
        camera.setParameters(param);
    }

    @Override

    protected void onDestroy() {
        //  Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Toast.makeText(this, "surfaceChanged", Toast.LENGTH_SHORT).show();

        refreshCamera(); //call method for refress camera
    }

    public void refreshCamera() {
        if (holder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
        }

        try {

            camera.setPreviewDisplay(holder);
            setParam();
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    public void setPicSize() {
        Camera.Parameters param = camera.getParameters();
        for (Camera.Size size : param.getSupportedPictureSizes()) {

            if (size.width <= deviceWidth  && size.height <= deviceHeight) {
                widthPic = size.width;
                heightPic = size.height;
            }
        }
    }

    @Override

    public void surfaceDestroyed(SurfaceHolder holder) {

        camera.release(); //for release a camera

    }

    private void prepareTessData() {
        try {
            File dir = getExternalFilesDir(TESS_DATA);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String fileList[] = {"mrz.traineddata"};
            for (String fileName : fileList) {
                String pathToDataFile = dir + "/" + fileName;

                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);

                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void startOCR() {

        try {
            refreshCamera();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            bitmapCrop = Bitmap.createBitmap(bitmapCrop, RectLeft, RectTop, RectRight - RectLeft-100, RectBottom - RectTop);
            System.out.println("R-L " + (RectRight - RectLeft ));
            String result = getText();

            System.out.println(result);
            if (result.length() != 89) {
                Toast.makeText(this, "Please try again ", Toast.LENGTH_SHORT).show();

            } else {
                timer.cancel();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Log.e("startOCR", e.getMessage());
        }
    }

    private String getText() {

        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            Log.e("new obj ", e.getMessage());
        }
        String dataPath = getExternalFilesDir("/").getPath() + "/";
        bitmapCrop = bitmapCrop.copy(Bitmap.Config.ARGB_8888, true);
        //bitmapCrop = resize(bitmapCrop,bitmapCrop.getWidth(),bitmapCrop.getHeight());
        // bitmapCrop = setGrayscale(bitmapCrop);
        //bitmapCrop = removeNoise(bitmapCrop);
        tessBaseAPI.init(dataPath, "mrz");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890<");
        tessBaseAPI.setImage(bitmapCrop);
        String result = "no result";
        try {
            result = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {

            Log.e("getText", e.getMessage());
        }
        tessBaseAPI.end();
        return result.replace(" ", "");
    }

    private void RequestRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            Toast.makeText(this, "CAMERA permission allows us to access CAMERA app", Toast.LENGTH_SHORT).show();
        else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Canceled", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    public Bitmap resize(Bitmap img, int newWidth, int newHeight) {
        Bitmap bmap = img.copy(img.getConfig(), true);

        double nWidthFactor = (double) img.getWidth() / (double) newWidth;
        double nHeightFactor = (double) img.getHeight() / (double) newHeight;

        double fx, fy, nx, ny;
        int cx, cy, fr_x, fr_y;
        int color1;
        int color2;
        int color3;
        int color4;
        byte nRed, nGreen, nBlue;

        byte bp1, bp2;

        for (int x = 0; x < bmap.getWidth(); ++x) {
            for (int y = 0; y < bmap.getHeight(); ++y) {

                fr_x = (int) Math.floor(x * nWidthFactor);
                fr_y = (int) Math.floor(y * nHeightFactor);
                cx = fr_x + 1;
                if (cx >= img.getWidth())
                    cx = fr_x;
                cy = fr_y + 1;
                if (cy >= img.getHeight())
                    cy = fr_y;
                fx = x * nWidthFactor - fr_x;
                fy = y * nHeightFactor - fr_y;
                nx = 1.0 - fx;
                ny = 1.0 - fy;

                color1 = img.getPixel(fr_x, fr_y);
                color2 = img.getPixel(cx, fr_y);
                color3 = img.getPixel(fr_x, cy);
                color4 = img.getPixel(cx, cy);

                // Blue
                bp1 = (byte) (nx * Color.blue(color1) + fx * Color.blue(color2));
                bp2 = (byte) (nx * Color.blue(color3) + fx * Color.blue(color4));
                nBlue = (byte) (ny * (double) (bp1) + fy * (double) (bp2));

                // Green
                bp1 = (byte) (nx * Color.green(color1) + fx * Color.green(color2));
                bp2 = (byte) (nx * Color.green(color3) + fx * Color.green(color4));
                nGreen = (byte) (ny * (double) (bp1) + fy * (double) (bp2));

                // Red
                bp1 = (byte) (nx * Color.red(color1) + fx * Color.red(color2));
                bp2 = (byte) (nx * Color.red(color3) + fx * Color.red(color4));
                nRed = (byte) (ny * (double) (bp1) + fy * (double) (bp2));

                bmap.setPixel(x, y, Color.argb(255, nRed, nGreen, nBlue));
            }
        }

        bmap = setGrayscale(bmap);
        bmap = removeNoise(bmap);

        return bmap;
    }

    private Bitmap setGrayscale(Bitmap img) {
        Bitmap bmap = img.copy(img.getConfig(), true);
        int c;
        for (int i = 0; i < bmap.getWidth(); i++) {
            for (int j = 0; j < bmap.getHeight(); j++) {
                c = bmap.getPixel(i, j);
                byte gray = (byte) (.299 * Color.red(c) + .587 * Color.green(c)
                        + .114 * Color.blue(c));

                bmap.setPixel(i, j, Color.argb(255, gray, gray, gray));
            }
        }
        return bmap;
    }

    // RemoveNoise
    private Bitmap removeNoise(Bitmap bmap) {
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                if (Color.red(pixel) < 162 && Color.green(pixel) < 162 && Color.blue(pixel) < 162) {
                    bmap.setPixel(x, y, Color.BLACK);
                }
            }
        }
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                if (Color.red(pixel) > 162 && Color.green(pixel) > 162 && Color.blue(pixel) > 162) {
                    bmap.setPixel(x, y, Color.WHITE);
                }
            }
        }
        return bmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        timer.cancel();
    }


}