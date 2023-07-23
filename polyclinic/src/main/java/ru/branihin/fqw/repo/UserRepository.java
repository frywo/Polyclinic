package ru.branihin.fqw.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.branihin.fqw.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}