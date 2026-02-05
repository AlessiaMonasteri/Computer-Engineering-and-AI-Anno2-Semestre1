package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.LoginDTO;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.exceptions.UnauthorizedException;
import AlessiaMonasteri.BagECommerce.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTTools jwtTools;
    @Autowired
    private PasswordEncoder bcrypt;

    public String checkCredentialsAndGenerateToken(LoginDTO body){
        //Nel db verifico se esiste un utente che abbia quell'email
        User found = this.userService.findByEmail(body.email());
        //Se c'è, controllo la password in modo che sia uguale al payload (body)
        if(bcrypt.matches(body.password(), found.getPassword())){
            //Genero il token
            return jwtTools.createToken(found);
        } else {
            throw new UnauthorizedException("Incorrect Credentials!");
        }
    }
}
