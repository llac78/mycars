package com.project.mycars.controller;

import com.project.mycars.dto.ApiResponse;
import com.project.mycars.model.User;
import com.project.mycars.repository.UserRepository;
import com.project.mycars.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveUser(@RequestBody @Valid User userDetails){

        User user = this.userService.save(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id){
        User user = this.userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer id){
        ApiResponse response = this.userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody @Valid User userDetails){

        User user = userService.updateUser(id, userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
