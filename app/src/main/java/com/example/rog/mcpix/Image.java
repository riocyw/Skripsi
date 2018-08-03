package com.example.rog.mcpix;

import android.graphics.Bitmap;

/**
 * Created by ROG on 5/15/2018.
 */

public class Image {
    Bitmap bm;
    boolean isChecked=false;
    public Image(Bitmap bm){
        this.bm=bm;
    }

    public Bitmap getBm(){
        return bm;
    }

    public boolean isChecked(){
        return isChecked;
    }

    public void toogleChecked(){
        if (isChecked()){
            isChecked = false;
        }else{
            isChecked = true;
        }
    }
}
