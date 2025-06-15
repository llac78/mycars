package com.project.mycars.service;

import com.project.mycars.dto.CarDTO;
import com.project.mycars.model.Car;
import com.project.mycars.model.User;
import com.project.mycars.repository.CarRepository;
import com.project.mycars.repository.UserRepository;
import com.project.mycars.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CarService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final MessageSource messageSource;

    public CarService(JwtUtil jwtUtil, UserRepository userRepository, CarRepository carRepository, MessageSource messageSource) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.messageSource = messageSource;
    }

    public List<Car> getCars(Integer userId) {
        return carRepository.findByUserId(userId);
    }

    public Car save(CarDTO carDetails, HttpServletRequest request) {

        validateMissingFields(carDetails);
        validateYearField(carDetails.year());
        validateWhitespaceTextField(carDetails);
        validateLicencePlateFormat(carDetails.licensePlate());
        validateLicensePlateExists(carDetails.licensePlate());

        String username = getLoggedUsername(request);

        Optional<User> userBD = userRepository.findByLogin(username);
        if (userBD.isEmpty()) {
            String message = messageSource.getMessage("user.not.found", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        User user = userBD.get();

        Car car = new Car();
        car.setYear(carDetails.year());
        car.setLicensePlate(carDetails.licensePlate());
        car.setModel(carDetails.model());
        car.setColor(carDetails.color());
        car.setUser(user);

        return carRepository.save(car);
    }

    private void validateLicencePlateFormat(String licensePlate) {
        if (!licensePlate.matches("^[A-Z]{3}-\\d{4}$")) {
            String message = messageSource.getMessage("fields.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void validateYearField(Integer year) {
        int currentYear = LocalDate.now().getYear();
        if (year.toString().length() != 4 || year > currentYear || year < 1900){
            String message = messageSource.getMessage("fields.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void validateMissingFields(CarDTO carDetails) {
        if(carDetails.color() == null || carDetails.color().isBlank() || carDetails.year() == null
                || carDetails.model() == null || carDetails.model().isBlank()
                || carDetails.licensePlate() == null || carDetails.licensePlate().isBlank()){
            String message = messageSource.getMessage("fields.missing", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public Car updateCar(Integer id, CarDTO carDetails){

        validateMissingFields(carDetails);
        validateWhitespaceTextField(carDetails);
        validateYearField(carDetails.year());
        validateLicencePlateFormat(carDetails.licensePlate());
        validateLicensePlateExists(carDetails.licensePlate());

        Optional<Car> CarBD = carRepository.findById(id);

        if (CarBD.isEmpty()) {
            String message = messageSource.getMessage("car.not.found", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        Car car = CarBD.get();

        car.setYear(carDetails.year());
        car.setLicensePlate(carDetails.licensePlate());
        car.setModel(carDetails.model());
        car.setColor(carDetails.color());

        return carRepository.save(car);
    }

    private void validateWhitespaceTextField(CarDTO carDetails) {
        if (carDetails.licensePlate().trim().contains(" ") || carDetails.color().trim().contains(" ")){
            String message = messageSource.getMessage("fields.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }

    private void validateLicensePlateExists(String licensePlate) {
        if (carRepository.existsByLicensePlate(licensePlate)) {
            String message = messageSource.getMessage("car.field.licenseplate.exists", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }

    public String getLoggedUsername(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            String message = messageSource.getMessage("user.token.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }
        token = token.substring(7);
        return jwtUtil.extractUsername(token);
    }
}
