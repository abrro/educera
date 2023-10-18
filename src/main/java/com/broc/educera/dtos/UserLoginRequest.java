package com.broc.educera.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    @Email(message = "email must be a well formatted email address.")
    @NotBlank(message = "email cannot be null and it's trimmed length must greater than zero.")
    private String email;

    @NotBlank(message = "password cannot be null and it's trimmed length must greater than zero.")
    @Size(min = 8, max = 20, message = "password cannot be shorter than 8 or longer than 20 characters.")
    private String password;
}
