package com.example.rog.mcpix;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MataKuliah extends AppCompatActivity {
    private SQLiteDatabase db;
    private ArrayList<String> listDaftarMK = new ArrayList<>();
    private ArrayList<String> selected = new ArrayList<>();
    private ArrayList<DaftarMK> daftarMK = new ArrayList<>();
    private boolean statusSelected = false;
    private GridView gridView;
    private FloatingActionButton fab;
    private String input_nama_matkul="";
    private int input_semester = 0;
    private int input_sks = 0;
    private String input_dosen = "";
    private DaftarMKAdapter mkAdapter;
    private Menu options;
    private Toast t;
    private ArrayList<String> dropdown = new ArrayList<>();
    private Spinner spinner;
    private TextView warning;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mata_kuliah);
        getSupportActionBar().setTitle("Daftar Mata Kuliah");
        db = openOrCreateDatabase("pixature",MODE_PRIVATE,null);
        warning = (TextView) findViewById(R.id.warning_text);
        checkDB();
        gridView = (GridView)findViewById(R.id.maingv);
        mkAdapter = new DaftarMKAdapter(this,daftarMK);
        gridView.setAdapter(mkAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!statusSelected){
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MataKuliah.this);
                    LayoutInflater inflater = MataKuliah.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.matkul_description,null);
                    alertDialog.setPositiveButton("Tutup",null);
                    alertDialog.setNegativeButton("Lihat Catatan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), Gallery.class);
                            intent.putExtra("matkul",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Pixature/"+daftarMK.get(position).getNamaMK());
                            startActivity(intent);
                            finish();
                        }
                    });
                    alertDialog.setView(dialogView);
                    AlertDialog ad = alertDialog.create();
                    ad.setTitle("Deskripsi");
                    ad.show();


                    TextView tv_matkul = (TextView) ad.findViewById(R.id.sub1);
                    tv_matkul.setText(daftarMK.get(position).getNamaMK());
                    TextView tv_dosen = (TextView) ad.findViewById(R.id.sub2);
                    tv_dosen.setText(daftarMK.get(position).getDosen());
                    TextView tv_semester = (TextView) ad.findViewById(R.id.sub3);
                    tv_semester.setText(daftarMK.get(position).getSemester()+"");
                    TextView tv_sks = (TextView) ad.findViewById(R.id.sub4);
                    tv_sks.setText(daftarMK.get(position).getSks()+"");
                    TextView tv_na = (TextView) ad.findViewById(R.id.sub5);
                    tv_na.setText(daftarMK.get(position).getNA()+"/"+daftarMK.get(position).getGrade());
                    TextView tv_status = (TextView) ad.findViewById(R.id.sub6);
                    if (daftarMK.get(position).getStatus()==0){
                        tv_status.setText("Tidak Lulus");
                    }else{
                        tv_status.setText("Lulus");
                    }
                    Log.d("Berhasil Deskripsi",daftarMK.get(position).getNamaMK());
                }else{
                    if (daftarMK.get(position).isChecked()){
                        daftarMK.get(position).toogleChecked();
                        selected.remove(listDaftarMK.get(position));
                        if (selected.isEmpty()){
                            statusSelected = false;
                            getSupportActionBar().setTitle("Daftar Mata Kuliah");
                        }else{
                            getSupportActionBar().setTitle(selected.size()+" Selected");
                        }
                        if (selected.size()==1){
                            input_nama_matkul = selected.get(0);
                        }
                    }else{
                        daftarMK.get(position).toogleChecked();
                        selected.add(listDaftarMK.get(position));
                        getSupportActionBar().setTitle(selected.size()+" Selected");
                    }
                }
                if (options!=null){
                    onPrepareOptionsMenu(options);
                }
                mkAdapter.notifyDataSetChanged();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (!statusSelected){
                    statusSelected = true;
                    daftarMK.get(position).toogleChecked();
                    selected.add(listDaftarMK.get(position));
                    input_nama_matkul = selected.get(0);
                    getSupportActionBar().setTitle(selected.size()+" Selected");
                    mkAdapter.notifyDataSetChanged();
                }
                if (options!=null){
                    onPrepareOptionsMenu(options);
                }
                return true;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.floatMatkul);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MataKuliah.this);
                LayoutInflater inflater = MataKuliah.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.tambah_matkul,null);
                alertDialog.setPositiveButton("Tambah",null);
                alertDialog.setNegativeButton("Batal",null);
                alertDialog.setView(dialogView);

                final AlertDialog ad = alertDialog.create();
                ad.setTitle("Tambah Mata Kuliah Baru");
                ad.show();

                final TextInputEditText et_nama_matkul = (TextInputEditText) ad.findViewById(R.id.input1);
                final TextInputEditText et_sks = (TextInputEditText) ad.findViewById(R.id.input2);
                final TextInputEditText et_semester = (TextInputEditText) ad.findViewById(R.id.input3);
                final TextInputEditText et_dosen = (TextInputEditText) ad.findViewById(R.id.input4);

                et_nama_matkul.setRawInputType(InputType.TYPE_CLASS_TEXT);
                et_nama_matkul.setImeActionLabel("Next", EditorInfo.IME_ACTION_DONE);
                et_nama_matkul.setImeOptions(EditorInfo.IME_ACTION_DONE);
                et_sks.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                et_sks.setImeActionLabel("Next", EditorInfo.IME_ACTION_DONE);
                et_sks.setImeOptions(EditorInfo.IME_ACTION_DONE);
                et_semester.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                et_semester.setImeActionLabel("Next", EditorInfo.IME_ACTION_DONE);
                et_semester.setImeOptions(EditorInfo.IME_ACTION_DONE);
                et_dosen.setRawInputType(InputType.TYPE_CLASS_TEXT);
                et_dosen.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
                et_dosen.setImeOptions(EditorInfo.IME_ACTION_DONE);

                et_nama_matkul.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                    }
                });

                et_nama_matkul.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            et_sks.requestFocus();
                            return true;
                        }
                        return false;
                    }
                });

                et_sks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            et_semester.requestFocus();
                            return true;
                        }
                        return false;
                    }
                });
                et_sks.setFilters(new InputFilter[]{ new InputFilterMinMax(1,6)});

                et_semester.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            et_dosen.requestFocus();
                            return true;
                        }
                        return false;
                    }
                });
                et_semester.setFilters(new InputFilter[]{ new InputFilterMinMax(1,15)});

                et_dosen.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            ad.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                            return true;
                        }
                        return false;
                    }
                });

                ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean status_matkul = false;
                        if (et_nama_matkul.getText().toString().length()!=0) {
                            input_nama_matkul = et_nama_matkul.getText().toString();
                        }
                        if (et_sks.getText().length()!=0){
                            input_sks = Integer.parseInt(et_sks.getText().toString());
                        }
                        if (et_semester.getText().length()!=0){
                            input_semester = Integer.parseInt(et_semester.getText().toString());
                        }
                        if (et_dosen.getText().length()!=0){
                            input_dosen = et_dosen.getText().toString();
                        }

                        ContentValues cv = new ContentValues();
                        cv.put("nama_mata_kuliah",input_nama_matkul);
                        cv.put("semester",input_semester);
                        cv.put("sks",input_sks);
                        cv.put("dosen",input_dosen);
                        cv.put("nilai_akhir",0.0);
                        cv.put("grade","-");
                        cv.put("status",status_matkul);

                        if (et_nama_matkul.getText().toString().equals("")||et_semester.getText().toString()==null||et_sks.getText().toString()==null||et_dosen.getText().toString().equals("")){
                            if (t!=null){
                                t.cancel();
                            }
                            t = Toast.makeText(MataKuliah.this,"Silahkan Periksa Kembali Input yang Anda Masukkan!",Toast.LENGTH_SHORT);
                            t.show();
                        }else{
                            boolean matkul_available = true;
                            for(String list:listDaftarMK){
                                if (list.equalsIgnoreCase(input_nama_matkul)){
                                    if (t!=null){
                                        t.cancel();
                                    }
                                    t = Toast.makeText(MataKuliah.this,"Nama Mata Kuliah Sudah ada!",Toast.LENGTH_SHORT);
                                    t.show();
                                    matkul_available = false;
                                }
                            }
                            if (matkul_available){
                                try{
                                    db.insertOrThrow("mata_kuliah",null,cv);
                                    File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature"+File.separator+input_nama_matkul);
                                    directory.mkdirs();
                                    checkContent();
                                    gridView.invalidateViews();
                                    if (t!=null){
                                        t.cancel();
                                    }
                                    t = Toast.makeText(MataKuliah.this,"Berhasil Menambahkan Mata Kuliah Baru!",Toast.LENGTH_SHORT);
                                    t.show();
                                    ad.dismiss();
                                }catch (SQLException e){
                                    Log.e("Error",e.toString());
                                }
                            }
                        }
                        input_nama_matkul = "";
                        input_semester = 0;
                        input_sks = 0;
                        input_dosen = "";
                    }
                });

