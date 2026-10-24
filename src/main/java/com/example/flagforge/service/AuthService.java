package com.example.flagforge.service;

import com.example.flagforge.dto.LoginRequest;
import com.example.flagforge.dto.LoginResponse;
import com.example.flagforge.dto.UserRequest;
import com.example.flagforge.dto.UserResponse;
import com.example.flagforge.entity.User;
import com.example.flagforge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.flagforge.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
    }

    public UserResponse createUser(UserRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists");
        }
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User saveddUser=userRepository.save(user);
        return new UserResponse(
                saveddUser.getName(),
                saveddUser.getEmail(),
                saveddUser.getCreatedAt()
        );
    }
    public LoginResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.email()).orElseThrow(()->
                new RuntimeException(
                        "Invalid email and password "
                ));
        if(!passwordEncoder.matches( request.password(),user.getPasswordHash())){
            throw new RuntimeException("Invalid email and password");
        }
        String token=jwtService.generateToken(user.getEmail());
        return new LoginResponse(
                token,
                "Bearer",
                user.getName(),
                user.getEmail()
        );

    }
}
