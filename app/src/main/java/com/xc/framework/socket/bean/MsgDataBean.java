package com.xc.framework.socket.bean;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tony on 2017/10/24.
 */

public class MsgDataBean extends DefaultSendBean {

    public MsgDataBean(String data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 3);
            jsonObject.put("data", "" + data);
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
