package com.byakuya.boot.backend;

import com.byakuya.boot.backend.service.SubMailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    SubMailService subMailService;

    @Test
    void contextLoads() {
        subMailService.sendMessage(0L, "LuWdC", "", "18639429487");
    }

}
