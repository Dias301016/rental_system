package kz.project.RentalSystem.services;

import kz.project.RentalSystem.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Users getUserByEmail(String email);
}
