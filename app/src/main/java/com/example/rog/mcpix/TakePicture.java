package com.example.rog.mcpix;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.util.Calendar;

public class TakePicture extends AppCompatActivity {

    private static final int CAM_REQUEST=1313;
    private String imageName = (Calendar.getInstance().get(Calendar.DATE))+
            (Calendar.getInstance().get(Calendar.MONTH))+
            (String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2))+
            (Calendar.getInstance().get(Calendar.HOUR))+
            (Calendar.getInstance().get(Calendar.MINUTE))+
            (Calendar.getInstance().get(Calendar.SECOND))+".jpg";
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/Pixature/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        File directory = new File(Environment.getExternalStorageDirectory(),"Pictures/Pixature/");
//        directory.mkdirs();
        File image = new File(path,imageName);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri uriSavedImage = Uri.fromFile(image);
//        galleryAddPic(image);

//        intent.putExtra("android.intent.extra.quickCapture",true);
        if(intent.resolveActivity(getPackageManager())!=null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
            startActivityForResult(intent,CAM_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK )
        {
//            galleryAddPic((Bitmap) data.getExtras().get("data"));
            Intent intent = new Intent(getApplicationContext(),PhotoPreview.class).putExtra("imgPath",path).putExtra("imgName",imageName);
            startActivity(intent);
            finish();

//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            //camera stuff
//
//            //folder stuff
//            File directory = new File(Environment.getExternalStorageDirectory(),"Pictures/Pixature");
//            directory.mkdirs();
//
//
//            File image = new File(directory,
//                    (Calendar.getInstance().get(Calendar.DATE))+
//                    (Calendar.getInstance().get(Calendar.MONTH))+
//                    (String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2))+
//                    (Calendar.getInstance().get(Calendar.HOUR))+
//                    (Calendar.getInstance().get(Calendar.MINUTE))+
//                    (Calendar.getInstance().get(Calendar.SECOND))+".jpg");
//
//            Uri uriSavedImage = Uri.fromFile(image);

//
//            intent.putExtra("android.intent.extra.quickCapture",true);
//            if(intent.resolveActivity(getPackageManager())!=null) {
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
//                startActivityForResult(intent,CAM_REQUEST);
//            }


        }else{
//            galleryAddPic((Bitmap) data.getExtras().get("data"));
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

//    private void galleryAddPic(Bitmap bitmap) {
//        //3
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        //4
//        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
//        try {
//            file.createNewFile();
//            FileOutputStream fo = new FileOutputStream(file);
//            //5
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}
