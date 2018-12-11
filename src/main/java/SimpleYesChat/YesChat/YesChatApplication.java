package SimpleYesChat.YesChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
/*
* postgres=# create database YesChat_DB;
CREATE DATABASE
postgres=# create user YesChat with encrypted password 'yeschat';
CREATE ROLE
postgres=# grant all privileges on database YesChat_DB to YesChat;
GRANT
*/

@EnableAsync
@SpringBootApplication

public class YesChatApplication {

	public static void main(String[] args) {

		SpringApplication.run(YesChatApplication.class, args);



	}


	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(50);
		executor.setMaxPoolSize(60);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("restRequest-");
		executor.initialize();
		return executor;
	}


}
