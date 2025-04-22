package cn.polister.dianmeetingsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.polister.dianmeetingsystem.mapper")
public class DianMeetingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DianMeetingSystemApplication.class, args);
    }

}
