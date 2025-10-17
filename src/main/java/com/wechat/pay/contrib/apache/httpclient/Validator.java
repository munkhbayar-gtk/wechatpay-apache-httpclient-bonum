package com.wechat.pay.contrib.apache.httpclient;

import java.io.IOException;

import org.apache.hc.core5.http.ClassicHttpResponse;

/**
 * @author xy-peng
 */
public interface Validator {

    boolean validate(ClassicHttpResponse response) throws IOException;

    String getSerialNumber();
}
