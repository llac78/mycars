package com.project.mycars.controller;

import com.project.mycars.dto.ApiResponse;
import com.project.mycars.dto.CarDTO;
import com.project.mycars.model.Car;
import com.project.mycars.model.User;
import com.project.mycars.repository.CarRepository;
import com.project.mycars.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;
    private final CarRepository carRepository;
    private final MessageSource messageSource;

    public CarController(CarService carService, CarRepository carRepository, MessageSource messageSource) {
        this.carService = carService;
        this.carRepository = carRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public List<Car> getCars(){
        return carRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveCar(@RequestBody @Valid CarDTO carDetails){

        Car car = this.carService.save(carDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(car) ;
    }

//    @GetMapping
//    public ResponseEntity<List<Car>> getCarsByLoggedUser(@AuthenticationPrincipal User loggedUser) {
//        List<Car> cars = carRepository.findByUser(loggedUser);
//        return ResponseEntity.ok(cars);
//    }

    @GetMapping("{id}")
    public Car getCarById(@PathVariable Integer id){
        return carRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found. Try again later."));
    }

    @DeleteMapping("{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse> deleteCar(@PathVariable Integer id){
        return carRepository.findById(id)
                .map(car -> {
                    carRepository.delete(car);
                    String message = messageSource.getMessage("car.deleted.success", null, Locale.getDefault());
                    return ResponseEntity.ok(new ApiResponse(message));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found. Try again later."));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Car updateCar(@PathVariable Integer id, @RequestBody Car carDetails){
        Optional<Car> carDB = carRepository.findById(id);

        if (carDB.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found. Try again later.");
        }
        Car car = carDB.get();

        if(carRepository.existsByLicensePlate(carDetails.getLicensePlate()) && car.getLicensePlate().equals(carDetails.getLicensePlate())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "License Plate is already exists.");
        }
        car.setYear(carDetails.getYear());
        car.setLicensePlate(carDetails.getLicensePlate());
        car.setModel(carDetails.getModel());
        car.setColor(carDetails.getColor());

        return carRepository.save(car);
    }
}
