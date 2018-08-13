package by.issoft.opsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class OpsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpsAppApplication.class, args);
	}

}
