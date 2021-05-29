package kz.project.RentalSystem.services.impl;

import kz.project.RentalSystem.entities.Users;
import kz.project.RentalSystem.repositories.UserRepository;
import kz.project.RentalSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(s);
        if(user!=null) {
            User secUser = new User(user.getEmail(), user.getPassword(), user.getRoles());
            return secUser;
        }
        throw new UsernameNotFoundException("User not found");
    }
}
