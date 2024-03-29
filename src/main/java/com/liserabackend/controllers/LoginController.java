package com.liserabackend.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liserabackend.entity.Role;
import com.liserabackend.entity.User;
import com.liserabackend.services.LoginService;
import com.liserabackend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@CrossOrigin()
@RequestMapping("/api")
public class LoginController {
    private final UserService userService;
    LoginService loginService;



    @GetMapping("/getRole")
    public ResponseEntity<String> getRole(Principal principal){
        System.out.println( principal.getClass().getName() + " " + principal.getName());

        return ResponseEntity.ok(null);
    }
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { //check the header if not null & start with Bearer
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length()); //get refresh_token
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes()); //get the algorithm
                JWTVerifier verifier = JWT.require(algorithm).build(); //verify the algorithm
                DecodedJWT decodedJWT = verifier.verify(refresh_token);  //decode
                String username = decodedJWT.getSubject(); //get a username
                User user= userService.getUserByUserName(username).get(); //find a user in the system
                String access_token= JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) //10 mim
                        .withIssuer(request.getRequestURI())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String,String> tokens=new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                response.setHeader("error",exception.getMessage());

                response.setStatus(FORBIDDEN.value());
                Map<String,String> error=new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }else{
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
