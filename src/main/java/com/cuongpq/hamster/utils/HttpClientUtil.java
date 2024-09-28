package com.cuongpq.hamster.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtil {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";
    private static final Header[] HEADERS = {
            new BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT),
            new BasicHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE),
            new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    };

    // HTTP GET request
    public static String sendGet(String url, Map<String, String> headers) throws Exception {
        HttpGet request = new HttpGet(url);

        request.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
        headers.forEach(request::addHeader);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);

        return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
    }

    // HTTP POST request
    public static String sendPost(String url, JSONObject parameters) throws Exception {
        return sendPost(url, parameters, new HashMap<>());
    }

    public static String sendPost(String url, JSONObject parameters, Map<String, String> headers) throws Exception {
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeaders(HEADERS);
        headers.forEach(post::addHeader);
        post.setEntity(new StringEntity(parameters.toString(), ContentType.APPLICATION_JSON));
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(post);

        return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
    }

    public static String sendPut(String url, Map<String, String> headers) throws Exception {
        HttpPut put = new HttpPut(url);

        put.setHeaders(HEADERS);
        headers.forEach(put::addHeader);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(put);

        return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
    }
}
