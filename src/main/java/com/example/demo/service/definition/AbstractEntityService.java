package com.example.demo.service.definition;

import com.example.demo.model.AbstractEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AbstractEntityService<E extends AbstractEntity> {

    E findOne(Integer id);

    Optional<E> findOne(Example<E> example);

    List<E> findAll();

    List<E> findAll(Sort sort);

    List<E> findAll(Iterable<Integer> ids);

    Page<E> findAll(Pageable pageable);

    List<E> findAll(Example<E> example);

    List<E> findAll(Example<E> example, Sort sort);

    Page<E> findAll(Example<E> example, Pageable pageable);

    long count();

    long count(Example<E> example);

    boolean exists(Example<E> example);

    boolean exists(Integer id);

    E create(E toCreate);

    E update(E toUpdate);

    void delete(E toDelete);

    List<E> create(Iterable<E> toCreates);

    List<E> update(Iterable<E> toUpdates);

    void delete(Collection<E> toDelete);
}
