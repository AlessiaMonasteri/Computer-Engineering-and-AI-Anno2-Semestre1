package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.LoginDTO;
import AlessiaMonasteri.BagECommerce.DTO.LoginResponseDTO;
import AlessiaMonasteri.BagECommerce.DTO.NewUserDTO;
import AlessiaMonasteri.BagECommerce.DTO.NewUserResponseDTO;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.services.AuthService;
import AlessiaMonasteri.BagECommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    // USER + ADMIN + SUPERADMIN

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginDTO body){
        return new LoginResponseDTO(this.authService.checkCredentialsAndGenerateToken(body));

    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserResponseDTO createUser(@RequestBody @Validated NewUserDTO body, BindingResult validationResult) {

        if (validationResult.hasErrors()) {

            throw new ValidationException(validationResult.getFieldErrors().stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.userService.save(body);
    }
}
