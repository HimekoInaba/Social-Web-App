package boot.rest.restboot;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface RolesRepository extends JpaRepository<Roles, Long> {
}