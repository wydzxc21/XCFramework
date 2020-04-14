package com.xc.framework.socket.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuhao on 2017/5/22.
 */

public class HandShakeBean extends DefaultSendBean {

    public HandShakeBean() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 2);
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
