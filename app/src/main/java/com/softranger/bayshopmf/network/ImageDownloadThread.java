package com.softranger.bayshopmf.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.Imageble;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by macbook on 6/24/16.
 */
public class ImageDownloadThread<T extends Imageble> extends Thread {

    public static final int FINISHED = 1;
    private Handler mHandler;
    private Context mContext;
    private ArrayList<T> mObjects;
    private int width = Application.getPixelsFromDp(160);
    private int height = Application.getPixelsFromDp(120);

    public ImageDownloadThread(ArrayList<T> objects, Handler handler, Context context) {
        mHandler = handler;
        mObjects = new ArrayList<>();
        mObjects.addAll(objects);
        mContext = context;
    }

    public void setImageSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @SafeVarargs
    public ImageDownloadThread(Handler handler, Context context, T... objects) {
        mHandler = handler;
        mObjects = new ArrayList<>();
        mContext = context;
        Collections.addAll(mObjects, objects);
    }

    @Override
    public void run() {
        for (T object : mObjects) {
            try {
                String strUrl = Constants.Api.BASE_URL + object.getImageUrl();
                URL url = new URL(strUrl);
                Bitmap bitmap = downloadImage(url);
                object.setImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Message message = mHandler.obtainMessage();
                message.obj = object;
                mHandler.sendMessage(message);
            }
        }
        mHandler.sendEmptyMessage(FINISHED);
    }

    private Bitmap downloadImage(URL url) throws Exception {
        File image = new File(mContext.getCacheDir(), urlToFileName(url.toString()));
        if (!image.exists()) {
            image.createNewFile();
            BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteCount);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(image.getAbsolutePath(), options);
    }

    private String urlToFileName(String url) {
        return url.replace("/", "").replace(".", "") + ".png";
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
