package com.dwt.photovoltaic.Photovoltaic.System;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
@SecurityScheme(name="jwtAuth", scheme = "bearer", type= SecuritySchemeType.HTTP, bearerFormat = "JWT")
public class PhotovoltaicSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(PhotovoltaicSystemApplication.class, args);
	}

}
