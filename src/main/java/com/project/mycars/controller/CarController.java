package com.project.mycars.controller;

import com.project.mycars.dto.ApiResponse;
import com.project.mycars.dto.CarDTO;
import com.project.mycars.model.Car;
import com.project.mycars.model.User;
import com.project.mycars.repository.CarRepository;
import com.project.mycars.service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

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
    public ResponseEntity<List<Car>> getCars(@AuthenticationPrincipal User loggedUser) {
        List<Car> cars = carService.getCars(loggedUser.getId());
        return ResponseEntity.ok(cars);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveCar(@RequestBody @Valid CarDTO carDetails, HttpServletRequest request){

        Car car = this.carService.save(carDetails, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(car) ;
    }

    @GetMapping("{id}")
    public Car getCarById(@PathVariable Integer id){
        return carRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found. Try again later."));
    }

    @DeleteMapping("{id}")
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
    public ResponseEntity<?> updateCar(@PathVariable Integer id, @RequestBody CarDTO carDetails){

        Car car = carService.updateCar(id, carDetails);

        return ResponseEntity.status(HttpStatus.OK).body(car);
    }
}
