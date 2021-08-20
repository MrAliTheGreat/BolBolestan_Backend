package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.PassedCourse;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import jdk.jshell.execution.Util;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @GetMapping(value = "/{studentID}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Offering> getStudentCourses(@PathVariable String studentID){
        List<Offering> studentCourses = new ArrayList<>();
        Student student = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentID);

        for(Offering offering : student.getStudentCourses().keySet()){
            studentCourses.add(offering);
        }

        return studentCourses;
    }

    @GetMapping(value = "/{studentID}/currentTerm" , produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStudentCurrentTerm(@PathVariable String studentID){
        long term = -1;
        Student student = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentID);

        for(PassedCourse passedCourse : student.getStudentPassedCourses()){
            if(passedCourse.getTerm() > term){
                term = passedCourse.getTerm();
            }
        }

        term += 1;
        return Long.toString(term);
    }
}

