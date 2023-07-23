package ru.branihin.fqw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.branihin.fqw.models.Doctor;
import ru.branihin.fqw.models.Schedule;
import ru.branihin.fqw.repo.ScheduleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ScheduleService {
    @Autowired
    DoctorService doctorService;

    @Autowired
    ScheduleRepository scheduleRepository;

    public boolean saveSchedule(LocalDate date){
        Doctor doctor = doctorService.findByUsername(SecurityContextHolder.getContext().getAuthentication()
                .getName());
        if(doctor == null)  return false;
        Schedule schedule = new Schedule();
        Set<Schedule>scheduleSet = doctor.getSchedules();
        if(scheduleSet == null){
            scheduleSet = new HashSet<>();
        }
        scheduleSet.add(schedule);
        schedule.setDate(date);
        schedule.setDoctor(doctor);
        doctor.setSchedules(scheduleSet);

        scheduleRepository.save(schedule);
        return true;
    }

    public List<Schedule> findByDoctorId(Long id){
        Doctor doctor = doctorService.findDoctorById(id);
        if(doctor==null) return null;
        return new ArrayList<Schedule>(doctor.getSchedules());
    }

}
