package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;
    @Getter
    @Setter  // asla emin degilim get set methodlarindan

    private String profilePictureUrl; // Dosya yolu ya da URL yani einfach fotograf eklesin diye ekledim.

    private String bio;

    public void updateRole(UserRole newRole) {
        this.role = newRole;
    }

}
