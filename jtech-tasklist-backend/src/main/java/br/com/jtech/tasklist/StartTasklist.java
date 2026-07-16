package br.com.jtech.tasklist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartTasklist {

	public static void main(String[] args) {
		loadEnvFile();
		SpringApplication.run(StartTasklist.class, args);
	}

	private static void loadEnvFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("#")) {
					String[] parts = line.split("=", 2);
					if (parts.length == 2) {
						System.setProperty(parts[0].trim(), parts[1].trim());
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Warning: .env file not found. Using default environment variables.");
		}
	}

}
