package com.byakuya.boot.backend.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 田伯光 at 2022/9/24 19:04
 */
@RestController
@RequestMapping("security")
class SecurityController {
    @GetMapping("/authentication")
    public ResponseEntity<Authentication> getAuthentication() {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
    }
}
