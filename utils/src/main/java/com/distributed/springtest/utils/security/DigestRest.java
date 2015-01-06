package com.distributed.springtest.utils.security;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jonas on 2015-01-06.
 */
public class DigestRest {

    public static HttpEntity<String> getHeadersEntity(String serverURI, String username, String hashedPassword) {

        int counter = getCounter(serverURI, username);
        System.err.println("COUNTER: " + counter);

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
        headers.set("nc", username);
        headers.set("digest", digest);

        return new HttpEntity<String>("parameters", headers);
    }

    private static int getCounter(String serverURI, String username) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", username);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity response = restTemplate.exchange(serverURI + "/counter", HttpMethod.GET, entity, String.class);
        return (Integer)response.getBody();
    }
}
