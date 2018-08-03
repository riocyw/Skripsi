package com.example.rog.mcpix;

import java.sql.Blob;

/**
 * Created by ROG on 4/27/2018.
 */

public class DaftarMK {
    String namaMK;
    int semester;
    int sks;
    Float nilai_akhir;
    String grade;
    String dosen;
    int status;
    boolean visible = true;
    boolean isChecked = false;
    public DaftarMK(String namaMK, int semester, int sks, String dosen, String grade, Float nilai_akhir, int status){
        this.namaMK = namaMK;
        this.semester = semester;
        this.sks = sks;
        this.dosen = dosen;
        this.grade = grade;
        this.nilai_akhir = nilai_akhir;
        this.status = status;
    }

    public DaftarMK(String namaMK, int semester){
        this.namaMK = namaMK;
        this.semester = semester;
    }

    public String getNamaMK(){
        return namaMK;
    }

    public int getSemester(){
        return semester;
    }

    public int getSks(){
        return sks;
    }

    public String getDosen(){
        return dosen;
    }

    public Float getNA(){
        return nilai_akhir;
    }

    public String getGrade(){
        return grade;
    }

    public int getStatus(){
        return status;
    }

    public boolean getVisible(){
        return visible;
    }

    public void toogleVisible(){
        if (getVisible()){
            visible = false;
        }else{
            visible = true;
        }
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
