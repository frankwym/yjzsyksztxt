package com.manager.model;

public class ResultModel {
    //执行结果
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;


    //向前端ajax返回执行结果 1：成功，2：失败
    public int resultCode;

    //返回信息
    public Object msg = "";

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
