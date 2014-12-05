package database;

import java.sql.SQLException;

/**
 * Created by Patrik on 2014-12-04.
 */
public class Generator {

    public static void main( String[] args ) throws SQLException {
        try {
            com.jajja.jorm.generator.Generator generator = new com.jajja.jorm.generator.Generator();
            generator.addDatabase("player_resources", "com.distributed.player_resources.records").addSchema("public").addTable("buildings");
            generator.fetchMetadata();
            System.out.println(generator);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
