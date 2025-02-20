package com.webprojectSEA.WebBlogProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class   WebBlogProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBlogProjectApplication.class, args);
	}

}
