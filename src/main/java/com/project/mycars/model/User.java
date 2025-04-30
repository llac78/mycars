package com.project.mycars.model;

import com.project.mycars.validation.ValidName;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @NotNull
    @ValidName // created
    @NotBlank(message = "{fields.missing}")
    //@NotEmpty(message = "{user.field.firstname}") // @NotEmpty = nem null nem vazio
    private String firstName;

    @Column(nullable = false)
    @NotNull
    @ValidName // created
    @NotBlank(message = "{fields.missing}")
    //@NotEmpty(message = "{user.field.lastname}")
    private String lastName;

    @Column(nullable = false)
    @NotNull
    @NotBlank(message = "{fields.missing}")
    //@NotEmpty(message = "{user.field.email.mandatory}")
    @Email(message = "{fields.invalid}")
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$", message = "Email should be valid and end with .com")
    private String email;


}
