package kz.vaadin.repository;

import kz.vaadin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    User findByUsername(String username);
    User findById(long id);
}
