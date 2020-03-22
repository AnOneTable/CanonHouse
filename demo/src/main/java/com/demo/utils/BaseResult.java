package com.demo.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaohang
 */
public class BaseResult {

    private boolean success = false;
    private String errorCode;
    private String errorMsg;
    private Map<Object, Object> map;
    private String msg;

    public Map<?, ?> getMap() {
        return map;
    }

    public void setMap(Map<Object, Object> map) {
        this.map = map;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void success() {
        this.success = true;
    }

    public void put(Object key, Object msg) {
        if (map == null) {
            map = new HashMap<>(16);
        }
        map.put(key, msg);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String toJSONString(HttpServletRequest request) {
        String callback = request.getParameter("callback");
        return StringUtils.isNotBlank(callback) ? toJSONString(callback) : toJSONString();
    }

    public String toJSONString(String callback) {
        if (StringUtils.isNotBlank(callback)) {
            return callback + "(" + JSON.toJSONString(this) + ")";
        } else {
            return toJSONString();
        }
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }


    @Override
    public String toString() {
        return "BaseResult{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", map=" + map +
                ", msg='" + msg + '\'' +
                '}';
    }
}

