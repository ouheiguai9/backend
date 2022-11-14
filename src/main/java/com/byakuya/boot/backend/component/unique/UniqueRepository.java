package com.byakuya.boot.backend.component.unique;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 田伯光 at 2022/9/12 21:02
 */
interface UniqueRepository extends JpaRepository<Unique, UniqueId> {
}
