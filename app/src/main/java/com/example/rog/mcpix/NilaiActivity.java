package com.example.rog.mcpix;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.rambler.libs.swipe_layout.SwipeLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NilaiActivity extends AppCompatActivity {
    private Spinner spinner;
    private List<String> jenisd = Arrays.asList("KK","KB","UAS");
    private SQLiteDatabase db;
    private ArrayList<String> dropdown = new ArrayList<>();
    private ArrayList<Nilai> matkul = new ArrayList<>();
    private ArrayList<String> collapse = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private int input_id_matkul;
    private Float input_nilai;
    private LinearLayout rl;
    private int input_jenis2;
    private int semesterNow=0;
    private Float bobotKK = Float.parseFloat("0");
    private Float bobotKB = Float.parseFloat("0");
    private Float bobotUAS = Float.parseFloat("0");
    private float naKK = 0;
    private float naKB = 0;
    private float naUAS = 0;
    private float totalNilai = 0;
    private float fixKK = 0;
    private float fixKB = 0;
    private float fixUAS = 0;
    private int id = 0;
    private String grade = "";
    private int status = 0;
    private String input_date="";
    private TextView warning_txt;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nilai);
        db = openOrCreateDatabase("pixature",MODE_PRIVATE,null);
        String query = "SELECT DISTINCT semester from mata_kuliah ORDER BY semester ASC";
        Cursor cr = db.rawQuery(query,null);
        dropdown.clear();
        while (cr.moveToNext()){
            dropdown.add("Semester "+cr.getString(cr.getColumnIndex("semester")));
        }
        dropdown.add(0,"Semua");
        cr.close();

        query = "SELECT nama_mata_kuliah,id_mata_kuliah FROM mata_kuliah ORDER BY nama_mata_kuliah ASC";
        cr = db.rawQuery(query,null);
        while (cr.moveToNext()){
            Nilai n = new Nilai(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("id_mata_kuliah")));
            matkul.add(n);
        }
        cr.close();

        query = "SELECT * FROM nilai ORDER BY tgl_input ASC";
        cr = db.rawQuery(query,null);
        while (cr.moveToNext()){
            Log.d("NILAI",cr.getInt(cr.getColumnIndex("id_nilai"))+"__"+cr.getInt(cr.getColumnIndex("id_mata_kuliah"))+"__"+cr.getString(cr.getColumnIndex("tgl_input"))+"__"+cr.getFloat(cr.getColumnIndex("nilai"))+"__"+cr.getInt(cr.getColumnIndex("jenis")));
        }
        cr.close();

        query = "SELECT * FROM bobot_nilai";
        cr = db.rawQuery(query,null);
        while (cr.moveToNext()){
            Log.d("BOBOT",cr.getString(cr.getColumnIndex("id_bobot"))+"__"+cr.getInt(cr.getColumnIndex("jenis_bobot"))+"__"+cr.getInt(cr.getColumnIndex("besar_bobot")));
            if (cr.getString(cr.getColumnIndex("id_bobot")).equals("KK")){
                bobotKK = cr.getFloat(cr.getColumnIndex("besar_bobot"));
            }else if(cr.getString(cr.getColumnIndex("id_bobot")).equals("KB")){
                bobotKB = cr.getFloat(cr.getColumnIndex("besar_bobot"));
            }else{
                bobotUAS = cr.getFloat(cr.getColumnIndex("besar_bobot"));
            }
        }
        cr.close();

        rl = (LinearLayout) findViewById(R.id.rootL);

        refreshView();
        warning_txt = (TextView) findViewById(R.id.warning_text);
        if (matkul.size()<=0){
            warning_txt.setVisibility(View.VISIBLE);
        }else{
            warning_txt.setVisibility(View.INVISIBLE);
        }
    }

    public void refreshView(){
        for (String s:collapse){
            Log.d("COL",s);
        }
        rl.removeAllViews();
        matkul.clear();

        String query="";
        Cursor cr;
        if (semesterNow==0){
            query = "SELECT nama_mata_kuliah,id_mata_kuliah FROM mata_kuliah ORDER BY nama_mata_kuliah ASC";
            cr = db.rawQuery(query,null);
        }else{
            query = "SELECT nama_mata_kuliah,id_mata_kuliah FROM mata_kuliah WHERE semester =? ORDER BY nama_mata_kuliah ASC";
            cr = db.rawQuery(query,new String[]{semesterNow+""});
        }

        while (cr.moveToNext()){
            Nilai n = new Nilai(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("id_mata_kuliah")));
            matkul.add(n);
        }
        cr.close();
        for (final Nilai m:matkul){
            View view = getLayoutInflater().inflate(R.layout.item_nilai,null);
            Button btnExp = (Button) view.findViewById(R.id.exp);
            final ExpandableRelativeLayout exl = (ExpandableRelativeLayout) view.findViewById(R.id.exl);
            TextView nilai_akhir = (TextView) view.findViewById(R.id.nilai_akhir);
            TextView grade = (TextView) view.findViewById(R.id.grade);
            final Button add_nilai = (Button) view.findViewById(R.id.add_nilai);
            RelativeLayout j = (RelativeLayout) view.findViewById(R.id.j);

//            exl.setExpanded(true);
//            if (exl.isExpanded()){
//                Log.d("HAI1","1");
//                exl.toggle();
//            }else{
//                exl.expand();
//                Log.d("HAI2","1");
//            }
//            if (rl.getChildCount()==0){
//                Log.d("TAS",1+"");
//                if (collapse.contains(m.getMatkul())){
//                    exl.collapse();
//                }else{
//                    exl.expand();
//                }
//            }else {
//                if (exl.isExpanded()){
//                    exl.toggle();
//                }else{
//                    exl.toggle();
//                }
//                Log.d("TAS",3+"");
//            }

            add_nilai.setTag(m.getId());
            add_nilai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NilaiActivity.this);
                    LayoutInflater inflater = NilaiActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.tambah_nilai,null);
                    alertDialog.setView(dialogView);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Tambah",null);
                    alertDialog.setNegativeButton("Batal",null);
                    final AlertDialog ad = alertDialog.create();
                    ad.setTitle("Tambah Nilai Baru");
                    ad.show();
                    input_id_matkul = (int) v.getTag();
                    final TextView et_warning = (TextView) ad.findViewById(R.id.warning_folder);
                    final Spinner spinJenis = (Spinner) ad.findViewById(R.id.spinJenis);
                    ArrayAdapter<String> adapter=new ArrayAdapter<>(NilaiActivity.this,R.layout.spinner_selected2,jenisd);
                    adapter.setDropDownViewResource(R.layout.spinner_item2);
                    spinJenis.setAdapter(adapter);
                    final TextInputEditText nilai = (TextInputEditText) ad.findViewById(R.id.input1);
                    nilai.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 100), new DecimalDigitsInputFilter(3,2)});
                    final TextInputEditText tgl = (TextInputEditText) ad.findViewById(R.id.date);
                    final Calendar myCalendar = Calendar.getInstance();
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, month);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            tgl.setText(dateFormat.format(myCalendar.getTime()));
                        }
                    };

                    tgl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Date dnow = new Date();
                            DatePickerDialog dialog =  new DatePickerDialog(NilaiActivity.this, date,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                            dialog.getDatePicker().setMaxDate(new Date().getTime());
                            dialog.show();
                        }
                    });

                    ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("NILAI",nilai.getText().toString());
                            Log.d("NILAI",spinJenis.getSelectedItem().toString());
                            if (!spinJenis.getSelectedItem().toString().equals("")&&!nilai.getText().toString().equals("")&&!tgl.getText().toString().equals("")){
                                if (spinJenis.getSelectedItem().toString().equals("KK")){
                                    input_jenis2=1;
                                }else if(spinJenis.getSelectedItem().toString().equals("KB")){
                                    input_jenis2=2;
                                }else{
                                    input_jenis2=3;
                                }
                                input_date = tgl.getText().toString();
                                input_nilai = Float.parseFloat(nilai.getText().toString());
                                ContentValues cv = new ContentValues();
                                cv.put("id_mata_kuliah",input_id_matkul);
                                cv.put("nilai",input_nilai);
                                cv.put("jenis",input_jenis2);
                                cv.put("tgl_input",input_date);
                                et_warning.setVisibility(View.INVISIBLE);
                                db.insertOrThrow("nilai",null,cv);
                                ad.dismiss();
                                hitung(m);
                                refreshView();
                            }else{
                                et_warning.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    ad.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad.dismiss();
                        }
                    });
                }
            });

            nilai_akhir.setText(m.getNilai().toString());
            grade.setText(m.getGrade());
            btnExp.setText(m.getMatkul());
            btnExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (exl.isExpanded()){
                        collapse.remove(m.getMatkul());
                    }else{
                        collapse.add(m.getMatkul());
                    }
                    exl.toggle();
                }
            });

            ArrayMap<Integer,String> kk = m.getKk();
            ArrayMap<Integer,String> kb = m.getKb();
            ArrayMap<Integer,String> uas = m.getUas();
            Log.d("NILAIKK",kk.size()+"");
            Log.d("NILAIKB",kk.size()+"");
            Log.d("NILAIUAS",kk.size()+"");


            int prevTextViewId = 0;
            if (kk.size()>0){
                for(int i = 0;i<kk.size();i++){
                    View child = getLayoutInflater().inflate(R.layout.sub2, null);
                    final SwipeLayout sp = (SwipeLayout) child.findViewById(R.id.sw);
                    TextView tgl = (TextView) child.findViewById(R.id.tgl);
                    final TextView jenis = (TextView) child.findViewById(R.id.middle);
                    TextView nilai = (TextView) child.findViewById(R.id.nilai);
                    Button edit = (Button) child.findViewById(R.id.edit);
                    Button delete = (Button) child.findViewById(R.id.delete);
                    delete.setTag(kk.keyAt(i));
                    final List<String> item = Arrays.asList(kk.valueAt(i).split("_"));
                    tgl.setText(item.get(0));
                    nilai.setText(item.get(1));
                    jenis.setText("KK "+(i+1));
                    sp.setTag(kk.keyAt(i));

                    edit.setTag(kk.keyAt(i));
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NilaiActivity.this);
                            LayoutInflater inflater = NilaiActivity.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.tambah_nilai,null);
                            alertDialog.setView(dialogView);
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Ubah",null);
                            alertDialog.setNegativeButton("Batal",null);
                            final AlertDialog ad = alertDialog.create();
                            ad.setTitle("Ubah Nilai");
                            ad.show();
                            final int id_nilai = (int) v.getTag();
                            final TextView et_warning = (TextView) ad.findViewById(R.id.warning_folder);
                            final Spinner spinJenis = (Spinner) ad.findViewById(R.id.spinJenis);
                            ArrayAdapter<String> adapter=new ArrayAdapter<>(NilaiActivity.this,R.layout.spinner_selected2,jenisd);
                            adapter.setDropDownViewResource(R.layout.spinner_item2);
                            spinJenis.setAdapter(adapter);
                            final TextInputEditText nilai = (TextInputEditText) ad.findViewById(R.id.input1);
                            nilai.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 100), new DecimalDigitsInputFilter(3,2)});
                            nilai.setText(item.get(1));
                            String[] jenisNilai = jenis.getText().toString().split(" ");
                            final String[] dateinput = item.get(0).split("/");
                            if(jenisNilai[0].equals("KK")){
                                spinJenis.setSelection(0);
                            }else if(jenisNilai[0].equals("KB")){
                                spinJenis.setSelection(1);
                            }else{
                                spinJenis.setSelection(2);
                            }

                            final TextInputEditText tgl = (TextInputEditText) ad.findViewById(R.id.date);
                            tgl.setText(item.get(0));

                            Log.d("tanggal input",item.get(0));
                            final Calendar myCalendar = Calendar.getInstance();
                            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, month);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    tgl.setText(dateFormat.format(myCalendar.getTime()));
                                }
                            };


                            tgl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatePickerDialog dialog =  new DatePickerDialog(NilaiActivity.this, date,Integer.parseInt(dateinput[2]),Integer.parseInt(dateinput[1]),Integer.parseInt(dateinput[0]));
                                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                                    dialog.show();
                                }
                            });

                            ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("NILAI",nilai.getText().toString());
                                    Log.d("NILAI",spinJenis.getSelectedItem().toString());
                                    if (!spinJenis.getSelectedItem().toString().equals("")&&!nilai.getText().toString().equals("")&&!tgl.getText().toString().equals("")){
                                        if (spinJenis.getSelectedItem().toString().equals("KK")){
                                            input_jenis2=1;
                                        }else if(spinJenis.getSelectedItem().toString().equals("KB")){
                                            input_jenis2=2;
                                        }else{
                                            input_jenis2=3;
                                        }
                                        input_date = tgl.getText().toString();
                                        input_nilai = Float.parseFloat(nilai.getText().toString());
                                        ContentValues cv = new ContentValues();
                                        cv.put("nilai",input_nilai);
                                        cv.put("jenis",input_jenis2);
                                        cv.put("tgl_input",input_date);
                                        et_warning.setVisibility(View.INVISIBLE);
                                        db.update("nilai",cv,"id_nilai = "+ id_nilai,null);
                                        ad.dismiss();
                                        refreshView();
                                        hitung(m);
                                        Toast.makeText(NilaiActivity.this,"Berhasil Mengubah Nilai",Toast.LENGTH_SHORT).show();
                                    }else{
                                        et_warning.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            ad.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ad.dismiss();
                                }
                            });
                            Log.d("Edit",(int) v.getTag()+"");
                            sp.reset();
                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(NilaiActivity.this);
                            alert.setTitle("Warning");
                            alert.setMessage("Apakah Anda ingin menghapus Mata Kuliah yang Telah Dipilih?");
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
                                    db.delete("nilai","id_nilai="+(int) v.getTag(),null);
                                    hitung(m);
                                    refreshView();
                                    Toast.makeText(NilaiActivity.this,"Berhasil Menghapus Nilai",Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog test = alert.show();
                            sp.reset();
                        }
                    });
                    Log.d("KK",kk.valueAt(i));


                    sp.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                        int status = 0;
                        @Override
                        public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                        }

                        @Override
                        public void onSwipeClampReached(final SwipeLayout swipeLayout, boolean moveToRight) {
                            if (status==1){
                                LinearLayout fm = (LinearLayout) swipeLayout.findViewById(R.id.left);
                                fm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        swipeLayout.reset();
                                    }
                                });
                                status=0;
                            }else if (status==2){
                                LinearLayout ll = (LinearLayout) swipeLayout.findViewById(R.id.right);
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        swipeLayout.reset();
                                    }
                                });
                                status=0;
                            }
                        }

                        @Override
                        public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                            status=1;
                        }

                        @Override
                        public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                            status=2;
                        }
                    });
                    int curTextViewId = prevTextViewId + 1;
                    child.setId(curTextViewId);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW,prevTextViewId);
                    j.addView(child,params);
                    prevTextViewId = curTextViewId;
                }
            }

            if (kb.size()>0){
                for(int i = 0;i<kb.size();i++){
                    View child = getLayoutInflater().inflate(R.layout.sub2, null);
                    final SwipeLayout sp = (SwipeLayout) child.findViewById(R.id.sw);
                    TextView tgl = (TextView) child.findViewById(R.id.tgl);
                    final TextView jenis = (TextView) child.findViewById(R.id.middle);
                    TextView nilai = (TextView) child.findViewById(R.id.nilai);
                    Button edit = (Button) child.findViewById(R.id.edit);
                    Button delete = (Button) child.findViewById(R.id.delete);
                    delete.setTag(kb.keyAt(i));
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(NilaiActivity.this);
                            alert.setTitle("Warning");
                            alert.setMessage("Apakah Anda ingin menghapus Mata Kuliah yang Telah Dipilih?");
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
                                    db.delete("nilai","id_nilai="+(int) v.getTag(),null);
                                    hitung(m);
                                    refreshView();
                                    Toast.makeText(NilaiActivity.this,"Berhasil Menghapus Nilai",Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog test = alert.show();
                            sp.reset();
                        }
                    });
                    edit.setTag(kb.keyAt(i));
                    Log.d("KB",kb.valueAt(i));
                    final List<String> item = Arrays.asList(kb.valueAt(i).split("_"));
                    tgl.setText(item.get(0));
                    nilai.setText(item.get(1));
                    jenis.setText("KB "+(i+1));

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NilaiActivity.this);
                            LayoutInflater inflater = NilaiActivity.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.tambah_nilai,null);
                            alertDialog.setView(dialogView);
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Ubah",null);
                            alertDialog.setNegativeButton("Batal",null);
                            final AlertDialog ad = alertDialog.create();
                            ad.setTitle("Ubah Nilai");
                            ad.show();
                            final int id_nilai = (int) v.getTag();
                            final TextView et_warning = (TextView) ad.findViewById(R.id.warning_folder);
                            final Spinner spinJenis = (Spinner) ad.findViewById(R.id.spinJenis);
                            ArrayAdapter<String> adapter=new ArrayAdapter<>(NilaiActivity.this,R.layout.spinner_selected2,jenisd);
                            adapter.setDropDownViewResource(R.layout.spinner_item2);
                            spinJenis.setAdapter(adapter);
                            final TextInputEditText nilai = (TextInputEditText) ad.findViewById(R.id.input1);
                            nilai.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 100), new DecimalDigitsInputFilter(3,2)});
                            nilai.setText(item.get(1));
                            String[] jenisNilai = jenis.getText().toString().split(" ");
                            final String[] dateinput = item.get(0).split("/");
                            if(jenisNilai[0].equals("KK")){
                                spinJenis.setSelection(0);
                            }else if(jenisNilai[0].equals("KB")){
                                spinJenis.setSelection(1);
                            }else{
                                spinJenis.setSelection(2);
                            }

                            final TextInputEditText tgl = (TextInputEditText) ad.findViewById(R.id.date);
                            final Calendar myCalendar = Calendar.getInstance();
                            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, month);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    tgl.setText(dateFormat.format(myCalendar.getTime()));
                                }
                            };

                            tgl.setText(item.get(0));

                            tgl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatePickerDialog dialog =  new DatePickerDialog(NilaiActivity.this, date,Integer.parseInt(dateinput[2]),Integer.parseInt(dateinput[1]),Integer.parseInt(dateinput[0]));
                                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                                    dialog.show();
                                }
                            });

                            ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("NILAI",nilai.getText().toString());
                                    Log.d("NILAI",spinJenis.getSelectedItem().toString());
                                    if (!spinJenis.getSelectedItem().toString().equals("")&&!nilai.getText().toString().equals("")&&!tgl.getText().toString().equals("")){
                                        if (spinJenis.getSelectedItem().toString().equals("KK")){
                                            input_jenis2=1;
                                        }else if(spinJenis.getSelectedItem().toString().equals("KB")){
                                            input_jenis2=2;
                                        }else{
                                            input_jenis2=3;
                                        }
                                        input_date = tgl.getText().toString();
                                        input_nilai = Float.parseFloat(nilai.getText().toString());
                                        ContentValues cv = new ContentValues();
                                        cv.put("nilai",input_nilai);
                                        cv.put("jenis",input_jenis2);
                                        cv.put("tgl_input",input_date);
                                        et_warning.setVisibility(View.INVISIBLE);
                                        db.update("nilai",cv,"id_nilai = "+ id_nilai,null);
                                        ad.dismiss();
                                        refreshView();
                                        hitung(m);
                                        Toast.makeText(NilaiActivity.this,"Berhasil Mengubah Nilai",Toast.LENGTH_SHORT).show();
                                    }else{
                                        et_warning.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            ad.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ad.dismiss();
                                }
                            });
                            Log.d("Edit",(int) v.getTag()+"");
                            sp.reset();
                        }
                    });

                    sp.setTag(kb.keyAt(i));
                    sp.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                        int status = 0;
                        @Override
                        public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                        }

                        @Override
                        public void onSwipeClampReached(final SwipeLayout swipeLayout, boolean moveToRight) {
                            if (status==1){
                                LinearLayout fm = (LinearLayout) swipeLayout.findViewById(R.id.left);
                                fm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        swipeLayout.reset();
                                    }
                                });
                                status=0;
                            }else if (status==2){
                                LinearLayout ll = (LinearLayout) swipeLayout.findViewById(R.id.right);
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        swipeLayout.reset();
                                    }
                                });
                                status=0;
                            }
                        }

                        @Override
                        public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                            status=1;
                        }

                        @Override
                        public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                            status=2;
                        }
                    });
                    int curTextViewId = prevTextViewId + 1;
                    child.setId(curTextViewId);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW,prevTextViewId);
                    j.addView(child,params);
                    prevTextViewId = curTextViewId;
                }
            }

            if (uas.size()>0){
                for(int i = 0;i<uas.size();i++){
                    View child = getLayoutInflater().inflate(R.layout.sub2, null);
                    final SwipeLayout sp = (SwipeLayout) child.findViewById(R.id.sw);
                    TextView tgl = (TextView) child.findViewById(R.id.tgl);
                    final TextView jenis = (TextView) child.findViewById(R.id.middle);
                    TextView nilai = (TextView) child.findViewById(R.id.nilai);
                    Button edit = (Button) child.findViewById(R.id.edit);
                    Button delete = (Button) child.findViewById(R.id.delete);
                    delete.setTag(uas.keyAt(i));
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(NilaiActivity.this);
                            alert.setTitle("Warning");
                            alert.setMessage("Apakah Anda ingin menghapus Mata Kuliah yang Telah Dipilih?");
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
                                    db.delete("nilai","id_nilai="+(int) v.getTag(),null);
                                    hitung(m);
                                    refreshView();
                                    Toast.makeText(NilaiActivity.this,"Berhasil Menghapus Nilai",Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog test = alert.show();
                            sp.reset();
                        }
                    });
                    edit.setTag(uas.keyAt(i));
                    Log.d("KB",uas.valueAt(i));
                    final List<String> item = Arrays.asList(uas.valueAt(i).split("_"));
                    tgl.setText(item.get(0));
                    nilai.setText(item.get(1));
                    jenis.setText("UAS "+(i+1));

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NilaiActivity.this);
                            LayoutInflater inflater = NilaiActivity.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.tambah_nilai,null);
                            alertDialog.setView(dialogView);
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Ubah",null);
                            alertDialog.setNegativeButton("Batal",null);
                            final AlertDialog ad = alertDialog.create();
                            ad.setTitle("Ubah Nilai");
                            ad.show();
                            final int id_nilai = (int) v.getTag();
                            final TextView et_warning = (TextView) ad.findViewById(R.id.warning_folder);
                            final Spinner spinJenis = (Spinner) ad.findViewById(R.id.spinJenis);
                            ArrayAdapter<String> adapter=new ArrayAdapter<>(NilaiActivity.this,R.layout.spinner_selected2,jenisd);
                            adapter.setDropDownViewResource(R.layout.spinner_item2);
                            spinJenis.setAdapter(adapter);
                            final TextInputEditText nilai = (TextInputEditText) ad.findViewById(R.id.input1);
                            nilai.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 100), new DecimalDigitsInputFilter(3,2)});
                            nilai.setText(item.get(1));
                            String[] jenisNilai = jenis.getText().toString().split(" ");
                            final String[] dateinput = item.get(0).split("/");
                            if(jenisNilai[0].equals("KK")){
                                spinJenis.setSelection(0);
                            }else if(jenisNilai[0].equals("KB")){
                                spinJenis.setSelection(1);
                            }else{
                                spinJenis.setSelection(2);
                            }

                            final TextInputEditText tgl = (TextInputEditText) ad.findViewById(R.id.date);
                            final Calendar myCalendar = Calendar.getInstance();
                            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, month);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    tgl.setText(dateFormat.format(myCalendar.getTime()));
                                }
                            };

                            tgl.setText(item.get(0));

                            tgl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatePickerDialog dialog =  new DatePickerDialog(NilaiActivity.this, date,Integer.parseInt(dateinput[2]),Integer.parseInt(dateinput[1]),Integer.parseInt(dateinput[0]));
                                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                                    dialog.show();
                                }
                            });

                            ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("NILAI",nilai.getText().toString());
                                    Log.d("NILAI",spinJenis.getSelectedItem().toString());
                                    if (!spinJenis.getSelectedItem().toString().equals("")&&!nilai.getText().toString().equals("")&&!tgl.getText().toString().equals("")){
                                        if (spinJenis.getSelectedItem().toString().equals("KK")){
                                            input_jenis2=1;
                                        }else if(spinJenis.getSelectedItem().toString().equals("KB")){
                                            input_jenis2=2;
                                        }else{
                                            input_jenis2=3;
                                        }
                                        input_date = tgl.getText().toString();
                                        input_nilai = Float.parseFloat(nilai.getText().toString());
                                        ContentValues cv = new ContentValues();
                                        cv.put("nilai",input_nilai);
                                        cv.put("jenis",input_jenis2);
                                        cv.put("tgl_input",input_date);
                                        et_warning.setVisibility(View.INVISIBLE);
                                        db.update("nilai",cv,"id_nilai = "+ id_nilai,null);
                                        ad.dismiss();
                                        refreshView();
                                        hitung(m);
                                        Toast.makeText(NilaiActivity.this,"Berhasil Mengubah Nilai",Toast.LENGTH_SHORT).show();
                                    }else{
                                        et_warning.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            ad.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ad.dismiss();
                                }
                            });
                            Log.d("Edit",(int) v.getTag()+"");
                            sp.reset();
                        }
                    });

                    sp.setTag(uas.keyAt(i));
                    sp.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                        int status = 0;
                        @Override
                        public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                        }

                        @Override
                        public void onSwipeClampReached(final SwipeLayout swipeLayout, boolean moveToRight) {
                            if (status==1){
                                LinearLayout fm = (LinearLayout) swipeLayout.findViewById(R.id.left);
                                fm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        swipeLayout.reset();
                                    }
                                });
                                status=0;
                            }else if (status==2){
                                LinearLayout ll = (LinearLayout) swipeLayout.findViewById(R.id.right);
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        swipeLayout.reset();
                                    }
                                });
                                status=0;
                            }
                        }

                        @Override
                        public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                            status=1;
                        }

                        @Override
                        public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                            status=2;
                        }
                    });
                    int curTextViewId = prevTextViewId + 1;
                    child.setId(curTextViewId);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW,prevTextViewId);
                    j.addView(child,params);
                    prevTextViewId = curTextViewId;
                }
            }

            Log.d("Child",exl.isExpanded()+"");
            if (!collapse.contains(m.getMatkul())){
                exl.expand();
            }
            rl.addView(view);
        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }

    public class InputFilterMinMax implements InputFilter {

        private Float min, max;

        public InputFilterMinMax(int min, int max) {
            this.min =  Float.parseFloat(String.valueOf(min));
            this.max =  Float.parseFloat(String.valueOf(max));
        }


        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                Float input = Float.parseFloat(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(Float a, Float b, Float c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
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
                if (spinner.getSelectedItem().toString().equals("Semua")){
                    semesterNow = 0;
                }else{
                    semesterNow = Integer.parseInt(spinner.getSelectedItem().toString().substring(spinner.getSelectedItem().toString().indexOf(" ")+1));
                }
                refreshView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getMenuInflater().inflate(R.menu.nilai_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.hitung:
                hitungNA();
                return true;
            case R.id.bobot:
                setelBobot();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupSpinner(Spinner spin){
        //wrap the items in the Adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.spinner_selected,dropdown);
        //assign adapter to the Spinner
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(adapter);
    }

    private void hitung(Nilai m){

        if (!m.getGrade().equals("-")){
            id = m.getId();
            ArrayMap<Integer,String> kk = m.getKk();
            ArrayMap<Integer,String> kb = m.getKb();
            ArrayMap<Integer,String> uas = m.getUas();

            naKK = 0;
            naKB = 0;
            naUAS = 0;

            if(kk.size()>0){
                for (int i = 0;i<kk.size();i++){
                    List<String> item = Arrays.asList(kk.valueAt(i).split("_"));
                    naKK = naKK+Float.parseFloat(item.get(1));
                }
            }

            naKK = naKK/kk.size();

            if(kb.size()>0){
                for (int i = 0;i<kb.size();i++){
                    List<String> item = Arrays.asList(kb.valueAt(i).split("_"));
                    naKB = naKB+Float.parseFloat(item.get(1));
                }
            }

            naKB = naKB/kb.size();

            if(uas.size()>0){
                for (int i = 0;i<uas.size();i++){
                    List<String> item = Arrays.asList(uas.valueAt(i).split("_"));
                    naUAS = naUAS+Float.parseFloat(item.get(1));
                }
            }

            naUAS = naUAS/uas.size();

            fixKK = Float.parseFloat(String.valueOf(bobotKK))/100;
            fixKB = Float.parseFloat(String.valueOf(bobotKB))/100;
            fixUAS = Float.parseFloat(String.valueOf(bobotUAS))/100;


            if (kk.size()>0){
                totalNilai = totalNilai+(naKK*fixKK);
            }

            if (kb.size()>0){
                totalNilai = totalNilai+(naKB*fixKB);
            }

            if (uas.size()>0){
                totalNilai = totalNilai+(naUAS*fixUAS);
            }

            if (totalNilai>=95&&totalNilai<=100){
                grade = "A";
            }else if(totalNilai>=90&&totalNilai<=94.99){
                grade = "AB";
            }else if(totalNilai>=82&&totalNilai<=89.99){
                grade = "BA";
            }else if(totalNilai>=73&&totalNilai<=81.99){
                grade = "B";
            }else if(totalNilai>=65&&totalNilai<=72.99){
                grade = "BC";
            }else if(totalNilai>=60&&totalNilai<=64.99){
                grade = "CB";
            }else if(totalNilai>=56&&totalNilai<=59.99){
                grade = "C";
            }else if(totalNilai>=50&&totalNilai<=55.99){
                grade = "CD";
            }else if(totalNilai>=40&&totalNilai<=49.99){
                grade = "D";
            }else{
                grade = "E";
            }

            if (totalNilai>=56){
                status = 1;
            }else{
                status = 0;
            }

            DecimalFormat df = new DecimalFormat("##.##");

            Log.d("NAKK",naKK+"");
            Log.d("NAKB",naKB+"");
            Log.d("NAUAS",naUAS+"");
            Log.d("TOTALNA",totalNilai+"");
            Log.d("BOBOT",fixKK+"_"+fixKB+"_"+fixUAS);
            Log.d("GRADE",grade+"_"+status);

            ContentValues cv = new ContentValues();
            cv.put("nilai_akhir",Float.parseFloat(df.format(totalNilai)));
            cv.put("grade",grade);
            cv.put("status",status);
            db.update("mata_kuliah",cv,"id_mata_kuliah ="+m.getId(),null);

            naKK = 0;
            naKB = 0;
            naUAS = 0;
            totalNilai = 0;
            fixKK = 0;
            fixKB = 0;
            fixUAS = 0;
            totalNilai = 0;
            grade = "";
            status = 0;

            Toast.makeText(NilaiActivity.this,"Berhasil Memperbarui Nilai Akhir!",Toast.LENGTH_SHORT).show();
        }

    }

    private void hitungNA(){
        AlertDialog.Builder alert = new AlertDialog.Builder(NilaiActivity.this);
        alert.setTitle("Warning");
        alert.setMessage("Apakah Anda ingin menghitung Nilai Akhir dari Semester yang Telah Dipilih?");
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
                for(Nilai m:matkul){
                    id = m.getId();
                    ArrayMap<Integer,String> kk = m.getKk();
                    ArrayMap<Integer,String> kb = m.getKb();
                    ArrayMap<Integer,String> uas = m.getUas();

                    naKK = 0;
                    naKB = 0;
                    naUAS = 0;

                    if(kk.size()>0){
                        for (int i = 0;i<kk.size();i++){
                            List<String> item = Arrays.asList(kk.valueAt(i).split("_"));
                            naKK = naKK+Float.parseFloat(item.get(1));
                        }
                    }

                    naKK = naKK/kk.size();

                    if(kb.size()>0){
                        for (int i = 0;i<kb.size();i++){
                            List<String> item = Arrays.asList(kb.valueAt(i).split("_"));
                            naKB = naKB+Float.parseFloat(item.get(1));
                        }
                    }

                    naKB = naKB/kb.size();

                    if(uas.size()>0){
                        for (int i = 0;i<uas.size();i++){
                            List<String> item = Arrays.asList(uas.valueAt(i).split("_"));
                            naUAS = naUAS+Float.parseFloat(item.get(1));
                        }
                    }

                    naUAS = naUAS/uas.size();

                    fixKK = Float.parseFloat(String.valueOf(bobotKK))/100;
                    fixKB = Float.parseFloat(String.valueOf(bobotKB))/100;
                    fixUAS = Float.parseFloat(String.valueOf(bobotUAS))/100;


                    if (kk.size()>0){
                        totalNilai = totalNilai+(naKK*fixKK);
                    }

                    if (kb.size()>0){
                        totalNilai = totalNilai+(naKB*fixKB);
                    }

                    if (uas.size()>0){
                        totalNilai = totalNilai+(naUAS*fixUAS);
                    }

                    if (totalNilai>=95&&totalNilai<=100){
                        grade = "A";
                    }else if(totalNilai>=90&&totalNilai<=94.99){
                        grade = "AB";
                    }else if(totalNilai>=82&&totalNilai<=89.99){
                        grade = "BA";
                    }else if(totalNilai>=73&&totalNilai<=81.99){
                        grade = "B";
                    }else if(totalNilai>=65&&totalNilai<=72.99){
                        grade = "BC";
                    }else if(totalNilai>=60&&totalNilai<=64.99){
                        grade = "CB";
                    }else if(totalNilai>=56&&totalNilai<=59.99){
                        grade = "C";
                    }else if(totalNilai>=50&&totalNilai<=55.99){
                        grade = "CD";
                    }else if(totalNilai>=40&&totalNilai<=49.99){
                        grade = "D";
                    }else{
                        grade = "E";
                    }

                    if (totalNilai>=56){
                        status = 1;
                    }else{
                        status = 0;
                    }

                    DecimalFormat df = new DecimalFormat("##.##");

                    Log.d("NAKK",naKK+"");
                    Log.d("NAKB",naKB+"");
                    Log.d("NAUAS",naUAS+"");
                    Log.d("TOTALNA",totalNilai+"");
                    Log.d("BOBOT",fixKK+"_"+fixKB+"_"+fixUAS);
                    Log.d("GRADE",grade+"_"+status);

                    ContentValues cv = new ContentValues();
                    cv.put("nilai_akhir",Float.parseFloat(df.format(totalNilai)));
                    cv.put("grade",grade);
                    cv.put("status",status);
                    db.update("mata_kuliah",cv,"id_mata_kuliah ="+m.getId(),null);

                    naKK = 0;
                    naKB = 0;
                    naUAS = 0;
                    totalNilai = 0;
                    fixKK = 0;
                    fixKB = 0;
                    fixUAS = 0;
                    totalNilai = 0;
                    grade = "";
                    status = 0;

                    Toast.makeText(NilaiActivity.this,"Berhasil Memperbarui Nilai Akhir!",Toast.LENGTH_SHORT).show();

                }
                refreshView();
            }
        });
        AlertDialog test = alert.show();
    }

    private void setelBobot(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NilaiActivity.this);
        LayoutInflater inflater = NilaiActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.setel_boot,null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ubah",null);
        alertDialog.setNegativeButton("Batal",null);
        alertDialog.setNeutralButton("Default",null);
        final AlertDialog ad = alertDialog.create();
        ad.setTitle("Atur Bobot Nilai");
        ad.show();
        final EditText bobotkk = (EditText) ad.findViewById(R.id.bkk);
        final EditText bobotkb = (EditText) ad.findViewById(R.id.bkb);
        final EditText bobotuas = (EditText) ad.findViewById(R.id.buas);
        final TextView et_warning = (TextView) ad.findViewById(R.id.warning_folder);

        bobotkk.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 100)});
        bobotkb.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 100)});
        bobotuas.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 100)});

        bobotkk.setText(bobotKK+"");
        bobotkb.setText(bobotKB+"");
        bobotuas.setText(bobotUAS+"");

        ad.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        ad.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(NilaiActivity.this);
                alert.setTitle("Warning");
                alert.setMessage("Apakah Anda mengembalikan bobot nilai secara default?");
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
                        bobotKK = Float.parseFloat("10");
                        bobotKB = Float.parseFloat("25");
                        bobotUAS = Float.parseFloat("65");

                        ContentValues cv = new ContentValues();
                        cv.put("besar_bobot",bobotKK);
                        et_warning.setVisibility(View.INVISIBLE);
                        db.update("bobot_nilai",cv,"id_bobot = 'KK'",null);

                        cv = new ContentValues();
                        cv.put("besar_bobot",bobotKB);
                        et_warning.setVisibility(View.INVISIBLE);
                        db.update("bobot_nilai",cv,"id_bobot = 'KB'",null);

                        cv = new ContentValues();
                        cv.put("besar_bobot",bobotUAS);
                        et_warning.setVisibility(View.INVISIBLE);
                        db.update("bobot_nilai",cv,"id_bobot = 'UAS'",null);

                        ad.dismiss();

                        Toast.makeText(NilaiActivity.this,"Berhasil Merubah ke Default!",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog test = alert.show();
            }
        });

        ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Float.parseFloat(bobotkk.getText().toString())+Float.parseFloat(bobotkb.getText().toString())+Float.parseFloat(bobotuas.getText().toString()))==100){
                    bobotKK = Float.parseFloat(bobotkk.getText().toString());
                    bobotKB = Float.parseFloat(bobotkb.getText().toString());
                    bobotUAS = Float.parseFloat(bobotuas.getText().toString());

                    ContentValues cv = new ContentValues();
                    cv.put("besar_bobot",bobotKK);
                    et_warning.setVisibility(View.INVISIBLE);
                    db.update("bobot_nilai",cv,"id_bobot = 'KK'",null);

                    cv = new ContentValues();
                    cv.put("besar_bobot",bobotKB);
                    et_warning.setVisibility(View.INVISIBLE);
                    db.update("bobot_nilai",cv,"id_bobot = 'KB'",null);

                    cv = new ContentValues();
                    cv.put("besar_bobot",bobotUAS);
                    et_warning.setVisibility(View.INVISIBLE);
                    db.update("bobot_nilai",cv,"id_bobot = 'UAS'",null);

                    et_warning.setVisibility(View.INVISIBLE);
                    Toast.makeText(NilaiActivity.this,"Berhasil Merubah Bobot Nilai!",Toast.LENGTH_SHORT).show();
                    hitungNA();
                    ad.dismiss();
                }else{
                    et_warning.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    public class Nilai{
        String matkul;
        int id;
        ArrayMap<Integer, String> kk = new ArrayMap<>();
        ArrayMap<Integer, String> kb = new ArrayMap<>();
        ArrayMap<Integer, String> uas = new ArrayMap<>();
        String grade;
        Float nilai_akhir;

        public Nilai(String matkul,int id){
            this.id = id;
            this.matkul = matkul;
            String query = "SELECT grade,nilai_akhir FROM mata_kuliah WHERE id_mata_kuliah = ?";
            Cursor cr = db.rawQuery(query,new String[]{id+""});
            while (cr.moveToNext()){
                if (cr.getString(cr.getColumnIndex("grade"))!=null){
                    grade = cr.getString(cr.getColumnIndex("grade"));
                }else{
                    grade="-";
                }

                if (cr.getFloat(cr.getColumnIndex("nilai_akhir"))!=0){
                    nilai_akhir = cr.getFloat(cr.getColumnIndex("nilai_akhir"));
                }else{
                    nilai_akhir = Float.parseFloat("0");
                }

                Log.d("NilaiA",cr.getFloat(cr.getColumnIndex("nilai_akhir"))+"");

            }

            query = "SELECT * FROM nilai WHERE id_mata_kuliah = ? ORDER BY tgl_input";
            cr = db.rawQuery(query,new String[]{id+""});
            while (cr.moveToNext()){
                if (cr.getInt(cr.getColumnIndex("jenis"))==1){
                    String input =cr.getString(cr.getColumnIndex("tgl_input"))+"_"+cr.getFloat(cr.getColumnIndex("nilai"));
                    kk.put(cr.getInt(cr.getColumnIndex("id_nilai")),input);
                }else if(cr.getInt(cr.getColumnIndex("jenis"))==2){
                    String input =cr.getString(cr.getColumnIndex("tgl_input"))+"_"+cr.getFloat(cr.getColumnIndex("nilai"));
                    kb.put(cr.getInt(cr.getColumnIndex("id_nilai")),input);
                }else{
                    String input =cr.getString(cr.getColumnIndex("tgl_input"))+"_"+cr.getFloat(cr.getColumnIndex("nilai"));
                    uas.put(cr.getInt(cr.getColumnIndex("id_nilai")),input);
                }
            }
        }

        public int getId(){
            return id;
        }

        public String getMatkul(){
            return matkul;
        }

        public String getGrade(){
            return grade;
        }

        public Float getNilai(){
            return nilai_akhir;
        }

        public ArrayMap<Integer, String> getKk(){
            return kk;
        }

        public ArrayMap<Integer, String> getKb(){
            return kb;
        }

        public ArrayMap<Integer, String> getUas(){
            return uas;
        }
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

