package com.byakuya.boot.backend.component.authorization;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by 田伯光 at 2022/10/5 11:39
 */
interface AuthorizationRepository extends JpaRepository<Authorization, Long> {
    Stream<Authorization> findAllBySubjectIdInAndAuthType(Collection<@NotBlank Long> subjectId, Authorization.AuthType authType);
}
