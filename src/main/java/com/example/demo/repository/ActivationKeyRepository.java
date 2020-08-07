package com.example.demo.repository;

import com.example.demo.model.ActivationKey;
import com.example.demo.model.User;

public interface ActivationKeyRepository extends AbstractEntityRepository<ActivationKey> {
    ActivationKey getFirstByActivationKey(String activationKey);

    ActivationKey getFirstByUser(User user);
}
