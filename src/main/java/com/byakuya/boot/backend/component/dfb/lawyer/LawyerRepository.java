package com.byakuya.boot.backend.component.dfb.lawyer;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by 田伯光 at 2023/2/8 16:31
 */
interface LawyerRepository extends JpaRepository<Lawyer, Long>, JpaSpecificationExecutor<Lawyer> {
    @NotNull
    @Query("select l from Lawyer l where l.user.account.locked=false")
    List<Lawyer> findAllUnLocked();

    @EntityGraph("Lawyer.User")
    @Query("select l from Lawyer l where l.id=?1")
    Optional<Lawyer> findWithUser(Long id);

    @NotNull
    @EntityGraph("Lawyer.User")
    Page<Lawyer> findAll(Specification<Lawyer> spec, @NotNull Pageable pageable);

    @EntityGraph("Lawyer.Order")
    @Query("select l from Lawyer l left join l.orderList as o with o.createTime between ?1 and ?2")
    List<Lawyer> findAllWithOrder(LocalDateTime start, LocalDateTime end);
}
