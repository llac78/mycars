package com.project.mycars.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.mycars.validation.ValidName;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "car")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"year\"", nullable = false)
    @NotNull (message = "{fields.missing}")
    private Integer year;

    @Column(nullable = false, length = 8)
    private String licensePlate;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    private String model;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    @ValidName
    private String color;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
}
