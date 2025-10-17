package com.wechat.pay.contrib.apache.httpclient;

import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hc.core5.http.io.entity.BufferedHttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.HttpRequestWrapper;
import org.apache.hc.core5.http.message.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.wechat.pay.contrib.apache.httpclient.constant.WechatPayHttpHeaders.WECHAT_PAY_SERIAL;

public class SignatureExec5 implements ExecChainHandler {
    private static final String WECHAT_PAY_HOST_NAME_SUFFIX = ".mch.weixin.qq.com";
    private static final Logger log = LoggerFactory.getLogger(SignatureExec5.class);
    private final Credentials credentials;
    private final Validator validator;

    public SignatureExec5(Credentials credentials, Validator validator) {
        this.credentials = credentials;
        this.validator = validator;
    }

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request, ExecChain.Scope scope, ExecChain chain) throws IOException, HttpException {
        String host = request.getAuthority().getHostName();
        if(host.endsWith(WECHAT_PAY_HOST_NAME_SUFFIX)) {
            return executeWithSignature(request, scope, chain);
        }else {
            return chain.proceed(request, scope);
        }
    }

    private ClassicHttpResponse executeWithSignature(ClassicHttpRequest request, ExecChain.Scope scope, ExecChain chain) throws IOException, HttpException {

        request.addHeader(HttpHeaders.AUTHORIZATION, credentials.getSchema() + " " + credentials.getToken(request));
        request.addHeader(WECHAT_PAY_SERIAL, validator.getSerialNumber());

        ClassicHttpResponse response = chain.proceed(request, scope);

        int code = response.getCode();
        if(code >= HttpStatus.SC_OK && code < HttpStatus.SC_MULTIPLE_CHOICES) {
            convertToRepeatableResponseEntity(response);
            if(!validator.validate(response)) {
                throw new HttpException("应答的微信支付签名验证失败");
            }
        }else {
            // 错误应答需要打日志
            log.error("应答的状态码不为200-299。status code[{}]\trequest headers[{}]", code,
                    Arrays.toString(request.getHeaders()));
            if(isEntityEnclosing(request) && !isUploadHttpPost(request)) {
                HttpEntity entity = request.getEntity();
                String body = EntityUtils.toString(entity);
                log.error("应答的状态码不为200-299。request body[{}]", body);
            }
        }
        return response;
    }


    private boolean isEntityEnclosing(ClassicHttpRequest request) {
        return request.getEntity() != null;
    }

    protected void convertToRepeatableRequestEntity(ClassicHttpRequest request) throws IOException {
        if (isEntityEnclosing(request)) {
            HttpEntity entity = request.getEntity();
            if (entity != null) {
                request.setEntity(new BufferedHttpEntity(entity));
            }
        }
    }
    protected void convertToRepeatableResponseEntity(ClassicHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            response.setEntity(new BufferedHttpEntity(entity));
        }
    }

    private boolean isUploadHttpPost(ClassicHttpRequest request) {
        return request instanceof WechatPayUploadHttpPost;
    }
}
