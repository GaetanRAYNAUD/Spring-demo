package com.example.demo.repository;

import com.example.demo.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractEntityRepository<E extends AbstractEntity> extends JpaRepository<E, Integer> {
}
