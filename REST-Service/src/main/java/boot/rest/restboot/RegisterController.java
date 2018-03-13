package boot.rest.restboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    /*private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/rest/register")
    public User register(@RequestParam(value="username", defaultValue = "default") String username,
                         @RequestParam(value="password", defaultValue = "default") String password,
                         @RequestParam(value="confirmPassword", defaultValue = "default") String confirmPassword,
                         @RequestParam(value="email", defaultValue = "default") String email) {
        return new User(counter.incrementAndGet(),
                username, password, confirmPassword, email);
    }*/

    @RequestMapping(value = "/newuser", method = RequestMethod.POST)
    public ResponseEntity<String> createEmployee(@RequestBody User user)
    {
        System.out.println(user.getUsername());
        userService.add(user);

        return new ResponseEntity(HttpStatus.CREATED);
    }
}
