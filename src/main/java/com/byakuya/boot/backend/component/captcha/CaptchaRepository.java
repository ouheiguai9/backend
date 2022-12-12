package com.byakuya.boot.backend.component.captcha;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 田伯光 at 2022/9/12 21:02
 */
interface CaptchaRepository extends JpaRepository<Captcha, CaptchaId> {
}
