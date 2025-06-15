package com.project.mycars.controller;

import com.project.mycars.dto.AuthRequest;
import com.project.mycars.dto.AuthResponse;
import com.project.mycars.dto.CarDTO;
import com.project.mycars.dto.UserResponse;
import com.project.mycars.model.User;
import com.project.mycars.repository.UserRepository;
import com.project.mycars.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, MessageSource messageSource) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.login(),
                    request.password()));
            Optional<User> userBD = userRepository.findByLogin(request.login());
            if (userBD.isEmpty()) {
                String message = messageSource.getMessage("user.login.password.invalid", null, Locale.getDefault());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
            }
            User user = userBD.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            String token = jwtUtil.generateToken(userBD.get().getLogin());

            return ResponseEntity.ok(new AuthResponse(token, userBD.get().getId(), userBD.get().getFirstName(),
                    userBD.get().getLastName(), userBD.get().getBirthday(), userBD.get().getPhone(),
                    userBD.get().getLogin()));

        } catch (BadCredentialsException e) {
            String message = messageSource.getMessage("user.login.password.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);

        } catch (InternalAuthenticationServiceException e) {
            String message = messageSource.getMessage("user.login.password.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User userDetails) {

        Optional<User> userBD = userRepository.findByLogin(userDetails.getLogin());
        if (userBD.isEmpty()) {
            String message = messageSource.getMessage("user.not.found", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        User user = userBD.get();

        List<CarDTO> carDtos = user.getCars().stream()
                .map(car -> new CarDTO(
                        car.getYear(),
                        car.getLicensePlate(),
                        car.getModel(),
                        car.getColor()
                ))
                .toList();

        return ResponseEntity.ok(new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthday(),
                user.getLogin(),
                user.getPhone(),
                carDtos,
                user.getCreatedAt(),
                user.getLastLogin()
        ));
    }

}

