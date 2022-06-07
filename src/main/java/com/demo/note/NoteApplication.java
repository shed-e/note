package com.demo.note;

import com.github.pagehelper.PageInterceptor;
import org.aopalliance.intercept.Interceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class NoteApplication {

    public static void main(String[] args) {

        SpringApplication.run(NoteApplication.class, args);
    }

}

