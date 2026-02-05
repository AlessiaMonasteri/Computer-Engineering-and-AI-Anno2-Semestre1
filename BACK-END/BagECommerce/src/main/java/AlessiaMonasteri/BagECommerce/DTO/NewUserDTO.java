package AlessiaMonasteri.BagECommerce.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewUserDTO(
        @NotBlank(message = "The name is a required field")
        @Size(min = 2, max = 30, message = "The name must be between 2 and 30 characters long")
        String name,
        @NotBlank(message = "The surname is a required field")
        @Size(min = 2, max = 30, message = "The surname must be between 2 and 30 characters long")
        String surname,
        @NotBlank(message = "The email is a required field")
        @Email(message = "The email is not in the correct format")
        String email,
        @NotBlank(message = "The password is a required field")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
        String password
        ) {
}
