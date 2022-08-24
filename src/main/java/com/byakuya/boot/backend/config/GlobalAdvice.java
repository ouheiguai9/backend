package com.byakuya.boot.backend.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ganzl on 2021/2/4.
 */
@ControllerAdvice
public class GlobalAdvice {
    @ModelAttribute
    public void addTokenAttributes(HttpServletRequest request, Model model) {
        // todo
    }
}
