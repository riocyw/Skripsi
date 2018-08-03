package com.example.rog.mcpix;

import android.app.FragmentBreadCrumbs;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChooseFolder extends AppCompatActivity {
    private ArrayList<String> listFolder = new ArrayList<>();
    private CustomAdapter customAdapter = new CustomAdapter();
    private ArrayList<DaftarMK> daftarMK = new ArrayList<>();
    private ArrayList<String> dropdown = new ArrayList<>();
    private SQLiteDatabase db;
    private LinearLayout breadCrumbs;
    private int viewStatus = 1;
    private String pathNow;
    private ListView lv;
    private TextView tv_warning;
    private Spinner spinner;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Pilih Folder");
        setContentView(R.layout.activity_choose_folder);
        db = openOrCreateDatabase("pixature",MODE_PRIVATE,null);
        breadCrumbs =(LinearLayout) findViewById(R.id.breadcrumb);
        tv_warning = (TextView) findViewById(R.id.buat_folder_warning);
        lv = (ListView) findViewById(R.id.listFolder1);
        listFolder.clear();

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature");
        File folderUmum = new File(file.getAbsolutePath()+"/Umum");
        if (!folderUmum.exists()){
            folderUmum.mkdirs();
        }
        pathNow = file.getAbsolutePath().toString();

        lv.setAdapter(customAdapter);
        checkContent();
        checkBC(file);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (viewStatus>1){
                    File file = (File) view.getTag();
                    pathNow = file.getAbsolutePath();
                    db.close();
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("datapath",pathNow);
                    startActivity(intent);
                    finish();
                }else{
                    invalidateOptionsMenu();
                    File file = (File) view.getTag();
                    File[] files = file.listFiles();
                    Toast.makeText(ChooseFolder.this,file.getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();
                    listFolder.clear();

                    String[] pathfiles = file.getAbsolutePath().substring(file.getAbsolutePath().toString().indexOf("Pixature")).split("/");
                    List<String> pf = Arrays.asList(pathfiles);
                    viewStatus = pf.size();

                    pathNow = file.getAbsolutePath();

                    if (files!=null){
                        for(File f:files){
                            if (f.isDirectory()){
                                listFolder.add(f.getName());
                            }
                        }
                    }

                    if (listFolder.size()>0){
                        Collections.sort(listFolder, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareToIgnoreCase(o2);
                            }
                        });
                        tv_warning.setVisibility(View.INVISIBLE);
                    }else{
                        tv_warning.setVisibility(View.VISIBLE);
                    }


                    ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                    checkBC(file);
                    Toast.makeText(ChooseFolder.this,pathNow,Toast.LENGTH_SHORT).show();
                }
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
                }else{
                    semester = Integer.parseInt(semester_dropdown.substring(semester_dropdown.indexOf(" ")+1));
                    sortAsDropdown(semester);
                }
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
//                Toast.makeText(Gallery.this,semester_dropdown+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getMenuInflater().inflate(R.menu.choose_folder_menu,menu);
        return super.onCreateOptionsMenu(menu);
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
            DaftarMK mk = new DaftarMK(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("semester")));
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
            tv_warning.setVisibility(View.INVISIBLE);
        }else{
            tv_warning.setVisibility(View.VISIBLE);
        }
        ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
        Log.d("daftarMK : ",semester+"");
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
        listFolder.clear();

        DaftarMK mk = new DaftarMK("Umum",0);
        daftarMK.add(mk);

        query = "SELECT * FROM mata_kuliah ORDER BY nama_mata_kuliah ASC";
        Cursor cr = db.rawQuery(query,null);
        while(cr.moveToNext()){
            File folder = new File(directory, cr.getString(cr.getColumnIndex("nama_mata_kuliah")));
            folder.mkdirs();
            mk = new DaftarMK(cr.getString(cr.getColumnIndex("nama_mata_kuliah")),cr.getInt(cr.getColumnIndex("semester")));
            daftarMK.add(mk);
        }

        if (daftarMK.size()>0){
            Collections.sort(daftarMK, new Comparator<DaftarMK>() {
                @Override
                public int compare(DaftarMK o1, DaftarMK o2) {
                    return o1.getNamaMK().compareToIgnoreCase(o2.getNamaMK());
                }
            });
            tv_warning.setVisibility(View.INVISIBLE);
        }else{
            tv_warning.setVisibility(View.VISIBLE);
        }

        dropdown.clear();
        dropdown.add(0,"Semua");
        query = "SELECT DISTINCT semester from mata_kuliah ORDER BY semester ASC";
        cr = db.rawQuery(query,null);
        while (cr.moveToNext()){
            dropdown.add("Semester "+cr.getString(cr.getColumnIndex("semester")));
        }
        System.out.println(dropdown.toString());
        Log.d("Daftar",daftarMK.size()+"");

        ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
    }

    public void setupSpinner(Spinner spin){
        //wrap the items in the Adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.spinner_selected,dropdown);
        //assign adapter to the Spinner
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_addfolder:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChooseFolder.this);
                LayoutInflater inflater = ChooseFolder.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.tambah_folder,null);
                alertDialog.setPositiveButton("Tambah",null);
                alertDialog.setNegativeButton("Batal",null);
                alertDialog.setCancelable(false);
                alertDialog.setView(dialogView);

                final AlertDialog ad = alertDialog.create();
                ad.setTitle("Tambah Folder Materi");
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
                                        listFolder.add(f.getName());
                                    }
                                }
                            }

                            if (listFolder.size()>0){
                                Collections.sort(listFolder, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return o1.compareToIgnoreCase(o2);
                                    }
                                });
                                tv_warning.setVisibility(View.INVISIBLE);
                            }else{
                                tv_warning.setVisibility(View.VISIBLE);
                            }

                            ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                            checkBC(file);
                            Toast.makeText(ChooseFolder.this,"Folder Berhasil dibuat! : "+path.getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (viewStatus>1){
            menu.setGroupVisible(R.id.spingroup,false);
            Log.d("view",viewStatus+"");
            menu.setGroupVisible(R.id.group_btnaddfolder,true);
        }else{
            menu.setGroupVisible(R.id.spingroup,true);
            menu.setGroupVisible(R.id.group_btnaddfolder,false);
            Log.d("view",viewStatus+"");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkBC(File file){
        breadCrumbs.removeAllViews();
        String pathRow = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String path = file.getAbsolutePath().toString().substring(file.getAbsolutePath().toString().indexOf("Pixature"));
        String[] arraypath = path.split("/");
        List<String> ap  = Arrays.asList(arraypath);
        Log.d("ARRAYPATH",path);

        for(int i = 0;i<ap.size();i++){
            final View view = getLayoutInflater().inflate(R.layout.breadcrumb_layout,null);
            final View.OnClickListener listener = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    invalidateOptionsMenu();
                    File path = (File) v.getTag();
                    String[] pathfiles = path.getAbsolutePath().substring(path.getAbsolutePath().toString().indexOf("Pixature")).split("/");
                    List<String> pf = Arrays.asList(pathfiles);
                    listFolder.clear();

                    pathNow = path.getAbsolutePath();

                    for(String j:pathfiles){
                        Log.d("PJ",j);
                    }
                    viewStatus = pf.size();
                    Toast.makeText(ChooseFolder.this,pathNow,Toast.LENGTH_SHORT).show();
                    File[] files = path.listFiles();

                    if (viewStatus<2){
                        checkContent();
                    }else{

                        if (files!=null) {
                            for (File f : files) {
                                if (f.isDirectory()) {
                                    listFolder.add(f.getName());
                                }
                            }
                        }else{
                            listFolder.clear();
                        }
                        if (listFolder.size()>0){
                            Collections.sort(listFolder, new Comparator<String>() {
                                @Override
                                public int compare(String o1, String o2) {
                                    return o1.compareToIgnoreCase(o2);
                                }
                            });
                            tv_warning.setVisibility(View.INVISIBLE);
                        }else{
                            tv_warning.setVisibility(View.VISIBLE);
                        }
                        Log.d("TES 2",listFolder.toString());
                    }
                    ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                    checkBC(path);
                }
            };

            Button path1 = (Button) view.findViewById(R.id.btnC);
            pathRow = pathRow+"/"+ap.get(i);
            path1.setTag(new File(pathRow));
            Log.d("PAATH1",pathNow);
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
        String query = "SELECT DISTINCT semester from mata_kuliah ORDER BY semester ASC";
        Cursor cr = db.rawQuery(query,null);
        dropdown.clear();
        dropdown.add(0,"Semua");
        while (cr.moveToNext()){
            dropdown.add("Semester "+cr.getString(cr.getColumnIndex("semester")));
        }
    }

    @Override
    public void onBackPressed() {
        if (viewStatus==2){
            invalidateOptionsMenu();
            checkContent();
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString(),"Pixature");

            ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
            viewStatus=1;
            pathNow = path.getAbsolutePath();
            checkBC(path);
            Log.d("Masuk sini","1");
        }else if(viewStatus<2){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            Log.d("Masuk sini","2");
            db.close();
        }else{
            invalidateOptionsMenu();
            File path = new File(pathNow);
            String[] pathfiles = path.getAbsolutePath().substring(path.getAbsolutePath().toString().indexOf("Pixature")).split("/");
            List<String> pf = Arrays.asList(pathfiles);
            String pathRow = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            for(int i = 0;i<pf.size()-1;i++){
                pathRow = pathRow+"/"+pf.get(i);
            }
            path = new File(pathRow);
            File[] files = path.listFiles();
            listFolder.clear();
            if (files!=null){
                for(File f:files){
                    if (f.isDirectory()){
                        listFolder.add(f.getName());
                    }
                }
            }

            viewStatus = pf.size()-1;
            if (listFolder.size()>0){
                Collections.sort(listFolder, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareToIgnoreCase(o2);
                    }
                });
                tv_warning.setVisibility(View.INVISIBLE);
            }else{
                tv_warning.setVisibility(View.VISIBLE);
            }
            ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
            pathNow = path.getAbsolutePath();
            checkBC(path);
            Toast.makeText(ChooseFolder.this,pathNow,Toast.LENGTH_SHORT).show();
            Log.d("Masuk sini","3");
        }

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            if (viewStatus==1){
                return daftarMK.size();
            }else{
                return listFolder.size();
            }
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
            imageView.setImageResource(R.drawable.newfolder);
            File file;
            if (viewStatus==1){
                if (daftarMK.get(i).getNamaMK().length()>24){
                    String newName = daftarMK.get(i).getNamaMK().substring(0,10)+"...";
                    textView_name.setText(newName);
                }else{
                    textView_name.setText(daftarMK.get(i).getNamaMK());
                }

                convertView.setTag(new File(pathNow+"/"+daftarMK.get(i).getNamaMK()));
                file = new File(pathNow+"/"+daftarMK.get(i).getNamaMK());
            }else{
                if (listFolder.get(i).length()>24){
                    String newName = listFolder.get(i).substring(0,10)+"...";
                    textView_name.setText(newName);
                }else{
                    textView_name.setText(listFolder.get(i));
                }

                convertView.setTag(new File(pathNow+"/"+listFolder.get(i)));
                file = new File(pathNow+"/"+listFolder.get(i));
            }

            File[] files = file.listFiles();

            Log.d("Hasil",file.getAbsolutePath());

            if (files != null) {
                if (viewStatus==1){
                    for (File f:files){
                        if (f.isDirectory()){
                            count++;
                        }
                    }
                    textView_description.setText(count + " Materi");
                }else{
                    for (File f:files){
                        if (f.getName().endsWith(".jpg")){
                            count++;
                        }
                    }
                    textView_description.setText(count + " Foto");
                }
            }

            return convertView;
        }
    }
}
