package com.project.mycars.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.mycars.validation.ValidName;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @ValidName // created
    @NotBlank(message = "{fields.missing}")
    private String firstName;

    @Column(nullable = false)
    @ValidName // created
    @NotBlank(message = "{fields.missing}")
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    @Email(message = "{fields.invalid}")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT-3")
    @Column(nullable = false)
    @NotNull(message = "{fields.missing}")
    @Temporal(TemporalType.DATE) // date without hours
    private Date birthday;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    private String login;


    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    @Pattern(regexp = "\\d{9}", message = "{fields.invalid}")
    private String phone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars = new ArrayList<>();

}
