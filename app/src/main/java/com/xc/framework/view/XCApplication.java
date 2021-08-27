package com.xc.framework.view;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * @author ZhangXuanChen
 * @date 2021/8/20
 * @package com.hollcon.nagene.view
 * @description XCApplication
 */
public class XCApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
