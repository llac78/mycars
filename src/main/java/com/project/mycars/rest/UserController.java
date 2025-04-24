package com.project.mycars.rest;

import com.project.mycars.dto.ApiResponse;
import com.project.mycars.model.entity.User;
import com.project.mycars.model.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Autowired
    public UserController(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User saveUser(@RequestBody @Valid User user){
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX "+user.toString());
        if(user.getFirstName() == null &&user.getLastName() == null && user.getEmail() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation error occurred");
        }
        return this.userRepository.save(user);
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable Integer id){
        return userRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found. Try again later."));
    }

    @DeleteMapping("{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
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
    public User updateUser(@PathVariable Integer id, @RequestBody User userDetails){
        Optional<User> userBD = userRepository.findById(id);

        if (userBD.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found. Try again later.");
        }

        User user = userBD.get();

        if (userRepository.existsByEmail(userDetails.getEmail()) && user.getEmail().equals(userDetails.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already exists.");
        }

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());

        return userRepository.save(user);

    }
}
