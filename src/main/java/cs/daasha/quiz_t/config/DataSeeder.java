package cs.daasha.quiz_t.config;

import cs.daasha.quiz_t.entity.Role;
import cs.daasha.quiz_t.entity.User;
import cs.daasha.quiz_t.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("op@1234"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@quizapp.com");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}