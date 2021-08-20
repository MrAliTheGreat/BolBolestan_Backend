package BolBol.CA5_Server;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.Scheduler.WaitingList;
import BolBol.CA5_Server.Repository.BolBolRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class Ca5ServerApplication {

	public static void main(String[] args) {
		BolBolRepo.getInstance().createAllTables();
		BolBolSystem.getBolBolSystemInstance().setAllExternalServerInfo("http://138.197.181.131:5200/api/courses" ,
																		"http://138.197.181.131:5200/api/students" ,
																		"http://138.197.181.131:5200/api/grades/");
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new WaitingList(), 0, 1, TimeUnit.MINUTES);
		SpringApplication.run(Ca5ServerApplication.class, args);
	}

}