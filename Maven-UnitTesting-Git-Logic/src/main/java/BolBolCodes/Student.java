package BolBolCodes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private ArrayList<Offering> studentCourses = new ArrayList<Offering>();
    private ArrayList<Boolean> finalizedOfferings = new ArrayList<Boolean>();

    public Student(String studentId , String name , String enteredAt){
        this.studentId = studentId;
        this.name = name;
        this.enteredAt = enteredAt;
    }

    public boolean addToWeeklyScheduleOfStudent(Offering offer){
        if(offer.getCapacity() <= 0){
            JSONObject resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "CapacityError " + offer.getCode());
            System.out.println(resultMessage);
            return false;
        }

        for(Offering offering : this.studentCourses){
            if(offering.getCode().equals(offer.getCode())){
                JSONObject resultMessage = new JSONObject();
                resultMessage.put("success" , false);
                resultMessage.put("error" , "Offer already exists in schedule!");
                System.out.println(resultMessage);
                return false;
            }
        }

        studentCourses.add(offer);
        finalizedOfferings.add(false);

        // Success Message
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , true);
        resultMessage.put("data" , "Offer was added to student's weekly schedule successfully!");
        System.out.println(resultMessage);
        return true;
    }

    private int getTotalUnitsFinalized(){
        int totalUnits = 0;
        for(int i = 0 ; i < this.studentCourses.size() ; i++){
            if(this.finalizedOfferings.get(i)){
                totalUnits += this.studentCourses.get(i).getUnits();
            }
        }
        return totalUnits;
    }

    public boolean removeFromWeeklyScheduleOfStudent(String code){
        JSONObject resultMessage;
        for(int i = 0 ; i < studentCourses.size() ; i++){
            if(studentCourses.get(i).getCode().equals(code)){
                if(finalizedOfferings.get(i)){
                    if(getTotalUnitsFinalized() - studentCourses.get(i).getUnits() < 12){
                        resultMessage = new JSONObject();
                        resultMessage.put("success" , false);
                        resultMessage.put("error" , "MinimumUnitsError");
                        System.out.println(resultMessage);
                        return false;
                    }
                    studentCourses.get(i).incrementCapacity();
                }
                studentCourses.remove(i);
                finalizedOfferings.remove(i);
                // Success Message
                resultMessage = new JSONObject();
                resultMessage.put("success" , true);
                resultMessage.put("data" , "Offer was removed from student's weekly schedule successfully!");
                System.out.println(resultMessage);
                return true;
            }
        }

        resultMessage = new JSONObject();
        resultMessage.put("success" , false);
        resultMessage.put("error" , "OfferingNotFound");
        System.out.println(resultMessage);
        return false;
    }

    public boolean getWeeklyScheduleOfStudent(){
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , true);

        JSONObject weeklyScheduleJSON = new JSONObject();

        JSONObject courseJSON , classTime , examTime;
        JSONArray arrCoursesJSON = new JSONArray();

        for(int i = 0; i < studentCourses.size() ; i++){
            classTime = new JSONObject();
            classTime.put("days" , studentCourses.get(i).getDays());
            classTime.put("time" , studentCourses.get(i).getTime());

            examTime = new JSONObject();
            examTime.put("start" , studentCourses.get(i).getExamTimeStart());
            examTime.put("end" , studentCourses.get(i).getExamTimeEnd());

            courseJSON = new JSONObject();
            courseJSON.put("code" , studentCourses.get(i).getCode());
            courseJSON.put("name" , studentCourses.get(i).getName());
            courseJSON.put("classTime" , classTime);
            courseJSON.put("examTime" , examTime);
            if(finalizedOfferings.get(i)){
                courseJSON.put("status" , "finalized");
            }else{
                courseJSON.put("status" , "non-finalized");
            }

            arrCoursesJSON.add(courseJSON);
        }

        weeklyScheduleJSON.put("weeklySchedule" , arrCoursesJSON);

        resultMessage.put("data" , weeklyScheduleJSON);
        System.out.println(resultMessage);
        return true;
    }

    private boolean checkNumberTakenUnits(){
        JSONObject resultMessage;
        long totalNumUnits = 0;

        for(Offering offer : studentCourses){
            totalNumUnits += offer.getUnits();
        }

        if(totalNumUnits >= 12 && totalNumUnits <= 20){
            return true;
        }
        else if(totalNumUnits < 12){
            resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "MinimumUnitsError");
            System.out.println(resultMessage);
        }
        else{
            resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "MaximumUnitsError");
            System.out.println(resultMessage);
        }
        return false;
    }

    private boolean checkCourseCapacity(){
        for(Offering offer : studentCourses){
            if(offer.getCapacity() <= 0){
                JSONObject resultMessage = new JSONObject();
                resultMessage.put("success" , false);
                resultMessage.put("error" , "CapacityError " + offer.getCode());
                System.out.println(resultMessage);
                return false;
            }
        }
        return true;
    }

    public int calculateTimeValue(String time){
        int timeValue = 0;
        String[] timeDetails = time.split(":" , 2);
        timeValue += (Integer.parseInt(timeDetails[0]) * 60);

        if(timeDetails.length == 2){
            timeValue += Integer.parseInt(timeDetails[1]);
        }
        return timeValue;
    }

    private boolean checkTwoCourseTime(Offering offer_1 , Offering offer_2){
        String[] time_offer_1 = offer_1.getTime().split("-" , 2);
        String[] time_offer_2 = offer_2.getTime().split("-" , 2);

        int startOffer1 , endOffer1 , startOffer2 , endOffer2;
        startOffer1 = calculateTimeValue(time_offer_1[0]); endOffer1 = calculateTimeValue(time_offer_1[1]);
        startOffer2 = calculateTimeValue(time_offer_2[0]); endOffer2 = calculateTimeValue(time_offer_2[1]);

        if(startOffer1 < endOffer2 && startOffer2 < endOffer1){
            JSONObject resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "ClassTimeCollisionError " + offer_1.getCode() + " " + offer_2.getCode());
            System.out.println(resultMessage);
            return false;
        }

        return true;
    }

    private boolean checkTwoCourseDays(Offering offer_1 , Offering offer_2){
        for(String day_1 : offer_1.getDays()){
            for(String day_2 : offer_2.getDays()){
                if(day_2.equals(day_1)){
                    if(!checkTwoCourseTime(offer_1 , offer_2)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkCourseTime(){
        for(int i = 0 ; i < studentCourses.size() - 1 ; i++){
            for(int j = i + 1 ; j < studentCourses.size() ; j++){
                if(!checkTwoCourseDays(studentCourses.get(i) , studentCourses.get(j))){
                    return false;
                }
            }
        }
        return true;
    }

    private int calculateExamDateValue(String date){
        // Each month is 30 days!
        String[] dateDetails = date.split("-" , 3);
        return (Integer.parseInt(dateDetails[0]) * 365) + (Integer.parseInt(dateDetails[1]) * 30) + (Integer.parseInt(dateDetails[2]));
    }

    private int calculateExamTimeValue(String time){
        String[] timeDetails = time.split(":" , 3);
        return (Integer.parseInt(timeDetails[0]) * 3600) + (Integer.parseInt(timeDetails[1]) * 60) + (Integer.parseInt(timeDetails[2]));

    }

    private boolean checkTwoCourseExam(Offering offer1 , Offering offer2){
        // 0: date , 1: time
        String[] examTime_offer1_start = offer1.getExamTimeStart().split("T" , 2);
        String[] examTime_offer1_end = offer1.getExamTimeEnd().split("T" , 2);
        String[] examTime_offer2_start = offer2.getExamTimeStart().split("T" , 2);
        String[] examTime_offer2_end = offer2.getExamTimeEnd().split("T" , 2);

        int startDate1 , endDate1 , startDate2 , endDate2;
        startDate1 = calculateExamDateValue(examTime_offer1_start[0]); endDate1 = calculateExamDateValue(examTime_offer1_end[0]);
        startDate2 = calculateExamDateValue(examTime_offer2_start[0]); endDate2 = calculateExamDateValue(examTime_offer2_end[0]);

        int startTime1 , endTime1 , startTime2 , endTime2;
        startTime1 = calculateExamTimeValue(examTime_offer1_start[1]); endTime1 = calculateExamTimeValue(examTime_offer1_end[1]);
        startTime2 = calculateExamTimeValue(examTime_offer2_start[1]); endTime2 = calculateExamTimeValue(examTime_offer2_end[1]);

        if(startDate1 <= endDate2 && startDate2 <= endDate1){
            if(startTime1 < endTime2 && startTime2 < endTime1){
                JSONObject resultMessage = new JSONObject();
                resultMessage.put("success" , false);
                resultMessage.put("error" , "ExamTimeCollisionError " + offer1.getCode() + " " + offer2.getCode());
                System.out.println(resultMessage);
                return false;
            }
        }
        return true;
    }

    private boolean checkExamTime(){
        for(int i = 0 ; i < studentCourses.size() - 1 ; i++){
            for(int j = i + 1 ; j < studentCourses.size() ; j++){
                if(!checkTwoCourseExam(studentCourses.get(i) , studentCourses.get(j))){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean finalizeScheduleOfStudent(){
        if(!checkNumberTakenUnits()){
            return false;
        }
        if(!checkCourseCapacity()){
            return false;
        }
        if(!checkCourseTime()){
            return false;
        }
        if(!checkExamTime()){
            return false;
        }

        for(int i = 0; i < studentCourses.size() ; i++){
            studentCourses.get(i).decrementCapacity();
            finalizedOfferings.set(i , true);
        }

        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , true);
        resultMessage.put("data" , "Offers were finalized!");
        System.out.println(resultMessage);
        return true;
    }

    public String getStudentId() {
        return studentId;
    }

    protected ArrayList<Boolean> getFinalizedOfferingsForTEST() {
        return finalizedOfferings;
    }

    protected ArrayList<Offering> getStudentCoursesForTEST() {
        return studentCourses;
    }
}
