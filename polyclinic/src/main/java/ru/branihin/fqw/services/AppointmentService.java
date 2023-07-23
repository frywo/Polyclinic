package ru.branihin.fqw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.branihin.fqw.models.Appointment;
import ru.branihin.fqw.models.User;
import ru.branihin.fqw.repo.AppointmentRepository;

import javax.transaction.Transactional;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    AppointmentRepository appointmentRepository;

    public void save(Appointment appointment){
        appointmentRepository.save(appointment);
    }

    @Transactional
    public List<String> getWorkedTimeByDoctorIdAndDate(Long id, String date, boolean isUser){

        LocalDate date1;
        if(isUser){
            date = date.substring(0, 6) + "20" + date.substring(6, 8);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            date1 = LocalDate.parse(date, formatter);
        } else {
            date1 = LocalDate.parse(date);
        }


        List<Time> timeNotString = appointmentRepository.findTimesByDoctorIdAndDate(id, date1);
        List<String> timeString = new ArrayList<>();

        for (Time time : timeNotString) {
            timeString.add(time.toString().substring(0,5));
        }

        return timeString;
    }

    @Transactional
    public List<String> getFutureAppointmentsTime(Long id, String date, boolean isUser){

        LocalDate date1;
        if(isUser){
            date = date.substring(0, 6) + "20" + date.substring(6, 8);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            date1 = LocalDate.parse(date, formatter);
        } else {
            date1 = LocalDate.parse(date);
        }


        List<Time> timeNotString = appointmentRepository.findTimesFalseByDoctorIdAndDate(id, date1, false);
        List<String> timeString = new ArrayList<>();

        for (Time time : timeNotString) {
            timeString.add(time.toString().substring(0,5));
        }

        return timeString;
    }

    @Transactional
    public Appointment findAppointmentByDoctorIdAndTimeAndDate(String time, Long id, LocalDate date){

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Time timeOk;
        try {
            Date parsedDate = format.parse(time);
            timeOk = new Time(parsedDate.getTime());
            return appointmentRepository.findAppointment(id,timeOk, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Appointment findAppointmentById(Long id){
        return appointmentRepository.findAppointmentById(id);
    }

    public List<Appointment> getFutureAppointments(Long userId, LocalDate date){
        date = date.minusDays(1);
        return appointmentRepository.findFutureAppointmentsByUserId(userId,date);
    }

    public List<Appointment> getHistoryAppointments(Long userId){
        return appointmentRepository.findPastAppointmentsByUserId(userId, true);
    }

}
