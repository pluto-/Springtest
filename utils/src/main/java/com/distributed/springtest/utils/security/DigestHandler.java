package com.distributed.springtest.utils.security;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonas on 2015-01-06.
 */
public class DigestHandler {

    private Map<String, DigestUser> users;

    public DigestHandler(InputStream fileStream) throws IOException {
        users = new HashMap<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
        try {
            String line = br.readLine();

            while (line != null) {
                String[] split = line.split(" ");

                String username = split[0];
                String password = split[1];

                users.put(username, new DigestUser(username, password));

                line = br.readLine();
            }
        } finally {
            br.close();
        }
    }

    public boolean handle(String username, int nc, String digest) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // digest should be username:MD5(password):nc
        DigestUser user = users.get(username);

        if(user == null || nc <= user.getNc()) {
            return false;
        }

        String correctString = user.getUsername() + ":" + user.getHashedPassword() + ":" + nc;

        MessageDigest m=MessageDigest.getInstance("MD5");
        m.update(correctString.getBytes(),0,correctString.length());
        String correctDigest = new BigInteger(1,m.digest()).toString(16);

        if(!correctDigest.equals(digest)) {
            return false;
        }

        user.setNc(nc);
        return true;
    }

    public int getCounter(String username) {
        if(!users.containsKey(username)) {
            return -1;
        }
        return users.get(username).getNc();
    }
}
