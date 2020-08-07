package com.example.demo.service.definition;

import com.example.demo.model.ActivationKey;
import com.example.demo.model.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ActivationKeyService extends AbstractEntityService<ActivationKey> {

    ActivationKey create(User user);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    ActivationKey createNewTransaction(User user);

    ActivationKey getByActivationKey(String activationKey);

    ActivationKey getByUser(User user);
}
