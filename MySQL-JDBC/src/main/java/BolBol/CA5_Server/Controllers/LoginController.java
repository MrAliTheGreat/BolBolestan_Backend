package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.BolBolRepo;
import jdk.jshell.execution.Util;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Objects;

@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping(value = "/{studentID}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public Student checkUserExistence(@PathVariable String studentID){
        try {
            Student student = BolBolRepo.getInstance().findStudentFromIdDB(studentID);
            return student;
        }catch (SQLException e){
            System.out.println("Error in checkUserExistence: " + e.getMessage());
            return new Student();
        }
    }
}

