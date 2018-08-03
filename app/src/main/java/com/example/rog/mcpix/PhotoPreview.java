package com.example.rog.mcpix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PhotoPreview extends AppCompatActivity {

    private String imgPath="";
    private String imgName = "";
    private String idfrom = "";
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        imgPath = getIntent().getStringExtra("imgPath");
        imgName = getIntent().getStringExtra("imgName");
        idfrom = getIntent().getStringExtra("idfrom");
        PhotoView imgView = (PhotoView) findViewById(R.id.photoview);
        getSupportActionBar().setTitle(imgName);
//        Image img = new Image(imgPath+imgName);
//        Matrix matrix = new Matrix();
//            matrix.postRotate(lastOrientation);
//            Bitmap bmp = BitmapFactory.decodeFile(imgPath+File.separator+imgName);
//            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//        File pictureFile = new File(imgPath+File.separator+imgName);
//        FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(pictureFile);
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Bitmap bmp = (decodeSampledBitmapFromPath(imgPath + File.separator + imgName, 500, 500));
//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(imgPath + File.separator + imgName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
//        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("90")) {
//            bmp = rotate(bmp, 90);
//        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("270")) {
//            bmp = rotate(bmp, 270);
//        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("180")) {
//            bmp = rotate(bmp, 180);
//        }
//            imgView.setImageBitmap(decodeSampledBitmapFromPath(imgPath + File.separator + imgName, 300, 300));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeFile(imgPath + File.separator + imgName,options);
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        Glide.with(PhotoPreview.this)
                .load(stream.toByteArray())
                .placeholder(R.mipmap.load_pic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgView);
//        Glide.with(this)
//                .load(imgPath+File.separator+imgName)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(imgView);

            BottomNavigationView bmv = (BottomNavigationView) findViewById(R.id.botmenu);

            bmv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.navigation_ocr) {

                        Intent intent = new Intent(getApplicationContext(), ConvertIMG.class).putExtra("imgPath", imgPath).putExtra("imgName", imgName).putExtra("uniqueId","Camera").putExtra("idfrom",idfrom);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class).putExtra("datapath", imgPath).putExtra("idfrom",idfrom);
                        startActivity(intent);
                    }
                    return true;
                }
            });
        }

//    public static Bitmap rotate(Bitmap bitmap, int degree) {
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//
//        Matrix mtx = new Matrix();
//        mtx.postRotate(degree);
//
//        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
//    }

//    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(path, options);
//    }
//
//    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) >= reqHeight
//                    && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.putExtra("datapath",imgPath).putExtra("idfrom",idfrom);
        startActivity(intent);
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
    }
}

