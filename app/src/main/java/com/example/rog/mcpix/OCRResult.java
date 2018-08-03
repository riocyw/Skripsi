package com.example.rog.mcpix;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OCRResult extends AppCompatActivity {

    private String uniqueID="";
    private String imgPath="";
    private String imgName = "";
    private int position = 0;
    private EditText et;
    private Toast t;
    private String idfrom = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrresult);
        uniqueID = getIntent().getStringExtra("uniqueId");
        imgPath = getIntent().getStringExtra("imgPath");
        imgName = getIntent().getStringExtra("imgName");
        position = getIntent().getIntExtra("position",0);
        idfrom = getIntent().getStringExtra("idfrom");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.menufloat);
        final FloatingActionButton copyBtn = (FloatingActionButton) findViewById(R.id.copyBtn);
        final FloatingActionButton pdfBtn = (FloatingActionButton) findViewById(R.id.pdfBtn);

        final Animation showMFloat = AnimationUtils.loadAnimation(OCRResult.this, R.anim.show_button);
        final Animation hideMFloat = AnimationUtils.loadAnimation(OCRResult.this, R.anim.hide_button);
        final Animation showMBtn = AnimationUtils.loadAnimation(OCRResult.this, R.anim.show_btn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (copyBtn.getVisibility()==View.VISIBLE){
                    copyBtn.setClickable(false);
                    copyBtn.hide();
                    pdfBtn.setClickable(false);
                    pdfBtn.hide();

                    fab.setAnimation(hideMFloat);
                }else{
                    copyBtn.setClickable(true);
                    copyBtn.show();
                    pdfBtn.setClickable(true);
                    pdfBtn.show();
                    copyBtn.startAnimation(showMBtn);
                    pdfBtn.startAnimation(showMBtn);
                    fab.setAnimation(showMFloat);
                }
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClip();

            }
        });

        pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Image img = null;
                Document document = new Document(PageSize.A4);
                File myFile = new File(imgPath,imgName.substring(0,imgName.indexOf("."))+".pdf");
//                try {

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inSampleSize = 4;
//                    Bitmap bmp = BitmapFactory.decodeFile(imgPath+"/"+imgName,options);
//                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
//
//                    img = Image.getInstance(stream.toByteArray());
//
//                    float width = img.getPlainWidth();
//                    float height = img.getPlainHeight();
//
//                    if (width>height){
//                        img.scaleAbsolute(450,250);
//                    }else{
//                        img.scaleAbsolute(250,450);
//                    }
//                } catch (BadElementException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                Font bold = new Font(Font.FontFamily.HELVETICA,15,Font.BOLD);
                Chunk chunk = new Chunk(imgName.substring(0,imgName.indexOf(".")),bold);
                Log.d("PDF",imgName);
                Paragraph title = new Paragraph(chunk);
                title.setAlignment(Element.ALIGN_CENTER);

                Log.d("FF",myFile.getAbsolutePath());
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(myFile));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                document.open();

                try {
                    document.add(title);
                    document.add(Chunk.NEWLINE);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                Paragraph p = new Paragraph(et.getText().toString());
                try {
                    document.add(p);
//                    document.add(img);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                document.close();
                File file = new File(imgPath,imgName.substring(0,imgName.indexOf("."))+".pdf");
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file),"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                    Toast.makeText(OCRResult.this,"Berhasil membuat pdf!",Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }
            }
        });

//        String filename = getIntent().getStringExtra("image");
//        Bitmap bmp=null;
//        try {
//            FileInputStream is = this.openFileInput(filename);
//            bmp = BitmapFactory.decodeStream(is);
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String ocr = getIntent().getStringExtra("ocr");

        et = (EditText) findViewById(R.id.ocrText);
//        PhotoView imgv = (PhotoView) findViewById(R.id.viewOcr);

        et.setText(ocr);
//        imgv.setImageBitmap(bmp);
        Toast.makeText(OCRResult.this,getIntent().getStringExtra("uniqueId"),Toast.LENGTH_SHORT).show();
    }


    private void copyToClip(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Hasil OCR", et.getText().toString());
        clipboard.setPrimaryClip(clip);
        if (t!=null){
            t.cancel();
        }
        t.makeText(OCRResult.this,"Berhasil disalin ke clipboard!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("uniqueId").equals("Camera")){
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            intent.putExtra("datapath",imgPath).putExtra("idfrom",idfrom);
            startActivity(intent);
            Runtime.getRuntime().freeMemory();
            Runtime.getRuntime().gc();
            finish();
        }else{
            Intent intent = new Intent(getApplicationContext(), com.example.rog.mcpix.PhotoView.class);
            intent.putExtra("path",imgPath).putExtra("image",imgName).putExtra("position",position);
            startActivity(intent);
            Runtime.getRuntime().freeMemory();
            Runtime.getRuntime().gc();
            finish();
        }

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
