package com.example.demo.service.impl;

import com.example.demo.common.RandomUtils;
import com.example.demo.model.ResetKey;
import com.example.demo.model.User;
import com.example.demo.repository.ResetKeyRepository;
import com.example.demo.service.definition.ResetKeyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ResetKeyServiceImpl extends AbstractEntityServiceImpl<ResetKey, ResetKeyRepository> implements ResetKeyService {

    public ResetKeyServiceImpl(ResetKeyRepository repository) {
        super(repository);
    }

    @Override
    public ResetKey create(User user) {
        if (user.isEnabled()) {
            ResetKey resetKey = getByUser(user);

            if (resetKey == null) {
                resetKey = new ResetKey();
                resetKey.setResetDate(new Date());
                resetKey.setResetKey(RandomUtils.generateRandomKey());
                resetKey.setUser(user);

                return create(resetKey);
            } else {
                resetKey.setResetDate(new Date());
                resetKey.setResetKey(RandomUtils.generateRandomKey());

                return update(resetKey);
            }
        }

        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResetKey createNewTransaction(User user) {
        return create(user);
    }

    @Override
    public ResetKey getByResetKey(String resetKey) {
        return this.repository.getFirstByResetKey(resetKey);
    }

    @Override
    public ResetKey getByUser(User user) {
        return this.repository.getFirstByUser(user);
    }

    @Override
    public void deleteByUser(User user) {
        this.repository.deleteByUser(user);
    }
}
