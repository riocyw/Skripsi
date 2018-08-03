package com.example.rog.mcpix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConvertIMG extends AppCompatActivity {

    private Bitmap bmp;
    private Bitmap btmp;
    private String hasil = "";
    private String idfrom = "";
    private long startTime;
    private long endTime;
    private long totalTime;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_img);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.drawable.loading).into(imageViewTarget);

        new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.currentTimeMillis();
                final long start = System.nanoTime();
                String imgPath = getIntent().getStringExtra("imgPath");
                String imgName = getIntent().getStringExtra("imgName");
                int position = getIntent().getIntExtra("position",0);
                String uniqueId = getIntent().getStringExtra("uniqueId");
                String idfrom = getIntent().getStringExtra("idfrom");

//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 5;
//                bmp = BitmapFactory.decodeFile(imgPath+File.separator+imgName,options);
//                Utils.bitmapToMat(bmp,imageMat);
//                detectText(imageMat);
//                Bitmap newBitmap = bmp.copy(bmp.getConfig(),true);
//                Utils.matToBitmap(imageMat,newBitmap);
//
//                progress = true;
//
//                File pictureFile = getOutputMediaFile();
//                try {
//                    FileOutputStream fos;
//                    fos = new FileOutputStream(pictureFile);
//                    newBitmap.compress(Bitmap.CompressFormat.JPEG,100 , fos);
//                    fos.flush();
//                    fos.close();
//                } catch (FileNotFoundException e) {
//                    Log.d("Error", "File not found: " + e.getMessage());
//                } catch (IOException e) {
//                    Log.d("Error", "Error accessing file: " + e.getMessage());
//                }
//
//                if (progress){
//                Intent intent = new Intent(getApplicationContext(), OCRResult.class);
//                String filename = "bitmap.jpg";
//                FileOutputStream stream = null;
//                try {
//                    stream = openFileOutput(filename, Context.MODE_PRIVATE);
//                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                    //Cleanup
//                    stream.close();
//                    newBitmap.recycle();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                intent.putExtra("image", filename).putExtra("ocr",hasil);
//                Log.d("Progress:","True");
//                startActivity(intent);
//                finish();
//                }else{
//                    Log.d("Progress:","False");
//                }
//                    Mobile Vision Code
//                    sRectPaint = new Paint();
//                    sRectPaint.setColor(Color.RED);
//                    sRectPaint.setStyle(Paint.Style.STROKE);
//                    sRectPaint.setStrokeWidth(4.0f);
                    bmp = BitmapFactory.decodeFile(imgPath+"/"+imgName);
                    Log.d("Nama = ",imgName.substring(0,3));
//                    Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(ConvertIMG.this).build();
                    Frame imageFrame = new Frame.Builder()
                            .setBitmap(bmp)
                            .build();
                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
//                    Canvas canvas = new Canvas(mutableBitmap);
                    List<TextBlock> arrayList = new ArrayList<>(textBlocks.size());
                    for (int i = 0; i < textBlocks.size(); i++){
                        arrayList.add(textBlocks.valueAt(i));
                    }

                    Collections.sort(arrayList, new Comparator<TextBlock>() {
                        @Override
                        public int compare(TextBlock t1, TextBlock t2) {
                            int diffOfTops = t1.getBoundingBox().top -  t2.getBoundingBox().top;
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
                        hasil = hasil+ textBlock.getValue()+"\n";

//                        List<? extends com.google.android.gms.vision.text.Text> textComponents = textBlock.getComponents();
//                        for (com.google.android.gms.vision.text.Text currentText : textComponents) {
//                            RectF rect = new RectF(currentText.getBoundingBox());
//                            rect.left = scaleX(rect.left);
//                            rect.top = scaleY(rect.top);
//                            rect.right = scaleX(rect.right);
//                            rect.bottom = scaleY(rect.bottom);
//                            canvas.drawRect(rect, sRectPaint);
//                        }
                        // Do something with value
                    }
                final long end = System.nanoTime();

                System.out.println("Took: " + ((end - start) / 1000000) + "ms");
                    Intent intent = new Intent(getApplicationContext(), OCRResult.class).putExtra("imgPath",imgPath).putExtra("imgName",imgName).putExtra("position",position).putExtra("uniqueId",uniqueId).putExtra("idfrom",idfrom);

//                    String filename = "bitmap.jpg";
//                    FileOutputStream stream = null;
//                    try {
//                        stream = openFileOutput(filename, Context.MODE_PRIVATE);
//                        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        //Cleanup
//                        stream.close();
//                        mutableBitmap.recycle();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    intent.putExtra("ocr",hasil);
                    Log.d("Progress:","True");
                    startActivity(intent);
                    finish();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                doInBackground();
            }
        }).start();

