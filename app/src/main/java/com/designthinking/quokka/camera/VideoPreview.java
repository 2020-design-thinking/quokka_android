package com.designthinking.quokka.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.designthinking.quokka.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoPreview extends ImageProvider implements SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder holder;

    private Camera camera;

    private MediaPlayer mediaPlayer;

    private int width, height;

    private boolean captureFlag = false;
    private boolean stop = false;

    public VideoPreview(Context context, Handler handler){
        super(context);
        this.context = context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                request();
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    @Override
    public void request(){
        if(stop) return;

        final Bitmap[] bitmap = {Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888)};

        PixelCopy.request(this, bitmap[0], new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                FileOutputStream fileStream;
                try {
                    File imageFile = new File(context.getCacheDir(), "temp_img.jpg");
                    imageFile.createNewFile();

                    fileStream = new FileOutputStream(imageFile);
                    bitmap[0].compress(Bitmap.CompressFormat.JPEG, 80, fileStream);
                    fileStream.flush();
                    fileStream.close();

                    getListener().updateImageFile(imageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bitmap[0].recycle();
                bitmap[0] = null;
            }
        }, getHandler());
    }

    @Override
    public void stop(){
        mediaPlayer.stop();
        mediaPlayer.release();

        stop = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer = MediaPlayer.create(context, R.raw.testvid2);
        mediaPlayer.setDisplay(holder);
        mediaPlayer.setLooping(true);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = mediaPlayer.getVideoWidth() / 4;
        layoutParams.height = mediaPlayer.getVideoHeight() / 4;
        setLayoutParams(layoutParams);

        mediaPlayer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
