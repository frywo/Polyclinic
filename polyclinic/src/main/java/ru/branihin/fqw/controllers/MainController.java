package ru.branihin.fqw.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.branihin.fqw.models.User;
import ru.branihin.fqw.repo.UserRepository;

@Controller
public class MainController {

    @Autowired
    UserRepository userRepository;

    String userName;

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



    @GetMapping("/")
    public String homePage(Model model){

        model.addAttribute("isUserLoggedIn", whoLoggedIn());
        model.addAttribute("NameUser", userName);
        return "home";
    }

    @GetMapping("/home")
    public String homePg(){
        return "home";
    }
}
