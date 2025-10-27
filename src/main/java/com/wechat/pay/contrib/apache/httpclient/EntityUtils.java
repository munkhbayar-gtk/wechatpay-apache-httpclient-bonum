package com.wechat.pay.contrib.apache.httpclient;

import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EntityUtils {
     private static String readBody(HttpEntity entity, Charset charset) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        entity.writeTo(baos);
        return baos.toString(charset);
    }

    public static String toString(HttpEntity entity) throws IOException, ParseException {
        return toString(entity, StandardCharsets.UTF_8);
    }
    public static String toString(HttpEntity entity, Charset charset) throws IOException, ParseException {
         try{
            return  org.apache.hc.core5.http.io.entity.EntityUtils.toString(entity, charset);
         }catch (UnsupportedOperationException e) {
            return readBody(entity, charset);
         }
    }
}
