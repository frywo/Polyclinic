package ru.branihin.fqw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.branihin.fqw.models.Doctor;
import ru.branihin.fqw.models.Schedule;
import ru.branihin.fqw.models.User;
import ru.branihin.fqw.repo.DoctorRepository;
import ru.branihin.fqw.repo.UserRepository;

import java.util.*;


@Service
public class DoctorService {

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    UserRepository userRepository;


    public boolean saveDoctor(Doctor doctor){
        User userFromDB;

        try {
            userFromDB = userRepository.findById(doctor.getId()).get();
        } catch (NoSuchElementException e){
            return false;
        }

        if(doctorRepository.findById(doctor.getId()).isPresent()) return false;

        if(!userFromDB.getRoles().iterator().next().getName().equals("ROLE_DOCTOR")) return false;

        doctorRepository.save(doctor);

        return true;
    }

    public List<String> getWorkDates(Long id){
        Doctor doctor = doctorRepository.findById(id).get();
        Set<Schedule> qwe = doctor.getSchedules();
        Iterator<Schedule> schedules = qwe.iterator();
        List<String> dates = new ArrayList<>();
        while (schedules.hasNext()){
            dates.add(schedules.next().getDate().toString());
        }
        return dates;
    }

    public Doctor findByUsername(String username){
        User user = userRepository.findByUsername(username);
        if(user== null) return null;
        return doctorRepository.findById(user.getId()).get();
    }

    public boolean fireDoctorById(Long id){
        if(!doctorRepository.findById(id).isPresent()){
            return false;
        }
        Doctor doctor = doctorRepository.findById(id).get();
        doctor.setWork(false);
        return true;
    }

    public List<Doctor> findAll(){
        return doctorRepository.findAll();
    }


    public Doctor findDoctorById(Long id){
        return doctorRepository.findById(id).get();
    }
}
