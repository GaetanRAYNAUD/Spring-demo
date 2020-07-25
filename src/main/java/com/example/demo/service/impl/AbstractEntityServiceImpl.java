package com.example.demo.service.impl;

import com.example.demo.model.AbstractEntity;
import com.example.demo.repository.AbstractEntityRepository;
import com.example.demo.service.definition.AbstractEntityService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractEntityServiceImpl<E extends AbstractEntity, R extends AbstractEntityRepository<E>> implements AbstractEntityService<E> {

    protected final R repository;

    protected AbstractEntityServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public E findOne(Integer id) {
        return this.repository.getOne(id);
    }

    @Override
    public Optional<E> findOne(Example<E> example) {
        return this.repository.findOne(example);
    }

    @Override
    public List<E> findAll() {
        return this.repository.findAll();
    }

    @Override
    public List<E> findAll(Sort sort) {
        return this.repository.findAll(sort);
    }

    @Override
    public List<E> findAll(Iterable<Integer> ids) {
        return this.repository.findAllById(ids);
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Override
    public List<E> findAll(Example<E> example) {
        return this.repository.findAll(example);
    }

    @Override
    public List<E> findAll(Example<E> example, Sort sort) {
        return this.repository.findAll(example, sort);
    }

    @Override
    public Page<E> findAll(Example<E> example, Pageable pageable) {
        return this.repository.findAll(example, pageable);
    }

    @Override
    public long count() {
        return this.repository.count();
    }

    @Override
    public long count(Example<E> example) {
        return this.repository.count(example);
    }

    @Override
    public boolean exists(Example<E> example) {
        return this.repository.exists(example);
    }

    @Override
    public boolean exists(Integer id) {
        return this.repository.existsById(id);
    }

    @Override
    public E create(E toCreate) {
        Preconditions.checkArgument(toCreate != null);
        return this.repository.save(toCreate);
    }

    @Override
    public E update(E toUpdate) {
        Preconditions.checkArgument(toUpdate != null && toUpdate.getId() != null);
        return this.repository.save(toUpdate);
    }

    @Override
    public void delete(E toDelete) {
        if (toDelete != null) {
            this.repository.delete(toDelete);
        }
    }

    @Override
    public List<E> create(Iterable<E> toCreates) {
        Preconditions.checkArgument(toCreates != null);
        if (Iterables.isEmpty(toCreates)) {
            return Lists.newArrayList();
        }

        return this.repository.saveAll(toCreates);
    }

    @Override
    public List<E> update(Iterable<E> toUpdates) {
        Preconditions.checkArgument(toUpdates != null);
        toUpdates.forEach(entity -> Preconditions.checkArgument(entity != null && entity.getId() != null));
        return this.repository.saveAll(toUpdates);
    }

    @Override
    public void delete(Collection<E> toDelete) {
        this.repository.deleteInBatch(toDelete);
    }
}
