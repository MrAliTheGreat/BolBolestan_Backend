package BolBol.CA5_Server;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.Scheduler.MainScheduler;
import BolBol.CA5_Server.Domain.Scheduler.WaitingList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Ca5ServerApplication {

	public static void main(String[] args) {
		BolBolSystem.getBolBolSystemInstance().setAllExternalServerInfo("http://138.197.181.131:5100/api/courses" ,
																		"http://138.197.181.131:5100/api/students" ,
																		"http://138.197.181.131:5100/api/grades/");
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new WaitingList(), 0, 15, TimeUnit.MINUTES);
		SpringApplication.run(Ca5ServerApplication.class, args);
	}

}