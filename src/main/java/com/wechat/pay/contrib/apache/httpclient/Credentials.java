package com.wechat.pay.contrib.apache.httpclient;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.HttpRequestWrapper;

import java.io.IOException;
import java.net.URISyntaxException;
//import org.apache.http.client.methods.HttpRequestWrapper;

/**
 * @author xy-peng
 */
public interface Credentials {

    String getSchema();

    String getToken(HttpRequestWrapper request) throws IOException;

    String getToken(ClassicHttpRequest request) throws IOException;
}
