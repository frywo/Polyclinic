package ru.branihin.fqw.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.branihin.fqw.models.Appointment;
import ru.branihin.fqw.models.Schedule;
import ru.branihin.fqw.models.User;
import ru.branihin.fqw.repo.ScheduleRepository;
import ru.branihin.fqw.repo.UserRepository;
import ru.branihin.fqw.services.AppointmentService;
import ru.branihin.fqw.services.DoctorService;
import ru.branihin.fqw.services.ScheduleService;
import ru.branihin.fqw.services.UserService;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ScheduleService scheduleService;


    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AppointmentService appointmentService;


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



    private Time parseStringTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            long ms = format.parse(time).getTime();
            return new Time(ms);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/user")
    public String doctorMain(Model model){

        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);
        return "user/user";
    }

    @GetMapping("/user/addRequest")
    public String doctorSchedule(@RequestParam(value = "doctorId", required = false) Long doctorId, Model model){

        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);

        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = currentDate.plusMonths(3L);
        model.addAttribute("doctors", doctorService.findAll());

        if (doctorId != null) {

            model.addAttribute("schedules", doctorService.getWorkDates(doctorId));
            model.addAttribute("minDate", currentDate);
            model.addAttribute("maxDate", maxDate);
            model.addAttribute("DOCTOR", doctorId);
        } else {
            model.addAttribute("appointment", new Appointment());
        }
        return "user/addAppointmentRequest";
    }

    @PostMapping("/user/addRequest")
    public String addSchedule(@RequestParam("date") String date,
                              @RequestParam("docId") Long id, Model model,
                              RedirectAttributes redirectAttributes) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date1 = LocalDate.parse(date, formatter);

        redirectAttributes.addAttribute("doctorId", id);
        redirectAttributes.addAttribute("date", date1);


        return "redirect:/user/setTime";
    }

    @GetMapping("/user/setTime")
    public String timeAppointmentGet(@RequestParam(value = "doctorId") Long id,
                                     @RequestParam(value = "date") String date, Model model){

        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);

        model.addAttribute("docId", id);
        model.addAttribute("date", date);
        model.addAttribute("blockedTimes", appointmentService.getWorkedTimeByDoctorIdAndDate(id, date, true));
        return "user/setTime";
    }

    @PostMapping("/user/setTime")
    public String timeAppointmentPost(@RequestParam("time") String time,
                                      @RequestParam("docId") Long docId,
                                      @RequestParam("date") String date){

        date = date.substring(0, 6) + "20" + date.substring(6, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date1 = LocalDate.parse(date, formatter);


        Time parsedTime = parseStringTime(time);
        if(parsedTime== null){
            return "user/addAppointmentRequest";
        }


        Appointment appointment = new Appointment();
        appointment.setUser(userService.findUserByName(
                SecurityContextHolder.getContext()
                        .getAuthentication().getName()));
        appointment.setDate(date1);
        appointment.setTime(parsedTime);
        appointment.setDoctor(doctorService.findDoctorById(docId));
        appointmentService.save(appointment);
        return "redirect:/user";
    }


    @GetMapping("/user/checkFutureAppointments")
    public String checkFutureAppointmentsGet(Model model){
        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);

        List<Appointment> appointments = appointmentService.getFutureAppointments(
                userService.findUserByName(
                        SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName())
                        .getId(),
                LocalDate.now()
        );
        model.addAttribute("appointments", appointments);

        return "user/FutureAppointments";
    }

    @PostMapping("/user/checkFutureAppointments")
    public String checkFutureAppointmentsPost(){
        return "redirect:/user";
    }

    @GetMapping("/user/checkHistory")
    public String checkHistoryGet(Model model){

        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);

        List<Appointment> appointments = appointmentService.getHistoryAppointments(
                userService.findUserByName(
                                SecurityContextHolder.getContext()
                                        .getAuthentication()
                                        .getName())
                        .getId()
        );
        model.addAttribute("appointments", appointments);
        return "user/checkHistoryAppointments";
    }

    @PostMapping("/user/checkHistory")
    public String checkHistoryPost(){
        return "redirect:/user";
    }
}
