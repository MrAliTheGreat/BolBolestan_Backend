package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Domain.Models.FrontStudent;
import BolBol.CA5_Server.Repository.BolBolRepo;
import jdk.jshell.execution.Util;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Objects;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/{studentID}")
    public FrontStudent getUser(@PathVariable String studentID){
        try{
            return new FrontStudent(BolBolRepo.getInstance().findStudentFromIdDB(studentID));
        }catch (SQLException e){
            System.out.println("Error in HomeController: " + e.getMessage());
            return null;
        }
    }

    @GetMapping("/JWT")
    public FrontStudent getUserByJWT(){
        try{
            // The part inside findStudentFromEmailDB will give us the email
            return new FrontStudent(BolBolRepo.getInstance().findStudentFromEmailDB(SecurityContextHolder.getContext().getAuthentication().getName()));
        }catch (SQLException e){
            System.out.println("Error in HomeController JWT: " + e.getMessage());
            return null;
        }
    }
}

