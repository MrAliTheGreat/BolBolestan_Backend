package BolBol.CA5_Server.Domain.Models;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.PassedCourse;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;

import java.util.*;

public class FrontStudent extends Student{
    // Fields
    private String totalAverageGrade;
    private String numUnitsPassed;

    private HashMap<String , List<PassedCourse>> passedCoursesBySemester = new HashMap<>();
    private HashMap<String , String> averageGradeBySemester = new HashMap<>();
    private HashMap<String , String> courseNamesByCode = new HashMap<>();

    public FrontStudent(Student student){
        super(student.getStudentId() , student.getName() , student.getSecondName() , student.getBirthDate() ,
              student.getField(), student.getFaculty(), student.getLevel(), student.getStatus(), student.getImgAddress());

        this.totalAverageGrade = String.format("%.2f" , student.calculateGPA() );
        this.numUnitsPassed = Integer.toString(student.calculateTotalNumPassedUnits());
        for(PassedCourse passedCourse : student.getStudentPassedCourses()){
             /*
               Checks if term exists
               if yes do nothing and add passedCourse to list of that key
               if no make new ArrayList in that key first then add the passedCourse to the list
             */

            this.passedCoursesBySemester.computeIfAbsent(Long.toString(passedCourse.getTerm()), k -> new ArrayList<>()).add(passedCourse);
        }

        Iterator<HashMap.Entry<String , List<PassedCourse>> > semestersGpaIter = this.passedCoursesBySemester.entrySet().iterator();
        while(semestersGpaIter.hasNext()){
            Map.Entry<String , List<PassedCourse>> semestersGpaTuple = semestersGpaIter.next();
            averageGradeBySemester.put(semestersGpaTuple.getKey() , String.format("%.2f" , calculateSemesterGPA(semestersGpaTuple.getValue()) ) );
        }

        for(PassedCourse passedCourse : student.getStudentPassedCourses()){
            this.courseNamesByCode.put(passedCourse.getCode() , BolBolSystem.getBolBolSystemInstance().findOfferNameByCode(passedCourse.getCode()));
        }
    }

    public String getTotalAverageGrade() {
        return totalAverageGrade;
    }

    public String getNumUnitsPassed() {
        return numUnitsPassed;
    }

    public HashMap<String, List<PassedCourse>> getPassedCoursesBySemester() {
        return passedCoursesBySemester;
    }

    public HashMap<String, String> getAverageGradeBySemester() {
        return averageGradeBySemester;
    }

    public HashMap<String, String> getCourseNamesByCode() {
        return courseNamesByCode;
    }
}
