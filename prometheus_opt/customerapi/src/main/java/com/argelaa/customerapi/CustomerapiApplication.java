package com.argelaa.customerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class CustomerapiApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(CustomerapiApplication.class, args);
	}
}
