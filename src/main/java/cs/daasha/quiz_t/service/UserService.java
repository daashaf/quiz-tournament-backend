package cs.daasha.quiz_t.service;

import cs.daasha.quiz_t.dto.RegisterRequest;
import cs.daasha.quiz_t.dto.UpdateProfileRequest;
import cs.daasha.quiz_t.dto.UserResponse;
import cs.daasha.quiz_t.entity.Role;
import cs.daasha.quiz_t.entity.User;
import cs.daasha.quiz_t.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerPlayer(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(Role.PLAYER);
        User saved = userRepository.save(user);

        return toUserResponse(saved);
    }

    public UserResponse updateProfile(String currentUsername, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBio(request.getBio());
        User saved = userRepository.save(user);

        return toUserResponse(saved);
    }

    private UserResponse toUserResponse(User saved) {
        UserResponse response = new UserResponse();
        response.setId(saved.getId());
        response.setUsername(saved.getUsername());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());
        response.setEmail(saved.getEmail());
        response.setRole(saved.getRole());
        return response;
    }

    public UserResponse createInstructor(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(Role.INSTRUCTOR);
        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    public UserResponse createAdmin(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(Role.ADMIN);
        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }
}