package com.example.rog.mcpix;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class JadwalActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ArrayList<String> matkul = new ArrayList<>();
    private ArrayList<String> dropdown = new ArrayList<>();
    private ArrayList<Jadwal> jsenin = new ArrayList<>();
    private ArrayList<Jadwal> jselasa = new ArrayList<>();
    private ArrayList<Jadwal> jrabu = new ArrayList<>();
    private ArrayList<Jadwal> jkamis = new ArrayList<>();
    private ArrayList<Jadwal> jjumat = new ArrayList<>();
    private ArrayList<Jadwal> jsabtu = new ArrayList<>();
    private List<String> hari = Arrays.asList("Senin","Selasa","Rabu","Kamis","Jumat","Sabtu");
    private SQLiteDatabase db;
    private int input_id_matkul;
    private int input_id_jadwal;
    private int semester;
    private int semesterNow;
    private int input_hari;
    private String input_ruangan;
    private String dosen;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private String nama_matkul;
    private String hari_ini;
    private String mulai;
    private String selesai;
    private Spinner spinner;
    private LinearLayout senin;
    private LinearLayout selasa;
    private LinearLayout rabu;
    private LinearLayout kamis;
    private LinearLayout jumat;
    private LinearLayout sabtu;
    private Cursor cr;
    private Cursor cr2;
    private String query;
    private TextView wsenin;
    private TextView wselasa;
    private TextView wrabu;
    private TextView wkamis;
    private TextView wjumat;
    private TextView wsabtu;
    private LinearLayout.LayoutParams params;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = openOrCreateDatabase("pixature",MODE_PRIVATE,null);
        setContentView(R.layout.activity_jadwal);
        senin = (LinearLayout) findViewById(R.id.senin);
        wsenin = (TextView) findViewById(R.id.warning_senin);
        selasa = (LinearLayout) findViewById(R.id.selasa);
        wselasa = (TextView) findViewById(R.id.warning_selasa);
        rabu = (LinearLayout) findViewById(R.id.rabu);
        wrabu = (TextView) findViewById(R.id.warning_rabu);
        kamis = (LinearLayout) findViewById(R.id.kamis);
        wkamis = (TextView) findViewById(R.id.warning_kamis);
        jumat = (LinearLayout) findViewById(R.id.jumat);
        wjumat = (TextView) findViewById(R.id.warning_jumat);
        sabtu = (LinearLayout) findViewById(R.id.sabtu);
        wsabtu = (TextView) findViewById(R.id.warning_sabtu);

        query = "SELECT DISTINCT semester from mata_kuliah ORDER BY semester ASC";
        cr = db.rawQuery(query,null);
        dropdown.clear();
        while (cr.moveToNext()){
            dropdown.add("Semester "+cr.getString(cr.getColumnIndex("semester")));
        }
        cr.close();

        query = "SELECT * from jadwal ORDER BY id_jadwal ASC";
        cr = db.rawQuery(query,null);
        while (cr.moveToNext()){
            Log.d("ISI JADWAL",cr.getInt(cr.getColumnIndex("id_jadwal"))+"__"+cr.getInt(cr.getColumnIndex("id_mata_kuliah")));
        }

        if (dropdown.size()>0){
            semesterNow = Integer.parseInt(dropdown.get(dropdown.size()-1).substring(dropdown.get(dropdown.size()-1).indexOf(" ")+1));
        }else{
            semesterNow = 0;
        }

        checkSpinMatkul();
        CheckContent();

        fab = (FloatingActionButton) findViewById(R.id.addTime);

