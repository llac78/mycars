package com.project.mycars.service;

import com.project.mycars.dto.ApiResponse;
import com.project.mycars.model.User;
import com.project.mycars.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, MessageSource messageSource, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User save(User userDetails) {

        validateWhitespaceTextField(userDetails);
        validateEmailFormat(userDetails.getEmail());
        validateEmailExists(userDetails.getEmail());
        validateLoginExists(userDetails.getLogin());

        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));

        return userRepository.save(userDetails);
    }

    public User getUserById(Integer id) {
        Optional<User> userBD = userRepository.findById(id);
        if (userBD.isEmpty()) {
            String message = messageSource.getMessage("user.not.found", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        return userBD.get();
    }

    public ApiResponse deleteUser(Integer id){

        Optional<User> userBD = userRepository.findById(id);
        if (userBD.isEmpty()) {
            String message = messageSource.getMessage("user.not.found", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        userRepository.delete(userBD.get());
        String message = messageSource.getMessage("user.deleted.success", null, Locale.getDefault());

        return new ApiResponse(message);
    }

    public User updateUser(Integer id, User userDetails){

        Optional<User> userBD = userRepository.findById(id);

        if (userBD.isEmpty()) {
            String message = messageSource.getMessage("user.not.found", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        User user = userBD.get();

        validateMissingFields(userDetails);
        validateWhitespaceTextField(userDetails);

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setBirthday(userDetails.getBirthday());

        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setPassword(userDetails.getPassword());
        user.setPhone(userDetails.getPhone());

        return userRepository.save(user);
    }

    private void validateMissingFields(User userDetails) {
        if(userDetails.getFirstName() == null && userDetails.getLastName() == null && userDetails.getEmail() == null
            && userDetails.getBirthday() == null && userDetails.getPassword() == null && userDetails.getPhone() == null){
            String message = messageSource.getMessage("fields.missing", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void validateWhitespaceTextField(User userDetails) {
        if (userDetails.getFirstName().trim().contains(" ") || userDetails.getLastName().trim().contains(" ")
            || userDetails.getEmail().trim().contains(" ") || userDetails.getLogin().trim().contains(" ")
            || userDetails.getPassword().trim().contains(" ") || userDetails.getPhone().trim().contains(" ")){
            String message = messageSource.getMessage("fields.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }

    private void validateEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            String message = messageSource.getMessage("user.field.email.exists", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }

    private void validateEmailFormat(String email) {
        if (!email.matches("^[\\w-.]+@[\\w-]+\\.com$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("fields.invalid", null, Locale.getDefault()));
        }
    }

    private void validateLoginExists(String login) {
        if (userRepository.existsByLogin(login)){
            String message = messageSource.getMessage("user.field.login.exists", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }
}
