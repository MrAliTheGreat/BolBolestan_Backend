package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.PassedCourse;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.BolBolRepo;
import jdk.jshell.execution.Util;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @GetMapping(value = "/{studentID}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Offering> getStudentCourses(@PathVariable String studentID){
        List<Offering> studentCourses = new ArrayList<>();

        HashMap<Offering , Student.OfferStatus> studentCoursesHashMap;
        try{
            studentCoursesHashMap = BolBolRepo.getInstance().getStudentCoursesDB(studentID);
        }catch (SQLException e){
            System.out.println("Error in ScheduleController getStudentCourses: " + e.getMessage());
            studentCoursesHashMap = new HashMap<>();
        }

        for(Offering offering : studentCoursesHashMap.keySet()){
            studentCourses.add(offering);
        }

        return studentCourses;
    }

    @GetMapping(value = "/JWT" , produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Offering> getStudentCoursesJWT(){
        String studentId = getStudentIdFromKeyJWT("getStudentCoursesJWT");
        return getStudentCourses(studentId);
    }

    @GetMapping(value = "/{studentID}/currentTerm" , produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStudentCurrentTerm(@PathVariable String studentID){
        int currentSemester;

        try{
            currentSemester = BolBolRepo.getInstance().getLastTermNumberStudentDB(studentID);
        }catch (SQLException e){
            System.out.println("Error in getStudentCurrentTerm: " + e.getMessage());
            return "-1";
        }
        return Integer.toString(currentSemester + 1);
    }

    @GetMapping(value = "/currentTerm" , produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStudentCurrentTermJWT(){
        String studentId = getStudentIdFromKeyJWT("getStudentCurrentTermJWT");
        return getStudentCurrentTerm(studentId);
    }

    private String getStudentIdFromKeyJWT(String functionName){
        try{
            return BolBolRepo.getInstance().findStudentFromEmailDB(SecurityContextHolder.getContext().getAuthentication().getName()).getStudentId();
        }catch(SQLException e){
            System.out.println("Error in " + functionName + ": " + e.getMessage());
            return null;
        }
    }
}

