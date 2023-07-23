package ru.branihin.fqw.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.branihin.fqw.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
