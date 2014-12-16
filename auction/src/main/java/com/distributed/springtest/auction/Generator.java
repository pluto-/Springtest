package com.distributed.springtest.auction;

import java.sql.SQLException;

/**
 * Created by Patrik on 2014-12-04.
 */
public class Generator {

    public static void main( String[] args ) throws SQLException {
        try {
            com.jajja.jorm.generator.Generator generator = new com.jajja.jorm.generator.Generator();
            generator.addDatabase("auction", "com.distributed.springtest.auction.records").addSchema("public").addTable("completed_auctions");
            generator.fetchMetadata();
            System.out.println(generator);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}