//                ContentValues contentValues = new ContentValues();
//                contentValues.put("nama_mata_kuliah","Test2");
//                contentValues.put("semester",6);
//                contentValues.put("sks",2);
//                contentValues.put("nilai_akhir",95.5);
//                contentValues.put("grade","A");
//                contentValues.put("status",true);
//                contentValues.put("update_at","20:18:18 20-4-2018");
//                try{
//                    db.insertOrThrow("mata_kuliah",null,contentValues);
//                    File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature"+File.separator+"Test2");
//                    directory.mkdirs();
//                }catch (SQLException e){
//                    Log.e("Error",e.toString());
//                }

                Log.d("Berhasil Input Data","Test 2 berhasil diinput!");
            }
        });
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
//                Toast.makeText(MataKuliah.this,semester_dropdown+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        Setting untuk menu pojok kanan atas
        options = menu;
        getMenuInflater().inflate(R.menu.side_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setupSpinner(Spinner spin){
        //wrap the items in the Adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.spinner_selected,dropdown);
        //assign adapter to the Spinner
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(adapter);
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

        if(statusSelected){
            menu.findItem(R.id.spinner).setVisible(false);
        }else{
            menu.findItem(R.id.spinner).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteMatkul();
                return true;
            case R.id.edit:
                editMatkul();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteMatkul(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MataKuliah.this);
        alert.setTitle("Warning");
        alert.setMessage("Apakah Anda ingin menghapus Mata Kuliah yang Telah Dipilih?");
        alert.setCancelable(false);
        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MataKuliah.this);
                alert.setTitle("Warning");
                alert.setMessage("Apakah Anda Ingin Menghapus Folder Catatan dari Mata Kuliah yang Telah Dipilih?");
                alert.setCancelable(false);
                alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        selected.clear();
                    }
                });
                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        for (String s:selected){
                            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Pixature/"+s);
                            Log.d("HAPUS",directory.getAbsolutePath());
                            try {
                                FileUtils.forceDelete(directory);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        selected.clear();
                    }
                });
                AlertDialog test = alert.show();
                for (String s:selected){
                    db.delete("mata_kuliah","nama_mata_kuliah='"+s+"'",null);
                }
                checkContent();
                gridView.invalidateViews();
                if (t!=null){
                    t.cancel();
                }
                t = Toast.makeText(MataKuliah.this,"Data Berhasil Dihapus",Toast.LENGTH_SHORT);
                t.show();
                statusSelected = false;
                getSupportActionBar().setTitle("Daftar Mata Kuliah");
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

    private void editMatkul(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MataKuliah.this);
        LayoutInflater inflater = MataKuliah.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tambah_matkul,null);
        alertDialog.setPositiveButton("Ubah",null);
        alertDialog.setNegativeButton("Batal",null);
        alertDialog.setView(dialogView);

        String query = "SELECT * FROM mata_kuliah where nama_mata_kuliah = '"+input_nama_matkul+"'";
        Cursor cr = db.rawQuery(query,null);
        cr.moveToFirst();
        int id = 0;
        id = cr.getInt(cr.getColumnIndex("id_mata_kuliah"));
        input_nama_matkul = cr.getString(cr.getColumnIndex("nama_mata_kuliah"));
        input_sks = cr.getInt(cr.getColumnIndex("sks"));
        input_semester = cr.getInt(cr.getColumnIndex("semester"));
        input_dosen = cr.getString(cr.getColumnIndex("dosen"));

        final AlertDialog ad = alertDialog.create();
        ad.setTitle("Tambah Mata Kuliah Baru");
        ad.show();

        final TextInputEditText et_nama_matkul = (TextInputEditText) ad.findViewById(R.id.input1);
        et_nama_matkul.setText(input_nama_matkul);

        final TextInputEditText et_sks = (TextInputEditText) ad.findViewById(R.id.input2);
        et_sks.setText(input_sks+"");
        et_sks.setFilters(new InputFilter[]{ new InputFilterMinMax(1,6)});

        final TextInputEditText et_semester = (TextInputEditText) ad.findViewById(R.id.input3);
        et_semester.setText(""+input_semester);
        et_semester.setFilters(new InputFilter[]{ new InputFilterMinMax(1,15)});

        final TextInputEditText et_dosen = (TextInputEditText) ad.findViewById(R.id.input4);
        et_dosen.setText(input_dosen);
        et_nama_matkul.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View arg0, int arg1, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)&&(arg1== KeyEvent.KEYCODE_ENTER)||(event.getAction() == KeyEvent.ACTION_DOWN)&&(arg1 ==KeyEvent.FLAG_EDITOR_ACTION))
                {
                    et_nama_matkul.clearFocus();
                    et_sks.requestFocus();
                    return true;
                }
