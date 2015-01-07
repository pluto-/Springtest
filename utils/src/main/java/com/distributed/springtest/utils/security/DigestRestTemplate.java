package com.distributed.springtest.utils.security;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jonas on 2015-01-06.
 */
public class DigestRestTemplate extends RestTemplate {

    private String serverURI;
    private String username;
    private String hashedPassword;

    public DigestRestTemplate(String serverURI, String username, String hashedPassword) {
        this.serverURI = serverURI;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public <T> ResponseEntity<T> get(String uri, Class<T> responseType) {
        HttpEntity<Object> parameters = getHeadersEntity(serverURI, null, username, hashedPassword);
        return this.exchange(uri, HttpMethod.GET, parameters, responseType);
    }

    public <T> ResponseEntity<T> post(String uri, Object request, Class<T> responseType) {
        HttpEntity<Object> parameters = getHeadersEntity(serverURI, request, username, hashedPassword);
        return this.exchange(uri, HttpMethod.POST, parameters, responseType);
    }

    public void put(String uri, Object request) {
        HttpEntity<Object> parameters = getHeadersEntity(serverURI, request, username, hashedPassword);
        this.put(uri, HttpMethod.PUT, parameters);
    }

    private HttpEntity<Object> getHeadersEntity(String serverURI, Object body, String username, String hashedPassword) {

        int counter = getCounter(serverURI, username) + 1;

        if(counter == -1) {
            return null;
        }

        String string = username + ":" + hashedPassword + ":" + counter;

        MessageDigest m= null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(string.getBytes(),0,string.length());
        String digest = new BigInteger(1,m.digest()).toString(16);

        HttpHeaders headers = new HttpHeaders();
        headers.set("username", username);
        headers.set("nc", String.valueOf(counter));
        headers.set("digest", digest);

        if(body == null) {
            return new HttpEntity<Object>("parameters", headers);
        } else {
            return new HttpEntity<Object>(body, headers);
        }
    }

    private int getCounter(String serverURI, String username) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", username);
        HttpEntity<String> parameters = new HttpEntity<String>("parameters", headers);
        ResponseEntity<Integer> nc = restTemplate.exchange(serverURI + "/counter", HttpMethod.GET, parameters, Integer.class);
        if(nc.getStatusCode() != HttpStatus.OK) {
            return -1;
        } else {
            return nc.getBody();
        }
    }
}
