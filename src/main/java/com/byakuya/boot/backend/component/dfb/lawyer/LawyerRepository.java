package com.byakuya.boot.backend.component.dfb.lawyer;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by 田伯光 at 2023/2/8 16:31
 */
interface LawyerRepository extends JpaRepository<Lawyer, Long> {
    @NotNull
    @Query("select lawyer from Lawyer lawyer where lawyer.user.account.locked=false")
    List<Lawyer> findAll();

    @EntityGraph("Lawyer.Order")
    @Query("select l from Lawyer l left join l.orderList as o with o.createTime between ?1 and ?2")
    List<Lawyer> findAllWithOrder(LocalDateTime start, LocalDateTime end);
}
