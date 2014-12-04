package hello;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import com.distributed.springtest.records.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping("/add")
    public Object addUser(@RequestParam("name") String name, @RequestParam("isMan") Boolean isMan) throws SQLException {
        User user = new User();
        user.setName(name);
        user.setIsMan(isMan);
        user.save();
        user.transaction().commit();
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }
}