package com.easydb.easydb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class EasydbApplication {
	public static void main(String[] args) {
		SpringApplication.run(EasydbApplication.class, args);
	}
}
