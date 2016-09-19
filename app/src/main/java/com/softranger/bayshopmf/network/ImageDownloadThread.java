package com.softranger.bayshopmf.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.CountryCode;
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

    public ImageDownloadThread(ArrayList<T> objects, Handler handler, Context context) {
        mHandler = handler;
        mObjects = new ArrayList<>();
        mObjects.addAll(objects);
        mContext = context;
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
                String strUrl = object.getImageUrl();
                URL url = new URL(strUrl);
                Bitmap bitmap = downloadImage(url);
                object.setImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (object.getImage() == null) {
                    Bitmap image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_image);
                    object.setImage(image);
                }
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
        return BitmapFactory.decodeFile(image.getAbsolutePath());
    }

    private String urlToFileName(String url) {
        return url.replace("/", "").replace(".", "") + ".png";
    }
}
