package com.byakuya.boot.backend.component.parameter;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author ganzl
 * @createTime 2022/4/14 17:18
 * @description ParameterRepository
 */
public interface ParameterRepository extends PagingAndSortingRepository<Parameter, Long> {
    Optional<Parameter> findByGroupKeyAndItemKey(String group, String item);

    List<Parameter> findByGroupKeyOrderByOrderingAsc(String group);
}
