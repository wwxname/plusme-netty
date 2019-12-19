package com.plusme.rope.model;

/**
 * @author plusme
 * @create 2019-11-30 22:55
 */
public class PlusmeService {

    public PlusmeProtocol.Response execute(PlusmeProtocol.Request request) throws InterruptedException {
        String method = request.getMethod();
        String jsonStr = "未知";
        if (method.equals("GET")) {
            jsonStr = "查询";
        } else if (method.equals("DELETE")) {
            jsonStr = "删除";
        } else {
            jsonStr = method;
        }
        return PlusmeProtocol.Response.newBuilder().setJsonStr(jsonStr).build();
    }
}
