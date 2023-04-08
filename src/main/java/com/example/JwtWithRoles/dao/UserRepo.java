package com.example.JwtWithRoles.dao;

import com.example.JwtWithRoles.modal.DaoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<DaoUser, Integer> {
    DaoUser findByUsername(String username);
}
