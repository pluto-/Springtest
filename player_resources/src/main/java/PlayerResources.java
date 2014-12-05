import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Jonas on 2014-12-05.
 */
@ComponentScan
@EnableAutoConfiguration
public class PlayerResources {

    public static void main(String[] args) {

        SpringApplication.run(PlayerResources.class, args);
    }
}
