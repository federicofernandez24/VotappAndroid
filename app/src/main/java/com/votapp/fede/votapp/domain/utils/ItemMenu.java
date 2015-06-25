package com.votapp.fede.votapp.domain.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by fede on 24/6/15.
 */
public class ItemMenu {

    public ItemMenu(){}
    public ItemMenu(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    private String title;
    private int icon;


}
