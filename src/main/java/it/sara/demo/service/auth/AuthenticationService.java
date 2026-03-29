package it.sara.demo.service.auth;


import it.sara.demo.exception.GenericException;
import it.sara.demo.service.util.JwtUtil;
import it.sara.demo.web.auth.dto.AuthenticationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${login.username}")
    private String username;

    @Value("${login.password}")
    private String password;

    @Value("${login.role}")
    private String role;


    public String authenticateAndGenerateToken(String username, String password) {

        if (!username.equals(this.username) || !password.equals(this.password)) {
            throw new GenericException(401, "Invalid credentials");
        }

        AuthenticationResult result = new AuthenticationResult(username, role);

        return jwtUtil.generateToken(
            result.getUsername(),
            result.getRole()
        );
    }
}

