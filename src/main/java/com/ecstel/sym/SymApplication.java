package com.ecstel.sym;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

@Slf4j
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
//@MapperScan("com.ecstel.sym.mapper.ecp")
public class SymApplication {

	public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder()
                .sources(SymApplication.class)
                .listeners(new ApplicationPidFileWriter("./batch.pid"))
                .build();
        application.run(args);
	}
}
