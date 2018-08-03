package com.example.rog.mcpix;

/**
 * Created by ROG on 5/15/2018.
 */

public class FolderPhoto {
    private String namaFolder;
    private boolean isChecked = false;

    public FolderPhoto(String namaFolder){
        this.namaFolder = namaFolder;
    }

    public String getNamaFolder(){
        return namaFolder;
    }

    public boolean isChecked(){
        return isChecked;
    }

    public void toggleChecked(){
        if (isChecked){
            isChecked = false;
        }else{
            isChecked = true;
        }
    }
}
