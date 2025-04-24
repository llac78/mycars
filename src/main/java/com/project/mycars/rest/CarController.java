package com.project.mycars.rest;

import com.project.mycars.model.entity.Car;
import com.project.mycars.model.entity.User;
import com.project.mycars.model.repository.CarRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository carRepository;

    @Autowired
    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping
    public List<Car> getCars(){
        return carRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car saveCar(@RequestBody @Valid Car car){
        return this.carRepository.save(car);
    }

    @GetMapping("{id}")
    public Car getCarById(@PathVariable Integer id){
        return carRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found. Try again later."));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Integer id){
        carRepository.findById(id)
                .map(car -> {
                    carRepository.delete(car);
                    return Void.TYPE;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found. Try again later."));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCar(@PathVariable Integer id, @RequestBody Car carDetails){
        carRepository.findById(id)
                .map(car -> {
                    car.setYearCar(carDetails.getYearCar());
                    car.setLicensePlate(carDetails.getLicensePlate());
                    car.setModelCar(carDetails.getModelCar());
                    car.setColorCar(carDetails.getColorCar());
                    return carRepository.save(car);
                })
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found. Try again later."));
    }
}
