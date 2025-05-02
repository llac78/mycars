package com.project.mycars.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.mycars.validation.ValidName;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    //@NotEmpty(message = "{user.field.firstname}") // @NotEmpty = nem null nem vazio
    private String firstName;

    @Column(nullable = false)
    @ValidName // created
    @NotBlank(message = "{fields.missing}")
    //@NotEmpty(message = "{user.field.lastname}")
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    //@NotEmpty(message = "{user.field.email.mandatory}")
    @Email(message = "{fields.invalid}")
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$", message = "Email should be valid and end with .com")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT-3")
    @Column(nullable = false)
    @NotNull(message = "{fields.missing}")
    @Temporal(TemporalType.DATE) // Garante que só a data seja salva, sem hora
    private Date birthday;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    //@NotEmpty(message = "{user.field.firstname}") // @NotEmpty = nem null nem vazio
    private String login;

    @Column(nullable = false)
    @NotBlank(message = "{fields.missing}")
    //@NotEmpty(message = "{user.field.firstname}") // @NotEmpty = nem null nem vazio
    @Size(min = 9, max = 9, message = "{fields.invalid}")
    private String phone;



}
