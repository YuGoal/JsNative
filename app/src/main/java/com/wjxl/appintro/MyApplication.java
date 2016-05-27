package com.wjxl.appintro;

import android.app.Application;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * Created by lenovo on 2016/5/25.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //设置主题
        ThemeConfig themeConfig = new ThemeConfig.Builder()
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        //配置imageLoader
        ImageLoader imageLoader = new FrescoImageLoader(this);
        CoreConfig coreConfig = new CoreConfig.Builder(this,imageLoader,themeConfig)
                .build();

        GalleryFinal.init(coreConfig);

    }
}
