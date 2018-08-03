package com.example.rog.mcpix;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConvertPDF extends AppCompatActivity {
    private Bitmap bmp;
    private String imgPath="";
    private String hasil="";
    private String pdfName = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_img);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.drawable.loading).into(imageViewTarget);
        new Thread(new Runnable() {
            @Override
            public void run() {
                imgPath = getIntent().getStringExtra("imgPath");
                Document document = new Document(PageSize.A4);
                File directory = new File(imgPath);
                File[] files = directory.listFiles();
                String[] materi = directory.getAbsolutePath().split("/");
                List<String> fileMateri = Arrays.asList(materi);

                pdfName = fileMateri.get(fileMateri.size()-1)+".pdf";

                File myFile = new File(imgPath,pdfName);

                Font bold = new Font(Font.FontFamily.HELVETICA,15,Font.BOLD);
                Chunk chunk = new Chunk(pdfName.substring(0,pdfName.indexOf(".")),bold);
                Log.d("PDF",pdfName);
                Paragraph title = new Paragraph(chunk);
                title.setAlignment(Element.ALIGN_CENTER);

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

                int count = 1;

                for (File f:files){

                    if (f.getName().endsWith(".jpg")&&f.getName().substring(0,3).equals("TXT")){
                        bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(ConvertPDF.this).build();
                        Frame imageFrame = new Frame.Builder()
                                .setBitmap(bmp)
                                .build();
                        SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

                        List<TextBlock> arrayList = new ArrayList<>(textBlocks.size());
                        for (int i = 0; i < textBlocks.size(); i++) {
                            arrayList.add(textBlocks.valueAt(i));
                        }

                        Collections.sort(arrayList, new Comparator<TextBlock>() {
                            @Override
                            public int compare(TextBlock t1, TextBlock t2) {
                                int diffOfTops = t1.getBoundingBox().top - t2.getBoundingBox().top;
                                int diffOfLefts = t1.getBoundingBox().left - t2.getBoundingBox().left;

                                if (diffOfTops != 0) {
                                    return diffOfTops;
                                }
                                return diffOfLefts;
                            }
                        });


                        for (int i = 0; i < arrayList.size(); i++) {
                            TextBlock textBlock = arrayList.get(i);
                            Log.i("GOOGLE API", textBlock.getValue());
                            hasil = hasil + textBlock.getValue() + "\n";
                        }

                        if (hasil.length()>0){
                            chunk = new Chunk("Halaman "+count,bold);
                            title = new Paragraph(chunk);
                            title.setAlignment(Element.ALIGN_LEFT);

                            try {
                                document.add(title);
                                document.add(Chunk.NEWLINE);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        }

                        Paragraph p = new Paragraph(hasil);
                        try {
                            document.add(p);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                        hasil="";
                    }
                    else if(f.getName().endsWith(".jpg")&&f.getName().substring(0,3).equals("IMG")){

                        chunk = new Chunk("Halaman "+count,bold);
                        title = new Paragraph(chunk);
                        title.setAlignment(Element.ALIGN_LEFT);

                        try {
                            document.add(title);
                            document.add(Chunk.NEWLINE);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }

                        Image img = null;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath(),options);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);

                        try {
                            img = Image.getInstance(stream.toByteArray());
                        } catch (BadElementException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        float width = img.getPlainWidth();
                        float height = img.getPlainHeight();

                        if (width>height){
                            img.scaleAbsolute(450,250);
                        }else{
                            img.scaleAbsolute(250,450);
                        }
                        try {
                            document.add(img);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
                    count++;
                }

                document.close();

                Intent intent = new Intent(getApplicationContext(), Gallery.class);
                intent.putExtra("path",imgPath).putExtra("pdfName",pdfName);
                startActivity(intent);
                finish();
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause(){
        super.onPause();

    }
    @Override
    protected void onResume(){
        super.onResume();

    }

    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
    }
}
