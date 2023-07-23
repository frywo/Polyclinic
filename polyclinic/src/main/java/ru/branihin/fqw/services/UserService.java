package ru.branihin.fqw.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.branihin.fqw.models.Doctor;
import ru.branihin.fqw.models.Role;
import ru.branihin.fqw.models.User;
import ru.branihin.fqw.repo.RoleRepository;
import ru.branihin.fqw.repo.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;

    @Autowired
    DoctorService doctorService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public User findUserByName(String name) { return userRepository.findByUsername(name);
    }


    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean setRole(String role, String user, String speciality){
        User userFromDB;
        if((userFromDB = userRepository.findByUsername(user)) == null) return false;

        Role roleDoctor = roleRepository.findByName("ROLE_DOCTOR");
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");


        switch (role){
            case "DOCTOR":
                Set<Role> doctorSet = new HashSet<>();
                doctorSet.add(roleDoctor);
                userFromDB.setRoles(doctorSet);

                Doctor doctor = new Doctor();
                doctor.setUser(userFromDB);
                doctor.setSpeciality(speciality);
                doctor.setWork(true);
                doctor.setId(userFromDB.getId());
                userFromDB.setDoctor(doctor);

                userRepository.save(userFromDB);
                doctorService.saveDoctor(doctor);

                 return true;
            case "ADMIN":
                Set<Role> adminSet = new HashSet<>();
                adminSet.add(roleAdmin);
                userFromDB.setRoles(adminSet);
                userRepository.save(userFromDB);
                return true;
        }

        return false;
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT * FROM User * WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }
}