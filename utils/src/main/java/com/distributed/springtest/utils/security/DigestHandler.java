package com.distributed.springtest.utils.security;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the incoming digests. It contains a list of DigestUsers (which contains the username, password
 * and counter).
 *
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

    /**
     * Checks whether the incoming data is correct. Checks whether the username exists, if the nc is greater than the
     * last one and if the digest is correct.
     *
     * @param username the username in the header.
     * @param nc the counter received. Must be greater than the counter that the DigestHandler has.
     * @param digest the received digest, will be checks that this is the same as the correct digest.
     * @return true if all is valid, otherwise false.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
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
