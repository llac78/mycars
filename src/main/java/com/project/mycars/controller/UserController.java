package com.project.mycars.controller;

import com.project.mycars.dto.ApiResponse;
import com.project.mycars.model.User;
import com.project.mycars.repository.UserRepository;
import com.project.mycars.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    //@Autowired
    public UserController(UserService userService, UserRepository userRepository, MessageSource messageSource) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveUser(@RequestBody @Valid User userDetails){

        User user = this.userService.save(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(user) ;
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable Integer id){
        return userRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found. Try again later."));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer id){
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    String message = messageSource.getMessage("user.deleted.success", null, Locale.getDefault());
                    return ResponseEntity.ok(new ApiResponse(message));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found. Try again later."));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody @Valid User userDetails){

        User user = userService.updateUser(id, userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
