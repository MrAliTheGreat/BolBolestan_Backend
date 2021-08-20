package ExternalServicesHandlers;

import BolBolCodes.Offering;
import BolBolCodes.PassedCourse;
import BolBolCodes.Student;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class templateHandler {

    private static String createFillableFormat(String name){
        return "#" + name + "#";
    }

    private static String createStringsFormatCourses(ArrayList<String> arrayListStrings){
        String result = "";
        if(arrayListStrings.isEmpty()){
            return "";
        }

        for(String name : arrayListStrings){
            result += name;
            result += "|";
        }
        return result.substring(0 , result.length() - 1);
    }

    private static String createStringsFormatCourse(ArrayList<String> arrayListStrings){
        String result = "";
        if(arrayListStrings.isEmpty()){
            return "";
        }

        for(String name : arrayListStrings){
            result += name;
            result += ", ";
        }
        return result.substring(0 , result.length() - 2);
    }

    private static String replaceValuesInCoursesHTML(Offering offering , String singleCourseHTML){
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("code") , offering.getCode());
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("classCode") , offering.getClassCode());
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("name") , offering.getName());
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("units") , Long.toString(offering.getUnits()));
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("capacity") , Long.toString(offering.getCapacity()));
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("type") , offering.getType());
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("days") , createStringsFormatCourses(offering.getDays()));
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("time") , offering.getTime());
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("examStart") , offering.getExamTimeStart());
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("examEnd") , offering.getExamTimeEnd());
        singleCourseHTML = singleCourseHTML.replaceAll(createFillableFormat("prerequisites") , createStringsFormatCourses(offering.getPrerequisites()));
        return singleCourseHTML;
    }

    private static HashMap<String , String> createPlanTableTimeFormat(){
        HashMap<String , String> timeCoordinates = new HashMap<>();
        timeCoordinates.put("7:30-9:00" , "1");
        timeCoordinates.put("9:00-10:30" , "2");
        timeCoordinates.put("10:30-12:00" , "3");
        timeCoordinates.put("14:00-15:30" , "4");
        timeCoordinates.put("16:00-17:30" , "5");

        return timeCoordinates;
    }

    private static HashMap<String , String> createPlanTableDayFormat(){
        HashMap<String , String> dayCoordinates = new HashMap<>();
        dayCoordinates.put("Saturday" , "1");
        dayCoordinates.put("Sunday" , "2");
        dayCoordinates.put("Monday" , "3");
        dayCoordinates.put("Tuesday" , "4");
        dayCoordinates.put("Wednesday" , "5");

        return dayCoordinates;
    }

    private static String clearUnusedSpots(String planHTML){
        for(int i = 1 ; i <= 5 ; i++){
            for(int j = 1 ; j <= 5 ; j++){
                planHTML = planHTML.replaceAll(createFillableFormat(Integer.toString(i) + Integer.toString(j)) , "");
            }
        }
        return planHTML;
    }

    public static String getTemplate(String templateName){
        String templateResult = null;
        try{
            templateResult = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/" + templateName)));
        }catch(IOException err){
            System.out.println(err.getMessage());
        }
        return templateResult;
    }

    public static String fillCoursesMainTemplate(Offering offering, String singleCourseHTML){
        return replaceValuesInCoursesHTML(offering , singleCourseHTML);
    }

    public static String fillProfilePassedCourseTemplate(PassedCourse passedCourse , String singlePassedCourseHTML){
        singlePassedCourseHTML = singlePassedCourseHTML.replaceAll(createFillableFormat("code") , passedCourse.getCode());
        singlePassedCourseHTML = singlePassedCourseHTML.replaceAll(createFillableFormat("grade") , Long.toString(passedCourse.getGrade()));
        return singlePassedCourseHTML;
    }

    public static String fillProfilePreTemplate(Student student , String stringGPA , int totalUnits , String singleProfileInfoHTML){
        singleProfileInfoHTML = singleProfileInfoHTML.replaceAll(createFillableFormat("id") , student.getStudentId());
        singleProfileInfoHTML = singleProfileInfoHTML.replaceAll(createFillableFormat("name") , student.getName());
        singleProfileInfoHTML = singleProfileInfoHTML.replaceAll(createFillableFormat("secondName") , student.getSecondName());
        singleProfileInfoHTML = singleProfileInfoHTML.replaceAll(createFillableFormat("birthDate") , student.getBirthDate());
        singleProfileInfoHTML = singleProfileInfoHTML.replaceAll(createFillableFormat("GPA") , stringGPA);
        singleProfileInfoHTML = singleProfileInfoHTML.replaceAll(createFillableFormat("totalUnits") , Integer.toString(totalUnits));

        return singleProfileInfoHTML;
    }

    public static String fillCourseTemplate(Offering offering , String courseHTML){
        courseHTML = courseHTML.replaceAll(createFillableFormat("code") , offering.getCode());
        courseHTML = courseHTML.replaceAll(createFillableFormat("classCode") , offering.getClassCode());
        courseHTML = courseHTML.replaceAll(createFillableFormat("units") , Long.toString(offering.getUnits()));
        courseHTML = courseHTML.replaceAll(createFillableFormat("days") , createStringsFormatCourse(offering.getDays()));
        courseHTML = courseHTML.replaceAll(createFillableFormat("time") , offering.getTime());

        return courseHTML;
    }

    public static String fillAddCourseResultTemplate(boolean funcCallResult , String addCourseResultHTML){
        if(funcCallResult){
            return addCourseResultHTML.replaceAll(createFillableFormat("Message") , "Success!");
        }
        return addCourseResultHTML.replaceAll(createFillableFormat("Message") , "Failed!");
    }

    public static String fillChangePlanTemplate(Student student , String courseHTML){
        String allCoursesHTML = "";
        String helperCourseHTML;
        for(Offering offer : student.getStudentCourses()){
            helperCourseHTML = courseHTML;
            helperCourseHTML = helperCourseHTML.replaceAll(createFillableFormat("code") , offer.getCode());
            helperCourseHTML = helperCourseHTML.replaceAll(createFillableFormat("classCode") , offer.getClassCode());
            helperCourseHTML = helperCourseHTML.replaceAll(createFillableFormat("name") , offer.getName());
            helperCourseHTML = helperCourseHTML.replaceAll(createFillableFormat("units") , Long.toString(offer.getUnits()));
            helperCourseHTML = helperCourseHTML.replaceAll(createFillableFormat("studentId") , student.getStudentId());
            allCoursesHTML += helperCourseHTML;
        }
        return allCoursesHTML;
    }

    public static String fillPlanHTML(Student student , String planHTML){
        HashMap<String , String> timeEncodings = createPlanTableTimeFormat();
        HashMap<String , String> dayEncodings = createPlanTableDayFormat();

        for(Offering offer : student.getStudentCourses()){
            for(String day : offer.getDays()){
                planHTML = planHTML.replaceAll(createFillableFormat(dayEncodings.get(day) + timeEncodings.get(offer.getTime())) , offer.getName());
            }
        }

        return clearUnusedSpots(planHTML);
    }

    public static String fillSubmitTemplate(String studentId , String totalOfferUnits , String submitHTML){
        submitHTML = submitHTML.replaceAll(createFillableFormat("studentId") , studentId);
        submitHTML = submitHTML.replaceAll(createFillableFormat("totalUnitsOffers") , totalOfferUnits);
        return submitHTML;
    }
}
