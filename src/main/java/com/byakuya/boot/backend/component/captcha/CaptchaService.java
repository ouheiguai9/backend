package com.byakuya.boot.backend.component.captcha;

import com.byakuya.boot.backend.exception.ValidationFailedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

/**
 * Created by 田伯光 at 2022/12/3 0:09
 */
@Service
public class CaptchaService {
    private final CaptchaRepository captchaRepository;

    public CaptchaService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    public void add(Long tenantId, Type captchaType, String target, String value, LocalDateTime end, boolean force) throws ValidationFailedException {
        add(tenantId, captchaType, target, value, LocalDateTime.now(), end, force);
    }

    public void add(Long tenantId, Type captchaType, String target, String value, LocalDateTime start, LocalDateTime end, boolean force) throws ValidationFailedException {
        Captcha captcha = new Captcha().setId(new CaptchaId().setTenantId(tenantId).setCaptchaType(captchaType).setTarget(target)).setNew(true);
        captchaRepository.findById(Objects.requireNonNull(captcha.getId())).ifPresent(old -> {
            if (!force && old.isValid() && old.getEnd().isAfter(LocalDateTime.now())) {
                throw ValidationFailedException.buildWithCode("error.captcha.valid");
            }
            captcha.markNotNew();
        });
        captcha.setValue(value).setStart(start).setEnd(end).setValid(true);
        captchaRepository.save(captcha);
    }

    public void check(Long tenantId, Type captchaType, String target, String value, boolean ignoreCase) throws ValidationFailedException {
        Captcha captcha = captchaRepository.findById(new CaptchaId().setTenantId(tenantId).setCaptchaType(captchaType).setTarget(target)).orElse(new Captcha().setValid(false));
        LocalDateTime now = LocalDateTime.now();
        if (captcha.isValid() && captcha.getStart().isBefore(now) && captcha.getEnd().isAfter(now) && ((ignoreCase && captcha.getValue().equalsIgnoreCase(value)) || captcha.getValue().equals(value))) {
            captcha.setValid(false);
            captchaRepository.save(captcha);
            return;
        }
        throw ValidationFailedException.buildWithCode("error.captcha");
    }


    public String createNumberCaptcha(int len) {
        int justLen = Math.max(4, Math.min(8, len));
        int max = (int) (Math.pow(10, justLen) - 1), min = (int) Math.pow(10, justLen - 1);
        return String.valueOf(new Random().nextInt(max - min) + min);
    }

    public String createCaptcha(int len) {
        int justLen = Math.max(4, Math.min(8, len));
        Random random = new Random();
        int mark = random.nextInt(1 << justLen);
        StringBuilder builder = new StringBuilder();
        while (justLen > 0) {
            if ((mark & 1) == 0) {
                builder.append(random.nextInt(10));
            } else {
                builder.append((char) (random.nextInt(26) + (random.nextBoolean() ? 65 : 97)));
            }
            mark = mark >> 1;
            justLen--;
        }
        return builder.toString();
    }
}
