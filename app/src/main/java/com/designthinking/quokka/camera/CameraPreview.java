package com.designthinking.quokka.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.hardware.Camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraPreview extends ImageProvider implements SurfaceHolder.Callback {

    private final int TARGET_WIDTH = 384;
    private final int TARGET_HEIGHT = 384 * 2;

    private Context context;
    private SurfaceHolder holder;

    private Camera camera;

    private int width, height;

    private boolean captureFlag = false;
    private boolean stop = false;

    public CameraPreview(Context context, Handler handler){
        super(context);
        this.context = context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                request();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    public void request(){
        captureFlag = true;
    }

    @Override
    public void stop(){
        stop = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (null == camera) {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
        }

        Camera.Parameters params = camera.getParameters();

        for(Camera.Size size : params.getSupportedPictureSizes()){
            width = size.height;
            height = size.width;
            if(width < TARGET_WIDTH && height < TARGET_HEIGHT) break;
        }

        Log.i("CAMERA", width + " x " + height);

        params.setPreviewSize(height, width);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if(captureFlag){
                    Camera.Parameters parameters = camera.getParameters();
                    int imageFormat = parameters.getPreviewFormat();
                    Bitmap bitmap = null;

                    if (imageFormat == ImageFormat.NV21) {
                        int w = parameters.getPreviewSize().width;
                        int h = parameters.getPreviewSize().height;

                        YuvImage yuvImage = new YuvImage(data, imageFormat, w, h, null);
                        Rect rect = new Rect(0, 0, w, h);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        yuvImage.compressToJpeg(rect, 100, outputStream);

                        bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
                    }
                    else if (imageFormat == ImageFormat.JPEG || imageFormat == ImageFormat.RGB_565) {
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    }

                    if (bitmap != null) {
                        // Rotate
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        FileOutputStream fileStream;
                        try {
                            File imageFile = new File(context.getCacheDir(), "temp_img.jpg");
                            imageFile.createNewFile();

                            fileStream = new FileOutputStream(imageFile);
                            rotated.compress(Bitmap.CompressFormat.JPEG, 80, fileStream);
                            fileStream.flush();
                            fileStream.close();

                            getListener().updateImageFile(imageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bitmap.recycle();
                        bitmap = null;
                        rotated.recycle();
                        rotated = null;
                        captureFlag = false;
                    }
                }
            }
        });

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera == null) {
            return;
        }
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
