package com.example.demo.service.definition;

import com.example.demo.model.ResetKey;
import com.example.demo.model.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ResetKeyService extends AbstractEntityService<ResetKey> {

    ResetKey create(User user);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    ResetKey createNewTransaction(User user);

    ResetKey getByResetKey(String resetKey);

    ResetKey getByUser(User user);

    void deleteByUser(User user);
}
