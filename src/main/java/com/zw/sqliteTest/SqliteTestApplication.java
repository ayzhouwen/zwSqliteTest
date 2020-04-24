package com.zw.sqliteTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SqliteTestApplication {

	public static void main(String[] args) {
		System.setProperty("hikaricp.configurationFile","hikaricp.configurationFile");
		SpringApplication.run(SqliteTestApplication.class, args);
	}

}
