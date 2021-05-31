package kz.project.RentalSystem.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="f_name")
    private String fName;

    @Column(name="user_avatar")
    private String userAvatar;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Roles> roles;

}
