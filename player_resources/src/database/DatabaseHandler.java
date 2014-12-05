package database;

/**
 * Created by Jonas on 2014-12-05.
 */
public class DatabaseHandler {

    public DatabaseHandler(int port) {
        Generator generator = new com.jajja.jorm.generator.Generator();
        generator.addDatabase("springtest", "com.distributed.springtest.records").addSchema("public").addTable("users");
        generator.fetchMetadata();
    }
}
