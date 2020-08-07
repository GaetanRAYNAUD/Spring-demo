package com.example.demo.repository;

import com.example.demo.model.User;

public interface UserRepository extends AbstractEntityRepository<User> {
    User getFirstByUsername(String username);

    User getFirstByEmail(String username);
}
