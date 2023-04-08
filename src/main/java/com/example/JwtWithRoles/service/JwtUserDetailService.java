package com.example.JwtWithRoles.service;

import com.example.JwtWithRoles.dao.RoleRepo;
import com.example.JwtWithRoles.dao.UserRepo;
import com.example.JwtWithRoles.modal.DaoUser;
import com.example.JwtWithRoles.modal.DtoUser;
import com.example.JwtWithRoles.modal.Role;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DaoUser user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found by username: " + username);
        }
        return new User(user.getUsername(), user.getPassword(), getAuthority(user));
    }
    
    private Set<SimpleGrantedAuthority> getAuthority(DaoUser user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

    public DaoUser saveUser(DtoUser user) {
        DaoUser daoUser = new DaoUser();
        daoUser.setUsername(user.getUsername());
        daoUser.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepo.findRoleByName("USER"));
        if (user.getUsername().contains("admin")) {
        	roles.add(roleRepo.findRoleByName("ADMIN"));
        }
        daoUser.setRoles(roles);
        return userRepo.save(daoUser);
    }
    
    public List<DaoUser> getAllUsers() {
    	return userRepo.findAll();
    }

}
