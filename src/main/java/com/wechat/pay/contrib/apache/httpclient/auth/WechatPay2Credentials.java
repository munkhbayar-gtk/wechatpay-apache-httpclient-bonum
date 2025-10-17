package com.wechat.pay.contrib.apache.httpclient.auth;

import com.wechat.pay.contrib.apache.httpclient.Credentials;
import com.wechat.pay.contrib.apache.httpclient.WechatPayUploadHttpPost;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.HttpRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xy-peng
 */
public class WechatPay2Credentials implements Credentials {

    protected static final Logger log = LoggerFactory.getLogger(WechatPay2Credentials.class);

    protected static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    protected static final SecureRandom RANDOM = new SecureRandom();
    protected final String merchantId;
    protected final Signer signer;

    public WechatPay2Credentials(String merchantId, Signer signer) {
        this.merchantId = merchantId;
        this.signer = signer;
    }

    public String getMerchantId() {
        return merchantId;
    }

    protected long generateTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    protected String generateNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

    @Override
    public final String getSchema() {
        return "WECHATPAY2-SHA256-RSA2048";
    }


    @Override
    public final String getToken(HttpRequestWrapper request) throws IOException {
        /*
        String nonceStr = generateNonceStr();
        long timestamp = generateTimestamp();

        String message = buildMessage(nonceStr, timestamp, request);
        log.debug("authorization message=[{}]", message);

        Signer.SignatureResult signature = signer.sign(message.getBytes(StandardCharsets.UTF_8));

        String token = "mchid=\"" + getMerchantId() + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + signature.certificateSerialNumber + "\","
                + "signature=\"" + signature.sign + "\"";
        log.debug("authorization token=[{}]", token);

        return token;
         */
        throw new IllegalArgumentException("Not implemented yed");
    }

    @Override
    public String getToken(ClassicHttpRequest request) throws IOException{
        String nonceStr = generateNonceStr();
        long timestamp = generateTimestamp();

        try{
            String message = buildMessage(nonceStr, timestamp, request);
            log.debug("authorization message=[{}]", message);

            Signer.SignatureResult signature = signer.sign(message.getBytes(StandardCharsets.UTF_8));
            String token = "mchid=\"" + getMerchantId() + "\","
                    + "nonce_str=\"" + nonceStr + "\","
                    + "timestamp=\"" + timestamp + "\","
                    + "serial_no=\"" + signature.certificateSerialNumber + "\","
                    + "signature=\"" + signature.sign + "\"";
            log.debug("authorization token=[{}]", token);
            return token;
        } catch (ParseException | URISyntaxException e) {
            throw new IOException(e);
        }
    }

    protected String buildMessage(String nonce, long timestamp, HttpRequestWrapper request) throws IOException {
        /*
        URI uri = request.getURI();
        String canonicalUrl = uri.getRawPath();
        if (uri.getQuery() != null) {
            canonicalUrl += "?" + uri.getRawQuery();
        }

        String body = "";
        // PATCH,POST,PUT

        if (request instanceof WechatPayUploadHttpPost) {
            body = ((WechatPayUploadHttpPost) request).getMeta();
        } else if (request.get) {
            body = EntityUtils.toString(((HttpEntityEnclosingRequest) request).getEntity(), StandardCharsets.UTF_8);
        }

        return request.getMethod() + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonce + "\n"
                + body + "\n";

         */
        throw new RuntimeException("Not implemented yet");
    }

    protected String buildMessage(String nonce, long timestamp, ClassicHttpRequest request) throws IOException, URISyntaxException, ParseException {
        URI uri = request.getUri();
        String canonicalUrl = uri.getRawPath();
        if (uri.getQuery() != null) {
            canonicalUrl += "?" + uri.getRawQuery();
        }

        String body = "";
        // PATCH,POST,PUT
        if (request instanceof WechatPayUploadHttpPost) {
            body = ((WechatPayUploadHttpPost) request).getMeta();
        } else if (request.getEntity() != null) {
            body = EntityUtils.toString(request.getEntity(), StandardCharsets.UTF_8);
        }

        return request.getMethod() + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonce + "\n"
                + body + "\n";
    }

}
