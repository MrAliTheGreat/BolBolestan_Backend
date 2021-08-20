package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import jdk.jshell.execution.Util;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping(value = "/{studentID}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public Student checkUserExistence(@PathVariable String studentID){
        Student student = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentID);
        if(Objects.isNull(student)){
            return new Student();
        }
        return student;
    }
}

