package com.distributed.springtest.utils.records;

import java.sql.SQLException;

/**
 * Created by Patrik on 2014-12-04.
 */
public class Generator {

    public static void main( String[] args ) throws SQLException {
        try {
            com.jajja.jorm.generator.Generator generator = new com.jajja.jorm.generator.Generator();
            generator.addDatabase("login", "com.distributed.springtest.utils.records.login").addSchema("public").addTable("user_authorization");
            generator.getDatabase("login").getSchema("public").addTable("user_authentication");
            generator.fetchMetadata();
            System.out.println(generator);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}