package com.project.mycars.service;

import com.project.mycars.dto.CarDTO;
import com.project.mycars.model.Car;
import com.project.mycars.model.User;
import com.project.mycars.repository.CarRepository;
import com.project.mycars.repository.UserRepository;
import jakarta.validation.Validator;
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

    private final Validator validator;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final MessageSource messageSource;

    public CarService(Validator validator, UserRepository userRepository, CarRepository carRepository, MessageSource messageSource) {
        this.validator = validator;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.messageSource = messageSource;
    }

    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public Car save(CarDTO carDetails) {

        validateMissingFields(carDetails);
        validateYearField(carDetails);
        validateWhitespaceTextField(carDetails);

        validateLicencePlateFormat(carDetails);
        validateLicensePlateExists(carDetails.licensePlate());

        Optional<User> userBD = userRepository.findById(carDetails.user());
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

    private void validateLicencePlateFormat(CarDTO carDetails) {
        if (!carDetails.licensePlate().matches("^[A-Z]{3}-\\d{4}$")) {
            String message = messageSource.getMessage("fields.invalid", null, Locale.getDefault());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void validateYearField(CarDTO carDetails) {
        int currentYear = LocalDate.now().getYear();
        if (carDetails.year().toString().length() != 4 || carDetails.year() > currentYear
            || carDetails.year() < 1900){
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

//    public Car updateCar(Integer id, Car CarDetails){
//
//        Optional<Car> CarBD = CarRepository.findById(id);
//
//        if (CarBD.isEmpty()) {
//            String message = messageSource.getMessage("Car.not.found", null, Locale.getDefault());
//
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
//        }
//        Car Car = CarBD.get();
//
//        if(CarDetails.getFirstName() == null && CarDetails.getLastName() == null && CarDetails.getEmail() == null
//            && CarDetails.getBirthday() == null && CarDetails.getPassword() == null && CarDetails.getPhone() == null){
//            String message = messageSource.getMessage("fields.missing", null, Locale.getDefault());
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
//        }
//        validateWhitespaceTextField(CarDetails);
//
//        Car.setFirstName(CarDetails.getFirstName());
//        Car.setLastName(CarDetails.getLastName());
//
//        Car.setBirthday(CarDetails.getBirthday());
//
//
//
//        return CarRepository.save(Car);
//    }

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
}
