package cs.daasha.quiz_t.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username
    @Column(nullable = false, unique = true)
    private String username;

    // Password
    @Column(nullable = false)
    private String password;

    // First name
    @Column(nullable = false)
    private String firstName;

    // Last name
    @Column(nullable = false)
    private String lastName;

    // Email
    @Column(nullable = false, unique = true)
    private String email;

    // User role
    @Enumerated(EnumType.STRING)
    private Role role;

    // Profile picture
    private String profilePicture;

    // Phone number
    private String phoneNumber;

    // Bio
    private String bio;

    // Date of birth
    private LocalDate dateOfBirth;
}