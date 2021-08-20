package BolBolCodes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class BolBolSystem {

    private ArrayList<Student> students = new ArrayList<Student>();
    private ArrayList<Offering> offerings = new ArrayList<Offering>();
    private Student userStudent;

    private boolean setUserStudent(String studentId){
        Student foundStudent = findStudentFromId(studentId);
        if(Objects.isNull(foundStudent)){
            JSONObject resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "StudentNotFound");
            System.out.println(resultMessage);
            return false;
        }
        this.userStudent = foundStudent;
        return true;
    }

    private boolean checkOfferExistence(String code){
        Offering foundOffer = findOfferFromCode(code);
        if(Objects.isNull(foundOffer)){
            JSONObject resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "OfferingNotFound");
            System.out.println(resultMessage);
            return false;
        }
        return true;
    }

    private Student findStudentFromId(String studentId){
        for(Student searchStudent : this.students){
            if(searchStudent.getStudentId().equals(studentId)){
                return searchStudent;
            }
        }
        return null;
    }

    private Offering findOfferFromCode(String code){
        for(Offering searchOffer : this.offerings){
            if(searchOffer.getCode().equals(code)){
                return searchOffer;
            }
        }
        return null;
    }

    // If Needed
    public BolBolSystem(){}

    public boolean addOffer(Offering offer){
        if(!Objects.isNull(findOfferFromCode(offer.getCode()))){
            JSONObject failMessage = new JSONObject();
            failMessage.put("success" , false);
            failMessage.put("error" , "Offering with this code already exists");
            System.out.println(failMessage);
            return false;
        }
        offerings.add(offer);

        // Success Message
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , true);
        resultMessage.put("data" , "Offer was added successfully!");
        System.out.println(resultMessage);
        return true;
    }

    public boolean addStudent(Student student){
        if(!Objects.isNull(findStudentFromId(student.getStudentId()))){
            JSONObject failMessage = new JSONObject();
            failMessage.put("success" , false);
            failMessage.put("error" , "Student with this ID already exists");
            System.out.println(failMessage);
            return false;
        }
        students.add(student);

        // Success Message
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , true);
        resultMessage.put("data" , "Student was added successfully!");
        System.out.println(resultMessage);
        return true;
    }

    public boolean getOfferings(String studentId){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }

        // Show Offer
        JSONObject resultMessage = new JSONObject() , offerJSON;
        JSONArray arrOffersJSON = new JSONArray();
        resultMessage.put("success" , true);

        boolean visited[] = new boolean[offerings.size()];

        for(int i = 0 ; i < offerings.size() ; i++){
            for(int j = i ; j < offerings.size(); j++){
                if(offerings.get(i).getName().equals(offerings.get(j).getName()) && !visited[j]){
                    visited[j] = true;
                    offerJSON = new JSONObject();
                    offerJSON.put("code" , offerings.get(j).getCode());
                    offerJSON.put("name" , offerings.get(j).getName());
                    offerJSON.put("Instructor" , offerings.get(j).getInstructor());
                    arrOffersJSON.add(offerJSON);
                }
            }
        }

        resultMessage.put("data" , arrOffersJSON);
        System.out.println(resultMessage);
        return true;
    }

    public boolean getOffering(String studentId , String code){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code)){
            return false;
        }

        Offering foundOffer = findOfferFromCode(code);
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , true);

        JSONObject classTime = new JSONObject();
        classTime.put("days" , foundOffer.getDays());
        classTime.put("time" , foundOffer.getTime());

        JSONObject examTime = new JSONObject();
        examTime.put("start" , foundOffer.getExamTimeStart());
        examTime.put("end" , foundOffer.getExamTimeEnd());

        JSONObject offerJSON = new JSONObject();
        offerJSON.put("code" , foundOffer.getCode());
        offerJSON.put("name" , foundOffer.getName());
        offerJSON.put("Instructor" , foundOffer.getInstructor());
        offerJSON.put("units" , foundOffer.getUnits());
        offerJSON.put("classTime" , classTime);
        offerJSON.put("examTime" , examTime);
        offerJSON.put("capacity" , foundOffer.getCapacity());
        offerJSON.put("prerequisites" , foundOffer.getPrerequisites());

        resultMessage.put("data" , offerJSON);
        System.out.println(resultMessage);
        return true;
    }

    public boolean addToWeeklySchedule(String studentId , String code){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code)){
            return false;
        }

        return findStudentFromId(studentId).addToWeeklyScheduleOfStudent(findOfferFromCode(code));
    }

    public boolean removeFromWeeklySchedule(String studentId , String code){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code)){
            return false;
        }

        return findStudentFromId(studentId).removeFromWeeklyScheduleOfStudent(code);
    }

    public boolean getWeeklySchedule(String studentId){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }
        return this.userStudent.getWeeklyScheduleOfStudent();
    }

    public boolean finalizeSchedule(String studentId){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }

        return findStudentFromId(studentId).finalizeScheduleOfStudent();
    }

    public boolean executeUnknownCommand(){
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , false);
        resultMessage.put("error" , "UnknownCommand");
        System.out.println(resultMessage);
        return false;
    }

    protected Student getUserStudentForTEST() {
        return this.userStudent;
    }
}
