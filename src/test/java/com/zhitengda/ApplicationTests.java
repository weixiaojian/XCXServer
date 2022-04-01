package com.zhitengda;

import com.zhitengda.mapper.CommonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;

@SpringBootTest
class ApplicationTests {

    @Autowired
    CommonMapper commonMapper;

    @Test
    void contextLoads() {
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        System.out.println(path);

        String property = System.getProperty("user.dir");
        System.out.println(property);
    }

}
