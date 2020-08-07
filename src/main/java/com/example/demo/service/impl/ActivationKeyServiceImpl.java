package com.example.demo.service.impl;

import com.example.demo.common.RandomUtils;
import com.example.demo.model.ActivationKey;
import com.example.demo.model.User;
import com.example.demo.repository.ActivationKeyRepository;
import com.example.demo.service.definition.ActivationKeyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ActivationKeyServiceImpl extends AbstractEntityServiceImpl<ActivationKey, ActivationKeyRepository> implements ActivationKeyService {

    public ActivationKeyServiceImpl(ActivationKeyRepository repository) {
        super(repository);
    }

    @Override
    public ActivationKey create(User user) {
        if (!user.isEnabled()) {
            ActivationKey activationKey = getByUser(user);

            if (activationKey == null) {
                activationKey = new ActivationKey();
                activationKey.setActivationDate(new Date());
                activationKey.setActivationKey(RandomUtils.generateRandomKey());
                activationKey.setUser(user);

                return create(activationKey);
            } else {
                activationKey.setActivationDate(new Date());
                activationKey.setActivationKey(RandomUtils.generateRandomKey());

                return update(activationKey);
            }
        }

        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ActivationKey createNewTransaction(User user) {
        return create(user);
    }

    @Override
    public ActivationKey getByActivationKey(String activationKey) {
        return this.repository.getFirstByActivationKey(activationKey);
    }

    @Override
    public ActivationKey getByUser(User user) {
        return this.repository.getFirstByUser(user);
    }
}