//        CheckContent();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(JadwalActivity.this);
                LayoutInflater inflater = JadwalActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.tambah_jadwal,null);
                alertDialog.setView(dialogView);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Tambah",null);
                alertDialog.setNegativeButton("Batal",null);
                final AlertDialog ad = alertDialog.create();
                ad.setTitle("Tambah Jadwal Baru");
                ad.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

                final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(JadwalActivity.this);
                View dialogView2 = inflater.inflate(R.layout.tambah_jadwal2,null);
                alertDialog2.setView(dialogView2);
                alertDialog2.setCancelable(false);
                alertDialog2.setPositiveButton("Tambah",null);
                alertDialog2.setNegativeButton("Batal",null);
                final AlertDialog ad2 = alertDialog2.create();
                ad2.setTitle("Tambah Jadwal Baru");
                ad2.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

                ad.show();

                final Spinner spin = (Spinner) ad.findViewById(R.id.spinMatkul);
                final Spinner spinHari = (Spinner) ad.findViewById(R.id.spinHari);
                final EditText et_ruangan = (EditText) ad.findViewById(R.id.input1);
                final TextView et_warning = (TextView) ad.findViewById(R.id.warning_folder);
                et_ruangan.clearFocus();

                ArrayAdapter<String> adapter=new ArrayAdapter<>(JadwalActivity.this,R.layout.spinner_selected2,matkul);
                ArrayAdapter<String> adapter2=new ArrayAdapter<>(JadwalActivity.this,R.layout.spinner_selected2,hari);

                //assign adapter to the Spinner
                adapter.setDropDownViewResource(R.layout.spinner_item2);
                adapter2.setDropDownViewResource(R.layout.spinner_item2);
                spinHari.setAdapter(adapter2);
                spin.setAdapter(adapter);

                ad.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DEDEDE"));
                ad.getButton(DialogInterface.BUTTON_POSITIVE).setClickable(false);
                ad.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });

                Button btn = (Button) ad.findViewById(R.id.btnNext);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (spin==null||spinHari.getSelectedItem().toString().equals("")||et_ruangan.getText().toString().equals("")){
                            et_warning.setVisibility(View.VISIBLE);
                        }else{
                            et_warning.setVisibility(View.INVISIBLE);
                            ad.dismiss();
                            ad2.show();
                            final TimePicker mulai = (TimePicker) ad2.findViewById(R.id.timePicker);
                            mulai.setIs24HourView(true);
                            final TimePicker selesai = (TimePicker) ad2.findViewById(R.id.timePicker2);
                            final TextView et_warning2 = (TextView) ad2.findViewById(R.id.warning_folder);
                            selesai.setIs24HourView(true);
                            Button btn2 = (Button) ad2.findViewById(R.id.btnPrev);
                            btn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ad2.dismiss();
                                    ad.show();
                                }
                            });
                            ad2.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    Date a = parseDate(mulai.getCurrentHour()+":"+mulai.getCurrentMinute());
                                    Date b = parseDate(selesai.getCurrentHour()+":"+selesai.getCurrentMinute());

                                    if (a.before(b)){
                                        String q = "SELECT id_mata_kuliah from mata_kuliah WHERE nama_mata_kuliah = ?";
                                        Cursor cursor = db.rawQuery(q,new String[]{spin.getSelectedItem().toString()});
                                        if(cursor.getCount() > 0) {
                                            cursor.moveToFirst();
                                            input_id_matkul = cursor.getInt(cursor.getColumnIndex("id_mata_kuliah"));
                                        }
                                        cursor.close();
                                        if (spinHari.getSelectedItem().toString().equals("Senin")){
                                            input_hari = 1;
                                        }else if(spinHari.getSelectedItem().toString().equals("Selasa")){
                                            input_hari = 2;
                                        }else if(spinHari.getSelectedItem().toString().equals("Rabu")){
                                            input_hari = 3;
                                        }else if(spinHari.getSelectedItem().toString().equals("Kamis")){
                                            input_hari = 4;
                                        }else if(spinHari.getSelectedItem().toString().equals("Jumat")){
                                            input_hari = 5;
                                        }else{
                                            input_hari = 6;
                                        }

                                        String minute ="";
                                        String hour = "";

                                        if (mulai.getCurrentMinute()<10){
                                            minute = "0"+mulai.getCurrentMinute();
                                        }else{
                                            minute = ""+mulai.getCurrentMinute();
                                        }

                                        if (mulai.getCurrentHour()<10){
                                            hour = "0"+mulai.getCurrentHour();
                                        }else{
                                            hour = ""+mulai.getCurrentHour();
                                        }

                                        String timeM =hour+":"+minute;

                                        if (selesai.getCurrentMinute()<10){
                                            minute = "0"+selesai.getCurrentMinute();
                                        }else{
                                            minute = ""+selesai.getCurrentMinute();
                                        }

                                        if (selesai.getCurrentHour()<10){
                                            hour = "0"+selesai.getCurrentHour();
                                        }else{
                                            hour = ""+selesai.getCurrentHour();
                                        }

                                        String timeS =hour+":"+minute;

                                        hour="";
                                        minute="";

                                        input_ruangan = et_ruangan.getText().toString();
                                        Log.d("Hasil",input_id_matkul+"__"+input_hari+"__"+input_ruangan+"__"+timeM+"__"+timeS);

                                        ContentValues cv = new ContentValues();
                                        cv.put("id_mata_kuliah",input_id_matkul);
                                        cv.put("hari",input_hari);
                                        cv.put("mulai",timeM);
                                        cv.put("selesai",timeS);
                                        cv.put("ruangan",input_ruangan);

                                        et_warning2.setVisibility(View.VISIBLE);
                                        db.insertOrThrow("jadwal",null,cv);
                                        ad2.dismiss();
                                        CheckContent();
                                        et_warning2.setVisibility(View.INVISIBLE);
                                    }else{
                                        et_warning2.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            ad2.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ad2.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    public class Jadwal{

        private int id_jadwal;
        private int semester;
        private String nama_matkul;
        private String ruangan;
        private String hari;
        private String mulai;
        private String selesai;
        private String dosen;
        private Date dmulai;
        private Date dselesai;

        public Jadwal(int id_jadwal, int semester, String nama_matkul, String ruangan, String hari, String mulai, String selesai,String dosen){
            this.id_jadwal = id_jadwal;
            this.semester = semester;
            this.nama_matkul = nama_matkul;
            this.ruangan = ruangan;
            this.hari = hari;
            this.mulai = mulai;
            this.selesai = selesai;
            this.dmulai = parseDate(mulai);
            this.dselesai = parseDate(selesai);
            this.dosen = dosen;
        }

        public int getIdJadwal(){
            return id_jadwal;
        }

        public String getNama(){
            return nama_matkul;
        }

        public String getRuangan(){
            return ruangan;
        }

        public String getHari(){
            return hari;
        }

        public String getMulai(){
            return mulai;
        }

        public String getSelesai(){
            return selesai;
        }

        public Date getDmulai(){
            return dmulai;
        }

        public Date getDselesai(){
            return dselesai;
        }

        public int getSemester(){return semester;}

        public String getDosen(){return dosen;}
    }

    public boolean onCreateOptionsMenu(Menu menu) {
//        Setting Untuk Dropdown Menu
        getMenuInflater().inflate(R.menu.spinner_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) item.getActionView();
        spinner.setPopupBackgroundResource(R.color.white);
        spinner.setDropDownVerticalOffset((int) (getSupportActionBar().getHeight() * 0.9));
        setupSpinner(spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semesterNow = Integer.parseInt(spinner.getSelectedItem().toString().substring(spinner.getSelectedItem().toString().indexOf(" ")+1));
                checkSpinMatkul();
                CheckContent();
                Log.d("SemesternOW",semesterNow+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void setupSpinner(Spinner spin){
        //wrap the items in the Adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.spinner_selected,dropdown);
        //assign adapter to the Spinner
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(adapter);
        spin.setSelection(dropdown.size()-1);
    }

    private Date parseDate(String date) {

        try {
            return dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private void CheckContent(){

        senin.removeAllViews();
        selasa.removeAllViews();
        rabu.removeAllViews();
        kamis.removeAllViews();
        jumat.removeAllViews();
        sabtu.removeAllViews();
        jsenin.clear();
        jselasa.clear();
        jrabu.clear();
        jkamis.clear();
        jjumat.clear();
        jsabtu.clear();

        query = "SELECT * FROM jadwal ORDER BY id_jadwal ASC";
        cr = db.rawQuery(query,null);

        while (cr.moveToNext()){
            query = "SELECT nama_mata_kuliah,semester,dosen from mata_kuliah WHERE id_mata_kuliah =?";
            cr2 = db.rawQuery(query,new String[]{cr.getString(cr.getColumnIndex("id_mata_kuliah"))});
            if(cr2.getCount() > 0) {
                cr2.moveToFirst();
                nama_matkul = cr2.getString(cr2.getColumnIndex("nama_mata_kuliah"));
                semester = cr2.getInt(cr2.getColumnIndex("semester"));
                dosen = cr2.getString(cr2.getColumnIndex("dosen"));
            }
            cr2.close();

            if (cr.getInt(cr.getColumnIndex("hari"))==1){
                hari_ini = "Senin";
            }else if(cr.getInt(cr.getColumnIndex("hari"))==2){
                hari_ini = "Selasa";
            }else if(cr.getInt(cr.getColumnIndex("hari"))==3){
                hari_ini = "Rabu";
            }else if(cr.getInt(cr.getColumnIndex("hari"))==4){
                hari_ini = "Kamis";
            }else if(cr.getInt(cr.getColumnIndex("hari"))==5){
                hari_ini = "Jumat";
            }else{
                hari_ini = "Sabtu";
            }

            input_ruangan = cr.getString(cr.getColumnIndex("ruangan"));
            mulai = cr.getString(cr.getColumnIndex("mulai"));
            selesai = cr.getString(cr.getColumnIndex("selesai"));
            input_id_jadwal =cr.getInt(cr.getColumnIndex("id_jadwal"));

            Jadwal j = new Jadwal(input_id_jadwal,semester,nama_matkul,input_ruangan, hari_ini,mulai,selesai,dosen);

            if (j.getHari().equals("Senin")){
                jsenin.add(j);
            }else if(j.getHari().equals("Selasa")){
                jselasa.add(j);
            }else if(j.getHari().equals("Rabu")){
                jrabu.add(j);
            }else if(j.getHari().equals("Kamis")){
                jkamis.add(j);
            }else if(j.getHari().equals("Jumat")){
                jjumat.add(j);
            }else{
                jsabtu.add(j);
            }

            Log.d("Jadwal",nama_matkul+"__"+hari_ini+"__"+cr.getString(cr.getColumnIndex("mulai"))+"__"+cr.getString(cr.getColumnIndex("selesai"))+"__"+cr.getString(cr.getColumnIndex("ruangan")));
        }
        cr.close();

        input_id_jadwal = 0;
        input_id_matkul = 0;
        semester = 0;
        nama_matkul = "";
        input_ruangan = "";
        hari_ini = "";
        mulai = "";
        selesai = "";
        dosen="";

        if (jsenin.size()>0){
            Collections.sort(jsenin, new Comparator<Jadwal>() {
                @Override
                public int compare(Jadwal o1, Jadwal o2) {
                    if (o1.getDmulai().before(o2.getDmulai())){
                        return -1;
                    }else if(o1.getDmulai().after(o2.getDmulai())){
                        return 1;
                    }else{
                        return 0;
                    }
                }
            });

            for (Jadwal jadwal:jsenin){
                if (jadwal.getSemester()==semesterNow&&semesterNow>0){
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 50;
                    params.rightMargin = 50;
                    params.topMargin = 50;
                    params.bottomMargin = 50;
                    View view = convertView(jadwal);
                    senin.addView(view,params);
                }
                Log.d("JJ",jadwal.getMulai());
            }
        }

        if (senin.getChildCount()>0){
            wsenin.setVisibility(View.GONE);
        }else{
            wsenin.setVisibility(View.VISIBLE);
        }

        if (jselasa.size()>0){
            Collections.sort(jselasa, new Comparator<Jadwal>() {
                @Override
                public int compare(Jadwal o1, Jadwal o2) {
                    if (o1.getDmulai().before(o2.getDmulai())){
                        return -1;
                    }else if(o1.getDmulai().after(o2.getDmulai())){
                        return 1;
                    }else{
                        return 0;
                    }

                }
            });

            for (Jadwal jadwal:jselasa){
                if (jadwal.getSemester()==semesterNow&&semesterNow>0){
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 50;
                    params.rightMargin = 50;
                    params.topMargin = 50;
                    params.bottomMargin = 50;
                    View view = convertView(jadwal);
                    selasa.addView(view,params);
                }
                Log.d("JJ",jadwal.getMulai());
            }
        }

        if (selasa.getChildCount()>0){
            wselasa.setVisibility(View.GONE);
        }else{
            wselasa.setVisibility(View.VISIBLE);
        }

        if (jrabu.size()>0){
            Collections.sort(jrabu, new Comparator<Jadwal>() {
                @Override
                public int compare(Jadwal o1, Jadwal o2) {
                    if (o1.getDmulai().before(o2.getDmulai())){
                        return -1;
                    }else if(o1.getDmulai().after(o2.getDmulai())){
                        return 1;
                    }else{
                        return 0;
                    }

                }
            });

            for (Jadwal jadwal:jrabu){
                if (jadwal.getSemester()==semesterNow&&semesterNow>0){
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 50;
                    params.rightMargin = 50;
                    params.topMargin = 50;
                    params.bottomMargin = 50;
                    View view = convertView(jadwal);
                    rabu.addView(view,params);
                }
                Log.d("JJ",jadwal.getMulai());
            }
        }

        if (rabu.getChildCount()>0){
            wrabu.setVisibility(View.GONE);
        }else{
            wrabu.setVisibility(View.VISIBLE);
        }

        if (jkamis.size()>0){
            Collections.sort(jkamis, new Comparator<Jadwal>() {
                @Override
                public int compare(Jadwal o1, Jadwal o2) {
                    if (o1.getDmulai().before(o2.getDmulai())){
                        return -1;
                    }else if(o1.getDmulai().after(o2.getDmulai())){
                        return 1;
                    }else{
                        return 0;
                    }

                }
            });

            for (Jadwal jadwal:jkamis){
                if (jadwal.getSemester()==semesterNow&&semesterNow>0){
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 50;
                    params.rightMargin = 50;
                    params.topMargin = 50;
                    params.bottomMargin = 50;
                    View view = convertView(jadwal);
                    kamis.addView(view,params);
                }
                Log.d("JJ",jadwal.getMulai());
            }
        }

        if (kamis.getChildCount()>0){
            wkamis.setVisibility(View.GONE);
        }else{
            wkamis.setVisibility(View.VISIBLE);
        }

        if (jjumat.size()>0){
            Collections.sort(jjumat, new Comparator<Jadwal>() {
                @Override
                public int compare(Jadwal o1, Jadwal o2) {
                    if (o1.getDmulai().before(o2.getDmulai())){
                        return -1;
                    }else if(o1.getDmulai().after(o2.getDmulai())){
                        return 1;
                    }else{
                        return 0;
                    }

                }
            });

            for (Jadwal jadwal:jjumat){
                if (jadwal.getSemester()==semesterNow&&semesterNow>0){
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 50;
                    params.rightMargin = 50;
                    params.topMargin = 50;
                    params.bottomMargin = 50;
                    View view = convertView(jadwal);
                    jumat.addView(view,params);
                }
                Log.d("JJ",jadwal.getMulai());
            }
        }

        if (jumat.getChildCount()>0){
            wjumat.setVisibility(View.GONE);
        }else{
            wjumat.setVisibility(View.VISIBLE);
        }

        if (jsabtu.size()>0){
            Collections.sort(jsabtu, new Comparator<Jadwal>() {
                @Override
                public int compare(Jadwal o1, Jadwal o2) {
                    if (o1.getDmulai().before(o2.getDmulai())){
                        return -1;
                    }else if(o1.getDmulai().after(o2.getDmulai())){
                        return 1;
                    }else{
                        return 0;
                    }

                }
            });

            for (Jadwal jadwal:jsabtu){
                if (jadwal.getSemester()==semesterNow&&semesterNow>0){
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 50;
                    params.rightMargin = 50;
                    params.topMargin = 50;
                    params.bottomMargin = 50;
                    View view = convertView(jadwal);
                    sabtu.addView(view,params);
                }
                Log.d("JJ",jadwal.getMulai());
            }
        }

        if (sabtu.getChildCount()>0){
            wsabtu.setVisibility(View.GONE);
        }else{
            wsabtu.setVisibility(View.VISIBLE);
        }
    }

    public View convertView(final Jadwal jadwal){
        View view = getLayoutInflater().inflate(R.layout.jadwal_item,null);
        final Button btnSenin = (Button) view.findViewById(R.id.btnMore);
        TextView nama_matkul = (TextView) view.findViewById(R.id.nama_matkul);
        TextView waktu = (TextView) view.findViewById(R.id.waktu);
        TextView ruangan = (TextView) view.findViewById(R.id.ruangan);
        TextView dosen = (TextView) view.findViewById(R.id.dosen);
        nama_matkul.setText(jadwal.getNama());
        waktu.setText(jadwal.getMulai()+" - "+jadwal.getSelesai());
        ruangan.setText(jadwal.getRuangan());
        dosen.setText(jadwal.getDosen());
        btnSenin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(JadwalActivity.this,btnSenin);
                pop.getMenuInflater().inflate(R.menu.side_menu,pop.getMenu());

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                deleteJadwal(jadwal.getNama(),jadwal.getIdJadwal());
                                return true;
                            case R.id.edit:
                                editJadwal(jadwal.getIdJadwal());
                                return true;
                        }
                        return false;
                    }
                });
                pop.show();
            }
        });

        return view;
    }

    private void editJadwal(final int jadwalId){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(JadwalActivity.this);
        LayoutInflater inflater = JadwalActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tambah_jadwal,null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Edit",null);
        alertDialog.setNegativeButton("Batal",null);
        final AlertDialog ad = alertDialog.create();
        ad.setTitle("Edit Jadwal");
        ad.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        String[] mulaiT = new String[2];
        String[] selesaiT = new String[2];
        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(JadwalActivity.this);
        View dialogView2 = inflater.inflate(R.layout.tambah_jadwal2,null);
        alertDialog2.setView(dialogView2);
        alertDialog2.setCancelable(false);
        alertDialog2.setPositiveButton("Edit",null);
        alertDialog2.setNegativeButton("Batal",null);
        final AlertDialog ad2 = alertDialog2.create();
        ad2.setTitle("Edit Jadwal");
        ad2.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        ad.show();
        int posMatkul = 0;

        query = "SELECT jadwal.*,mata_kuliah.nama_mata_kuliah from jadwal JOIN mata_kuliah ON jadwal.id_mata_kuliah=mata_kuliah.id_mata_kuliah WHERE jadwal.id_jadwal =?";
        cr2 = db.rawQuery(query,new String[]{jadwalId+""});
        if(cr2.getCount() > 0) {
            cr2.moveToFirst();
            nama_matkul = cr2.getString(cr2.getColumnIndex("nama_mata_kuliah"));
            input_hari = cr2.getInt(cr2.getColumnIndex("hari"));
            input_ruangan = cr2.getString(cr2.getColumnIndex("ruangan"));
            mulaiT = cr2.getString(cr2.getColumnIndex("mulai")).split(":");
            selesaiT = cr2.getString(cr2.getColumnIndex("selesai")).split(":");
        }
        Log.d("Ruangan",cr2.getString(cr2.getColumnIndex("ruangan")));
        cr2.close();

        for (int i=0;i<matkul.size();i++){
            if (matkul.get(i).equals(nama_matkul)){
                posMatkul = i;
            }
        }

        final Spinner spin = (Spinner) ad.findViewById(R.id.spinMatkul);
        final Spinner spinHari = (Spinner) ad.findViewById(R.id.spinHari);
        final EditText et_ruangan = (EditText) ad.findViewById(R.id.input1);
        final TextView et_warning = (TextView) ad.findViewById(R.id.warning_folder);
        et_ruangan.clearFocus();


        ArrayAdapter<String> adapter=new ArrayAdapter<>(JadwalActivity.this,R.layout.spinner_selected2,matkul);
        ArrayAdapter<String> adapter2=new ArrayAdapter<>(JadwalActivity.this,R.layout.spinner_selected2,hari);

        //assign adapter to the Spinner
        adapter.setDropDownViewResource(R.layout.spinner_item2);
        adapter2.setDropDownViewResource(R.layout.spinner_item2);
        spinHari.setAdapter(adapter2);
        spin.setAdapter(adapter);

        spinHari.setSelection(input_hari-1);
        spin.setSelection(posMatkul);
        et_ruangan.setText(input_ruangan);

        ad.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DEDEDE"));
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setClickable(false);
        ad.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        Button btn = (Button) ad.findViewById(R.id.btnNext);
        final String[] finalMulaiT = mulaiT;
        final String[] finalSelesaiT = selesaiT;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spin==null||spinHari.getSelectedItem().toString().equals("")||et_ruangan.getText().toString().equals("")){
                    et_warning.setVisibility(View.VISIBLE);
                }else{
                    et_warning.setVisibility(View.INVISIBLE);
                    ad.dismiss();
                    ad2.show();
                    final TimePicker mulai = (TimePicker) ad2.findViewById(R.id.timePicker);
                    mulai.setIs24HourView(true);
                    mulai.setCurrentHour(Integer.parseInt(finalMulaiT[0]));
                    mulai.setCurrentMinute(Integer.parseInt(finalMulaiT[1]));
                    final TimePicker selesai = (TimePicker) ad2.findViewById(R.id.timePicker2);
                    selesai.setIs24HourView(true);
                    selesai.setCurrentHour(Integer.parseInt(finalSelesaiT[0]));
                    selesai.setCurrentMinute(Integer.parseInt(finalSelesaiT[1]));
                    final TextView et_warning2 = (TextView) ad2.findViewById(R.id.warning_folder);
                    Button btn2 = (Button) ad2.findViewById(R.id.btnPrev);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad2.dismiss();
                            ad.show();
                        }
                    });
                    ad2.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Date a = parseDate(mulai.getCurrentHour()+":"+mulai.getCurrentMinute());
                            Date b = parseDate(selesai.getCurrentHour()+":"+selesai.getCurrentMinute());

                            if (a.before(b)){
                                String q = "SELECT id_mata_kuliah from mata_kuliah WHERE nama_mata_kuliah = ?";
                                Cursor cursor = db.rawQuery(q,new String[]{spin.getSelectedItem().toString()});
                                if(cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    input_id_matkul = cursor.getInt(cursor.getColumnIndex("id_mata_kuliah"));
                                }
                                cursor.close();
                                if (spinHari.getSelectedItem().toString().equals("Senin")){
                                    input_hari = 1;
                                }else if(spinHari.getSelectedItem().toString().equals("Selasa")){
                                    input_hari = 2;
                                }else if(spinHari.getSelectedItem().toString().equals("Rabu")){
                                    input_hari = 3;
                                }else if(spinHari.getSelectedItem().toString().equals("Kamis")){
                                    input_hari = 4;
                                }else if(spinHari.getSelectedItem().toString().equals("Jumat")){
                                    input_hari = 5;
                                }else{
                                    input_hari = 6;
                                }

                                String minute ="";
                                String hour = "";

                                if (mulai.getCurrentMinute()<10){
                                    minute = "0"+mulai.getCurrentMinute();
                                }else{
                                    minute = ""+mulai.getCurrentMinute();
                                }

                                if (mulai.getCurrentHour()<10){
                                    hour = "0"+mulai.getCurrentHour();
                                }else{
                                    hour = ""+mulai.getCurrentHour();
                                }

                                String timeM =hour+":"+minute;

                                if (selesai.getCurrentMinute()<10){
                                    minute = "0"+selesai.getCurrentMinute();
                                }else{
                                    minute = ""+selesai.getCurrentMinute();
                                }

                                if (selesai.getCurrentHour()<10){
                                    hour = "0"+selesai.getCurrentHour();
                                }else{
                                    hour = ""+selesai.getCurrentHour();
                                }

                                String timeS =hour+":"+minute;

                                hour="";
                                minute="";

                                input_ruangan = et_ruangan.getText().toString();
                                Log.d("Hasil",input_id_matkul+"__"+input_hari+"__"+input_ruangan+"__"+timeM+"__"+timeS);

                                ContentValues cv = new ContentValues();
                                cv.put("id_mata_kuliah",input_id_matkul);
                                cv.put("hari",input_hari);
                                cv.put("mulai",timeM);
                                cv.put("selesai",timeS);
                                cv.put("ruangan",input_ruangan);

                                et_warning2.setVisibility(View.VISIBLE);
                                db.update("jadwal",cv,"id_jadwal = "+ jadwalId,null);
                                ad2.dismiss();
                                CheckContent();
                                et_warning2.setVisibility(View.INVISIBLE);
                            }else{
                                et_warning2.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    ad2.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad2.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void deleteJadwal(String nama_matkul, final int jadwal_id){
        final AlertDialog.Builder alert = new AlertDialog.Builder(JadwalActivity.this);
        alert.setTitle("Warning");
        alert.setMessage("Apakah Anda ingin menghapus jadwal "+nama_matkul+" yang Telah Dipilih?");
        alert.setCancelable(false);
        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.delete("jadwal","id_jadwal="+jadwal_id,null);
                Toast.makeText(JadwalActivity.this,"Berhasil Menghapus Jadwal!", Toast.LENGTH_SHORT).show();
                CheckContent();
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

    private void checkSpinMatkul(){
        matkul.clear();
        String query = "SELECT nama_mata_kuliah FROM mata_kuliah WHERE semester =? ORDER BY nama_mata_kuliah ASC";
        cr = db.rawQuery(query,new String[]{semesterNow+""});
        while(cr.moveToNext()) {
            matkul.add(cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
        }
        cr.close();
    }

    @Override
    public void onBackPressed() {
        db.close();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
        finish();
    }
}
