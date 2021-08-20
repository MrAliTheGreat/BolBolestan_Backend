package ServerCodes;

import BolBolCodes.PassedCourse;
import BolBolCodes.Student;
import ExternalServicesHandlers.importHandler;
import ExternalServicesHandlers.templateHandler;
import BolBolCodes.BolBolSystem;
import BolBolCodes.Offering;
import io.javalin.Javalin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class ServerInterface {

    private BolBolSystem bolbolSystem = new BolBolSystem();
    private Javalin server;


    public void setAllImportedData(String coursesAddress , String studentsAddress , String gradesAddressBASE){
        this.bolbolSystem.setBolBolCoursesExternalServer(coursesAddress);
        this.bolbolSystem.setBolBolStudentsExternalServer(studentsAddress);
        this.bolbolSystem.setStudentsGradesExternalServer(gradesAddressBASE);
    }

    public void start(String coursesAddress , String studentsAddress , String gradesAddressBASE , int serverPort){
        setAllImportedData(coursesAddress , studentsAddress , gradesAddressBASE);
        runServer(serverPort);
    }

    private String createCoursesHTML(){
        String resultHTML = templateHandler.getTemplate("coursesPre.html");
        String singleCourseHTML = templateHandler.getTemplate("coursesMain.html");

        for(Offering offering : this.bolbolSystem.getOfferings()){
            resultHTML += templateHandler.fillCoursesMainTemplate(offering , singleCourseHTML);
        }

        resultHTML += templateHandler.getTemplate("coursesPost.html");

        return resultHTML;
    }

    private String createProfileHTML(String studentId){
        Student targetStudent = this.bolbolSystem.findStudentFromId(studentId);
        String resultHTML = templateHandler.getTemplate("profilePre.html");
        String singlePassedCourseHTML = templateHandler.getTemplate("profilePassedCourse.html");

        resultHTML = templateHandler.fillProfilePreTemplate(targetStudent , String.format("%.2f" , targetStudent.calculateGPA()) ,
                                                            targetStudent.calculateTotalNumPassedUnits() , resultHTML);

        for (PassedCourse passedCourse : targetStudent.getStudentPassedCourses()){
            resultHTML += templateHandler.fillProfilePassedCourseTemplate(passedCourse , singlePassedCourseHTML);
        }

        resultHTML += templateHandler.getTemplate("coursesPost.html"); // Cause end parts are similar

        return resultHTML;
    }

    private String createCourseHTML(String courseId , String classCode){
        Offering targetOffer = this.bolbolSystem.findOfferFromIdAndCode(courseId , classCode);
        String resultHTML = templateHandler.getTemplate("course.html");

        resultHTML = templateHandler.fillCourseTemplate(targetOffer , resultHTML);
        return resultHTML;
    }

    private String createAddCourseResultHTML(String studentId , String courseId , String classCode){
        String resultHTML = templateHandler.getTemplate("addCourseResult.html");

        boolean funcCallResult = this.bolbolSystem.addToWeeklySchedule(studentId , courseId , classCode);

        resultHTML = templateHandler.fillAddCourseResultTemplate(funcCallResult , resultHTML);

        return resultHTML;
    }

    private String createChangePlanGetHTML(String studentId){
        Student targetStudent = this.bolbolSystem.findStudentFromId(studentId);
        String resultHTML = templateHandler.getTemplate("change_planPre.html");
        String courseHTML = templateHandler.getTemplate("change_planMain.html");

        resultHTML += templateHandler.fillChangePlanTemplate(targetStudent , courseHTML);

        resultHTML += templateHandler.getTemplate("coursesPost.html"); // Cause end parts are similar

        return resultHTML;
    }

    private String createChangePlanPostHTML(String studentId , String courseId , String classCode){
        this.bolbolSystem.removeFromWeeklySchedule(studentId , courseId , classCode);
        return createChangePlanGetHTML(studentId);
    }

    private String createPlanHTML(String studentId){
        Student targetStudent = this.bolbolSystem.findStudentFromId(studentId);
        String resultHTML = templateHandler.getTemplate("plan.html");
        resultHTML = templateHandler.fillPlanHTML(targetStudent , resultHTML);
        return resultHTML;
    }

    private String createSubmitHTML(String studentId){
        Student targetStudent = this.bolbolSystem.findStudentFromId(studentId);
        String resultHTML = templateHandler.getTemplate("submit.html");
        resultHTML = templateHandler.fillSubmitTemplate(targetStudent.getStudentId() ,
                                                        Integer.toString(targetStudent.calculateTotalUnitsOffers()) , resultHTML);
        return resultHTML;
    }

    public void runServer(int serverPort){
        this.server = Javalin.create().start(serverPort);
        this.server.get("/" , ctx -> ctx.html(templateHandler.getTemplate("firstPage.html")));

        this.server.get("/courses" , ctx -> {
            ctx.html(createCoursesHTML());
        });

        this.server.get("/profile/:studentId" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findStudentFromId(ctx.pathParam("studentId")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else{
                ctx.html(createProfileHTML(ctx.pathParam("studentId")));
            }
        });

        this.server.get("/course/:courseId/:classCode" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findOfferFromIdAndCode(ctx.pathParam("courseId") , ctx.pathParam("classCode")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else {
                ctx.html(createCourseHTML(ctx.pathParam("courseId"), ctx.pathParam("classCode")));
            }
        });

        this.server.post("/addCourseResult/:courseId/:classCode" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findStudentFromId(ctx.formParam("std_id"))) ||
               Objects.isNull(this.bolbolSystem.findOfferFromIdAndCode(ctx.pathParam("courseId") , ctx.pathParam("classCode")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else {
                ctx.html(createAddCourseResultHTML(ctx.formParam("std_id"), ctx.pathParam("courseId"), ctx.pathParam("classCode")));
            }
        });

        this.server.get("/change_plan/:studentId" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findStudentFromId(ctx.pathParam("studentId")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else {
                ctx.html(createChangePlanGetHTML(ctx.pathParam("studentId")));
            }
        });

        this.server.post("/change_plan/:studentId" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findStudentFromId(ctx.pathParam("studentId")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else {
                ctx.html(createChangePlanPostHTML(ctx.pathParam("studentId") , ctx.formParam("course_code"), ctx.formParam("class_code")));
            }
        });

        this.server.get("/plan/:studentId" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findStudentFromId(ctx.pathParam("studentId")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else {
                ctx.html(createPlanHTML(ctx.pathParam("studentId")));
            }
        });

        this.server.get("/submit/:studentId" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findStudentFromId(ctx.pathParam("studentId")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else {
                ctx.html(createSubmitHTML(ctx.pathParam("studentId")));
            }
        });

        this.server.post("/submit/:studentId" , ctx -> {
            if(Objects.isNull(this.bolbolSystem.findStudentFromId(ctx.pathParam("studentId")))){
                ctx.html(templateHandler.getTemplate("404.html"));
            }else {
                boolean funcCallResult = this.bolbolSystem.finalizeSchedule(ctx.pathParam("studentId"));
                if(funcCallResult){
                    ctx.redirect("/submit_ok");
                }else{
                    ctx.redirect("/submit_failed");
                }
            }
        });

        this.server.get("/submit_ok" , ctx -> ctx.html(templateHandler.getTemplate("submit_ok.html")));

        this.server.get("/submit_failed" , ctx -> ctx.html(templateHandler.getTemplate("submit_failed.html")));

    }

    public void stopServer(){
        this.server.stop();
    }


    public ArrayList<Student> getBolBolSystemStudentsForTEST() {
        return this.bolbolSystem.getStudents();
    }

    public ArrayList<Offering> getBolBolSystemOffersForTEST() {
        return this.bolbolSystem.getOfferings();
    }
}
