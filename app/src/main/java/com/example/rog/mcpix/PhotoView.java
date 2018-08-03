package com.example.rog.mcpix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PhotoView extends AppCompatActivity {

    private ArrayList<String> image = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<Image> bmp = new ArrayList<>();
    private String path = "";
    private String imageName =  "";
    private int posImg;
    private SliderPagerAdapter mAdapter;
    private ViewPagerFixed vp;
    List<Fragment> fragments = new ArrayList<>();
    private Menu options;
    private LinearLayout breadMenu;
    private TextView txWarning;
    private Toast t;
    private String pathMenu="";
    private ArrayList<String> chooseMenu = new ArrayList<>();
    private int viewMenu =1;
    private SQLiteDatabase db;
    private String uniqueID = "PhotoView";
    private String namaFile = "";
    private String jenis ="";
    private LinearLayout lvw;

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
        setContentView(R.layout.activity_photo_view);
        path = getIntent().getStringExtra("path");
        imageName = getIntent().getStringExtra("image");
        BitmapFactory.Options options =  new BitmapFactory.Options();
        options.inSampleSize = 4;
        posImg = getIntent().getIntExtra("position",0);
        vp = (ViewPagerFixed) findViewById(R.id.pager);

        db = openOrCreateDatabase("pixature",MODE_PRIVATE,null);
        t.makeText(this,posImg+"",Toast.LENGTH_SHORT).show();

        File file = new File(path);
        File[] files = file.listFiles();
        Arrays.sort( files, new Comparator<File>() {
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

        if (files!=null){
            for(File f:files){
                if (f.getName().endsWith(".jpg")){
                    name.add(f.getName());
                    fragments.add(FragmentSlider.newInstance(file.getAbsolutePath()+"/"+f.getName()));
                }
            }
        }


        getSupportActionBar().setTitle(imageName);
//        photoAdapter.notifyDataSetChanged();
        mAdapter = new SliderPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        vp.setCurrentItem(posImg);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.menufloat);
        final FloatingActionButton ocrBtn = (FloatingActionButton) findViewById(R.id.pdfBtn);
        final FloatingActionButton copyBtn = (FloatingActionButton) findViewById(R.id.copyBtn);
        final FloatingActionButton moveBtn = (FloatingActionButton) findViewById(R.id.moveBtn);
        final FloatingActionButton renameBtn = (FloatingActionButton) findViewById(R.id.renameBtn);
        final FloatingActionButton deleteBtn = (FloatingActionButton) findViewById(R.id.deleteBtn);

        final LinearLayout l1 = (LinearLayout) findViewById(R.id.l1);
        final LinearLayout l2 = (LinearLayout) findViewById(R.id.l2);
        final LinearLayout l3 = (LinearLayout) findViewById(R.id.l3);
        final LinearLayout l4 = (LinearLayout) findViewById(R.id.l4);
        final LinearLayout l5 = (LinearLayout) findViewById(R.id.l5);
        final TextView t1 = (TextView) findViewById(R.id.t1);
        final TextView t2 = (TextView) findViewById(R.id.t2);
        final TextView t3 = (TextView) findViewById(R.id.t3);
        final TextView t4 = (TextView) findViewById(R.id.t4);
        final TextView t5 = (TextView) findViewById(R.id.t5);


        final Animation showMFloat = AnimationUtils.loadAnimation(PhotoView.this, R.anim.show_button);
        final Animation hideMFloat = AnimationUtils.loadAnimation(PhotoView.this, R.anim.hide_button);
        final Animation showMBtn = AnimationUtils.loadAnimation(PhotoView.this, R.anim.show_layout);
        final Animation hideMBtn = AnimationUtils.loadAnimation(PhotoView.this, R.anim.show_btn_down);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ocrBtn.isShown()){
                    ocrBtn.setClickable(false);
                    ocrBtn.hide();
                    l1.setVisibility(View.GONE);
                    t1.setVisibility(View.GONE);
                    copyBtn.setClickable(false);
                    l2.setVisibility(View.GONE);
                    t2.setVisibility(View.GONE);
                    copyBtn.hide();
                    moveBtn.setClickable(false);
                    moveBtn.hide();
                    l3.setVisibility(View.GONE);
                    t3.setVisibility(View.GONE);
                    renameBtn.setClickable(false);
                    renameBtn.hide();
                    l4.setVisibility(View.GONE);
                    t4.setVisibility(View.GONE);
                    deleteBtn.setClickable(false);
                    deleteBtn.hide();
                    l5.setVisibility(View.GONE);
                    t5.setVisibility(View.GONE);

                    fab.startAnimation(hideMFloat);
                }else{
                    ocrBtn.setClickable(true);
                    ocrBtn.show();
                    l1.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.VISIBLE);
                    copyBtn.setClickable(true);
                    copyBtn.show();
                    l2.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    moveBtn.setClickable(true);
                    moveBtn.show();
                    l3.setVisibility(View.VISIBLE);
                    t3.setVisibility(View.VISIBLE);
                    renameBtn.setClickable(true);
                    renameBtn.show();
                    l4.setVisibility(View.VISIBLE);
                    t4.setVisibility(View.VISIBLE);
                    deleteBtn.setClickable(true);
                    deleteBtn.show();
                    l5.setVisibility(View.VISIBLE);
                    t5.setVisibility(View.VISIBLE);

                    l1.startAnimation(showMBtn);
                    l2.startAnimation(showMBtn);
                    l3.startAnimation(showMBtn);
                    l4.startAnimation(showMBtn);
                    l5.startAnimation(showMBtn);
                    fab.startAnimation(showMFloat);
                }
            }
        });

        ocrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ocrBtn.isShown()){
                    doOCR();
                }
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (copyBtn.isShown()){
                    copyMateri();
                }
            }
        });

        moveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moveBtn.isShown()){
                    moveMateri();
                }

            }
        });

        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (renameBtn.isShown()){
                    renameMateri();
                }

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteBtn.isShown()){
                    deleteMateri();
                }

            }
        });

    }


    public class SliderPagerAdapter extends FragmentStatePagerAdapter {

        public SliderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    posImg = position;
                    imageName = name.get(position);
                    ((PhotoView) PhotoView.this).getSupportActionBar().setTitle(name.get(position));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            return FragmentSlider.newInstance(fragments.get(position).getArguments().getString("params"));
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

    private void doOCR(){
        db.close();
        Intent intent = new Intent(getApplicationContext(), ConvertIMG.class);
        intent.putExtra("imgPath",path).putExtra("imgName",imageName).putExtra("position",posImg).putExtra("uniqueId",uniqueID);
        startActivity(intent);
        finish();
    }

    private void renameMateri(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PhotoView.this);
        LayoutInflater inflater = PhotoView.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tambah_folder,null);
        alertDialog.setPositiveButton("Ubah",null);
        alertDialog.setNegativeButton("Batal",null);
        alertDialog.setCancelable(false);
        alertDialog.setView(dialogView);

        final AlertDialog ad = alertDialog.create();
        ad.setTitle("Ubah Nama");
        ad.show();
        TextInputEditText et_nama = (TextInputEditText) ad.findViewById(R.id.input1);
        namaFile = name.get(posImg).substring(4,name.get(posImg).indexOf("."));
        jenis = name.get(posImg).substring(0,3);
        et_nama.setText(namaFile);
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
                namaFile = et_nama.getText().toString();
                TextView warning = (TextView) ad.findViewById(R.id.warning_folder);
                String hasilNama = jenis+"_"+namaFile+".jpg";

                boolean statusName = true;
                File file = new File(path);
                File[] files = file.listFiles();
                if (files!=null){
                    for (File f:files){
                        if (f.getName().equalsIgnoreCase(hasilNama)){
                            statusName = false;
                        }
                    }
                }

                if (statusName){
                    warning.setVisibility(View.INVISIBLE);
                    File from = new File(path+File.separator+name.get(posImg));
                    File to = new File(path+File.separator+hasilNama);
                    Log.d("From",from.getAbsolutePath());
                    Log.d("To",to.getAbsolutePath());
                    if (from.renameTo(to)){
                        Toast.makeText(PhotoView.this,"Rename Berhasil!",Toast.LENGTH_SHORT).show();
                        name.set(posImg,hasilNama);

                        Log.d("NAMAFILE",name.get(posImg));
                        imageName = hasilNama;
                    }else{
                        Toast.makeText(PhotoView.this,"Rename Gagal!",Toast.LENGTH_SHORT).show();
                    }
                    namaFile = "";
                    ad.dismiss();
                }else{
                    warning.setVisibility(View.VISIBLE);
                }
            }
        });

        ad.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()){
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        ad.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                namaFile = "";
            }
        });
    }

    private void deleteMateri(){
        AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
        alert.setTitle("Warning");
        alert.setMessage("Apakah Anda ingin menghapus Mata Kuliah yang Telah Dipilih Beserta Isinya?");
        alert.setCancelable(false);
        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File directory = new File(path,name.get(posImg));
                Log.d("Hapus",directory.getAbsolutePath());
                try {
                    FileUtils.forceDelete(directory);
                    name.remove(posImg);
                    if (name.size()<=posImg){
                        posImg = name.size()-1;
                    }
                    imageName = name.get(posImg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (t!=null){
                    t.cancel();
                }
                t = Toast.makeText(PhotoView.this,"Data Berhasil Dihapus",Toast.LENGTH_SHORT);
                t.show();
                if (name.size()>0){
                    db.close();
                    Intent intent = new Intent(getApplicationContext(), PhotoView.class);
                    intent.putExtra("path",path).putExtra("image",imageName).putExtra("position",posImg);
                    startActivity(intent);
                    finish();
                }else{
                    db.close();
                    Intent intent = new Intent(getApplicationContext(), Gallery.class);
                    intent.putExtra("path",path);
                    startActivity(intent);
                    finish();
                }
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()){
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        AlertDialog test = alert.show();
    }

    private void checkViews() {
        File directory = new File(pathMenu);
        directory.mkdirs();
        File[] files = directory.listFiles();
        String[] pathfrom = path.substring(path.toString().indexOf("Pixature")).split("/");
        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });

            chooseMenu.clear();

            if (viewMenu==1){
                String query = "SELECT * FROM mata_kuliah ORDER BY nama_mata_kuliah ASC";
                Cursor cr = db.rawQuery(query,null);
                while(cr.moveToNext()){
                    for (File f:files){
                        if (cr.getString(cr.getColumnIndex("nama_mata_kuliah")).equalsIgnoreCase(f.getName())&&f.isDirectory()){
                            File folder = new File(directory, cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
                            folder.mkdirs();
                            chooseMenu.add(folder.getAbsolutePath());
                        }

                    }
                }
                chooseMenu.add("/storage/emulated/0/Pictures/Pixature/Umum");
                Log.d("DISINI","1");
            }else{
                File path = null;
                path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature/"+pathfrom[1]+"/"+pathfrom[2]);


                for (File f:files){
                    if (f.isDirectory() && !f.getAbsolutePath().equalsIgnoreCase(path.getAbsolutePath())){
                        chooseMenu.add(f.getAbsolutePath());
                    }

                }
                Log.d("DISINI","2");
            }
        }
    }

    private void refreshView(){
        db.close();
        File directory = new File(path);
        File[] list = directory.listFiles();
        List<File> f = Arrays.asList(list);
        boolean status = true;
        for (File file:f){
            if (!file.getName().contains(".jpg")){
                status=false;
            }
        }
        Intent intent;
        if (status){
            intent = new Intent(getApplicationContext(), PhotoView.class);
            intent.putExtra("path",path).putExtra("image",imageName).putExtra("position",posImg);
        }else{
            intent = new Intent(getApplicationContext(), Gallery.class);
            intent.putExtra("path",path);
        }
        startActivity(intent);
        finish();
    }


    private void moveMateri(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(PhotoView.this);
        pathMenu = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/Pixature";
        checkViews();
        builderSingle.setCancelable(false);
        final LayoutInflater inflater = PhotoView.this.getLayoutInflater();
        final View dv = inflater.inflate(R.layout.template_choose_menu,null);
        breadMenu = (LinearLayout) dv.findViewById(R.id.breadcrumb);
        final Button btnCancel = (Button) dv.findViewById(R.id.btnCancel);
        txWarning = (TextView) dv.findViewById(R.id.warningMenu);
        lvw = (LinearLayout) dv.findViewById(R.id.parentPanel);
        final TextView judul = (TextView) dv.findViewById(R.id.judul);


        builderSingle.setView(dv);
        WindowManager wm = (WindowManager) PhotoView.this.getSystemService(Context.WINDOW_SERVICE);
        Display dispay = wm.getDefaultDisplay();
        Point size = new Point();
        dispay.getSize(size);
        int width = (size.x*9)/10;
        int height = (size.y*9)/10;
        final AlertDialog dialog = builderSingle.create();
        dialog.show();
        File pathm = new File(pathMenu);
        checkBC(pathm,dialog);
        if(chooseMenu.size()>0){
            for (String s:chooseMenu){
                final View child = inflater.inflate(R.layout.template_list_menu,null);
                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                String[] name = s.split("/");
                final List<String> pathname = Arrays.asList(name);
                tv.setText(pathname.get(pathname.size()-1));
                judul.setText("Pilih Folder Mata Kuliah:");

                child.setTag(new File(s));
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBC((File) v.getTag(),dialog);
                        viewMenu++;
                        judul.setText("Pilih Folder Materi:");
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
                                        checkBC(file1,dialog);

                                        boolean statusCopy=true;
                                        final ArrayList<String> status = new ArrayList<String>();
                                        File targetLocation = new File(pathMenu,imageName);
                                        Log.d("Target",file1.getAbsolutePath()+"__"+targetLocation.exists());
                                        if (targetLocation.exists()){
                                            statusCopy=false;
                                        }


                                        Log.d("stauts copy", statusCopy+"");

                                        if (statusCopy) {
                                            File folderTarget = new File(path, imageName);

                                            folderTarget.renameTo(targetLocation);
                                            status.add("*");
                                            if (status.size() >= 1) {
                                                Toast.makeText(PhotoView.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PhotoView.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.dismiss();
                                            refreshView();
                                        }else{
                                            AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                                            alert.setTitle("File Tujuan Sudah Ada");
                                            alert.setMessage("Apakah Anda Tetap Ingin Memindah Telah Dipilih?");
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog1, int which) {
                                                    File folderTarget = new File(path,imageName);
                                                    File targetLocation = new File(pathMenu,imageName);

                                                    try {
                                                        FileUtils.forceDelete(targetLocation);
                                                        folderTarget.renameTo(targetLocation);
                                                        status.add("*");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    if (status.size()>=1){
                                                        Toast.makeText(PhotoView.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(PhotoView.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
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
                        String[] path1 = pathMenu.split("/");
                        pathMenu = "";
                        if (path1.length>0){
                            List<String> pf = Arrays.asList(path1);
                            for (int i = 0;i<pf.size()-1;i++){
                                pathMenu = pathMenu+"/"+pf.get(i);
                            }
                        }
                        viewMenu--;
                        lvw.removeAllViews();
                        checkViews();
                        checkBC(new File(pathMenu),dialog);
                        if(chooseMenu.size()>0){
                            for (String s:chooseMenu){
                                final View child = inflater.inflate(R.layout.template_list_menu,null);
                                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                String[] name = s.split("/");
                                final List<String> pathname = Arrays.asList(name);
                                tv.setText(pathname.get(pathname.size()-1));
                                judul.setText("Pilih Folder Mata Kuliah:");

                                child.setTag(new File(s));
                                child.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkBC((File) v.getTag(),dialog);
                                        viewMenu++;
                                        if (viewMenu==2) {
                                            judul.setText("Pilih Folder Materi:");
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
                                                            checkBC(file1,dialog);

                                                            boolean statusCopy=true;
                                                            final ArrayList<String> status = new ArrayList<String>();
                                                            File targetLocation = new File(pathMenu,imageName);
                                                            Log.d("Target",file1.getAbsolutePath()+"__"+targetLocation.exists());
                                                            if (targetLocation.exists()){
                                                                statusCopy=false;
                                                            }


                                                            Log.d("stauts copy", statusCopy+"");

                                                            if (statusCopy) {
                                                                File folderTarget = new File(path, imageName);
                                                                folderTarget.renameTo(targetLocation);
                                                                status.add("*");
                                                                if (status.size() >= 1) {
                                                                    Toast.makeText(PhotoView.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(PhotoView.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                dialog.dismiss();
                                                                refreshView();
                                                            }else{
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                                                                alert.setTitle("File Tujuan Sudah Ada");
                                                                alert.setMessage("Apakah Anda Tetap Ingin Memindah Telah Dipilih?");
                                                                alert.setCancelable(false);
                                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog1, int which) {
                                                                        File folderTarget = new File(path,imageName);
                                                                        File targetLocation = new File(pathMenu,path);

                                                                        try {
                                                                            FileUtils.forceDelete(targetLocation);
                                                                            folderTarget.renameTo(targetLocation);
                                                                            status.add("*");
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                        if (status.size()>=1){
                                                                            Toast.makeText(PhotoView.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                                        }else{
                                                                            Toast.makeText(PhotoView.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
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

    private void copyMateri(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(PhotoView.this);
        pathMenu = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/Pixature";
        checkViews();
        builderSingle.setCancelable(false);
        final LayoutInflater inflater = PhotoView.this.getLayoutInflater();
        final View dv = inflater.inflate(R.layout.template_choose_menu,null);
        lvw = (LinearLayout) dv.findViewById(R.id.parentPanel);
        final Button btnCancel = (Button) dv.findViewById(R.id.btnCancel);
        txWarning = (TextView) dv.findViewById(R.id.warningMenu);
        final TextView judul = (TextView) dv.findViewById(R.id.judul);
        breadMenu = (LinearLayout) dv.findViewById(R.id.breadcrumb);

        builderSingle.setView(dv);
        WindowManager wm = (WindowManager) PhotoView.this.getSystemService(Context.WINDOW_SERVICE);
        Display dispay = wm.getDefaultDisplay();
        Point size = new Point();
        dispay.getSize(size);
        int width = (size.x*9)/10;
        int height = (size.y*9)/10;
        final AlertDialog dialog = builderSingle.create();
        dialog.show();
        checkCopy(new File(pathMenu),dialog);
        if(chooseMenu.size()>0){
            for (String s:chooseMenu){
                final View child = inflater.inflate(R.layout.template_list_menu,null);
                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                String[] name = s.split("/");
                final List<String> pathname = Arrays.asList(name);
                tv.setText(pathname.get(pathname.size()-1));
                judul.setText("Pilih Folder Mata Kuliah:");

                child.setTag(new File(s));
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkCopy((File)v.getTag(),dialog);
                            viewMenu++;
                        if (viewMenu==2) {
                            judul.setText("Pilih Folder Materi:");
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
                                            checkCopy(file1,dialog);

                                            boolean statusCopy=true;
                                            final ArrayList<String> status = new ArrayList<String>();
                                            File targetLocation = new File(pathMenu,imageName);
                                            Log.d("Target",file1.getAbsolutePath()+"__"+targetLocation.exists());
                                            if (targetLocation.exists()){
                                                statusCopy=false;
                                            }


                                            Log.d("stauts copy", statusCopy+"");

                                            if (statusCopy) {
                                                File folderTarget = new File(path, imageName);
                                                try {
                                                    FileUtils.copyFile(folderTarget,targetLocation);
                                                    status.add("*");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                if (status.size() >= 1) {
                                                    Toast.makeText(PhotoView.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(PhotoView.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                                refreshView();
                                            }else{
                                                AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                                                alert.setTitle("File Tujuan Sudah Ada");
                                                alert.setMessage("Apakah Anda Tetap Ingin Memindah Telah Dipilih?");
                                                alert.setCancelable(false);
                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog1, int which) {
                                                        File folderTarget = new File(path,imageName);
                                                        File targetLocation = new File(pathMenu,imageName);

                                                        try {
                                                            FileUtils.forceDelete(targetLocation);
                                                            FileUtils.copyFile(folderTarget,targetLocation,false);
                                                            status.add("*");
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }

                                                        if (status.size()>=1){
                                                            Toast.makeText(PhotoView.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(PhotoView.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
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
                        String[] path1 = pathMenu.split("/");
                        pathMenu = "";
                        if (path1.length>0){
                            List<String> pf = Arrays.asList(path1);
                            for (int i = 0;i<pf.size()-1;i++){
                                pathMenu = pathMenu+"/"+pf.get(i);
                            }
                        }
                        viewMenu--;
                        checkViews();
                        lvw.removeAllViews();
                        checkCopy(new File(pathMenu),dialog);
                        if(chooseMenu.size()>0){
                            for (String s:chooseMenu){
                                final View child = inflater.inflate(R.layout.template_list_menu,null);
                                TextView tv = (TextView) child.findViewById(R.id.textView_name);
                                String[] name = s.split("/");
                                final List<String> pathname = Arrays.asList(name);
                                tv.setText(pathname.get(pathname.size()-1));
                                judul.setText("Pilih Folder Mata Kuliah:");

                                child.setTag(new File(s));
                                child.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        viewMenu++;
                                        checkCopy((File) v.getTag(),dialog);
                                        if (viewMenu==2) {
                                            judul.setText("Pilih Folder Materi:");
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
                                                            checkCopy(file1,dialog);

                                                            boolean statusCopy=true;
                                                            final ArrayList<String> status = new ArrayList<String>();
                                                            File targetLocation = new File(pathMenu,imageName);
                                                            Log.d("Target",file1.getAbsolutePath()+"__"+targetLocation.exists());
                                                            if (targetLocation.exists()){
                                                                statusCopy=false;
                                                            }


                                                            Log.d("stauts copy", statusCopy+"");

                                                            if (statusCopy) {
                                                                File folderTarget = new File(path, imageName);
                                                                try {
                                                                    FileUtils.copyFile(folderTarget,targetLocation);
                                                                    status.add("*");
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                if (status.size() >= 1) {
                                                                    Toast.makeText(PhotoView.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(PhotoView.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                dialog.dismiss();
                                                                refreshView();
                                                            }else{
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                                                                alert.setTitle("File Tujuan Sudah Ada");
                                                                alert.setMessage("Apakah Anda Tetap Ingin Memindah Telah Dipilih?");
                                                                alert.setCancelable(false);
                                                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog1, int which) {
                                                                        File folderTarget = new File(path,imageName);
                                                                        File targetLocation = new File(pathMenu,path);

                                                                        try {
                                                                            FileUtils.forceDelete(targetLocation);
                                                                            FileUtils.copyFile(folderTarget,targetLocation,false);
                                                                            status.add("*");
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                        if (status.size()>=1){
                                                                            Toast.makeText(PhotoView.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                                        }else{
                                                                            Toast.makeText(PhotoView.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
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


    private void checkBC(File file, final AlertDialog dialog){
        breadMenu.removeAllViews();
        String pathRow = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String pathm = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("Pixature"));
        String[] arraypath = pathm.split("/");
        List<String> ap  = Arrays.asList(arraypath);
        final LayoutInflater inflater = PhotoView.this.getLayoutInflater();
        Log.d("ARRAYPATH",arraypath.toString());
        Log.d("ARY",ap.size()+"");

        for(int i = 0;i<ap.size();i++){
            View view = getLayoutInflater().inflate(R.layout.breadcrumb_layout,null);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    invalidateOptionsMenu();
                    pathMenu = ((File) v.getTag()).getAbsolutePath();
                    String[] pathfiles = pathMenu.substring(pathMenu.toString().indexOf("Pixature")).split("/");
                    List<String> pf = Arrays.asList(pathfiles);
                    viewMenu = pf.size();
                    checkViews();
                    checkBC(new File(pathMenu),dialog);
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
                                    checkBC((File) v.getTag(),dialog);
                                    viewMenu++;
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
                                                    checkBC(file1,dialog);

                                                    boolean statusCopy=true;
                                                    final ArrayList<String> status = new ArrayList<String>();
                                                    File targetLocation = new File(pathMenu,imageName);
                                                    Log.d("Target",file1.getAbsolutePath()+"__"+targetLocation.exists());
                                                    if (targetLocation.exists()){
                                                        statusCopy=false;
                                                    }


                                                    Log.d("stauts copy", statusCopy+"");

                                                    if (statusCopy) {
                                                        File folderTarget = new File(path, imageName);

                                                        folderTarget.renameTo(targetLocation);
                                                        status.add("*");
                                                        Log.d("HASIL1",folderTarget.getAbsolutePath());
                                                        Log.d("HASIL2",targetLocation.getAbsolutePath());
                                                        if (status.size() >= 1) {
                                                            Toast.makeText(PhotoView.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(PhotoView.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        dialog.dismiss();
                                                        refreshView();
                                                    }else{
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                                                        alert.setTitle("File Tujuan Sudah Ada");
                                                        alert.setMessage("Apakah Anda Tetap Ingin Memindah Telah Dipilih?");
                                                        alert.setCancelable(false);
                                                        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                File folderTarget = new File(path,imageName);
                                                                File targetLocation = new File(pathMenu,imageName);

                                                                try {
                                                                    FileUtils.forceDelete(targetLocation);
                                                                    folderTarget.renameTo(targetLocation);
                                                                    status.add("*");
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                if (status.size()>=1){
                                                                    Toast.makeText(PhotoView.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    Toast.makeText(PhotoView.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
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
                                }
                            });
                            lvw.addView(child);
                        }
                        txWarning.setVisibility(View.INVISIBLE);
                    }else{
                        txWarning.setVisibility(View.VISIBLE);
                    }

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


    private void checkCopy(File file, final AlertDialog dialog){
        breadMenu.removeAllViews();
        String pathRow = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String pathm = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("Pixature"));
        String[] arraypath = pathm.split("/");
        List<String> ap  = Arrays.asList(arraypath);
        final LayoutInflater inflater = PhotoView.this.getLayoutInflater();
        Log.d("ARRAYPATH",arraypath.toString());
        Log.d("ARY",ap.size()+"");

        for(int i = 0;i<ap.size();i++){
            View view = getLayoutInflater().inflate(R.layout.breadcrumb_layout,null);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    invalidateOptionsMenu();
                    pathMenu = ((File) v.getTag()).getAbsolutePath();
                    String[] pathfiles = pathMenu.substring(pathMenu.toString().indexOf("Pixature")).split("/");
                    List<String> pf = Arrays.asList(pathfiles);
                    viewMenu = pf.size();
                    checkViews();
                    checkCopy(new File(pathMenu),dialog);
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
                                    checkCopy((File) v.getTag(),dialog);
                                    viewMenu++;
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
                                                    checkCopy(file1,dialog);

                                                    boolean statusCopy=true;
                                                    final ArrayList<String> status = new ArrayList<String>();
                                                    File targetLocation = new File(pathMenu,imageName);
                                                    Log.d("Target",file1.getAbsolutePath()+"__"+targetLocation.exists());
                                                    if (targetLocation.exists()){
                                                        statusCopy=false;
                                                    }


                                                    Log.d("stauts copy", statusCopy+"");

                                                    if (statusCopy) {
                                                        File folderTarget = new File(path, imageName);
                                                        try {
                                                            FileUtils.copyFile(folderTarget,targetLocation);
                                                            status.add("*");
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (status.size() >= 1) {
                                                            Toast.makeText(PhotoView.this, "Berhasil Copy File!", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(PhotoView.this, "Gagal Copy File!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        dialog.dismiss();
                                                        refreshView();
                                                    }else{
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                                                        alert.setTitle("File Tujuan Sudah Ada");
                                                        alert.setMessage("Apakah Anda Tetap Ingin Memindah Telah Dipilih?");
                                                        alert.setCancelable(false);
                                                        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                File folderTarget = new File(path,imageName);
                                                                File targetLocation = new File(pathMenu,imageName);

                                                                try {
                                                                    FileUtils.forceDelete(targetLocation);
                                                                    FileUtils.copyFile(folderTarget,targetLocation,false);
                                                                    status.add("*");
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                if (status.size()>=1){
                                                                    Toast.makeText(PhotoView.this,"Berhasil Copy File!",Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    Toast.makeText(PhotoView.this,"Gagal Copy File!",Toast.LENGTH_SHORT).show();
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
                                }
                            });
                            lvw.addView(child);
                        }
                        txWarning.setVisibility(View.INVISIBLE);
                    }else{
                        txWarning.setVisibility(View.VISIBLE);
                    }

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

    @Override
    public void onBackPressed() {
        db.close();
        Intent intent = new Intent(getApplicationContext(), Gallery.class);
        intent.putExtra("path",path);
        startActivity(intent);
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
        finish();
    }
}
