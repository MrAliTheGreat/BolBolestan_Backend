package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Domain.Models.FrontStudent;
import jdk.jshell.execution.Util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/{studentID}")
    public FrontStudent getUser(@PathVariable String studentID){
        return new FrontStudent(BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentID));
    }
}

