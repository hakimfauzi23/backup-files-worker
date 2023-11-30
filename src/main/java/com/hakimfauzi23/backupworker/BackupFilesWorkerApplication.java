package com.hakimfauzi23.backupworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackupFilesWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackupFilesWorkerApplication.class, args);
	}

}
