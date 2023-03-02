package com.byakuya.boot.backend.component.dfb.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 田伯光 at 2023/2/14 10:02
 */
interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    Page<Comment> findAllByVisibleIsTrue(Pageable pageable);

    @Query("select count(c.label1),count(c.label2),count(c.label3),count(c.label4),count(c.label5),count(c.label6) from Comment c where c.visible=true")
    Object countLabel();
}
