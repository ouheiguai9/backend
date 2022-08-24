package com.byakuya.boot.backend.component.parameter;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by 田伯光 at 2022/4/28 17:27
 */
public interface ParameterRepository extends PagingAndSortingRepository<Parameter, Long> {
    Optional<Parameter> findByGroupKeyAndItemKey(String group, String item);

    List<Parameter> findByGroupKeyOrderByOrderingAsc(String group);
}
