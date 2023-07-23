package ru.branihin.fqw.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.branihin.fqw.models.Appointment;
import ru.branihin.fqw.models.Doctor;
import ru.branihin.fqw.models.Schedule;
import ru.branihin.fqw.models.User;
import ru.branihin.fqw.repo.ScheduleRepository;
import ru.branihin.fqw.repo.UserRepository;
import ru.branihin.fqw.services.AppointmentService;
import ru.branihin.fqw.services.DoctorService;
import ru.branihin.fqw.services.ScheduleService;
import ru.branihin.fqw.services.UserService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class DoctorController {

    @Autowired
    UserService userService;

    @Autowired
    AppointmentService appointmentService;
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    private ScheduleRepository scheduleRepository;


    @Autowired
    private UserRepository userRepository;

    private String userName;

    private int whoLoggedIn(){
        if(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal() instanceof UserDetails){

            User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication()
                    .getName());
            if(user.getRoles().iterator().next().getName().equals("ROLE_USER")){
                userName = user.getLastName() +" "+ user.getName();
                return 1;
            }
            if(user.getRoles().iterator().next().getName().equals("ROLE_ADMIN")){
                userName = user.getLastName() +" "+ user.getName();
                return 2;
            }
            if(user.getRoles().iterator().next().getName().equals("ROLE_DOCTOR")){
                userName = user.getLastName() +" "+ user.getName();
                return 3;
            }
        } else {
            return 0;
        }
        return 0;
    }




    @GetMapping("/doctor")
    public String doctorMain(Model model){
        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);
        return "doctor/doctor";
    }
    private List<String> getDisableDates(){
        Doctor doctor = doctorService.findByUsername(SecurityContextHolder.getContext().getAuthentication()
                .getName());
        Set<Schedule> qwe = doctor.getSchedules();
        Iterator<Schedule> schedules = qwe.iterator();
        List<String> dates = new ArrayList<>();
        while (schedules.hasNext()){
            dates.add(schedules.next().getDate().toString());
        }
        return dates;
    }

    @GetMapping("/doctor/schedule")
    public String doctorSchedule(Model model){
        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);

        LocalDate currentDate = LocalDate.now();


        LocalDate maxDate = currentDate.plusMonths(3L);
        model.addAttribute("disableDates", getDisableDates());
        model.addAttribute("minDate", currentDate);
        model.addAttribute("maxDate", maxDate);
        return "doctor/doctorSchedule";
    }

    @PostMapping("/doctor/schedule")
    public String addSchedule(@RequestParam("date") String date, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date1 = LocalDate.parse(date, formatter);

        if(!scheduleService.saveSchedule(date1)) return "/doctor/schedule";

        return "redirect:/doctor";
    }

    @GetMapping("doctor/appointments")
    public String viewAppointments(Model model){

        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);

        String dateNow = LocalDate.now().toString();

        List<String> workedTimes =  appointmentService.getFutureAppointmentsTime(userService
                .findUserByName(SecurityContextHolder
                        .getContext().getAuthentication().getName())
                .getId(), dateNow, false);


        model.addAttribute("times", workedTimes);
        return "doctor/checkAppointments";
    }

    @PostMapping("doctor/appointments")
    public String startAppointment(@RequestParam("selectedTime") String time, RedirectAttributes attributes) {
        attributes.addAttribute("time", time);
        return "redirect:/doctor/beginAppointment";
    }

    @GetMapping("/doctor/beginAppointment")
    public String begin(@RequestParam(value = "time") String time, Model model){

        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);

        User userDoc = userService.findUserByName
                (SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        Doctor doctor = doctorService.findDoctorById(userDoc.getId());
        Appointment appointment = appointmentService.findAppointmentByDoctorIdAndTimeAndDate(time,
                doctor.getId(), LocalDate.now());
        model.addAttribute("appointment", appointment);

        model.addAttribute("time", time);
        return "doctor/startAppointment";
    }

    @PostMapping("/doctor/beginAppointment")
    public String finish(@RequestParam(value = "appointmentId") Long id,
                         @RequestParam(value = "text") String text){
        Appointment appointment = appointmentService.findAppointmentById(id);
        appointment.setText(text);
        appointment.setStatus(true);
        appointmentService.save(appointment);
        return "redirect:/doctor";
    }

}