//                Toast.makeText(MataKuliah.this, event.getAction()+"", Toast.LENGTH_SHORT).show();
                return false;
            }
        } );

        et_dosen.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View arg0, int arg1, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_DOWN)&&(arg1== KeyEvent.KEYCODE_ENTER))||((event.getAction() == KeyEvent.ACTION_DOWN)&&(arg1 ==KeyEvent.FLAG_EDITOR_ACTION)))
                {
                    ad.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                    return true;
                }
                return false;
            }
        } );

        final int finalId = id;
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("SEMESTER LOG",et_semester.getText().toString());

                if (et_nama_matkul.getText().toString().equals("")||et_semester.getText().toString()==null||et_sks.getText().toString()==null||et_dosen.getText().toString().equals("")){
                    if (t!=null){
                        t.cancel();
                    }
                    t = Toast.makeText(MataKuliah.this,"Silahkan Periksa Kembali Input yang Anda Masukkan!",Toast.LENGTH_SHORT);
                    t.show();
                }else{
                    ContentValues cv = new ContentValues();
                    cv.put("nama_mata_kuliah",et_nama_matkul.getText().toString());
                    cv.put("semester",Integer.parseInt(et_semester.getText().toString()));
                    cv.put("sks",Integer.parseInt(et_sks.getText().toString()));
                    cv.put("dosen",et_dosen.getText().toString());
                    try{
                        db.update("mata_kuliah",cv,"id_mata_kuliah = "+ finalId,null);

                        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature/");
                        File to = new File(directory,et_nama_matkul.getText().toString());
                        File folderlama = new File(directory,input_nama_matkul);
                        boolean sukses = folderlama.renameTo(to);
                        if (sukses){
                            System.out.println("Sukses!"+input_nama_matkul);
                        }else{
                            System.out.println("Gagal!");
                        }


//                        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature"+File.separator+input_nama_matkul);
//                        directory.mkdirs();
//
//                        File folderlama = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Pixature"+File.separator+nama_lama);
//
//                        File[] isi = folderlama.listFiles();
//
//                        for(File rename:isi){
//                            rename.renameTo(new File(Environment.getExternalStorageDirectory(),"Pixature"+File.separator+input_nama_matkul+File.separator+rename.getName()));
//                        }
//
//                        folderlama.delete();

                        checkContent();
                        gridView.invalidateViews();
                        for(DaftarMK mk :daftarMK){
                            if (mk.isChecked()){
                                mk.toogleChecked();
                            }
                        }
                        if (t!=null){
                            t.cancel();
                        }
                        t = Toast.makeText(MataKuliah.this,"Berhasil Mengubah Mata Kuliah Baru!",Toast.LENGTH_SHORT);
                        t.show();
                        ad.dismiss();
                        selected.clear();
                        getSupportActionBar().setTitle("Daftar Mata Kuliah");
                        statusSelected = false;
                        input_nama_matkul = "";
                        input_semester = 0;
                        input_sks = 0;
                        input_dosen = "";
                    }catch (SQLException e){
                        Log.e("Error",e.toString());
                    }
                }
            }
        });


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
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("nama_mata_kuliah","Test");
//        contentValues.put("semester",8);
//        contentValues.put("sks",3);
//        contentValues.put("nilai_akhir",95.5);
//        contentValues.put("grade","A");
//        contentValues.put("status",true);
//        contentValues.put("update_at","20:18:18 20-4-2018");
//
//        ContentValues contentValues2 = new ContentValues();
//        contentValues2.put("nama_mata_kuliah","Test");
//        contentValues2.put("semester",8);
//        contentValues2.put("sks",3);
//        contentValues2.put("nilai_akhir",95.5);
//        contentValues2.put("grade","A");
//        contentValues2.put("status",true);
//        contentValues2.put("update_at","20:18:18 20-4-2018");
//
//        db.insert("mata_kuliah",null,contentValues);
//        db.insert("mata_kuliah",null,contentValues2);

