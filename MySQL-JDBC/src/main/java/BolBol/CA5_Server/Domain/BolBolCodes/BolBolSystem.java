package BolBol.CA5_Server.Domain.BolBolCodes;

import BolBol.CA5_Server.Domain.ExternalServicesHandlers.importHandler;
import BolBol.CA5_Server.Repository.BolBolRepo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class BolBolSystem {
    // Fields
    private ArrayList<Student> students = new ArrayList<Student>();
    private ArrayList<Offering> offerings = new ArrayList<Offering>();
    private Student onlineStudent;

    private ArrayList<Offering> searchOffers = new ArrayList<>();
    private String searchName;
    private boolean isCategorized;

    // Constructor
    public BolBolSystem(){
        this.onlineStudent = null;
        this.searchName = "";
        isCategorized = false;
    }

    // Instance
    private static BolBolSystem bolBolSystemInstance;

    public static BolBolSystem getBolBolSystemInstance() {
        if(Objects.isNull(bolBolSystemInstance)){
            bolBolSystemInstance = new BolBolSystem();
        }
        return bolBolSystemInstance;
    }


    // Tools
    private ArrayList<String> JSONArrayToArrayList(JSONArray jsonArray){
        ArrayList<String> jsonString = new ArrayList<String>();
        for(int i = 0; i<jsonArray.size(); i++){
            jsonString.add((String) jsonArray.get(i));
        }
        return jsonString;
    }

    public boolean setOnlineStudent(String studentId){
        Student foundStudent = findStudentFromId(studentId);
        if(Objects.isNull(foundStudent)){
//            JSONObject resultMessage = new JSONObject();
//            resultMessage.put("success" , false);
//            resultMessage.put("error" , "StudentNotFound");
//            System.out.println(resultMessage);
            return false;
        }
        this.onlineStudent = foundStudent;
        return true;
    }

    public boolean logoutOnlineStudent(){
        this.onlineStudent = null;
        return true;
    }


    // Checkers
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

    private boolean checkOfferExistenceDB(String code , String classCode){
        try{
            BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(code , classCode);
            return true;
        }catch (SQLException e){
            JSONObject resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "OfferingNotFound");
            System.out.println(resultMessage);
            return false;
        }
    }


    // Finders
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

    public String findOfferNameByCode(String code){
        for(Offering searchOffer : this.offerings){
            if(searchOffer.getCode().equals(code)){
                return searchOffer.getName();
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


    // BolBolSystem Methods
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
        // Set onlineStudent
        if(!setOnlineStudent(studentId)){
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
        // Set onlineStudent
        if(!setOnlineStudent(studentId)){
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
        // This one is for adding to student courses
        if(!setOnlineStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code , classCode)){
            return false;
        }

        return findStudentFromId(studentId).addToWeeklyScheduleOfStudent(findOfferFromIdAndCode(code , classCode));
    }

    public String addToStudentSelectedNowOffers(String studentId , String code , String classCode){
        // This one is for adding to selected now offers
        if(!setOnlineStudent(studentId)){
            return "No one has logged in!";
        }
        if(!checkOfferExistenceDB(code , classCode)){
            return "Offer doesn't exist!";
        }

        try{
            return BolBolRepo.getInstance().findStudentFromIdDB(studentId).addToSelectedNowOffersDB(
                                            BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(code , classCode)
                                        );
        }catch (SQLException e){
            System.out.println("Error in addToStudentSelectedNowOffers: " + e.getMessage());
            return "FAILED";
        }
    }

    public boolean removeFromWeeklySchedule(String studentId , String code , String classCode){
        if(!setOnlineStudent(studentId)){
            return false;
        }
        if(!checkOfferExistence(code , classCode)){
            return false;
        }

        return findStudentFromId(studentId).removeFromWeeklyScheduleOfStudent(code , classCode);
    }

    public boolean removeFromStudentSelectedNowOffers(String studentId , String code , String classCode){
        if(!setOnlineStudent(studentId)){
            return false;
        }
        if(!checkOfferExistenceDB(code , classCode)){
            return false;
        }

        try{
            return BolBolRepo.getInstance().findStudentFromIdDB(studentId).removeFromSelectedNowOffersDB(code , classCode);
        }catch (SQLException e){
            System.out.println("Error in addToStudentSelectedNowOffers: " + e.getMessage());
            return false;
        }
    }

    public boolean getWeeklySchedule(String studentId){
        if(!setOnlineStudent(studentId)){
            return false;
        }
        return this.onlineStudent.getWeeklyScheduleOfStudent();
    }

    public String finalizeSchedule(String studentId){
        if(!setOnlineStudent(studentId)){
            return "No one has logged in!";
        }

        return findStudentFromId(studentId).finalizeScheduleOfStudent();
    }

    public String submitStudentSelectedNowOffers(String studentId){
        if(!setOnlineStudent(studentId)){
            return "No one has logged in!";
        }

        try{
            return BolBolRepo.getInstance().findStudentFromIdDB(studentId).submitSelectedNowOffers();
        }catch (SQLException e){
            System.out.println("Error in submitStudentSelectedNowOffers: " + e.getMessage());
            return "FAILED";
        }

//        return findStudentFromId(studentId).submitSelectedNowOffers();
    }

    public boolean executeUnknownCommand(){
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , false);
        resultMessage.put("error" , "UnknownCommand");
        System.out.println(resultMessage);
        return false;
    }


    // Setters
    private boolean setBolBolCoursesExternalServer(String courseAddress){
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

            this.addOffer(offering); // This will be omitted

            try{
                BolBolRepo.getInstance().insertOfferDB(offering);
            }catch (SQLException e){
                System.out.println("Error In adding Offering: " + e.getMessage());
            }

            try{
                BolBolRepo.getInstance().insertOfferDaysDB((String) jsonObject.get("code") , (String) jsonObject.get("classCode") ,
                                                            JSONArrayToArrayList((JSONArray) classTime.get("days")));
            }catch (SQLException e){
                System.out.println("Error In adding days: " + e.getMessage());
            }

            try{
                BolBolRepo.getInstance().insertOfferPrerequisitesDB((String) jsonObject.get("code") ,
                                                                    JSONArrayToArrayList((JSONArray) jsonObject.get("prerequisites")));
            }catch (SQLException e){
                System.out.println("Error In adding Prerequisites: " + e.getMessage());
            }

        }
        System.out.println("Courses were imported successfully!");
        return true;
    }

    private boolean setBolBolStudentsExternalServer(String studentsAddress){
        JSONArray jsonArrayStudents = importHandler.importFromAddress(studentsAddress);
        JSONObject jsonObject;
        Student student;
        for(Object object : jsonArrayStudents){
            jsonObject = (JSONObject) object;

            student = new Student((String) jsonObject.get("id") , (String) jsonObject.get("name") ,
                    (String) jsonObject.get("secondName"), (String) jsonObject.get("birthDate") ,
                    (String) jsonObject.get("field") , (String) jsonObject.get("faculty") ,
                    (String) jsonObject.get("level") , (String) jsonObject.get("status") ,
                    (String) jsonObject.get("img"));

            this.addStudent(student); // This will be omitted

            try{
                BolBolRepo.getInstance().insertStudentDB(student);
                System.out.println("Student " + student.getStudentId() + " was inserted into students DB!");
            }catch(SQLException e){
                System.out.println("Student " + student.getStudentId() + " already exists in student DB so no need to insert!");
            }

        }
        System.out.println("Students were imported successfully!");
        return true;
    }

    private boolean setStudentsGradesExternalServer(String gradesAddressBASE){
        JSONArray jsonArrayGrades;
        JSONObject jsonObject;
        PassedCourse passedCourse;

        // This whole for will be omitted
        for(Student student : this.students){
            jsonArrayGrades = importHandler.importFromAddress(gradesAddressBASE + student.getStudentId());
            for(Object object : jsonArrayGrades){
                jsonObject = (JSONObject) object;

                passedCourse = new PassedCourse((String) jsonObject.get("code") , (long) jsonObject.get("grade") ,
                        this.findCourseUnitsFromCode((String) jsonObject.get("code")) , (long) jsonObject.get("term"));
                student.addToPassedCourses(passedCourse);
            }
        }

        ArrayList<String> studentIDs;

        try{
            studentIDs = BolBolRepo.getInstance().getAllStudentIDsDB();
            for(String studentId : studentIDs){
                jsonArrayGrades = importHandler.importFromAddress(gradesAddressBASE + studentId);
                for(Object object : jsonArrayGrades){
                    jsonObject = (JSONObject) object;

                    passedCourse = new PassedCourse((String) jsonObject.get("code") , (long) jsonObject.get("grade") ,
                            this.findCourseUnitsFromCode((String) jsonObject.get("code")) , (long) jsonObject.get("term"));

                    try {
                        BolBolRepo.getInstance().insertPassedCourseDB(passedCourse , studentId);
                    }catch (SQLException err){
                        System.out.println("Error in inserting passedCourse: " + err.getMessage());
                    }
                }
            }
        }catch(SQLException e){
            System.out.println("Error in getting all studentIDs: " + e.getMessage());
        }

        System.out.println("Passed courses for each student were imported successfully!");
        return true;
    }

    public boolean setAllExternalServerInfo(String coursesAddr , String studentsAddr , String gradesBaseAddr){
        this.setBolBolCoursesExternalServer(coursesAddr);
        this.setBolBolStudentsExternalServer(studentsAddr);
        this.setStudentsGradesExternalServer(gradesBaseAddr);
        return true;
    }

    public boolean setSearchOffers(String searchName){
        if(this.isCategorized){
            return true;
        }

        this.searchName = searchName;

        this.searchOffers.clear();
        for(Offering offering : this.offerings){
            if(offering.getName().toLowerCase().contains(searchName.toLowerCase())){
                this.searchOffers.add(offering);
            }
        }
        return true;
    }

    public boolean setSearchOffersDB(String searchName){
        if(this.isCategorized){
            return true;
        }

        this.searchName = searchName;

        this.searchOffers.clear();
        try{
            for(Offering offering : BolBolRepo.getInstance().getSearchedOffersDB(searchName)){
                this.searchOffers.add(offering);
            }
            return true;
        }catch (SQLException e){
            System.out.println("Error in setSearchOffersDB: " + e.getMessage());
            return false;
        }
    }

    public boolean setSearchOffersByType(String searchType){
        this.searchOffers.clear();

        if (searchType.equals("all")){
            for(Offering offering : this.offerings){
                this.searchOffers.add(offering);
            }
        }
        else{
            for(Offering offering : this.offerings){
                if(offering.getType().equals(searchType)){
                    this.searchOffers.add(offering);
                }
            }
        }
        return true;
    }

    public boolean setSearchOffersByTypeDB(String searchType){
        this.searchOffers.clear();

        if (searchType.equals("all")){
            try{
                for(Offering offering : BolBolRepo.getInstance().getAllOffersDB()){
                    this.searchOffers.add(offering);
                }
            }catch (SQLException e){
                System.out.println("Error in setSearchOffersByType all: " + e.getMessage());
                return false;
            }
        }
        else{
            try {
                for(Offering offering : BolBolRepo.getInstance().getAllOffersByTypeDB(searchType)){
                    this.searchOffers.add(offering);
                }
            }catch (SQLException e){
                System.out.println("Error in setSearchOffersByType " + searchType + ": " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public void setCategorized(boolean categorized) {
        isCategorized = categorized;
    }

    // Getters
    public ArrayList<Offering> getSearchOffers() {
        return searchOffers;
    }

    public String getSearchName() {
        return searchName;
    }

    public Student getOnlineStudent() {
        return this.onlineStudent;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Offering> getOfferings() {
        return offerings;
    }
}
