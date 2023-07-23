package ru.branihin.fqw.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.branihin.fqw.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}