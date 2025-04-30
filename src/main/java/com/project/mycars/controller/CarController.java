package com.project.mycars.controller;

import com.project.mycars.dto.ApiResponse;
import com.project.mycars.model.Car;
import com.project.mycars.repository.CarRepository;
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

    private final CarRepository carRepository;
    private final MessageSource messageSource;

    @Autowired
    public CarController(CarRepository carRepository, MessageSource messageSource) {
        this.carRepository = carRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public List<Car> getCars(){
        return carRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car saveCar(@RequestBody @Valid Car car){

        if(car.getColorCar() == null && car.getYearCar() == null && car.getModelCar() == null && car.getLicensePlate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation error occurred");
        }
        return this.carRepository.save(car);
    }

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
        car.setYearCar(carDetails.getYearCar());
        car.setLicensePlate(carDetails.getLicensePlate());
        car.setModelCar(carDetails.getModelCar());
        car.setColorCar(carDetails.getColorCar());

        return carRepository.save(car);
    }
}