//        ContentValues contentValues2 = new ContentValues();
//        contentValues2.put("nama_mata_kuliah","Hai");


//        db.delete("mata_kuliah","id_mata_kuliah=1",null);
//        db.update("mata_kuliah",contentValues2,"id_mata_kuliah=2",null);

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
        listDaftarMK.clear();
        daftarMK.clear();

        query = "SELECT * FROM mata_kuliah ORDER BY nama_mata_kuliah ASC";
        Cursor cr = db.rawQuery(query,null);
        while(cr.moveToNext()){
            File folder = new File(directory, cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
            folder.mkdirs();
            listDaftarMK.add(cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
            DaftarMK mk = new DaftarMK(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("semester")),
                    cr.getInt(cr.getColumnIndex("sks")),cr.getString(cr.getColumnIndex("dosen")),cr.getString(cr.getColumnIndex("grade")),
                    cr.getFloat(cr.getColumnIndex("nilai_akhir")),cr.getInt(cr.getColumnIndex("status"))
                    );
            daftarMK.add(mk);
        }

        dropdown.clear();
        dropdown.add(0,"Semua");
        query = "SELECT DISTINCT semester from mata_kuliah ORDER BY semester ASC";
        cr = db.rawQuery(query,null);
        while (cr.moveToNext()){
            dropdown.add("Semester "+cr.getString(cr.getColumnIndex("semester")));
        }
        if (daftarMK.size()>0){
            warning.setVisibility(View.INVISIBLE);
        }else{
            warning.setVisibility(View.VISIBLE);
        }
        System.out.println(dropdown.toString());
        invalidateOptionsMenu();
        Log.d("daftarMK : ",listDaftarMK.toString());
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
        listDaftarMK.clear();
        daftarMK.clear();

        query = "SELECT * FROM mata_kuliah WHERE semester = ? ORDER BY nama_mata_kuliah ASC";
        Cursor cr = db.rawQuery(query,new String[]{semester+""});
        while(cr.moveToNext()){
            listDaftarMK.add(cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
            DaftarMK mk = new DaftarMK(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("semester")),
                    cr.getInt(cr.getColumnIndex("sks")),cr.getString(cr.getColumnIndex("dosen")),cr.getString(cr.getColumnIndex("grade")),
                    cr.getFloat(cr.getColumnIndex("nilai_akhir")),cr.getInt(cr.getColumnIndex("status"))
            );
            Log.d("Semester mk : ", String.valueOf(cr.getInt(cr.getColumnIndex("semester"))));
            daftarMK.add(mk);
        }
        gridView.invalidateViews();
        Log.d("daftarMK : ",semester+"");
        Log.d("daftarMK : ",listDaftarMK.toString());
    }

    private class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    @Override
    public void onBackPressed() {
        if (statusSelected){
            statusSelected = false;
            for (DaftarMK mk: daftarMK){
                if (mk.isChecked()){
                    mk.toogleChecked();
                }
            }
            selected.clear();
            getSupportActionBar().setTitle("Daftar Mata Kuliah");
            invalidateOptionsMenu();
            mkAdapter.notifyDataSetChanged();
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            Runtime.getRuntime().freeMemory();
            Runtime.getRuntime().gc();
            finish();
        }
    }
}
