package com.project.mycars.repository;

import com.project.mycars.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {

    boolean existsByLicensePlate(String licensePlate);
}
