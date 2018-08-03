package com.example.rog.mcpix;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Gallery extends AppCompatActivity {
    private SQLiteDatabase db;
    private ArrayList<String> selected = new ArrayList<>();
    private ArrayList<FolderPhoto> listFolder = new ArrayList<>();
    private ArrayList<String> dropdown = new ArrayList<>();
    private ArrayList<DaftarMK> daftarMK = new ArrayList<>();
    private ArrayList<FolderPhoto> imgList = new ArrayList<>();
    private ArrayList<String> chooseMenu = new ArrayList<>();
    private boolean statusSelected = false;
    private GridView gridView;
    private GridView photogv;
    private ImageAdapter imgAdapter;
    private DaftarMKAdapter mkAdapter;
    private TextView warning_text;
    private TextView txWarning;
    private Menu options;
    private String pathMenu="";
    private Toast t;
    private Spinner spinner;
    private LinearLayout breadCrumbs;
    private LinearLayout breadMenu;
    private int viewStatus = 1;
    private int viewMenu =1;
    private String input_nama="";
    private String pathNow;
    private ListView lv;
    private Gallery.CustomAdapter customAdapter = new Gallery.CustomAdapter();
    private RelativeLayout relativeLayout;
    private boolean copyNow = false;
    private CustomMenu menuAdapter = new CustomMenu();
    private String namaFile="";
    private String jenis="";
    private FloatingActionButton fab;
    private FloatingActionButton open;
    private FloatingActionButton pdf;
    private LinearLayout lvw;
    private String matkulname="";
    private LinearLayout l1;
    private LinearLayout l2;
    private TextView t1;
    private TextView t2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().setTitle("Gallery");
        db = openOrCreateDatabase("pixature",MODE_PRIVATE,null);
        breadCrumbs =(LinearLayout) findViewById(R.id.breadcrumb);
        warning_text = (TextView) findViewById(R.id.warning_text);
        lv = (ListView) findViewById(R.id.listFolder2);
        gridView = (GridView)findViewById(R.id.maingv);
        relativeLayout = (RelativeLayout) findViewById(R.id.layoutRoot);
        mkAdapter = new DaftarMKAdapter();
        photogv = (GridView)findViewById(R.id.photogv);
        imgAdapter = new ImageAdapter(getBaseContext(),imgList);
        fab = (FloatingActionButton) findViewById(R.id.convert);
        open = (FloatingActionButton) findViewById(R.id.openBtn);
        pdf = (FloatingActionButton) findViewById(R.id.pdfBtn);
        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);

        String getPath = getIntent().getStringExtra("path");
        String pdfName = getIntent().getStringExtra("pdfName");
        matkulname = getIntent().getStringExtra("matkul");

        if (getPath!=null){
            viewStatus = 3;
            File file = new File(getPath);
//            Toast.makeText(Gallery.this,file.getPath(),Toast.LENGTH_SHORT).show();
            Log.d("tag dari Gridview",file.getAbsolutePath());
            File[] files = file.listFiles();
            sortFile(files);
            checkBC(file);
            lv.setAdapter(null);
            Log.d("Gridview",viewStatus+"");
            pathNow = file.getAbsolutePath();
            if (files!=null){
                for(File f:files){
                    File bmpFile = new File(file.getAbsolutePath()+"/"+f.getName());
                    Log.d("img",bmpFile.getAbsolutePath());
                    if (f.getName().endsWith(".jpg")){
                        File imgbmp = new File(f.getAbsolutePath());
                        FolderPhoto image = new FolderPhoto (imgbmp.getAbsolutePath());
                        imgList.add(image);
                    }
                }
            }
            if (imgList.size()>0){
                warning_text.setVisibility(View.INVISIBLE);
            }else{
                warning_text.setVisibility(View.VISIBLE);
            }

            Log.d("imglist",imgAdapter.getCount()+"");
            imgAdapter.notifyDataSetChanged();
            photogv.setAdapter(imgAdapter);
            photogv.invalidateViews();
            relativeLayout.setBackgroundColor(getColor(R.color.white));
            if (pdfName!=null){
                File pdf = new File(getPath,pdfName);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(pdf),"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }
                Log.d("Tes",pdfName.substring(0,pdfName.indexOf(".")));
            }
        }else if(matkulname!=null){
            viewStatus = 2;
            relativeLayout.setBackgroundColor(getColor(R.color.white));
            photogv.setAdapter(null);
            Log.d("TAG SINi","3");
            imgList.clear();
            daftarMK.clear();
            listFolder.clear();
            File file = new File(matkulname);
            pathNow = file.getAbsolutePath();
            String[] pathfiles = file.getAbsolutePath().substring(file.getAbsolutePath().toString().indexOf("Pixature")).split("/");
            List<String> pf = Arrays.asList(pathfiles);
            viewStatus = pf.size();
            checkBC(file);
            Log.d("TAG SINi",file.getAbsolutePath());
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        FolderPhoto fp = new FolderPhoto(f.getName());
                        listFolder.add(fp);
                    }
                }
            }

            if (listFolder.size()>0){
                Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                    @Override
                    public int compare(FolderPhoto o1, FolderPhoto o2) {
                        return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                    }
                });
                warning_text.setVisibility(View.INVISIBLE);
            }else{
                warning_text.setVisibility(View.VISIBLE);
            }

            customAdapter.notifyDataSetChanged();
            lv.setAdapter(customAdapter);
        }else{
            checkDB();
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature");
            File folderUmum = new File(file.getAbsolutePath()+"/Umum");
            if (!folderUmum.exists()){
                folderUmum.mkdirs();
            }
            pathNow = file.getAbsolutePath().toString();
            checkBC(file);
            gridView.setAdapter(mkAdapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                daftarMK.clear();
                gridView.setAdapter(null);
                gridView.invalidateViews();
                Log.d("SM",viewStatus+"");
                viewStatus = 2;
                invalidateOptionsMenu();
                File file = (File) view.getTag();
                Log.d("tag dari Gridview",file.getAbsolutePath());
                pathNow = file.getAbsolutePath();
                File[] files = file.listFiles();
                checkBC(file);
                if (files!=null){
                    for(File f:files){
                        if (f.isDirectory()) {
                            FolderPhoto fp = new FolderPhoto(f.getName());
                            Log.d("photo",f.getName());
                            listFolder.add(fp);
                        }
                    }
                }
                if (listFolder.size()>0){
                    Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                        @Override
                        public int compare(FolderPhoto o1, FolderPhoto o2) {
                            return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                        }
                    });
                    warning_text.setVisibility(View.INVISIBLE);
                }else{
                    warning_text.setVisibility(View.VISIBLE);
                }

                lv.setAdapter(customAdapter);
                relativeLayout.setBackgroundColor(getColor(R.color.white));
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (statusSelected){
                    if (listFolder.get(position).isChecked()){
                        listFolder.get(position).toggleChecked();
                        selected.remove(listFolder.get(position).getNamaFolder());
                    }else{
                        listFolder.get(position).toggleChecked();
                        selected.add(listFolder.get(position).getNamaFolder());
                        getSupportActionBar().setTitle(selected.size()+" Selected");
                    }
                    if (selected.isEmpty()){
                        statusSelected = false;
                        getSupportActionBar().setTitle("Gallery");
                    }else{
                        getSupportActionBar().setTitle(selected.size()+" Selected");
                    }
                    if (selected.size()==1){
                        input_nama = selected.get(0);
                    }
                    invalidateOptionsMenu();
                    ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                }else{
                    viewStatus = 3;
                    File file = (File) view.getTag();
//                Toast.makeText(Gallery.this,file.getPath(),Toast.LENGTH_SHORT).show();
                    Log.d("tag dari Gridview",file.getAbsolutePath());
                    File[] files = file.listFiles();

                    sortFile(files);

                    checkBC(file);
                    lv.setAdapter(null);
                    Log.d("Gridview",viewStatus+"");
                    pathNow = file.getAbsolutePath();
                    if (files!=null){
                        for(File f:files){
                            File bmpFile = new File(file.getAbsolutePath()+"/"+f.getName());
                            Log.d("img",bmpFile.getAbsolutePath());
                            if (f.getName().endsWith(".jpg")){
                                File imgbmp = new File(f.getAbsolutePath());
                                FolderPhoto image = new FolderPhoto (imgbmp.getAbsolutePath());
                                imgList.add(image);
                            }
                        }
                    }
                    if (imgList.size()>0){
                        warning_text.setVisibility(View.INVISIBLE);
                    }else{
                        warning_text.setVisibility(View.VISIBLE);
                    }
                    invalidateOptionsMenu();
                    Log.d("imglist",imgAdapter.getCount()+"");
                    imgAdapter.notifyDataSetChanged();
                    photogv.setAdapter(imgAdapter);
                    photogv.invalidateViews();
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (!statusSelected){
                    statusSelected = true;
                    if (!listFolder.get(position).isChecked()){
                        listFolder.get(position).toggleChecked();
                    }
                    selected.add(listFolder.get(position).getNamaFolder());
                    if (selected.size()==1){
                        input_nama = selected.get(0);
                    }
                    getSupportActionBar().setTitle(selected.size()+" Selected");
                    mkAdapter.notifyDataSetChanged();
                }
                if (options!=null){
                    onPrepareOptionsMenu(options);
                }
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });

        photogv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (statusSelected){
                    if (imgList.get(position).isChecked()){
                        imgList.get(position).toggleChecked();
                        String[] pathfiles = imgList.get(position).getNamaFolder().split("/");
                        List<String> namepath = Arrays.asList(pathfiles);
                        selected.remove(namepath.get(namepath.size()-1));
                    }else{
                        imgList.get(position).toggleChecked();
                        String[] pathfiles = imgList.get(position).getNamaFolder().split("/");
                        List<String> namepath = Arrays.asList(pathfiles);
                        selected.add(namepath.get(namepath.size()-1));
                        getSupportActionBar().setTitle(selected.size()+" Selected");
                    }
                    if (selected.isEmpty()){
                        statusSelected = false;
                        getSupportActionBar().setTitle("Gallery");
                        if (!fab.isShown()) {
                            fab.show();
                            fab.setClickable(true);
                            open.show();
                            open.setClickable(true);
                            pdf.show();
                            pdf.setClickable(true);
                        }
                    }else{
                        getSupportActionBar().setTitle(selected.size()+" Selected");
                    }
                    if (selected.size()==1){
                        input_nama = selected.get(0);
                    }
                    invalidateOptionsMenu();
                    photogv.invalidateViews();
                }else{
                    String path = pathNow;
                    String[] img = imgList.get(position).getNamaFolder().split("/");
                    List<String> ap  = Arrays.asList(img);
                    String image = ap.get(ap.size()-1);
                    db.close();
                    Intent intent = new Intent(getApplicationContext(), PhotoView.class);
                    intent.putExtra("path",path).putExtra("image",image).putExtra("position",position);
                    startActivity(intent);
                    finish();
                }
            }
        });

        photogv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!statusSelected){
                    statusSelected = true;
                    if (!imgList.get(position).isChecked()){
                        imgList.get(position).toggleChecked();
                    }
                    String[] pathfiles = imgList.get(position).getNamaFolder().split("/");
                    List<String> namepath = Arrays.asList(pathfiles);
                    selected.add(namepath.get(namepath.size()-1));
                    if (selected.size()==1){
                        input_nama = selected.get(0);
                    }
                    if (fab.isShown()) {
                        fab.hide();
                        fab.setClickable(false);
                        open.hide();
                        open.setClickable(false);
                        pdf.hide();
                        pdf.setClickable(false);
                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                        t1.setVisibility(View.GONE);
                        t2.setVisibility(View.GONE);
                    }
                    getSupportActionBar().setTitle(selected.size()+" Selected");
                    mkAdapter.notifyDataSetChanged();
                }
                if (options!=null){
                    onPrepareOptionsMenu(options);
                }
                photogv.invalidateViews();
                return true;
            }
        });

        final Animation showMFloat = AnimationUtils.loadAnimation(Gallery.this, R.anim.show_button);
        final Animation hideMFloat = AnimationUtils.loadAnimation(Gallery.this, R.anim.hide_button);
        final Animation showMBtn = AnimationUtils.loadAnimation(Gallery.this, R.anim.show_btn_down);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fab.isShown()){
                    if (open.isShown()){
                        open.hide();
                        open.setClickable(false);
                        pdf.hide();
                        pdf.setClickable(false);
                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                        t1.setVisibility(View.GONE);
                        t2.setVisibility(View.GONE);
                        fab.startAnimation(hideMFloat);
                    }else{
                        open.show();
                        open.setClickable(true);
                        pdf.show();
                        pdf.setClickable(true);
                        l1.setVisibility(View.VISIBLE);
                        l2.setVisibility(View.VISIBLE);
                        t1.setVisibility(View.VISIBLE);
                        t2.setVisibility(View.VISIBLE);

                        l1.startAnimation(showMBtn);
                        l2.startAnimation(showMBtn);
                        fab.startAnimation(showMFloat);
                    }
                }
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (open.isShown()){
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("datapath",pathNow).putExtra("idfrom","Gallery");
                    startActivity(intent);
                    finish();
                }
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdf.isShown()){
                    if (imgList.size()>0){
                        AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                        alert.setTitle("Warning");
                        alert.setMessage("Apakah Anda ingin membuka tampilan dalam bentuk PDF?");
                        alert.setCancelable(false);
                        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), ConvertPDF.class);
                                intent.putExtra("imgPath",pathNow);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog test = alert.show();

                    }else{
                        Toast.makeText(Gallery.this,"Tidak ada File Foto!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    private void checkBC(File file){
        breadCrumbs.removeAllViews();
        String pathRow = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String path = file.getAbsolutePath().toString().substring(file.getAbsolutePath().toString().indexOf("Pixature"));
        String[] arraypath = path.split("/");
        List<String> ap  = Arrays.asList(arraypath);
        Log.d("ARRAYPATH",path);

        for(int i = 0;i<ap.size();i++){
            View view = getLayoutInflater().inflate(R.layout.breadcrumb_layout,null);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    invalidateOptionsMenu();
                    File path = (File) v.getTag();
                    String[] pathfiles = path.getAbsolutePath().substring(path.getAbsolutePath().toString().indexOf("Pixature")).split("/");
                    List<String> pf = Arrays.asList(pathfiles);
                    listFolder.clear();

                    pathNow = path.getAbsolutePath();

                    for (String j : pathfiles) {
                        Log.d("PJ", j);
                    }
                    viewStatus = pf.size();
                    if (viewStatus ==1) {
                        lv.setAdapter(null);
                        photogv.setAdapter(null);
                        daftarMK.clear();
                        listFolder.clear();
                        imgList.clear();
                        checkContent();
                        gridView.setAdapter(new DaftarMKAdapter());
                        gridView.invalidateViews();
                        checkBC(path);
                        relativeLayout.setBackgroundColor(getColor(R.color.bgmain));
                    } else if(viewStatus==2) {
                        gridView.setAdapter(null);
                        photogv.setAdapter(null);
                        listFolder.clear();
                        daftarMK.clear();
                        imgList.clear();
                        File[] files = path.listFiles();
                        if (files != null) {
                            for (File f : files) {
                                if (f.isDirectory()) {
                                    FolderPhoto fp = new FolderPhoto(f.getName());
                                    listFolder.add(fp);
                                }
                            }
                        }
                        if (listFolder.size()>0){
                            Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                                @Override
                                public int compare(FolderPhoto o1, FolderPhoto o2) {
                                    return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                                }
                            });
                            warning_text.setVisibility(View.INVISIBLE);
                        }else{
                            warning_text.setVisibility(View.VISIBLE);
                        }

                        lv.setAdapter(customAdapter);
                        ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                        checkBC(path);
//                        Toast.makeText(Gallery.this, pathNow, Toast.LENGTH_SHORT).show();

                    }else if(viewStatus==3){
                        listFolder.clear();
                        imgList.clear();
                        daftarMK.clear();
                        gridView.setAdapter(null);
                        lv.setAdapter(null);
                        File[] files = path.listFiles();
                        sortFile(files);
                        checkBC(path);
                        lv.setAdapter(null);
                        Log.d("Gridview",viewStatus+"");
                        if (files!=null){
                            for(File f:files){
                                File bmpFile = new File(path.getAbsolutePath()+"/"+f.getName());
                                Log.d("img",bmpFile.getAbsolutePath());
                                if (f.getName().endsWith(".jpg")){
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inSampleSize = 10;
                                    File imgbmp = new File(f.getAbsolutePath());
                                    FolderPhoto image = new FolderPhoto (imgbmp.getAbsolutePath());
                                    imgList.add(image);
                                }
                            }
                        }
                        if (imgList.size()>0){
                            warning_text.setVisibility(View.INVISIBLE);
                        }else{
                            warning_text.setVisibility(View.VISIBLE);
                        }

                        photogv.setAdapter(imgAdapter);
                        imgAdapter.notifyDataSetChanged();
                    }
                }
            };

            Button path1 = (Button) view.findViewById(R.id.btnC);
            pathRow = pathRow+"/"+ap.get(i);
            path1.setTag(new File(pathRow));
            Log.d("PATH",pathRow);
            path1.setText(ap.get(i));
            path1.setOnClickListener(listener);
            ImageView img = (ImageView) view.findViewById(R.id.imgC);
            if (i<ap.size()-1){
                img.setImageDrawable(getDrawable(R.mipmap.divider_grey));
            }else{
                img.setImageDrawable(getDrawable(R.mipmap.divider_black));
            }

            breadCrumbs.addView(view);
        }

        if (viewStatus==3){
            fab.show();
            fab.setClickable(true);
        }else{
            fab.hide();
            fab.setClickable(false);
            open.hide();
            open.setClickable(false);
            pdf.hide();
            pdf.setClickable(false);
            l1.setVisibility(View.INVISIBLE);
            l2.setVisibility(View.INVISIBLE);
            t1.setVisibility(View.INVISIBLE);
            t2.setVisibility(View.INVISIBLE);
        }

    }

    public void checkDB(){
        Scanner scan = new Scanner(getResources().openRawResource(R.raw.pixature));
        String query ="";
        while(scan.hasNextLine()){
            query = query+scan.nextLine()+"\n";
            if (query.trim().endsWith(";")){
                try{
                    db.execSQL(query);
                    System.out.println(query);
                }catch (SQLException e){
                    e.printStackTrace();
                }
                query = "";
            }
        }
        query = "SELECT DISTINCT semester from mata_kuliah ORDER BY semester ASC";
        Cursor cr = db.rawQuery(query,null);
        dropdown.clear();
        dropdown.add(0,"Semua");
        while (cr.moveToNext()){
            dropdown.add("Semester "+cr.getString(cr.getColumnIndex("semester")));
        }
        System.out.println(dropdown.toString());

        checkContent();
    }

    private void checkContent(){
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature");
        directory.mkdirs();
        File[] files = directory.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        String query;
        daftarMK.clear();

        query = "SELECT * FROM mata_kuliah ORDER BY nama_mata_kuliah ASC";
        Cursor cr = db.rawQuery(query,null);
        while(cr.moveToNext()){
            File folder = new File(directory, cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
            folder.mkdirs();
            DaftarMK mk = new DaftarMK(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("semester")),
                    cr.getInt(cr.getColumnIndex("sks")),cr.getString(cr.getColumnIndex("dosen")),cr.getString(cr.getColumnIndex("grade")),
                    cr.getFloat(cr.getColumnIndex("nilai_akhir")),cr.getInt(cr.getColumnIndex("status"))
            );
            daftarMK.add(mk);
        }

        DaftarMK mk = new DaftarMK("Umum",0,0,"","",Float.parseFloat("0"),0);
        daftarMK.add(mk);

        if (daftarMK.size()>0){
            Collections.sort(daftarMK, new Comparator<DaftarMK>() {
                @Override
                public int compare(DaftarMK o1, DaftarMK o2) {
                    return o1.getNamaMK().compareToIgnoreCase(o2.getNamaMK());
                }
            });
            warning_text.setVisibility(View.INVISIBLE);
        }else{
            warning_text.setVisibility(View.VISIBLE);
        }

        dropdown.clear();
        dropdown.add(0,"Semua");
        query = "SELECT DISTINCT semester from mata_kuliah ORDER BY semester ASC";
        cr = db.rawQuery(query,null);
        while (cr.moveToNext()){
            dropdown.add("Semester "+cr.getString(cr.getColumnIndex("semester")));
        }
        System.out.println(dropdown.toString());
    }

    private void checkViews(){
        File directory = new File(pathMenu);
        directory.mkdirs();
        File[] files = directory.listFiles();
        String[] pathfrom = pathNow.substring(pathNow.toString().indexOf("Pixature")).split("/");
        if (files!=null){
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });

            chooseMenu.clear();

                if (viewMenu==1){
                    File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature/"+pathfrom[1]);
                    String query = "SELECT * FROM mata_kuliah ORDER BY nama_mata_kuliah ASC";
                    Cursor cr = db.rawQuery(query,null);
                    while(cr.moveToNext()){
                        for (File f:files){
                            if (viewMenu==2){
                                if (cr.getString(cr.getColumnIndex("nama_mata_kuliah")).equalsIgnoreCase(f.getName())&&f.isDirectory()&&!f.getAbsolutePath().equalsIgnoreCase(path.getAbsolutePath())){
                                    File folder = new File(directory, cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
                                    folder.mkdirs();
                                    chooseMenu.add(f.getAbsolutePath());
                                }
                            }else{
                                if (cr.getString(cr.getColumnIndex("nama_mata_kuliah")).equalsIgnoreCase(f.getName())&&f.isDirectory()){
                                    File folder = new File(directory, cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
                                    folder.mkdirs();
                                    chooseMenu.add(f.getAbsolutePath());
                                }
                            }
                        }
                    }

                    if (viewMenu==2){
                        if (!path.getAbsolutePath().equalsIgnoreCase("/storage/emulated/0/Pictures/Pixature/Umum")){
                            chooseMenu.add("/storage/emulated/0/Pictures/Pixature/Umum");
                        }
                    }else{
                        chooseMenu.add("/storage/emulated/0/Pictures/Pixature/Umum");
                    }


                    Log.d("choosemen2",path.getAbsolutePath()+"__"+viewMenu);
                }else{
                    File path = null;
                    if (viewStatus==3){
                        path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature/"+pathfrom[1]+"/"+pathfrom[2]);
                    }

                    for (File f:files){
                        if (viewStatus==3){
                            if (f.isDirectory() && !f.getAbsolutePath().equalsIgnoreCase(path.getAbsolutePath())){
                                chooseMenu.add(f.getAbsolutePath());
                            }
                        }else{
                            if (f.isDirectory()){
                                chooseMenu.add(f.getAbsolutePath());
                            }
                        }

                    }
                }

            if (chooseMenu.size()>0) {
                Collections.sort(chooseMenu, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareToIgnoreCase(o2);
                    }
                });
            }
        }

        for(String ch:chooseMenu){
            Log.d("choosemen",ch);
        }
    }

    private void sortAsDropdown(int semester){
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature");
        directory.mkdirs();
        File[] files = directory.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        String query;
        daftarMK.clear();

        query = "SELECT * FROM mata_kuliah WHERE semester = ? ORDER BY nama_mata_kuliah ASC";
        Cursor cr = db.rawQuery(query,new String[]{semester+""});
        while(cr.moveToNext()){
            DaftarMK mk = new DaftarMK(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("semester")),
                    cr.getInt(cr.getColumnIndex("sks")),cr.getString(cr.getColumnIndex("dosen")),cr.getString(cr.getColumnIndex("grade")),
                    cr.getFloat(cr.getColumnIndex("nilai_akhir")),cr.getInt(cr.getColumnIndex("status"))
            );
            Log.d("Semester mk : ", String.valueOf(cr.getInt(cr.getColumnIndex("semester"))));
            daftarMK.add(mk);
        }
        if (daftarMK.size()>0){
            Collections.sort(daftarMK, new Comparator<DaftarMK>() {
                @Override
                public int compare(DaftarMK o1, DaftarMK o2) {
                    return o1.getNamaMK().compareToIgnoreCase(o2.getNamaMK());
                }
            });
            warning_text.setVisibility(View.INVISIBLE);
        }else{
            warning_text.setVisibility(View.VISIBLE);
        }
        gridView.invalidateViews();
        Log.d("daftarMK : ",semester+"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        Setting Untuk Dropdown Menu
        getMenuInflater().inflate(R.menu.spinner_menu,menu);
        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) item.getActionView();
        spinner.setPopupBackgroundResource(R.color.white);
        spinner.setDropDownVerticalOffset((int) (getSupportActionBar().getHeight()*0.9));
        setupSpinner(spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String semester_dropdown = spinner.getSelectedItem().toString();
                int semester=0;
                if (semester_dropdown.equals("Semua")){
                    checkContent();
                    gridView.invalidateViews();
                }else{
                    semester = Integer.parseInt(semester_dropdown.substring(semester_dropdown.indexOf(" ")+1));
                    sortAsDropdown(semester);
                }
                gridView.invalidateViews();
//                Toast.makeText(Gallery.this,semester_dropdown+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Setting untuk menu pojok kanan atas
        options = menu;
        getMenuInflater().inflate(R.menu.gallery_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!statusSelected){
            menu.setGroupVisible(R.id.grupmenu,false);
            menu.setGroupVisible(R.id.grupmenu2,false);
        }else{
            menu.setGroupVisible(R.id.grupmenu,true);
            if (selected.size()>1){
                menu.setGroupVisible(R.id.grupmenu2,false);
            }else{
                menu.setGroupVisible(R.id.grupmenu2,true);
            }
        }
        if (viewStatus>1){
            menu.setGroupVisible(R.id.spingroup,false);
        }else{
            menu.setGroupVisible(R.id.spingroup,true);
        }

        if (viewStatus==2){
            menu.setGroupVisible(R.id.grupmenu3,true);
        }else{
            menu.setGroupVisible(R.id.grupmenu3,false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void setupSpinner(Spinner spin){
        //wrap the items in the Adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.spinner_selected,dropdown);
        //assign adapter to the Spinner
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                deleteMateri();
                return true;
            case R.id.rename:
                renameMateri();
                return true;
            case R.id.copy:
                copyMateri();
                return true;
            case R.id.move:
                moveMateri();
                return true;
            case R.id.newfolder:
                newFolder();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void newFolder(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Gallery.this);
        LayoutInflater inflater = Gallery.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tambah_folder,null);
        alertDialog.setPositiveButton("Tambah",null);
        alertDialog.setNegativeButton("Batal",null);
        alertDialog.setCancelable(false);
        alertDialog.setView(dialogView);

        final AlertDialog ad = alertDialog.create();
        ad.setTitle("Tambah Mata Kuliah Baru");
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_nama = (EditText) ad.findViewById(R.id.input1);
                TextView warning = (TextView) ad.findViewById(R.id.warning_folder);

                boolean statusName = true;
                File file = new File(pathNow);
                File[] files = file.listFiles();
                if (files!=null){
                    for (File f:files){
                        if (f.getName().equalsIgnoreCase(et_nama.getText().toString())){
                            statusName = false;
                        }
                    }
                }
                if (statusName){
                    warning.setVisibility(View.INVISIBLE);
                    File path = new File(pathNow+File.separator+et_nama.getText().toString());
                    path.mkdirs();
                    ad.dismiss();

                    listFolder.clear();

                    File file1 = new File(pathNow);
                    File[] files1 = file1.listFiles();

                    if (files1!=null){
                        for(File f:files1){
                            if (f.isDirectory()){
                                FolderPhoto fp = new FolderPhoto(f.getName());
                                listFolder.add(fp);
                            }
                        }
                    }

                    if (listFolder.size()>0){
                        Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                            @Override
                            public int compare(FolderPhoto o1, FolderPhoto o2) {
                                return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                            }
                        });
                        warning_text.setVisibility(View.INVISIBLE);
                    }else{
                        warning_text.setVisibility(View.VISIBLE);
                    }

                    ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                    checkBC(file);
                    Toast.makeText(Gallery.this,"Folder Berhasil dibuat! : "+path.getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();
                }else {
                    warning.setVisibility(View.VISIBLE);
                }

            }
        });

        ad.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }

    private void moveMateri(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(Gallery.this);
        pathMenu = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/Pixature";
        checkViews();
        builderSingle.setCancelable(false);
        final LayoutInflater inflater = Gallery.this.getLayoutInflater();
        final View dv = inflater.inflate(R.layout.template_choose_menu,null);
        lvw = (LinearLayout) dv.findViewById(R.id.parentPanel);
        breadMenu = (LinearLayout) dv.findViewById(R.id.breadcrumb);
        final Button btnCancel = (Button) dv.findViewById(R.id.btnCancel);
        txWarning = (TextView) dv.findViewById(R.id.warningMenu);

        builderSingle.setView(dv);
        WindowManager wm = (WindowManager) Gallery.this.getSystemService(Context.WINDOW_SERVICE);
        Display dispay = wm.getDefaultDisplay();
        Point size = new Point();
        dispay.getSize(size);
        int width = (size.x*9)/10;
        int height = (size.y*9)/10;
        final AlertDialog dialog = builderSingle.create();
        dialog.show();
        File pathm = new File(pathMenu);
        checkMove(pathm,dialog);
        if(chooseMenu.size()>0){
            for (String s:chooseMenu){
                final View child = inflater.inflate(R.layout.template_list_menu,null);
                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                String[] name = s.split("/");
                final List<String> pathname = Arrays.asList(name);
                tv.setText(pathname.get(pathname.size()-1));

                child.setTag(new File(s));
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkMove((File) v.getTag(),dialog);
                        if (viewStatus==3){
                            viewMenu++;
                        }
                        if (viewMenu==2 && viewStatus==3) {
                            File f = (File) v.getTag();
                            pathMenu = f.getAbsolutePath();
                            Log.d("pathmenu",pathMenu);
                            checkViews();
                            lvw.removeAllViews();
                            if(chooseMenu.size()>0){
                                for (String s:chooseMenu){
                                    View child = inflater.inflate(R.layout.template_list_menu,null);
                                    TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                    String[] name = s.split("/");
                                    List<String> pathname = Arrays.asList(name);
                                    tv.setText(pathname.get(pathname.size()-1));

                                    child.setTag(new File(s));
                                    child.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            File file1 = (File) v.getTag();
                                            pathMenu = file1.getAbsolutePath();
                                            checkMove(file1,dialog);
                                            boolean statusCopy=true;
                                            final ArrayList<String> status = new ArrayList<String>();
                                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                                            for (int i = 0;i<selected.size();i++) {
                                                File targetLocation = new File(pathMenu,selected.get(i));
                                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                if (targetLocation.exists()){
                                                    statusCopy=false;
                                                    exist.add(i);
                                                }
                                            }

                                            Log.d("stauts copy", statusCopy+"");

                                            if (statusCopy) {
                                                for (String path : selected) {
                                                    File folderTarget = new File(pathNow, path);
                                                    File targetLocation = new File(pathMenu, path);
                                                    folderTarget.renameTo(targetLocation);
                                                    status.add("*");
                                                }
                                                if (status.size() >= selected.size()) {
                                                    Toast.makeText(Gallery.this, "Berhasil Move File!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Gallery.this, "Gagal Move File!", Toast.LENGTH_SHORT).show();
                                                }
                                                refreshView();
                                                dialog.dismiss();
                                            }else{
                                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                alert.setTitle("Warning");
                                                alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                                alert.setCancelable(false);
                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog1, int which) {
                                                        for(int i=0;i<selected.size();i++){
                                                            File folderTarget = new File(pathNow,selected.get(i));
                                                            File targetLocation = new File(pathMenu,selected.get(i));

                                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                Log.d("Ada"," APa");
                                                                File[] list = targetLocation.listFiles();
                                                                File[] listb = folderTarget.listFiles();

                                                                sortFile(listb);
                                                                for (File lb : listb) {
                                                                    for (File l : list) {
                                                                        if (lb.getName().equalsIgnoreCase(l.getName())) {
                                                                            try {
                                                                                FileUtils.forceDelete(l);
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                                    lb.renameTo(new File(targetLocation.getAbsolutePath(), lb.getName()));
                                                                    status.add("*");
                                                                    Log.d("pathFol", lb.getAbsolutePath());
                                                                }
                                                                try {
                                                                    FileUtils.forceDelete(folderTarget);
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }else{
                                                                folderTarget.renameTo(targetLocation);
                                                                status.add("*");
                                                            }
                                                        }
                                                        if (status.size()>=selected.size()){
                                                            Toast.makeText(Gallery.this,"Berhasil Move File!",Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(Gallery.this,"Gagal Move File!",Toast.LENGTH_SHORT).show();
                                                        }
                                                        refreshView();
                                                        dialog.dismiss();
                                                        dialog1.dismiss();
                                                    }
                                                });
                                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                AlertDialog test = alert.show();
                                            }


                                            Log.d("CLICK","1");
                                            Log.d("Masuk Sini",pathMenu);
//                                            for(String path:selected){
//                                                Log.d("path",path);
//                                                File folderTarget = new File(pathNow,path);
//                                                File targetLocation = new File(pathMenu,path);
//                                                folderTarget.renameTo(targetLocation);
//                                            }
                                        }
                                    });
                                    lvw.addView(child);
                                }
                                txWarning.setVisibility(View.INVISIBLE);
                            }else{
                                txWarning.setVisibility(View.VISIBLE);
                            }
                            Log.d("Masuk Sini","2");
                            Log.d("Viewmenu",viewMenu+"_"+viewStatus);
                        }else{
                            File file1 = (File) v.getTag();
                            pathMenu = file1.getAbsolutePath();
                            Log.d("Masuk Sini","3");

                            boolean statusCopy=true;
                            final ArrayList<String> status = new ArrayList<String>();
                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                            for (int i = 0;i<selected.size();i++) {
                                File targetLocation = new File(pathMenu,selected.get(i));
                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                if (targetLocation.exists()){
                                    statusCopy=false;
                                    exist.add(i);
                                }
                            }

                            Log.d("Status Copy",statusCopy+"");

                            if (statusCopy) {
                                Log.d("Di","IF");
                                for (String path : selected) {
                                    File folderTarget = new File(pathNow, path);
                                    File targetLocation = new File(pathMenu, path);
                                    folderTarget.renameTo(targetLocation);
                                    status.add("*");
                                }
                                if (status.size() >= selected.size()) {
                                    Toast.makeText(Gallery.this, "Berhasil Move File!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Gallery.this, "Gagal Move File!", Toast.LENGTH_SHORT).show();
                                }
                                refreshView();
                                dialog.dismiss();
                            }else{
                                Log.d("Di","Else");
                                dialog.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                alert.setTitle("Warning");
                                alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                alert.setCancelable(false);
                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog1, int which) {
                                        Toast.makeText(Gallery.this,"Hai sayang",Toast.LENGTH_SHORT).show();
                                        Log.d("Selected SIze",selected.size()+"");
                                        for(int i=0;i<selected.size();i++){
                                            File folderTarget = new File(pathNow,selected.get(i));
                                            File targetLocation = new File(pathMenu,selected.get(i));

                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                Log.d("Ada"," APa");
                                                File[] list = targetLocation.listFiles();
                                                File[] listb = folderTarget.listFiles();

                                               sortFile(listb);
                                                for (File lb : listb) {
                                                    for (File l : list) {
                                                        if (lb.getName().equalsIgnoreCase(l.getName())) {
                                                            try {
                                                                FileUtils.forceDelete(l);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                    lb.renameTo(new File(targetLocation.getAbsolutePath(), lb.getName()));
                                                    status.add("*");
                                                    Log.d("pathFol", lb.getAbsolutePath());
                                                }
                                                try {
                                                    FileUtils.forceDelete(folderTarget);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Log.d("Apa"," APa");
                                                folderTarget.renameTo(targetLocation);
                                                status.add("*");
                                            }
                                        }

                                        if (status.size()>=selected.size()){
                                            Toast.makeText(Gallery.this,"Berhasil Move File!",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(Gallery.this,"Gagal Move File!",Toast.LENGTH_SHORT).show();
                                        }
                                        refreshView();
                                        dialog.dismiss();
                                        dialog1.dismiss();
                                    }
                                });
                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog test = alert.show();
                            }

                        }
                    }
                });
                lvw.addView(child);
            }
            txWarning.setVisibility(View.INVISIBLE);
        }else{
            txWarning.setVisibility(View.VISIBLE);
        }
        dialog.getWindow().setLayout(width,height);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(final DialogInterface dialog1, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
                    if (viewMenu>1){
                        String[] path = pathMenu.split("/");
                        pathMenu = "";
                        if (path.length>0){
                            List<String> pf = Arrays.asList(path);
                            for (int i = 0;i<pf.size()-1;i++){
                                pathMenu = pathMenu+"/"+pf.get(i);
                            }
                        }
                        viewMenu--;
                        checkViews();
                        lvw.removeAllViews();
                        checkMove(new File(pathMenu),dialog);
                        if(chooseMenu.size()>0){
                            for (String s:chooseMenu){
                                final View child = inflater.inflate(R.layout.template_list_menu,null);
                                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                String[] name = s.split("/");
                                final List<String> pathname = Arrays.asList(name);
                                tv.setText(pathname.get(pathname.size()-1));

                                child.setTag(new File(s));
                                child.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkMove((File) v.getTag(),dialog);
                                        Log.d("CLICK","1");
                                        if (viewStatus==3){
                                            viewMenu++;
                                        }
                                        if (viewMenu==2 && viewStatus==3) {
                                            File f = (File) v.getTag();
                                            pathMenu = f.getAbsolutePath();
                                            Log.d("pathmenu",pathMenu);
                                            checkViews();
                                            lvw.removeAllViews();
                                            if(chooseMenu.size()>0){
                                                for (String s:chooseMenu){
                                                    View child = inflater.inflate(R.layout.template_list_menu,null);
                                                    TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                                    String[] name = s.split("/");
                                                    List<String> pathname = Arrays.asList(name);
                                                    tv.setText(pathname.get(pathname.size()-1));

                                                    child.setTag(new File(s));
                                                    child.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            File file1 = (File) v.getTag();
                                                            pathMenu = file1.getAbsolutePath();
                                                            checkMove(file1,dialog);
                                                            boolean statusCopy=true;
                                                            final ArrayList<String> status = new ArrayList<String>();
                                                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                                                            for (int i = 0;i<selected.size();i++) {
                                                                File targetLocation = new File(pathMenu,selected.get(i));
                                                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                                if (targetLocation.exists()){
                                                                    statusCopy=false;
                                                                    exist.add(i);
                                                                }
                                                            }

                                                            Log.d("stauts copy", statusCopy+"");

                                                            if (statusCopy) {
                                                                for (String path : selected) {
                                                                    File folderTarget = new File(pathNow, path);
                                                                    File targetLocation = new File(pathMenu, path);
                                                                    folderTarget.renameTo(targetLocation);
                                                                    status.add("*");
                                                                }
                                                                if (status.size() >= selected.size()) {
                                                                    Toast.makeText(Gallery.this, "Berhasil Move File!", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(Gallery.this, "Gagal Move File!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                refreshView();
                                                                dialog.dismiss();
                                                            }else{
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                                alert.setTitle("Warning");
                                                                alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                                                alert.setCancelable(false);
                                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog1, int which) {
                                                                        for(int i=0;i<selected.size();i++){
                                                                            File folderTarget = new File(pathNow,selected.get(i));
                                                                            File targetLocation = new File(pathMenu,selected.get(i));

                                                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                                Log.d("Ada"," APa");
                                                                                File[] list = targetLocation.listFiles();
                                                                                File[] listb = folderTarget.listFiles();

                                                                                sortFile(listb);
                                                                                for (File lb : listb) {
                                                                                    for (File l : list) {
                                                                                        if (lb.getName().equalsIgnoreCase(l.getName())) {
                                                                                            try {
                                                                                                FileUtils.forceDelete(l);
                                                                                            } catch (IOException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    lb.renameTo(new File(targetLocation.getAbsolutePath(), lb.getName()));
                                                                                    status.add("*");
                                                                                    Log.d("pathFol", lb.getAbsolutePath());
                                                                                }
                                                                                try {
                                                                                    FileUtils.forceDelete(folderTarget);
                                                                                } catch (IOException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }else{
                                                                                folderTarget.renameTo(targetLocation);
                                                                                status.add("*");
                                                                            }
                                                                        }
                                                                        if (status.size()>=selected.size()){
                                                                            Toast.makeText(Gallery.this,"Berhasil Move File!",Toast.LENGTH_SHORT).show();
                                                                        }else{
                                                                            Toast.makeText(Gallery.this,"Gagal Move File!",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        refreshView();
                                                                        dialog.dismiss();
                                                                        dialog1.dismiss();
                                                                    }
                                                                });
                                                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                });
                                                                AlertDialog test = alert.show();
                                                            }
                                                        }
                                                    });
                                                    lvw.addView(child);
                                                }
                                                txWarning.setVisibility(View.INVISIBLE);
                                            }else{
                                                txWarning.setVisibility(View.VISIBLE);
                                            }
                                        }else{
                                            File file1 = (File) v.getTag();
                                            pathMenu = file1.getAbsolutePath();

                                            boolean statusCopy=true;
                                            final ArrayList<String> status = new ArrayList<String>();
                                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                                            for (int i = 0;i<selected.size();i++) {
                                                File targetLocation = new File(pathMenu,selected.get(i));
                                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                if (targetLocation.exists()){
                                                    statusCopy=false;
                                                    exist.add(i);
                                                }
                                            }
                                            if (statusCopy) {
                                                for (String path : selected) {
                                                    File folderTarget = new File(pathNow, path);
                                                    File targetLocation = new File(pathMenu, path);
                                                    folderTarget.renameTo(targetLocation);
                                                    status.add("*");
                                                }
                                                if (status.size() >= selected.size()) {
                                                    Toast.makeText(Gallery.this, "Berhasil Move File!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Gallery.this, "Gagal Move File!", Toast.LENGTH_SHORT).show();
                                                }
                                                refreshView();
                                                dialog.dismiss();
                                            }else{
                                                dialog.dismiss();
                                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                alert.setTitle("Warning");
                                                alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                                alert.setCancelable(false);
                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog1, int which) {
                                                        for(int i=0;i<selected.size();i++){
                                                            File folderTarget = new File(pathNow,selected.get(i));
                                                            File targetLocation = new File(pathMenu,selected.get(i));

                                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                Log.d("Ada"," APa");
                                                                File[] list = targetLocation.listFiles();
                                                                File[] listb = folderTarget.listFiles();

                                                                sortFile(listb);
                                                                for (File lb : listb) {
                                                                    for (File l : list) {
                                                                        if (lb.getName().equalsIgnoreCase(l.getName())) {
                                                                            try {
                                                                                FileUtils.forceDelete(l);
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                                    lb.renameTo(new File(targetLocation.getAbsolutePath(), lb.getName()));
                                                                    status.add("*");
                                                                    Log.d("pathFol", lb.getAbsolutePath());
                                                                }
                                                                try {
                                                                    FileUtils.forceDelete(folderTarget);
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }else{
                                                                folderTarget.renameTo(targetLocation);
                                                                status.add("*");
                                                            }
                                                        }

                                                        if (status.size()>=selected.size()){
                                                            Toast.makeText(Gallery.this,"Berhasil Move File!",Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(Gallery.this,"Gagal Move File!",Toast.LENGTH_SHORT).show();
                                                        }
                                                        refreshView();
                                                        dialog.dismiss();
                                                        dialog1.dismiss();
                                                    }
                                                });
                                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                AlertDialog test = alert.show();
                                            }

                                        }
                                    }
                                });
                                lvw.addView(child);
                            }
                            txWarning.setVisibility(View.INVISIBLE);
                        }else{
                            txWarning.setVisibility(View.VISIBLE);
                        }
                    }else{
                        dialog.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void refreshView(){
        invalidateOptionsMenu();
            if (viewStatus==3){
                File file = new File(pathNow);
                File[] files = file.listFiles();
                imgList.clear();
                if (files!=null){
                    sortFile(files);
                    for(File f:files){
                        if (f.getName().endsWith(".jpg")){
                            File imgbmp = new File(f.getAbsolutePath());
                            FolderPhoto image = new FolderPhoto (imgbmp.getAbsolutePath());
                            imgList.add(image);
                        }
                    }

                }
                if (!fab.isShown()) {
                    fab.show();
                    fab.setClickable(true);
                    open.show();
                    open.setClickable(true);
                    pdf.show();
                    pdf.setClickable(true);
                }
                if (imgList.size()>0){
                    warning_text.setVisibility(View.INVISIBLE);
                }else{
                    warning_text.setVisibility(View.VISIBLE);
                }
                imgAdapter.notifyDataSetChanged();
                photogv.invalidateViews();
                statusSelected = false;
                selected.clear();
                getSupportActionBar().setTitle("Gallery");
                Log.d("Viewmenu",viewMenu+"_"+viewStatus);
                viewMenu = 1;
            }else{
                listFolder.clear();
                File file = new File(pathNow);
                File[] files = file.listFiles();
                checkBC(file);
                if (files!=null){
                    for(File f:files){
                        if (f.isDirectory()) {
                            FolderPhoto fp = new FolderPhoto(f.getName());
                            Log.d("photo",f.getName());
                            listFolder.add(fp);
                        }
                    }
                }
                if (listFolder.size()>0){
                    Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                        @Override
                        public int compare(FolderPhoto o1, FolderPhoto o2) {
                            return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                        }
                    });
                    warning_text.setVisibility(View.INVISIBLE);
                }else{
                    warning_text.setVisibility(View.VISIBLE);
                }
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                statusSelected = false;
                selected.clear();
                getSupportActionBar().setTitle("Gallery");
                Log.d("Viewmenu",viewMenu+"_"+viewStatus);
                viewMenu = 1;
            }
    }

    private void checkMenu(final File file, final AlertDialog dialog){
        breadMenu.removeAllViews();
        String pathRow = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String path = file.getAbsolutePath().toString().substring(file.getAbsolutePath().toString().indexOf("Pixature"));
        final LayoutInflater inflater = Gallery.this.getLayoutInflater();
        String[] arraypath = path.split("/");
        List<String> ap  = Arrays.asList(arraypath);
        Log.d("ARRAYPATH",path);

        for(int i = 0;i<ap.size();i++){
            View view = getLayoutInflater().inflate(R.layout.breadcrumb_layout,null);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    invalidateOptionsMenu();
                    File path = (File) v.getTag();
                    pathMenu = path.getAbsolutePath();
                    String[] pathfiles = path.getAbsolutePath().substring(path.getAbsolutePath().toString().indexOf("Pixature")).split("/");
                    List<String> pf = Arrays.asList(pathfiles);
                    viewMenu = pf.size();
                    checkMenu(path,dialog);
                    checkViews();
                    lvw.removeAllViews();
                    if(chooseMenu.size()>0){
                        for (String s:chooseMenu){
                            final View child = inflater.inflate(R.layout.template_list_menu,null);
                            TextView tv = (TextView) child.findViewById(R.id.textView_name);
                            String[] name = s.split("/");
                            final List<String> pathname = Arrays.asList(name);
                            tv.setText(pathname.get(pathname.size()-1));

                            child.setTag(new File(s));
                            child.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("CLICK","1");
                                    checkMenu((File) v.getTag(),dialog);
                                    if (viewStatus==3){
                                        viewMenu++;
                                    }
                                    if (viewMenu==2 && viewStatus==3) {
                                        File f = (File) v.getTag();
                                        pathMenu = f.getAbsolutePath();
                                        Log.d("pathmenu",pathMenu);
                                        checkViews();
                                        lvw.removeAllViews();
                                        if(chooseMenu.size()>0){
                                            for (String s:chooseMenu){
                                                View child = inflater.inflate(R.layout.template_list_menu,null);
                                                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                                String[] name = s.split("/");
                                                List<String> pathname = Arrays.asList(name);
                                                tv.setText(pathname.get(pathname.size()-1));

                                                child.setTag(new File(s));
                                                child.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        File file1 = (File) v.getTag();
                                                        checkMenu(file1,dialog);
                                                        pathMenu = file1.getAbsolutePath();
                                                        boolean statusCopy=true;
                                                        final ArrayList<String> status = new ArrayList<String>();
                                                        final ArrayList<Integer> exist = new ArrayList<Integer>();
                                                        for (int i = 0;i<selected.size();i++) {
                                                            File targetLocation = new File(pathMenu,selected.get(i));
                                                            Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                            if (targetLocation.exists()){
                                                                statusCopy=false;
                                                                exist.add(i);
                                                            }
                                                        }

                                                        Log.d("stauts copy", statusCopy+"");

                                                        if (statusCopy) {
                                                            for (String path : selected) {
                                                                File folderTarget = new File(pathNow, path);
                                                                File targetLocation = new File(pathMenu, path);
                                                                if (folderTarget.isDirectory()){
                                                                    try {
                                                                        FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                                        status.add("*");
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }else{
                                                                    try {
                                                                        FileUtils.copyFile(folderTarget,targetLocation);
                                                                        status.add("*");
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                            if (status.size() >= selected.size()) {
                                                                Toast.makeText(Gallery.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(Gallery.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                            }
                                                            refreshView();
                                                            dialog.dismiss();
                                                        }else{
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                            alert.setTitle("Warning");
                                                            alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                                            alert.setCancelable(false);
                                                            alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog1, int which) {
                                                                    for(int i=0;i<selected.size();i++){
                                                                        File folderTarget = new File(pathNow,selected.get(i));
                                                                        File targetLocation = new File(pathMenu,selected.get(i));

                                                                        if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                            Log.d("Ada"," APa");
                                                                            File[] list = targetLocation.listFiles();
                                                                            File[] listb = folderTarget.listFiles();

                                                                            sortFile(listb);

                                                                            for(File lb:listb){
                                                                                for(File l:list){
                                                                                    if (lb.getName().equalsIgnoreCase(l.getName())){
                                                                                        try {
                                                                                            FileUtils.forceDelete(l);
                                                                                        } catch (IOException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                }
                                                                                Log.d("pt",lb.getAbsolutePath());
                                                                                try{
                                                                                    if (lb.isDirectory()){
                                                                                        status.add("*");
                                                                                        FileUtils.copyDirectory(lb,targetLocation);
                                                                                        Log.d("pathFol",lb.getAbsolutePath());
                                                                                    }else {
                                                                                        status.add("*");
                                                                                        FileUtils.copyFileToDirectory(lb,targetLocation);
                                                                                    }
                                                                                }catch (IOException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        }else{
                                                                            if (folderTarget.isDirectory()){

                                                                                try {
                                                                                    FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                                                    status.add("*");
                                                                                } catch (IOException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }else{
                                                                                try {
                                                                                    FileUtils.copyFile(folderTarget,targetLocation);
                                                                                    status.add("*");
                                                                                } catch (IOException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    if (status.size()>=selected.size()){
                                                                        Toast.makeText(Gallery.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                                    }else{
                                                                        Toast.makeText(Gallery.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    refreshView();
                                                                    dialog.dismiss();
                                                                    dialog1.dismiss();
                                                                }
                                                            });
                                                            alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialogInterface.dismiss();
                                                                }
                                                            });
                                                            AlertDialog test = alert.show();
                                                        }
                                                    }
                                                });
                                                lvw.addView(child);
                                            }
                                            txWarning.setVisibility(View.INVISIBLE);
                                        }else{
                                            txWarning.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        File file1 = (File) v.getTag();
                                        pathMenu = file1.getAbsolutePath();
                                        boolean statusCopy=true;
                                        final ArrayList<String> status = new ArrayList<String>();
                                        final ArrayList<Integer> exist = new ArrayList<Integer>();
                                        for (int i = 0;i<selected.size();i++) {
                                            File targetLocation = new File(pathMenu,selected.get(i));
                                            Log.d("Name a Location",targetLocation.getAbsolutePath());
                                            if (targetLocation.exists()){
                                                statusCopy=false;
                                                exist.add(i);
                                            }
                                        }

                                        if (statusCopy) {
                                            for (String path : selected) {
                                                File folderTarget = new File(pathNow, path);
                                                File targetLocation = new File(pathMenu, path);

                                                if (folderTarget.isDirectory()){

                                                    try {
                                                        FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                        status.add("*");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    try {
                                                        FileUtils.copyFile(folderTarget,targetLocation);
                                                        status.add("*");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            if (status.size() >= selected.size()) {
                                                Toast.makeText(Gallery.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Gallery.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                            }
                                            refreshView();
                                            dialog.dismiss();
                                        }else{
                                            dialog.dismiss();
                                            AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                            alert.setTitle("Warning");
                                            alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog1, int which) {
                                                    for(int i=0;i<selected.size();i++){
                                                        File folderTarget = new File(pathNow,selected.get(i));
                                                        File targetLocation = new File(pathMenu,selected.get(i));

                                                        if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                            Log.d("Ada"," APa");
                                                            File[] list = targetLocation.listFiles();
                                                            File[] listb = folderTarget.listFiles();

                                                            sortFile(listb);

                                                            for(File lb:listb){
                                                                for(File l:list){
                                                                    if (lb.getName().equalsIgnoreCase(l.getName())){
                                                                        try {
                                                                            FileUtils.forceDelete(l);
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                                Log.d("pt",lb.getAbsolutePath());
                                                                try{
                                                                    if (lb.isDirectory()){
                                                                        status.add("*");
                                                                        FileUtils.copyDirectory(lb,targetLocation);
                                                                        Log.d("pathFol",lb.getAbsolutePath());
                                                                    }else {
                                                                        status.add("*");
                                                                        FileUtils.copyFileToDirectory(lb,targetLocation);
                                                                    }
                                                                }catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }else{
                                                            try {
                                                                FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                                status.add("*");
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    if (status.size()>=selected.size()){
                                                        Toast.makeText(Gallery.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(Gallery.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
                                                    }
                                                    refreshView();
                                                    dialog.dismiss();
                                                    dialog1.dismiss();
                                                }
                                            });
                                            alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            AlertDialog test = alert.show();
                                        }

                                    }
                                }
                            });
                            lvw.addView(child);
                        }
                        txWarning.setVisibility(View.INVISIBLE);
                    }else{
                        txWarning.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(Gallery.this,file.getAbsolutePath()+"__"+viewMenu,Toast.LENGTH_SHORT).show();
                }
            };

            Button path1 = (Button) view.findViewById(R.id.btnC);
            pathRow = pathRow+"/"+ap.get(i);
            path1.setTag(new File(pathRow));
//            Log.d("PAATH1",pathNow);
            path1.setText(ap.get(i));
            path1.setOnClickListener(listener);
            ImageView img = (ImageView) view.findViewById(R.id.imgC);
            if (i<ap.size()-1){
                img.setImageDrawable(getDrawable(R.mipmap.divider_grey));
            }else{
                img.setImageDrawable(getDrawable(R.mipmap.divider_black));
            }

            breadMenu.addView(view);
        }

    }

    private void checkMove(final File file, final AlertDialog dialog){
        breadMenu.removeAllViews();
        String pathRow = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        String path = file.getAbsolutePath().toString().substring(file.getAbsolutePath().toString().indexOf("Pixature"));
        final LayoutInflater inflater = Gallery.this.getLayoutInflater();
        String[] arraypath = path.split("/");
        List<String> ap  = Arrays.asList(arraypath);
        Log.d("ARRAYPATH",path);

        for(int i = 0;i<ap.size();i++){
            View view = getLayoutInflater().inflate(R.layout.breadcrumb_layout,null);


            View.OnClickListener listener = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    invalidateOptionsMenu();
                    File path = (File) v.getTag();
                    pathMenu = path.getAbsolutePath();
                    String[] pathfiles = path.getAbsolutePath().substring(path.getAbsolutePath().toString().indexOf("Pixature")).split("/");
                    List<String> pf = Arrays.asList(pathfiles);
                    viewMenu = pf.size();
                    checkMove(path,dialog);
                    checkViews();
                    lvw.removeAllViews();
                    if(chooseMenu.size()>0){
                        for (String s:chooseMenu){
                            final View child = inflater.inflate(R.layout.template_list_menu,null);
                            TextView tv = (TextView) child.findViewById(R.id.textView_name);
                            String[] name = s.split("/");
                            final List<String> pathname = Arrays.asList(name);
                            tv.setText(pathname.get(pathname.size()-1));

                            child.setTag(new File(s));
                            child.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkMove((File) v.getTag(),dialog);
                                    if (viewStatus==3){
                                        viewMenu++;
                                    }
                                    if (viewMenu==2 && viewStatus==3) {
                                        File f = (File) v.getTag();
                                        pathMenu = f.getAbsolutePath();
                                        Log.d("pathmenu",pathMenu);
                                        checkViews();
                                        lvw.removeAllViews();
                                        if(chooseMenu.size()>0){
                                            for (String s:chooseMenu){
                                                View child = inflater.inflate(R.layout.template_list_menu,null);
                                                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                                String[] name = s.split("/");
                                                List<String> pathname = Arrays.asList(name);
                                                tv.setText(pathname.get(pathname.size()-1));

                                                child.setTag(new File(s));
                                                child.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        File file1 = (File) v.getTag();
                                                        pathMenu = file1.getAbsolutePath();
                                                        checkMove(file1,dialog);
                                                        boolean statusCopy=true;
                                                        final ArrayList<String> status = new ArrayList<String>();
                                                        final ArrayList<Integer> exist = new ArrayList<Integer>();
                                                        for (int i = 0;i<selected.size();i++) {
                                                            File targetLocation = new File(pathMenu,selected.get(i));
                                                            Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                            if (targetLocation.exists()){
                                                                statusCopy=false;
                                                                exist.add(i);
                                                            }
                                                        }

                                                        Log.d("stauts copy", statusCopy+"");

                                                        if (statusCopy) {
                                                            for (String path : selected) {
                                                                File folderTarget = new File(pathNow, path);
                                                                File targetLocation = new File(pathMenu, path);
                                                                folderTarget.renameTo(targetLocation);
                                                                status.add("*");
                                                            }
                                                            if (status.size() >= selected.size()) {
                                                                Toast.makeText(Gallery.this, "Berhasil Move File!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(Gallery.this, "Gagal Move File!", Toast.LENGTH_SHORT).show();
                                                            }
                                                            refreshView();
                                                            dialog.dismiss();
                                                        }else{
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                            alert.setTitle("Warning");
                                                            alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                                            alert.setCancelable(false);
                                                            alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog1, int which) {
                                                                    for(int i=0;i<selected.size();i++){
                                                                        File folderTarget = new File(pathNow,selected.get(i));
                                                                        File targetLocation = new File(pathMenu,selected.get(i));

                                                                        if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                            Log.d("Ada"," APa");
                                                                            File[] list = targetLocation.listFiles();
                                                                            File[] listb = folderTarget.listFiles();

                                                                            sortFile(listb);
                                                                            for (File lb : listb) {
                                                                                for (File l : list) {
                                                                                    if (lb.getName().equalsIgnoreCase(l.getName())) {
                                                                                        try {
                                                                                            FileUtils.forceDelete(l);
                                                                                        } catch (IOException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                }
                                                                                lb.renameTo(new File(targetLocation.getAbsolutePath(), lb.getName()));
                                                                                status.add("*");
                                                                                Log.d("pathFol", lb.getAbsolutePath());
                                                                            }
                                                                            try {
                                                                                FileUtils.forceDelete(folderTarget);
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }else{
                                                                            folderTarget.renameTo(targetLocation);
                                                                            status.add("*");
                                                                        }
                                                                    }
                                                                    if (status.size()>=selected.size()){
                                                                        Toast.makeText(Gallery.this,"Berhasil Move File!",Toast.LENGTH_SHORT).show();
                                                                    }else{
                                                                        Toast.makeText(Gallery.this,"Gagal Move File!",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    refreshView();
                                                                    dialog.dismiss();
                                                                    dialog1.dismiss();
                                                                }
                                                            });
                                                            alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialogInterface.dismiss();
                                                                }
                                                            });
                                                            AlertDialog test = alert.show();
                                                        }


                                                        Log.d("CLICK","1");
                                                        Log.d("Masuk Sini",pathMenu);
//                                            for(String path:selected){
//                                                Log.d("path",path);
//                                                File folderTarget = new File(pathNow,path);
//                                                File targetLocation = new File(pathMenu,path);
//                                                folderTarget.renameTo(targetLocation);
//                                            }
                                                    }
                                                });
                                                lvw.addView(child);
                                            }
                                            txWarning.setVisibility(View.INVISIBLE);
                                        }else{
                                            txWarning.setVisibility(View.VISIBLE);
                                        }
                                        Log.d("Masuk Sini","2");
                                        Log.d("Viewmenu",viewMenu+"_"+viewStatus);
                                    }else{
                                        File file1 = (File) v.getTag();
                                        pathMenu = file1.getAbsolutePath();
                                        Log.d("Masuk Sini","3");

                                        boolean statusCopy=true;
                                        final ArrayList<String> status = new ArrayList<String>();
                                        final ArrayList<Integer> exist = new ArrayList<Integer>();
                                        for (int i = 0;i<selected.size();i++) {
                                            File targetLocation = new File(pathMenu,selected.get(i));
                                            Log.d("Name a Location",targetLocation.getAbsolutePath());
                                            if (targetLocation.exists()){
                                                statusCopy=false;
                                                exist.add(i);
                                            }
                                        }

                                        Log.d("Status Copy",statusCopy+"");

                                        if (statusCopy) {
                                            Log.d("Di","IF");
                                            for (String path : selected) {
                                                File folderTarget = new File(pathNow, path);
                                                File targetLocation = new File(pathMenu, path);
                                                folderTarget.renameTo(targetLocation);
                                                status.add("*");
                                            }
                                            if (status.size() >= selected.size()) {
                                                Toast.makeText(Gallery.this, "Berhasil Move File!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Gallery.this, "Gagal Move File!", Toast.LENGTH_SHORT).show();
                                            }
                                            refreshView();
                                            dialog.dismiss();
                                        }else{
                                            Log.d("Di","Else");
                                            dialog.dismiss();
                                            AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                            alert.setTitle("Warning");
                                            alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog1, int which) {
                                                    Toast.makeText(Gallery.this,"Hai sayang",Toast.LENGTH_SHORT).show();
                                                    Log.d("Selected SIze",selected.size()+"");
                                                    for(int i=0;i<selected.size();i++){
                                                        File folderTarget = new File(pathNow,selected.get(i));
                                                        File targetLocation = new File(pathMenu,selected.get(i));

                                                        if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                            Log.d("Ada"," APa");
                                                            File[] list = targetLocation.listFiles();
                                                            File[] listb = folderTarget.listFiles();

                                                            sortFile(listb);
                                                            for (File lb : listb) {
                                                                for (File l : list) {
                                                                    if (lb.getName().equalsIgnoreCase(l.getName())) {
                                                                        try {
                                                                            FileUtils.forceDelete(l);
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                                lb.renameTo(new File(targetLocation.getAbsolutePath(), lb.getName()));
                                                                status.add("*");
                                                                Log.d("pathFol", lb.getAbsolutePath());
                                                            }
                                                            try {
                                                                FileUtils.forceDelete(folderTarget);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else{
                                                            Log.d("Apa"," APa");
                                                            folderTarget.renameTo(targetLocation);
                                                            status.add("*");
                                                        }
                                                    }

                                                    if (status.size()>=selected.size()){
                                                        Toast.makeText(Gallery.this,"Berhasil Move File!",Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(Gallery.this,"Gagal Move File!",Toast.LENGTH_SHORT).show();
                                                    }
                                                    refreshView();
                                                    dialog.dismiss();
                                                    dialog1.dismiss();
                                                }
                                            });
                                            alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            AlertDialog test = alert.show();
                                        }

                                    }
                                }
                            });
                            lvw.addView(child);
                        }
                        txWarning.setVisibility(View.INVISIBLE);
                    }else{
                        txWarning.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(Gallery.this,file.getAbsolutePath()+"__"+viewMenu,Toast.LENGTH_SHORT).show();
                }
            };

            Button path1 = (Button) view.findViewById(R.id.btnC);
            pathRow = pathRow+"/"+ap.get(i);
            path1.setTag(new File(pathRow));
//            Log.d("PAATH1",pathNow);
            path1.setText(ap.get(i));
            path1.setOnClickListener(listener);
            ImageView img = (ImageView) view.findViewById(R.id.imgC);
            if (i<ap.size()-1){
                img.setImageDrawable(getDrawable(R.mipmap.divider_grey));
            }else{
                img.setImageDrawable(getDrawable(R.mipmap.divider_black));
            }

            breadMenu.addView(view);
        }

    }


    private void copyMateri(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(Gallery.this);
        pathMenu = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/Pixature";
        checkViews();
        builderSingle.setCancelable(false);
        final LayoutInflater inflater = Gallery.this.getLayoutInflater();
        final View dv = inflater.inflate(R.layout.template_choose_menu,null);
        lvw = (LinearLayout) dv.findViewById(R.id.parentPanel);
        final Button btnCancel = (Button) dv.findViewById(R.id.btnCancel);
        txWarning = (TextView) dv.findViewById(R.id.warningMenu);
        final TextView judul = (TextView) dv.findViewById(R.id.judul);
        breadMenu = (LinearLayout) dv.findViewById(R.id.breadcrumb);

        builderSingle.setView(dv);
        WindowManager wm = (WindowManager) Gallery.this.getSystemService(Context.WINDOW_SERVICE);
        Display dispay = wm.getDefaultDisplay();
        Point size = new Point();
        dispay.getSize(size);
        int width = (size.x*9)/10;
        int height = (size.y*9)/10;
        final AlertDialog dialog = builderSingle.create();
        dialog.show();
        File pathm = new File(pathMenu);
        checkMenu(pathm,dialog);
        if(chooseMenu.size()>0){
            for (String s:chooseMenu){
                final View child = inflater.inflate(R.layout.template_list_menu,null);
                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                String[] name = s.split("/");
                final List<String> pathname = Arrays.asList(name);
                tv.setText(pathname.get(pathname.size()-1));

                child.setTag(new File(s));
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkMenu((File) v.getTag(),dialog);
                        if (viewStatus==3){
                            viewMenu++;
                        }
                        if (viewMenu==2 && viewStatus==3) {
                            File f = (File) v.getTag();
                            pathMenu = f.getAbsolutePath();
                            Log.d("pathmenu",pathMenu);
                            checkViews();
                            lvw.removeAllViews();
                            if(chooseMenu.size()>0){
                                for (String s:chooseMenu){
                                    View child = inflater.inflate(R.layout.template_list_menu,null);
                                    TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                    String[] name = s.split("/");
                                    List<String> pathname = Arrays.asList(name);
                                    tv.setText(pathname.get(pathname.size()-1));

                                    child.setTag(new File(s));
                                    child.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            File file1 = (File) v.getTag();
                                            checkMenu(file1,dialog);
                                            pathMenu = file1.getAbsolutePath();
                                            boolean statusCopy=true;
                                            final ArrayList<String> status = new ArrayList<String>();
                                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                                            for (int i = 0;i<selected.size();i++) {
                                                File targetLocation = new File(pathMenu,selected.get(i));
                                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                if (targetLocation.exists()){
                                                    statusCopy=false;
                                                    exist.add(i);
                                                }
                                            }

                                            Log.d("stauts copy", statusCopy+"");

                                            if (statusCopy) {
                                                for (String path : selected) {
                                                    File folderTarget = new File(pathNow, path);
                                                    File targetLocation = new File(pathMenu, path);
                                                    if (folderTarget.isDirectory()){
                                                        try {
                                                            FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                            status.add("*");
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else{
                                                        try {
                                                            FileUtils.copyFile(folderTarget,targetLocation);
                                                            status.add("*");
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                if (status.size() >= selected.size()) {
                                                    Toast.makeText(Gallery.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Gallery.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                }
                                                refreshView();
                                                dialog.dismiss();
                                            }else{
                                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                alert.setTitle("File Tujuan Sudah Ada");
                                                alert.setMessage("Apakah Anda Tetap Ingin Memindah Telah Dipilih?");
                                                alert.setCancelable(false);
                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog1, int which) {
                                                        for(int i=0;i<selected.size();i++){
                                                            File folderTarget = new File(pathNow,selected.get(i));
                                                            File targetLocation = new File(pathMenu,selected.get(i));
                                                            Log.d("TR",targetLocation.getAbsolutePath());

                                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                Log.d("Ada"," APa");
                                                                File[] list = targetLocation.listFiles();
                                                                File[] listb = folderTarget.listFiles();

                                                                sortFile(listb);

                                                                for(File lb:listb){
                                                                    for(File l:list){
                                                                        if (lb.getName().equalsIgnoreCase(l.getName())){
                                                                            try {
                                                                                FileUtils.forceDelete(l);
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                                    Log.d("pt",lb.getAbsolutePath());
                                                                    try{
                                                                        if (lb.isDirectory()){
                                                                            status.add("*");
                                                                            FileUtils.copyDirectory(lb,targetLocation);
                                                                            Log.d("pathFol",lb.getAbsolutePath());
                                                                        }else {
                                                                            status.add("*");
                                                                            FileUtils.copyFileToDirectory(lb,targetLocation);
                                                                        }
                                                                    }catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }else{
                                                                try {
                                                                    FileUtils.forceDelete(targetLocation);
                                                                    FileUtils.copyFile(folderTarget,targetLocation);
                                                                    status.add("*");
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                        if (status.size()>=selected.size()){
                                                            Toast.makeText(Gallery.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(Gallery.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
                                                        }
                                                        refreshView();
                                                        dialog.dismiss();
                                                        dialog1.dismiss();
                                                    }
                                                });
                                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                AlertDialog test = alert.show();
                                            }
                                        }
                                    });
                                    lvw.addView(child);
                                }
                                txWarning.setVisibility(View.INVISIBLE);
                            }else{
                                txWarning.setVisibility(View.VISIBLE);
                            }
                            Log.d("Masuk Sini","2");
                            Log.d("Viewmenu",viewMenu+"_"+viewStatus);
                        }else{
                            File file1 = (File) v.getTag();
                            pathMenu = file1.getAbsolutePath();
                            Log.d("Masuk Sini","3");
                            boolean statusCopy=true;
                            final ArrayList<String> status = new ArrayList<String>();
                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                            for (int i = 0;i<selected.size();i++) {
                                File targetLocation = new File(pathMenu,selected.get(i));
                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                if (targetLocation.exists()){
                                    statusCopy=false;
                                    exist.add(i);
                                }
                            }

                            Log.d("Status Copy",statusCopy+"");

                            if (statusCopy) {
                                Log.d("Di","IF");
                                for (String path : selected) {
                                    File folderTarget = new File(pathNow, path);
                                    File targetLocation = new File(pathMenu, path);
                                    if (folderTarget.isDirectory()){
                                        try {
                                            FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                            status.add("*");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        try {
                                            FileUtils.copyFile(folderTarget,targetLocation);
                                            status.add("*");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (status.size() >= selected.size()) {
                                    Toast.makeText(Gallery.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Gallery.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                }
                                refreshView();
                                dialog.dismiss();
                            }else{
                                Log.d("Di","Else");
                                dialog.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                alert.setTitle("Warning");
                                alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                alert.setCancelable(false);
                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog1, int which) {
                                        for(int i=0;i<selected.size();i++){
                                            File folderTarget = new File(pathNow,selected.get(i));
                                            File targetLocation = new File(pathMenu,selected.get(i));

                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                Log.d("Ada"," APa");
                                                File[] list = targetLocation.listFiles();
                                                File[] listb = folderTarget.listFiles();

                                                sortFile(listb);

                                                for(File lb:listb){
                                                    for(File l:list){
                                                        if (lb.getName().equalsIgnoreCase(l.getName())){
                                                            try {
                                                                FileUtils.forceDelete(l);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                    Log.d("pt",lb.getAbsolutePath());
                                                    try{
                                                        if (lb.isDirectory()){
                                                            status.add("*");
                                                            FileUtils.copyDirectory(lb,targetLocation);
                                                            Log.d("pathFol",lb.getAbsolutePath());
                                                        }else {
                                                            status.add("*");
                                                            FileUtils.copyFileToDirectory(lb,targetLocation);
                                                        }
                                                    }catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }else{
                                                try {
                                                    FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                    status.add("*");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        if (status.size()>=selected.size()){
                                            Toast.makeText(Gallery.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(Gallery.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
                                        }
                                        refreshView();
                                        dialog.dismiss();
                                        dialog1.dismiss();
                                    }
                                });
                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog test = alert.show();
                            }

                        }
                    }
                });
                lvw.addView(child);
            }
            txWarning.setVisibility(View.INVISIBLE);
        }else{
            txWarning.setVisibility(View.VISIBLE);
        }
        dialog.getWindow().setLayout(width,height);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(final DialogInterface dialog1, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
                    if (viewMenu>1){
                        String[] path = pathMenu.split("/");
                        pathMenu = "";
                        if (path.length>0){
                            List<String> pf = Arrays.asList(path);
                            for (int i = 0;i<pf.size()-1;i++){
                                pathMenu = pathMenu+"/"+pf.get(i);
                            }
                        }
                        viewMenu--;
                        checkViews();
                        checkMenu(new File(pathMenu),dialog);
                        lvw.removeAllViews();
                        if(chooseMenu.size()>0){
                            for (String s:chooseMenu){
                                final View child = inflater.inflate(R.layout.template_list_menu,null);
                                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                String[] name = s.split("/");
                                final List<String> pathname = Arrays.asList(name);
                                tv.setText(pathname.get(pathname.size()-1));

                                child.setTag(new File(s));
                                child.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("CLICK","1");
                                        checkMenu((File) v.getTag(),dialog);
                                        if (viewStatus==3){
                                            viewMenu++;
                                        }
                                        if (viewMenu==2 && viewStatus==3) {
                                            File f = (File) v.getTag();
                                            pathMenu = f.getAbsolutePath();
                                            Log.d("pathmenu",pathMenu);
                                            checkViews();
                                            lvw.removeAllViews();
                                            if(chooseMenu.size()>0){
                                                for (String s:chooseMenu){
                                                    View child = inflater.inflate(R.layout.template_list_menu,null);
                                                    TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                                    String[] name = s.split("/");
                                                    List<String> pathname = Arrays.asList(name);
                                                    tv.setText(pathname.get(pathname.size()-1));

                                                    child.setTag(new File(s));
                                                    child.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            File file1 = (File) v.getTag();
                                                            checkMenu(file1,dialog);
                                                            pathMenu = file1.getAbsolutePath();
                                                            boolean statusCopy=true;
                                                            final ArrayList<String> status = new ArrayList<String>();
                                                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                                                            for (int i = 0;i<selected.size();i++) {
                                                                File targetLocation = new File(pathMenu,selected.get(i));
                                                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                                if (targetLocation.exists()){
                                                                    statusCopy=false;
                                                                    exist.add(i);
                                                                }
                                                            }

                                                            Log.d("stauts copy", statusCopy+"");

                                                            if (statusCopy) {
                                                                for (String path : selected) {
                                                                    File folderTarget = new File(pathNow, path);
                                                                    File targetLocation = new File(pathMenu, path);
                                                                    if (folderTarget.isDirectory()){
                                                                        try {
                                                                            FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                                            status.add("*");
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }else{
                                                                        try {
                                                                            FileUtils.copyFile(folderTarget,targetLocation);
                                                                            status.add("*");
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                                if (status.size() >= selected.size()) {
                                                                    Toast.makeText(Gallery.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(Gallery.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                refreshView();
                                                                dialog.dismiss();
                                                            }else{
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                                alert.setTitle("Warning");
                                                                alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                                                alert.setCancelable(false);
                                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog1, int which) {
                                                                        for(int i=0;i<selected.size();i++){
                                                                            File folderTarget = new File(pathNow,selected.get(i));
                                                                            File targetLocation = new File(pathMenu,selected.get(i));

                                                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                                Log.d("Ada"," APa");
                                                                                File[] list = targetLocation.listFiles();
                                                                                File[] listb = folderTarget.listFiles();

                                                                                sortFile(listb);

                                                                                for(File lb:listb){
                                                                                    for(File l:list){
                                                                                        if (lb.getName().equalsIgnoreCase(l.getName())){
                                                                                            try {
                                                                                                FileUtils.forceDelete(l);
                                                                                            } catch (IOException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    Log.d("pt",lb.getAbsolutePath());
                                                                                    try{
                                                                                        if (lb.isDirectory()){
                                                                                            status.add("*");
                                                                                            FileUtils.copyDirectory(lb,targetLocation);
                                                                                            Log.d("pathFol",lb.getAbsolutePath());
                                                                                        }else {
                                                                                            status.add("*");
                                                                                            FileUtils.copyFileToDirectory(lb,targetLocation);
                                                                                        }
                                                                                    }catch (IOException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            }else{
                                                                                if (folderTarget.isDirectory()){

                                                                                    try {
                                                                                        FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                                                        status.add("*");
                                                                                    } catch (IOException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }else{
                                                                                    try {
                                                                                        FileUtils.copyFile(folderTarget,targetLocation);
                                                                                        status.add("*");
                                                                                    } catch (IOException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        if (status.size()>=selected.size()){
                                                                            Toast.makeText(Gallery.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                                        }else{
                                                                            Toast.makeText(Gallery.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        refreshView();
                                                                        dialog.dismiss();
                                                                        dialog1.dismiss();
                                                                    }
                                                                });
                                                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                });
                                                                AlertDialog test = alert.show();
                                                            }
                                                        }
                                                    });
                                                    lvw.addView(child);
                                                }
                                                txWarning.setVisibility(View.INVISIBLE);
                                            }else{
                                                txWarning.setVisibility(View.VISIBLE);
                                            }
                                        }else{
                                            File file1 = (File) v.getTag();
                                            pathMenu = file1.getAbsolutePath();
                                            boolean statusCopy=true;
                                            final ArrayList<String> status = new ArrayList<String>();
                                            final ArrayList<Integer> exist = new ArrayList<Integer>();
                                            for (int i = 0;i<selected.size();i++) {
                                                File targetLocation = new File(pathMenu,selected.get(i));
                                                Log.d("Name a Location",targetLocation.getAbsolutePath());
                                                if (targetLocation.exists()){
                                                    statusCopy=false;
                                                    exist.add(i);
                                                }
                                            }

                                            if (statusCopy) {
                                                for (String path : selected) {
                                                    File folderTarget = new File(pathNow, path);
                                                    File targetLocation = new File(pathMenu, path);

                                                    if (folderTarget.isDirectory()){

                                                        try {
                                                            FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                            status.add("*");
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else{
                                                        try {
                                                            FileUtils.copyFile(folderTarget,targetLocation);
                                                            status.add("*");
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                if (status.size() >= selected.size()) {
                                                    Toast.makeText(Gallery.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Gallery.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                }
                                                refreshView();
                                                dialog.dismiss();
                                            }else{
                                                dialog.dismiss();
                                                AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
                                                alert.setTitle("Warning");
                                                alert.setMessage("Apakah Anda Ingin Memindah Telah Dipilih?");
                                                alert.setCancelable(false);
                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog1, int which) {
                                                        for(int i=0;i<selected.size();i++){
                                                            File folderTarget = new File(pathNow,selected.get(i));
                                                            File targetLocation = new File(pathMenu,selected.get(i));

                                                            if(folderTarget.isDirectory()&&exist.contains(i)) {
                                                                Log.d("Ada"," APa");
                                                                File[] list = targetLocation.listFiles();
                                                                File[] listb = folderTarget.listFiles();

                                                                sortFile(listb);

                                                                for(File lb:listb){
                                                                    for(File l:list){
                                                                        if (lb.getName().equalsIgnoreCase(l.getName())){
                                                                            try {
                                                                                FileUtils.forceDelete(l);
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                                    Log.d("pt",lb.getAbsolutePath());
                                                                    try{
                                                                        if (lb.isDirectory()){
                                                                            status.add("*");
                                                                            FileUtils.copyDirectory(lb,targetLocation);
                                                                            Log.d("pathFol",lb.getAbsolutePath());
                                                                        }else {
                                                                            status.add("*");
                                                                            FileUtils.copyFileToDirectory(lb,targetLocation);
                                                                        }
                                                                    }catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }else{
                                                                try {
                                                                    FileUtils.copyDirectory(folderTarget,targetLocation,false);
                                                                    status.add("*");
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }

                                                        if (status.size()>=selected.size()){
                                                            Toast.makeText(Gallery.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(Gallery.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
                                                        }
                                                        refreshView();
                                                        dialog.dismiss();
                                                        dialog1.dismiss();
                                                    }
                                                });
                                                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                AlertDialog test = alert.show();
                                            }

                                        }
                                    }
                                });
                                lvw.addView(child);
                            }
                            txWarning.setVisibility(View.INVISIBLE);
                        }else{
                            txWarning.setVisibility(View.VISIBLE);
                        }
                    }else{
                        dialog.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    private File[] sortFile(File[] file){
        Arrays.sort( file, new Comparator<File>() {
            public int compare(File o1, File o2) {
                if (o1.lastModified()<o2.lastModified()) {
                    return -1;
                } else if (o1.lastModified()>o2.lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });
        return file;
    }

    private void renameMateri(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Gallery.this);
        LayoutInflater inflater = Gallery.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tambah_folder,null);
        alertDialog.setPositiveButton("Ubah",null);
        alertDialog.setNegativeButton("Batal",null);
        alertDialog.setCancelable(false);
        alertDialog.setView(dialogView);

        final AlertDialog ad = alertDialog.create();
        ad.setTitle("Ubah Nama");
        ad.show();
        TextInputEditText et_nama = (TextInputEditText) ad.findViewById(R.id.input1);
        if (viewStatus==3){
            String[] pathfiles = input_nama.split("/");
            List<String> namepath = Arrays.asList(pathfiles);
            input_nama = namepath.get(namepath.size()-1);
            input_nama = input_nama.substring(4,input_nama.indexOf("."));
            jenis = input_nama.substring(0,3);
        }
        et_nama.setText(input_nama);
        et_nama.setSelection(0,et_nama.getText().length());

        et_nama.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText et_nama = (TextInputEditText) ad.findViewById(R.id.input1);
                TextView warning = (TextView) ad.findViewById(R.id.warning_folder);

                if (viewStatus==3){
                    input_nama = et_nama.getText().toString();
                    namaFile = jenis+"_"+namaFile+".jpg";
                }

                boolean statusName = true;
                File file = new File(pathNow);
                File[] files = file.listFiles();
                if (files!=null){
                    for (File f:files){
                        if (viewStatus==3){
                            if (f.getName().equalsIgnoreCase(namaFile)){
                                statusName = false;
                            }
                        }else{
                            if (f.getName().equalsIgnoreCase(et_nama.getText().toString())){
                                statusName = false;
                            }
                        }
                    }
                }
                if (statusName){
                    warning.setVisibility(View.INVISIBLE);
                    File path = new File(pathNow+File.separator+input_nama);
                    File to;

                    if (viewStatus==3){
                        to = new File(pathNow+File.separator+namaFile);
                    }else{
                        to = new File(pathNow+File.separator+et_nama.getText().toString());
                    }
                    path.renameTo(to);
                    ad.dismiss();

                    listFolder.clear();

                    File file1 = new File(pathNow);
                    File[] files1 = file1.listFiles();

                    statusSelected=false;
                    input_nama = "";
                    selected.clear();

                    if (viewStatus==2){
                        if (files1!=null){
                            for(File f:files1){
                                if (f.isDirectory()){
                                    FolderPhoto fp = new FolderPhoto(f.getName());
                                    listFolder.add(fp);
                                }
                            }
                        }

                        if (listFolder.size()>0){
                            Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                                @Override
                                public int compare(FolderPhoto o1, FolderPhoto o2) {
                                    return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                                }
                            });
                            warning_text.setVisibility(View.INVISIBLE);
                        }else{
                            warning_text.setVisibility(View.VISIBLE);
                        }
                        ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                    }else{
                        imgList.clear();
                        if (files!=null){
                            sortFile(files);
                            for(File f:files){
                                File bmpFile = new File(file.getAbsolutePath()+"/"+f.getName());
                                Log.d("img",bmpFile.getAbsolutePath());
                                if (f.getName().endsWith(".jpg")){
                                    File imgbmp = new File(f.getAbsolutePath());
                                    FolderPhoto image = new FolderPhoto (imgbmp.getAbsolutePath());
                                    imgList.add(image);
                                }
                            }
                        }
                        imgAdapter.notifyDataSetChanged();
                        if (imgList.size()>0){
                            warning_text.setVisibility(View.INVISIBLE);
                        }else{
                            warning_text.setVisibility(View.VISIBLE);
                        }
                        photogv.invalidateViews();
                        if (!fab.isShown()) {
                            fab.show();
                            fab.setClickable(true);
                            open.show();
                            open.setClickable(true);
                            pdf.show();
                            pdf.setClickable(true);
                        }
                    }
                    getSupportActionBar().setTitle("Gallery");
                    invalidateOptionsMenu();
                    checkBC(file);
                }else {
                    warning.setVisibility(View.VISIBLE);
                }

            }
        });

        ad.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }

    private void deleteMateri(){
        AlertDialog.Builder alert = new AlertDialog.Builder(Gallery.this);
        alert.setTitle("Warning");
        alert.setMessage("Apakah Anda ingin menghapus Mata Kuliah yang Telah Dipilih Beserta Isinya?");
        alert.setCancelable(false);
        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (String s:selected){
                    File directory = new File(pathNow,s);
                    Log.d("Hapus",directory.getAbsolutePath());
                    try {
                        FileUtils.forceDelete(directory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(Gallery.this,"Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();

                if (viewStatus==2){
                    listFolder.clear();
                    File file = new File(pathNow);
                    File[] files = file.listFiles();
                    checkBC(file);
                    if (files!=null){
                        for(File f:files){
                            if (f.isDirectory()) {
                                FolderPhoto fp = new FolderPhoto(f.getName());
                                Log.d("photo",f.getName());
                                listFolder.add(fp);
                            }
                        }
                    }
                    if (listFolder.size()>0){
                        Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                            @Override
                            public int compare(FolderPhoto o1, FolderPhoto o2) {
                                return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                            }
                        });
                        warning_text.setVisibility(View.INVISIBLE);
                    }else{
                        warning_text.setVisibility(View.VISIBLE);
                    }
                    ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                }else{
                    imgList.clear();
                    File file = new File(pathNow);
                    File[] files = file.listFiles();
                    if (files!=null){
                        sortFile(files);
                        for(File f:files){
                            File bmpFile = new File(file.getAbsolutePath()+"/"+f.getName());
                            Log.d("img",bmpFile.getAbsolutePath());
                            if (f.getName().endsWith(".jpg")){
                                File imgbmp = new File(f.getAbsolutePath());
                                FolderPhoto image = new FolderPhoto (imgbmp.getAbsolutePath());
                                imgList.add(image);
                            }
                        }
                    }
                    imgAdapter.notifyDataSetChanged();
                    if (imgList.size()>0){
                        warning_text.setVisibility(View.INVISIBLE);
                    }else{
                        warning_text.setVisibility(View.VISIBLE);
                    }
                    photogv.invalidateViews();
                    if (!fab.isShown()) {
                        fab.show();
                        fab.setClickable(true);
                        open.show();
                        open.setClickable(true);
                        pdf.show();
                        pdf.setClickable(true);
                    }
                }

                relativeLayout.setBackgroundColor(getColor(R.color.white));
                if (t!=null){
                    t.cancel();
                }
                t = Toast.makeText(Gallery.this,"Data Berhasil Dihapus",Toast.LENGTH_SHORT);
                t.show();
                selected.clear();
                statusSelected = false;
                getSupportActionBar().setTitle("Gallery");
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog test = alert.show();
    }

    class CustomAdapter extends BaseAdapter {

        ArrayList<String> custom = new ArrayList<>();

        @Override
        public int getCount() {
            return listFolder.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            int count = 0;

            convertView = getLayoutInflater().inflate(R.layout.template_list_folder, null);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            TextView textView_name = (TextView)convertView.findViewById(R.id.textView_name);
            TextView textView_description = (TextView)convertView.findViewById(R.id.textView_description);
            RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.rootList);
            imageView.setImageResource(R.drawable.newfolder);
            if (listFolder.get(i).getNamaFolder().length()>24){
                String newName = listFolder.get(i).getNamaFolder().substring(0,10)+"...";
                textView_name.setText(newName);
            }else{
                textView_name.setText(listFolder.get(i).getNamaFolder());
            }

            if (listFolder.get(i).isChecked()){
                rl.setBackgroundColor(Color.parseColor("#CC6FB7FF"));
            }else{
                rl.setBackgroundColor(getColor(R.color.white));
            }

            File file = new File(pathNow+"/"+listFolder.get(i).getNamaFolder());
            convertView.setTag(file);
            File[] files = file.listFiles();

            if (files != null) {
                for (File f:files){
                    if (f.getName().endsWith(".jpg")){
                        count++;
                    }
                }
            }
            textView_description.setText(count + " Foto");

            return convertView;
        }
    }

    class CustomMenu extends BaseAdapter {

        ArrayList<String> custom = new ArrayList<>();

        @Override
        public int getCount() {
            return chooseMenu.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.choose_menu, null);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            TextView textView_name = (TextView)convertView.findViewById(R.id.textView_name);
            imageView.setImageResource(R.drawable.newfolder);
            if (chooseMenu.get(i).length()>24){
                String newName = chooseMenu.get(i).substring(0,10)+"...";
                textView_name.setText(newName);
            }else{
                textView_name.setText(chooseMenu.get(i));
            }
            return convertView;
        }
    }

    class DaftarMKAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return daftarMK.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            int jumlahMateri = 0;
            final LayoutInflater inflater = (LayoutInflater) Gallery.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.nama_matkul);
            if (daftarMK.get(i).getNamaMK().length() > 25) {
                String newName = daftarMK.get(i).getNamaMK().substring(0, 10) + "...";
                textView.setText(newName);
            } else {
                textView.setText(daftarMK.get(i).getNamaMK());
            }

            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature" + File.separator + daftarMK.get(i).getNamaMK());
            File[] files = directory.listFiles();
            convertView.setTag(directory);
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        jumlahMateri++;
                    }
                }
                TextView textView2 = (TextView) convertView.findViewById(R.id.jumlah_matkul);
                textView2.setText(jumlahMateri + "");
//        }
                if (daftarMK.get(i).isChecked()) {
//            Coding penanda selected'
                    CardView imageView = (CardView) convertView.findViewById(R.id.cv);
                    imageView.setCardBackgroundColor(Color.parseColor("#635fff"));
                } else {
//            menghilangkan penanda
                    CardView imageView = (CardView) convertView.findViewById(R.id.cv);
                    imageView.setCardBackgroundColor(Color.WHITE);
                }
                if (daftarMK.get(i).visible) {
                    CardView imageView = (CardView) convertView.findViewById(R.id.cv);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    CardView imageView = (CardView) convertView.findViewById(R.id.cv);
                    imageView.setVisibility(View.INVISIBLE);
                }

            }

            return convertView;
        }
    }

    class ImageAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<FolderPhoto> img = new ArrayList<>();

        public ImageAdapter(Context context, ArrayList<FolderPhoto> img){
            this.context=context;
            this.img=img;
        }
        @Override
        public int getCount() {
            return img.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_photo,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_thumbnail);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 10;
//        Bitmap bmp = BitmapFactory.decodeFile(img.get(i).getBm(),options);

//        imageView.setImageBitmap(bmp);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display dispay = wm.getDefaultDisplay();
            Point size = new Point();
            dispay.getSize(size);
            int width = size.x;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.requestLayout();
            imageView.getLayoutParams().height = width/3;

            Glide.with(context)
                    .load(img.get(i).getNamaFolder())
                    .crossFade()
                    .override(100,100)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                    .placeholder(context.getDrawable(R.mipmap.load_pic))
//                .dontAnimate()
                    .into(imageView);

            if (img.get(i).isChecked()){
                imageView.setColorFilter(Color.parseColor("#635fff"), PorterDuff.Mode.SCREEN);
            }else{
                imageView.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            }

            return view;
        }
    }


    @Override
    public void onBackPressed() {
        if (viewStatus==1){
            db.close();
            Log.d("TAG SINi","1");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
        finish();
        }else if(viewStatus==2){
            if (statusSelected){
                statusSelected = false;
                for (FolderPhoto fp:listFolder){
                    if (fp.isChecked()){
                        fp.toggleChecked();
                    }
                }
                selected.clear();
                getSupportActionBar().setTitle("Gallery");
                invalidateOptionsMenu();
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
            }else if(matkulname!=null){
                Intent intent = new Intent(getApplicationContext(), MataKuliah.class);
                startActivity(intent);
                Runtime.getRuntime().freeMemory();
                Runtime.getRuntime().gc();
                finish();
            }else{
                Log.d("TAG SINi","2");
                viewStatus = 1;
                invalidateOptionsMenu();
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature");
                pathNow = path.getAbsolutePath();
                lv.setAdapter(null);
                daftarMK.clear();
                listFolder.clear();
                checkContent();
                gridView.setAdapter(new DaftarMKAdapter());
                gridView.invalidateViews();
                checkBC(path);
                relativeLayout.setBackgroundColor(getColor(R.color.bgmain));
                Runtime.getRuntime().freeMemory();
                Runtime.getRuntime().gc();
            }
        }else{
            if (statusSelected){
                statusSelected = false;
                for (FolderPhoto fp:imgList){
                    if (fp.isChecked()){
                        fp.toggleChecked();
                    }
                }
                selected.clear();
                if (!fab.isShown()) {
                    fab.show();
                    fab.setClickable(true);
                    open.show();
                    open.setClickable(true);
                    pdf.show();
                    pdf.setClickable(true);
                }
                getSupportActionBar().setTitle("Gallery");
                invalidateOptionsMenu();
                photogv.invalidateViews();
            }else{
                photogv.setAdapter(null);
                Log.d("TAG SINi","3");
                imgList.clear();
                daftarMK.clear();
                listFolder.clear();
                String[] path = pathNow.split("/");
                String pathTo = "";
                if (path.length>0){
                    List<String> pf = Arrays.asList(path);
                    for (int i = 0;i<pf.size()-1;i++){
                        pathTo = pathTo+"/"+pf.get(i);
                    }
                }
                File file = new File(pathTo);
                pathNow = file.getAbsolutePath();
                String[] pathfiles = file.getAbsolutePath().substring(file.getAbsolutePath().toString().indexOf("Pixature")).split("/");
                List<String> pf = Arrays.asList(pathfiles);
                viewStatus = pf.size();
                checkBC(file);
                Log.d("TAG SINi",file.getAbsolutePath());
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            FolderPhoto fp = new FolderPhoto(f.getName());
                            listFolder.add(fp);
                        }
                    }
                }

                if (listFolder.size()>0){
                    Collections.sort(listFolder, new Comparator<FolderPhoto>() {
                        @Override
                        public int compare(FolderPhoto o1, FolderPhoto o2) {
                            return o1.getNamaFolder().compareToIgnoreCase(o2.getNamaFolder());
                        }
                    });
                    warning_text.setVisibility(View.INVISIBLE);
                }else{
                    warning_text.setVisibility(View.VISIBLE);
                }

                customAdapter.notifyDataSetChanged();
                lv.setAdapter(customAdapter);
                Runtime.getRuntime().freeMemory();
                Runtime.getRuntime().gc();
                invalidateOptionsMenu();
            }

        }
        Log.d("viewStat",viewStatus+"");
    }
}
