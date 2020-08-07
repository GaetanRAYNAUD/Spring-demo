package com.example.demo.repository;

import com.example.demo.model.ResetKey;
import com.example.demo.model.User;

public interface ResetKeyRepository extends AbstractEntityRepository<ResetKey> {
    ResetKey getFirstByResetKey(String resetKey);

    ResetKey getFirstByUser(User user);

    void deleteByUser(User user);
}
