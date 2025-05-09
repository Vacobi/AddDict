package ru.vstu.adddict;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableRetry
public class AddDictApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AddDictApplication.class)
				.bannerMode(Banner.Mode.OFF)
				.run(args);
	}

}
