package com.example.dell.pictureprocessing.detection;

import android.graphics.Bitmap;

import com.example.dell.pictureprocessing.IMain;


/**
 * 边缘检测方法
 * Created by smile on 2016/9/19.
 */
public abstract class Detection {

    protected IMain iMain;

    Detection(IMain iMain) {

        this.iMain = iMain;
    }

    /**
     * 进行边缘检测
     * @param originalBitmap 原始图片
     * @return
     */
    public abstract void detection(Bitmap originalBitmap);
}
