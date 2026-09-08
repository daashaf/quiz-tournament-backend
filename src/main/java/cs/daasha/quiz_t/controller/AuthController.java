package cs.daasha.quiz_t.controller;

import cs.daasha.quiz_t.dto.RegisterRequest;
import cs.daasha.quiz_t.dto.UserResponse;
import cs.daasha.quiz_t.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse savedUser = userService.registerPlayer(request);
        return ResponseEntity.ok(savedUser);
    }


}