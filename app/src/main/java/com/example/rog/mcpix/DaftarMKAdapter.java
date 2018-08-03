package com.example.rog.mcpix;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ROG on 4/27/2018.
 */

public class DaftarMKAdapter extends BaseAdapter {
    ArrayList<DaftarMK> daftarMK = new ArrayList<>();
    private Context context;

    public DaftarMKAdapter(Context context,ArrayList<DaftarMK> daftarMK){
        this.daftarMK = daftarMK;
        this.context = context;
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {
//        int jumlahMateri = 0;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        gridView = inflater.inflate(R.layout.grid_item,null);
        TextView textView = (TextView) gridView.findViewById(R.id.nama_matkul);
        if (daftarMK.get(position).getNamaMK().length()>25){
           String newName = daftarMK.get(position).getNamaMK().substring(0,10)+"...";
            textView.setText(newName);
        }else{
            textView.setText(daftarMK.get(position).getNamaMK());
        }

//        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pixature"+File.separator+daftarMK.get(position).getNamaMK());
//        directory.mkdirs();
//        File[] files = directory.listFiles();
//        if (files!=null){
//            for (int i = 0;i<files.length;i++){
//                if (files[i].isDirectory()){
//                    jumlahMateri++;
//                }
//            }
            TextView textView2 = (TextView) gridView.findViewById(R.id.jumlah_matkul);
            textView2.setText("Sem. "+daftarMK.get(position).getSemester());
//        }
        if (daftarMK.get(position).isChecked()){
//            Coding penanda selected'
            CardView imageView = (CardView) gridView.findViewById(R.id.cv);
            imageView.setCardBackgroundColor(Color.parseColor("#635fff"));
        }else{
//            menghilangkan penanda
            CardView imageView = (CardView) gridView.findViewById(R.id.cv);
            imageView.setCardBackgroundColor(Color.WHITE);
        }
        if (daftarMK.get(position).visible){
            CardView imageView = (CardView) gridView.findViewById(R.id.cv);
            imageView.setVisibility(View.VISIBLE);
        }else{
            CardView imageView = (CardView) gridView.findViewById(R.id.cv);
            imageView.setVisibility(View.INVISIBLE);
        }
        return gridView;
    }
}
