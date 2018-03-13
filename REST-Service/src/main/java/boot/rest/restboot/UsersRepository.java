package boot.rest.restboot;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface UsersRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    User findByUsername(String username);
    User findById(long id);
    void delete(User user);
}