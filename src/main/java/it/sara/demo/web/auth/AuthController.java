package it.sara.demo.web.auth;


import it.sara.demo.service.auth.AuthenticationService;
import it.sara.demo.web.auth.dto.LoginRequest;
import it.sara.demo.web.auth.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        String token = authenticationService.authenticateAndGenerateToken(
            request.getUsername(),
            request.getPassword()
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
