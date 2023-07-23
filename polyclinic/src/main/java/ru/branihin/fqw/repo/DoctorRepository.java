package ru.branihin.fqw.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.branihin.fqw.models.Doctor;
import ru.branihin.fqw.models.User;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
//    List<Doctor> findAll();
}