//        if (progress){
//            Intent intent = new Intent(getApplicationContext(), OCRResult.class);
//            intent.putExtra("BitmapImage",ocr).putExtra("ocr",OCRResult);
//            startActivity(intent);
//            finish();
//            Log.d("Selesai:","Sudah DiPindah");
//        }
    }

//    Mobile Vision Comparator
//    public static Comparator<TextBlock> TextBlockComparator = new Comparator<TextBlock>() {
//        public int compare(TextBlock textBlock1, TextBlock textBlock2) {
//            return textBlock1.getBoundingBox().top - textBlock2.getBoundingBox().top;
//        }
//    };

//    private void detectText(Mat mat){
//        Imgproc.cvtColor(imageMat, imageMat2, Imgproc.COLOR_RGB2GRAY);
//        Mat mRgba = mat;
//        Mat mGray = imageMat2;
//        Imgproc.equalizeHist(mGray,mGray);
//        Imgproc.GaussianBlur(mGray, mGray, new Size(5,5),2.2,2);
//        Scalar CONTOUR_COLOR = new Scalar(255, 0, 0, 0);
//        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
//        List<KeyPoint> listPoint = new ArrayList<>();
//        KeyPoint kPoint = new KeyPoint();
//        Mat mask = Mat.zeros(mGray.size(), CvType.CV_8UC1);
//        int rectanx1;
//        int rectany1;
//        int rectanx2;
//        int rectany2;
//
//        Scalar zeros = new Scalar(0,0,0);
//        final List<MatOfPoint> contour2 = new ArrayList<>();
//        Mat kernel = new Mat(1, 50, CvType.CV_8UC1, Scalar.all(255));
//        Mat morByte = new Mat();
//        Mat hierarchy = new Mat();
//
//        Rect rectan3 = new Rect();
//        int imgSize = mRgba.height() * mRgba.width();
//        FeatureDetector detector = FeatureDetector.create(FeatureDetector.MSER);
//        detector.detect(mGray, keyPoint);
//        listPoint = keyPoint.toList();
//        for(int ind = 0; ind < listPoint.size(); ind++){
//            kPoint = listPoint.get(ind);
//            rectanx1 = (int ) (kPoint.pt.x - 0.5 * kPoint.size);
//            rectany1 = (int ) (kPoint.pt.y - 0.5 * kPoint.size);
//            rectanx2 = (int) (kPoint.size);
//            rectany2 = (int) (kPoint.size);
//            if(rectanx1 <= 0){
//                rectanx1 = 1;
//            }
//            if(rectany1 <= 0){
//                rectany1 = 1;
//            }
//            if((rectanx1 + rectanx2) > mGray.width()){
//                rectanx2 = mGray.width() - rectanx1;
//            }
//            if((rectany1 + rectany2) > mGray.height()){
//                rectany2 = mGray.height() - rectany1;
//            }
//            Rect rectant = new Rect(rectanx1, rectany1, rectanx2, rectany2);
////            Memberikan Warna Pada Rect
//            Mat roi = new Mat(mask, rectant);
//            roi.setTo(CONTOUR_COLOR);
//        }
////        Mempertegas garis pada kotak dan huruf
//        Imgproc.morphologyEx(mask, morByte, Imgproc.MORPH_DILATE, kernel);
//        Imgproc.findContours(morByte, contour2, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
//        Bitmap bmp2 = null;
//
//        for(int i = 0; i<contour2.size(); ++i){
//            rectan3 = Imgproc.boundingRect(contour2.get(i));
//            try{
//                Mat croppedPart = mGray.submat(rectan3);
//                    Imgproc.adaptiveThreshold(croppedPart, croppedPart, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 40);
//                bmp2 = Bitmap.createBitmap(croppedPart.width(),croppedPart.height(),Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(croppedPart,bmp2);
//            }catch (Exception e){
//                Log.d("Error Crop ","Cropped Part error");
//            }
//
//            File pictureFile = getOutputMediaFile();
//            try {
//                FileOutputStream fos;
//                fos = new FileOutputStream(pictureFile);
//                bmp2.compress(Bitmap.CompressFormat.JPEG,100 , fos);
//                fos.flush();
//                fos.close();
//            } catch (FileNotFoundException e) {
//                Log.d("Error", "File not found: " + e.getMessage());
//            } catch (IOException e) {
//                Log.d("Error", "Error accessing file: " + e.getMessage());
//            }
//
//            if (bmp2!=null){
//                getTesseractTxt(bmp2);
//            }
//
//            if(rectan3.area() > 0.5 * imgSize || rectan3.area()<150 || rectan3.width / rectan3.height < 2){
//                Mat roi = new Mat(morByte, rectan3);
//                roi.setTo(zeros);
//            }else{
//                Imgproc.rectangle(mRgba, rectan3.br(), rectan3.tl(), CONTOUR_COLOR);
//            }
//        }
//        Collections.sort(contour2, new Comparator<MatOfPoint>() {
//            @Override
//            public int compare(MatOfPoint o1, MatOfPoint o2) {
//                return Double.valueOf(Imgproc.contourArea(o2)).compareTo(Imgproc.contourArea(o1));
//            }
//        });
//    }

    /** Create a File for saving an image or video */
