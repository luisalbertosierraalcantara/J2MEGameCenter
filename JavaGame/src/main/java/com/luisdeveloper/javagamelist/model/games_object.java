package com.luisdeveloper.javagamelist.model;


import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************
public class games_object {

    public int Id;
    public String Name;
    public String Url;
    public String Desc;
    public String Picture;
    public String Categoria;

    public int getId() {
        return this.Id;
    }

    public String getName() {
        return this.Name;
    }

    public String getUrl() {
        return this.Url;
    }

    public String getDesc() {
        return this.Desc;
    }

    public String getPicture() {
        return this.Picture;
    }

    public String getCategoria() {
        return this.Categoria;
    }


    public void setId(int id) {
        this.Id = id;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setDesc(String desc) {
        this.Desc = desc;
    }

    public void setPicture(String picture) {
        this.Picture = picture;
    }

    public void setUrl(String url) {
        this.Url = url;
    }

    public void setCategoria(String categoria) {
        this.Categoria = categoria;
    }
}
