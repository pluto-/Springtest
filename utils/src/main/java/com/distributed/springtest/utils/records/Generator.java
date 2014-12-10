package com.distributed.springtest.utils.records;

import java.sql.SQLException;

/**
 * Created by Patrik on 2014-12-04.
 */
public class Generator {

    public static void main( String[] args ) throws SQLException {
        try {
            com.jajja.jorm.generator.Generator generator = new com.jajja.jorm.generator.Generator();
            generator.addDatabase("login", "com.distributed.springtest.login.login").addSchema("public").addTable("players");
            generator.fetchMetadata();
            System.out.println(generator);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
