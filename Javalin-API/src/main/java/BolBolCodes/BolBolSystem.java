package BolBolCodes;

import ExternalServicesHandlers.importHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class BolBolSystem {

    private ArrayList<Student> students = new ArrayList<Student>();
    private ArrayList<Offering> offerings = new ArrayList<Offering>();
    private Student userStudent;


    private ArrayList<String> JSONArrayToArrayList(JSONArray jsonArray){
        ArrayList<String> jsonString = new ArrayList<String>();
        for(int i = 0; i<jsonArray.size(); i++){
            jsonString.add((String) jsonArray.get(i));
        }
        return jsonString;
    }

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

    private boolean checkOfferExistence(String code , String classCode){
        Offering foundOffer = findOfferFromIdAndCode(code , classCode);
        if(Objects.isNull(foundOffer)){
            JSONObject resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "OfferingNotFound");
            System.out.println(resultMessage);
            return false;
        }
        return true;
    }

    public Student findStudentFromId(String studentId){
        for(Student searchStudent : this.students){
            if(searchStudent.getStudentId().equals(studentId)){
                return searchStudent;
            }
        }
        return null;
    }

    public Offering findOfferFromIdAndCode(String code , String classCode){
        for(Offering searchOffer : this.offerings){
            if(searchOffer.getCode().equals(code) && searchOffer.getClassCode().equals(classCode)){
                return searchOffer;
            }
        }
        return null;
    }

    public long findCourseUnitsFromCode(String code){
        for(Offering offering : this.offerings){
            if(offering.getCode().equals(code)){
                return offering.getUnits();
            }
        }
        return -1;
    }

    // If Needed
    public BolBolSystem(){}

    public boolean addOffer(Offering offer){
        if(!Objects.isNull(findOfferFromIdAndCode(offer.getCode() , offer.getClassCode()))){
            JSONObject failMessage = new JSONObject();
            failMessage.put("success" , false);
            failMessage.put("error" , "Offering with this code already exists");
            System.out.println(failMessage);
            return false;
        }
        offerings.add(offer);

//        // Success Message
//        JSONObject resultMessage = new JSONObject();
//        resultMessage.put("success" , true);
//        resultMessage.put("data" , "Offer was added successfully!");
//        System.out.println(resultMessage);
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

//        // Success Message
//        JSONObject resultMessage = new JSONObject();
//        resultMessage.put("success" , true);
//        resultMessage.put("data" , "Student was added successfully!");
//        System.out.println(resultMessage);
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

    public boolean getOffering(String studentId , String code , String classCode){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code , classCode)){
            return false;
        }

        Offering foundOffer = findOfferFromIdAndCode(code , classCode);
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

    public boolean addToWeeklySchedule(String studentId , String code , String classCode){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code , classCode)){
            return false;
        }

        return findStudentFromId(studentId).addToWeeklyScheduleOfStudent(findOfferFromIdAndCode(code , classCode));
    }

    public boolean removeFromWeeklySchedule(String studentId , String code , String classCode){
        // Set userStudent
        if(!setUserStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code , classCode)){
            return false;
        }

        return findStudentFromId(studentId).removeFromWeeklyScheduleOfStudent(code , classCode);
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

    public boolean setBolBolCoursesExternalServer(String courseAddress){
        JSONArray jsonArrayCourses = importHandler.importFromAddress(courseAddress);
        JSONObject jsonObject;
        Offering offering;
        for(Object object : jsonArrayCourses){
            jsonObject = (JSONObject) object;

            JSONObject classTime = (JSONObject) jsonObject.get("classTime");
            JSONObject examTime = (JSONObject) jsonObject.get("examTime");
            offering = new Offering((String) jsonObject.get("code") , (String) jsonObject.get("name") ,
                    (String) jsonObject.get("instructor") , (long) jsonObject.get("units") ,
                    JSONArrayToArrayList((JSONArray) classTime.get("days")) ,
                    (String) classTime.get("time") , (String) examTime.get("start") ,
                    (String) examTime.get("end") , (long) jsonObject.get("capacity") ,
                    JSONArrayToArrayList((JSONArray) jsonObject.get("prerequisites")) ,
                    (String) jsonObject.get("classCode") , (String) jsonObject.get("type"));

            this.addOffer(offering);
        }
        System.out.println("Courses were imported successfully!");
        return true;
    }

    public boolean setBolBolStudentsExternalServer(String studentsAddress){
        JSONArray jsonArrayStudents = importHandler.importFromAddress(studentsAddress);
        JSONObject jsonObject;
        Student student;
        for(Object object : jsonArrayStudents){
            jsonObject = (JSONObject) object;

            student = new Student((String) jsonObject.get("id") , (String) jsonObject.get("name") ,
                    (String) jsonObject.get("secondName"), (String) jsonObject.get("birthDate"));

            this.addStudent(student);
        }
        System.out.println("Students were imported successfully!");
        return true;
    }

    public boolean setStudentsGradesExternalServer(String gradesAddressBASE){
        JSONArray jsonArrayGrades;
        JSONObject jsonObject;
        PassedCourse passedCourse;

        for(Student student : this.students){
            jsonArrayGrades = importHandler.importFromAddress(gradesAddressBASE + student.getStudentId());
            for(Object object : jsonArrayGrades){
                jsonObject = (JSONObject) object;

                passedCourse = new PassedCourse((String) jsonObject.get("code") , (long) jsonObject.get("grade") ,
                        this.findCourseUnitsFromCode((String) jsonObject.get("code")));
                student.addToPassedCourses(passedCourse);
            }
        }
        System.out.println("Passed courses for each student were imported successfully!");
        return true;
    }

    protected Student getUserStudentForTEST() {
        return this.userStudent;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Offering> getOfferings() {
        return offerings;
    }
}
