package com.project.mycars.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.NonNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotNull (message = "{car.field.year.mandatory}") // @NotEmpty = nem null nem vazio
    private Integer yearCar;

    @Column(nullable = false, length = 8)
    @NotNull(message = "{car.field.licenseplate.mandatory}")
    private String licensePlate;

    @Column(nullable = false)
    @NotNull(message = "{car.field.model.mandatory}")
    private String modelCar;

    @Column(nullable = false)
    @NotNull(message = "{car.field.color.mandatory}")
    private String colorCar;
}
