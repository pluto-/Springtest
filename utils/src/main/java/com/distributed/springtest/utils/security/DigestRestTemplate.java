package com.distributed.springtest.utils.security;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to create RestTemplates containing headers used by digest authentication.
 *
 * Created by Jonas on 2015-01-06.
 */
public class DigestRestTemplate {

    private String serverURI;
    private String username;
    private String hashedPassword;
    private RestTemplate template;

    public DigestRestTemplate(String serverURI, String username, String hashedPassword) {
        this.serverURI = serverURI;
        this.username = username;
        this.hashedPassword = hashedPassword;
        template = new RestTemplate();
    }

    public <T> ResponseEntity<T> get(String uri, Class<T> responseType) {
        HttpEntity<Object> parameters = getHeadersEntity(serverURI, null, username, hashedPassword);
        return template.exchange(uri, HttpMethod.GET, parameters, responseType);
    }

    public <T> ResponseEntity<T> post(String uri, Object request, Class<T> responseType) {
        HttpEntity<Object> parameters = getHeadersEntity(serverURI, request, username, hashedPassword);
        return template.exchange(uri, HttpMethod.POST, parameters, responseType);
    }

    public void put(String uri, Object request) {
        HttpEntity<Object> parameters = getHeadersEntity(serverURI, request, username, hashedPassword);
        template.exchange(uri, HttpMethod.PUT, parameters, Class.class);
    }

    public void delete(String uri) {
        HttpEntity<Object> parameters = getHeadersEntity(serverURI, null, username, hashedPassword);
        template.exchange(uri, HttpMethod.DELETE, parameters, Class.class);
    }

    /**
     * Constructs the header by asking the server what counter it has and uses the counter+1. Adds the username and
     * creates a digest which is also used as header.
     *
     * @param serverURI the URI to the target server. The server must be able to answer to /counter-requests.
     * @param body the body to be sent (can be null).
     * @param username the username.
     * @param hashedPassword the password hashed with MD5.
     * @return the resulting HttpEntity.
     */
    private HttpEntity getHeadersEntity(String serverURI, Object body, String username, String hashedPassword) {

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
            return new HttpEntity(headers);
        } else {
            return new HttpEntity(body, headers);
        }
    }

    /**
     * Asks the server for it's counter.
     *
     * @param serverURI the server URI.
     * @param username the username.
     * @return
     */
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
