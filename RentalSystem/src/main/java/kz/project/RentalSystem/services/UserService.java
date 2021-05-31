package kz.project.RentalSystem.services;

import kz.project.RentalSystem.entities.Roles;
import kz.project.RentalSystem.entities.Users;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    Users getUserByEmail(String email);
    Users getUser(Long id);
    Users createUser(Users user);
    List<Users> getAllUsers();
    Users saveUser(Users user);


}
