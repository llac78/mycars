package com.project.mycars.repository;

import com.project.mycars.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {

    boolean existsByLicensePlate(String licensePlate);

    List<Car> findByUserId(Integer userId);
}
