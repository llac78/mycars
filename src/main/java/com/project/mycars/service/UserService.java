package com.project.mycars.service;

import com.project.mycars.model.User;
import com.project.mycars.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public UserService(UserRepository userRepository,  MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User save(User userDetails) {

        validateEmailFormat(userDetails);
        validateEmailExists(userDetails);

        return userRepository.save(userDetails);
    }

    public User updateUser(Integer id, User userDetails){

        Optional<User> userBD = userRepository.findById(id);

        if (userBD.isEmpty()) {
            String message = messageSource.getMessage("user.not.found", null, Locale.getDefault());

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        User user = userBD.get();

        if(userDetails.getFirstName() == null && userDetails.getLastName() == null && userDetails.getEmail() == null){
            String message = messageSource.getMessage("fields.missing", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());

        return userRepository.save(user);
    }

    private void validateEmailExists(User userDetails) {
        if (userRepository.existsByEmail(userDetails.getEmail())) {
            String message = messageSource.getMessage("user.field.email.exists", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }

    private void validateEmailFormat(User userDetails) {
        if (!userDetails.getEmail().matches("^[\\w-.]+@[\\w-]+\\.com$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("fields.invalid", null, Locale.getDefault()));
        }
    }
}
