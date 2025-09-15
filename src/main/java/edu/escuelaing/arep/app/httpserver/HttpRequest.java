package edu.escuelaing.arep.app.httpserver;

import java.net.URI;

public class HttpRequest {
    URI requri;
    
    public HttpRequest(URI requri){
    this.requri = requri;
    }
    
    public String getValue(String paraName) {
        if (requri.getQuery() == null) {
            return null;
        }
        
        String[] params = requri.getQuery().split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paraName)) {
                return keyValue[1];
            }
        }
        return null;
    }

    
}