package com.fei.web.response;

import com.alibaba.fastjson.JSONObject;

/**
 * 响应信息
 *
 * @author jianying9
 */
public class Response
{

    private final String route;

    private String code = Response.SUCCESS;

    private String msg = "";

    private JSONObject data = null;

    public Response(String route)
    {
        this.route = route;
    }

    public String getRoute()
    {
        return route;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public JSONObject getData()
    {
        return data;
    }

    public void setData(JSONObject data)
    {
        this.data = data;
    }

    public JSONObject toJSONObject()
    {
        JSONObject output = new JSONObject();
        output.put("route", this.route);
        output.put("code", this.code);
        if (this.msg.isEmpty() == false) {
            output.put("msg", this.msg);
        }
        if (this.data != null) {
            output.put("data", this.data);
        }
        return output;
    }

    //成功
    public final static String SUCCESS = "success";
    //未登录
    public final static String UNLOGIN = "unlogin";
    //非法数据
    public final static String INVALID = "invalid";
    //无权限
    public final static String DENIED = "denied";
    //不存在
    public final static String NOTFOUND = "notfound";
    //异常
    public final static String EXCEPTION = "exception";

    public static JSONObject createNotfound(String route)
    {
        JSONObject output = new JSONObject();
        output.put("route", route);
        output.put("code", Response.NOTFOUND);
        return output;
    }

}