//    private File getOutputMediaFile(){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(getIntent().getStringExtra("imgPath"));
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("Pixature", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        DateFormat df = new java.text.SimpleDateFormat("ddMMyyHHmmss");
//        String date = df.format(java.util.Calendar.getInstance().getTime());
//        File mediaFile;
//        String imgName = "BOUNDING_"+ date + ".jpg";
//
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator + imgName);
//
//        return mediaFile;
//    }
//
//    private void getTesseractTxt(Bitmap bitmap){
//        tesseract = new TessBaseAPI();
//        tesseract.init(datapath,"ind");
//        tesseract.setImage(bitmap);
//        Log.d("Confidence",tesseract.meanConfidence()+"");
//        if (tesseract.meanConfidence()>70){
//            hasil = hasil+"\n"+tesseract.getUTF8Text();
//        }
//        Log.d("Word",tesseract.getUTF8Text());
//        tesseract.end();
//    }

//    private void doInBackground(){
//        dest = new Mat();
//        source = new Mat();
//        checkFile();
//        final String imgPath = getIntent().getStringExtra("imgPath");
//        final String imgName = getIntent().getStringExtra("imgName");
//
////        img = new Image(imgPath+imgName);
//        bmp = decodeSampledBitmapFromPath(imgPath+imgName,500,500);
////
////        ExifInterface exif = null;
////        try {
////            exif = new ExifInterface(imgPath + File.separator + imgName);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
////        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("90")) {
////            bmp = rotate(bmp, 90);
////        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("270")) {
////            bmp = rotate(bmp, 270);
////        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("180")) {
////            bmp = rotate(bmp, 180);
////        }
//
////        KONVERSI GAMBAR KE EKSTENSI MAT UNTUK MEMPROSES GAMBAR
//
//        Utils.bitmapToMat(bmp,source);
//        Mat kernel = new Mat(1, 50, CvType.CV_8UC1, Scalar.all(255));
//
////        MENGUBAH GAMBAR MENJADI GRAYSCALE DAN GAUSSIAN BLUR
//        Imgproc.cvtColor(source,dest,Imgproc.COLOR_BGR2GRAY);
//        Imgproc.GaussianBlur(dest, dest, new Size(5,5),0);
////
////        MELAKUKAN ADAPTIVE THRESHOLD DAN EROSI
//        Imgproc.adaptiveThreshold(dest, dest, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
//        Imgproc.morphologyEx(dest,dest,Imgproc.MORPH_CLOSE,kernel);
////        Imgproc.dilate(dest,dest,Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
////        Imgproc.erode(dest,dest,Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
//
////        MEMPROSES GAMBAR MENJADI BITMAP YANG SEMULA BEREKSTENSI MAT
//        btmp = Bitmap.createBitmap(dest.width(),dest.height(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(dest,btmp);
//
////        INISIALISASI TESSERACT
//        tesseract = new TessBaseAPI();
//        tesseract.init(datapath,"ind");
//        tesseract.setImage(btmp);
//
////        MENDAPATKAN HASIL TESSERACT
//        OCRResult = tesseract.getUTF8Text();
//        ocr = WriteFile.writeBitmap(tesseract.getThresholdedImage());
//        Log.d("Berhasil:","Diproses Fotonya");
//        progress = true;
//
//        if (progress){
//            Intent intent = new Intent(getApplicationContext(), OCRResult.class);
//            String filename = "bitmap.jpg";
//            FileOutputStream stream = null;
//            try {
//                stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
//                btmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                //Cleanup
//                stream.close();
//                btmp.recycle();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            intent.putExtra("image", filename).putExtra("ocr",OCRResult);
//            Log.d("Progress:","True");
//            startActivity(intent);
//            finish();
//        }else{
//            Log.d("Progress:","False");
//        }
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
//
//
//    private void checkFile() {
////        Toast.makeText(getBaseContext(),"Checking file...",Toast.LENGTH_SHORT).show();
//        String datafilepath = datapath+ "/tessdata/ind.traineddata";
//        datafile = new File(datafilepath);
//        if (!datafile.exists()) {
////            Toast.makeText(getBaseContext(),"Data doesnt exist, copying new file...",Toast.LENGTH_SHORT).show();
//            copyFiles();
//        }else{
////            Toast.makeText(getBaseContext(),"Checking data completed!",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void copyFiles() {
//        try {
//            String filepath = datapath + "/tessdata/";
//            AssetManager assetManager = getAssets();
//            File folderTarget = new File(datapath+"/tessdata");
//            folderTarget.mkdirs();
//
//            InputStream instream = assetManager.open("ind.traineddata");
//            OutputStream outstream = new FileOutputStream(filepath+"ind.traineddata");
//
//            byte[] buffer = new byte[1024];
//            int read;
//            while ((read = instream.read(buffer)) > 0) {
//                outstream.write(buffer, 0, read);
//            }
//            outstream.flush();
//            outstream.close();
//            instream.close();
//
//            File file = new File(filepath);
//            if (!file.exists()) {
////                Toast.makeText(getBaseContext(),"Copy File Failed!",Toast.LENGTH_SHORT).show();
//                throw new FileNotFoundException();
//            }else{
////                Toast.makeText(getBaseContext(),"Copy File Successful!",Toast.LENGTH_SHORT).show();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
        //android.os.Process.killProcess(android.os.Process.myPid());

        super.onDestroy();
        if(bmp!=null)
        {
            bmp.recycle();
            bmp=null;
        }
        if(btmp!=null)
        {
            btmp.recycle();
            btmp=null;
        }
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
//        tesseract.end();
        Log.d("SUDAH DIHENTIKAN","DIHENTIKAN");
    }
}
