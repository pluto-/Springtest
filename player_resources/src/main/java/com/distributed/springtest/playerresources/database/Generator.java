package com.distributed.springtest.playerresources.database;

import java.sql.SQLException;

/**
 * Created by Patrik on 2014-12-04.
 */
public class Generator {

    public static void main( String[] args ) throws SQLException {
        try {
            com.jajja.jorm.generator.Generator generator = new com.jajja.jorm.generator.Generator();
            generator.addDatabase("login", "com.distributed.login.gamecontent").addSchema("public").addTable("players");
            generator.fetchMetadata();
            System.out.println(generator);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
