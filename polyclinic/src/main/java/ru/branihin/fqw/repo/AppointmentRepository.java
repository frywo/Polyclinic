package ru.branihin.fqw.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.branihin.fqw.models.Appointment;
import ru.branihin.fqw.models.User;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a.time FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date")
    List<Time> findTimesByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT a.time FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date AND a.status = :status")
    List<Time> findTimesFalseByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("status") Boolean status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.time = :time AND a.date = :date")
    Appointment findAppointment(@Param("doctorId") Long doctorId, @Param("time") Time time, @Param("date") LocalDate date);
    Appointment findAppointmentById(Long id);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.date > :date")
    List<Appointment> findFutureAppointmentsByUserId(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND status = :status")
    List<Appointment> findPastAppointmentsByUserId(@Param("userId") Long userId,  @Param("status") Boolean status);
}
