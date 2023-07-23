package ru.branihin.fqw.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.branihin.fqw.models.User;
import ru.branihin.fqw.repo.UserRepository;
import ru.branihin.fqw.services.UserService;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;

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

    @GetMapping("/admin")
    public String userList(Model model) {
        model.addAttribute("allUsers", userService.allUsers());
        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);
        return "admin/admin";
    }

    @GetMapping("/admin/add")
    public String addRole(Model model){
        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);
        return "admin/addRole";
    }

    @PostMapping("/admin/add")
    public String addRolePost(@ModelAttribute("username") String nameUser,
                              @ModelAttribute("userRole") String role,
                              @ModelAttribute("speciality") String speciality){

        if(userService.setRole(role, nameUser, speciality)){
            return "redirect:/admin";
        }
        return "admin/addRole";
    }

    @GetMapping("/admin/deleteUser")
    public  String deletePage(Model model){
        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);
        return "admin/deleteUser";}


    @PostMapping("/admin/deleteUser")
    public String deleteUser(@ModelAttribute("username") String nameUser){
        User user = userService.findUserByName(nameUser);
        if(user==null){
            return "admin/deleteUser";
        }
        if(userService.deleteUser(user.getId())){
            return "redirect:/admin";
        }
        return "admin/deleteUser";
    }

    @PostMapping("/admin")
    public String  deleteUser(@RequestParam(required = true, defaultValue = "" ) Long userId,
                              @RequestParam(required = true, defaultValue = "" ) String action,
                              Model model) {
        if (action.equals("delete")){
            userService.deleteUser(userId);
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin/gt/{userId}")
    public String  gtUser(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);
        model.addAttribute("allUsers", userService.usergtList(userId));
        return "admin/admin";
    }

}