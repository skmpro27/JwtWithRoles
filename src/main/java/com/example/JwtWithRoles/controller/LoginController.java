package com.example.JwtWithRoles.controller;

import com.example.JwtWithRoles.config.JwtTokenUtils;
import com.example.JwtWithRoles.modal.DaoUser;
import com.example.JwtWithRoles.modal.DtoUser;
import com.example.JwtWithRoles.modal.JwtResponse;
import com.example.JwtWithRoles.service.JwtUserDetailService;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private JwtUserDetailService jwtUserDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @PostMapping(value = "/signUp")
    public ResponseEntity<?> saveUser(@RequestBody DtoUser user) {
        DaoUser daoUser = jwtUserDetailService.saveUser(user);
        return ResponseEntity.ok(daoUser);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateAndLoginUser(@RequestBody DtoUser user) throws Exception {
        authenticate(user);
        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(user.getUsername());
        String token = jwtTokenUtils.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
    
    @RolesAllowed("ADMIN")
    @GetMapping(value = "/users")
    public ResponseEntity<?> getAllUsers() {
    	return ResponseEntity.ok(jwtUserDetailService.getAllUsers());
    }

    private void authenticate(DtoUser user) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (DisabledException disabledException) {
            throw new Exception("USER_DISABLE", disabledException);
        } catch (BadCredentialsException badCredentialsException) {
            throw new Exception("INVALID_CREDENTIAL", badCredentialsException);
        }
    }

}
