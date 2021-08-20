package BolBol.CA5_Server.Domain.BolBolCodes;

import BolBol.CA5_Server.Repository.BolBolRepo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.*;

public class Student {
    // Fields
    private String studentId;
    private String name;
    private String secondName;
    private String birthDate;
    private String field;
    private String faculty;
    private String level;
    private String status;
    private String imgAddress;

    // Courses
    private HashMap<Offering , OfferStatus> studentCourses = new HashMap<>();
    private HashMap<Offering , OfferStatus> selectedNowOffers = new HashMap<>();
    private ArrayList<PassedCourse> studentPassedCourses = new ArrayList<>();

    // Offer Status Enum
    public enum OfferStatus{
        FINALIZED,
        NOT_FINALIZED,
        WAITING
    }

    // Constructor
    public Student(String studentId , String name , String secondName , String birthDate , String field , String faculty ,
                   String level , String status , String imgAddress){
        this.studentId = studentId;
        this.name = name;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.field = field;
        this.faculty = faculty;
        this.level = level;
        this.status = status;
        this.imgAddress = imgAddress;
    }

    public Student(){
        this.studentId = "";
    }


    // Finders

    // ADJUSTMENT!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // Since there's a new parameter term we have to check for all passed courses with a code
    private ArrayList<PassedCourse> findAllPassedCourseFromCode(String code){
        ArrayList<PassedCourse> allTargetPassedCourses = new ArrayList<PassedCourse>();
        for(PassedCourse passedCourse : this.studentPassedCourses){
            if(passedCourse.getCode().equals(code)){
                allTargetPassedCourses.add(passedCourse);
            }
        }
        return allTargetPassedCourses;
    }

    private PassedCourse findPassedCourseFromCodeAndTerm(String code , long term){
        for(PassedCourse passedCourse : this.studentPassedCourses){
            if(passedCourse.getCode().equals(code) && passedCourse.getTerm() == term){
                return passedCourse;
            }
        }
        return null;
    }

    // Checkers
    private boolean checkHasPassedPrerequisitesSingleOffer(ArrayList<String> prerequisites){
        for(String preCode : prerequisites){
            try{
                BolBolRepo.getInstance().getStudentPassedCoursesNotFailedFromCodeDB(this.studentId , preCode);
            }catch (SQLException e){
                System.out.println("Concern in checkHasPassedPrerequisitesSingleOffer: " + e.getMessage());
                // There was no foundPreCourses
                return false;
            }
        }
        return true;
    }

    private boolean checkSingleCourseTimeWithAllSelectedNowOffers(Offering offer){
        for(Offering otherOffer : this.selectedNowOffers.keySet()){
            if(!checkTwoCourseDays(offer , otherOffer)){
                return false;
            }
        }
        return true;
    }

    private boolean checkSingleCourseTimeWithAllSelectedNowOffersDB(Offering offer){
        String[] codeAndClassCode;
        try {
            for(String offerCodeAndClassCode : BolBolRepo.getInstance().getStudentSelectedNowOffersDB(this.studentId)){
                codeAndClassCode = offerCodeAndClassCode.split("-"); // code , classCode
                if(!checkTwoCourseDays(offer , BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(codeAndClassCode[0] , codeAndClassCode[1])) ){
                    return false;
                }
            }
            return true;
        }catch (SQLException e){
            System.out.println("Error in checkSingleCourseTimeWithAllSelectedNowOffersDB: " + e.getMessage());
            return false;
        }
    }

    private boolean checkSingleExamTimeWithAllSelectedNowOffers(Offering offer){
        for(Offering otherOffer : this.selectedNowOffers.keySet()){
            if (!checkTwoCourseExam(offer, otherOffer)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSingleExamTimeWithAllSelectedNowOffersDB(Offering offer){
        String[] codeAndClassCode;
        try {
            for(String offerCodeAndClassCode : BolBolRepo.getInstance().getStudentSelectedNowOffersDB(this.studentId)){
                codeAndClassCode = offerCodeAndClassCode.split("-"); // code , classCode
                if(!checkTwoCourseExam(offer , BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(codeAndClassCode[0] , codeAndClassCode[1])) ){
                    return false;
                }
            }
            return true;
        }catch (SQLException e){
            System.out.println("Error in checkSingleExamTimeWithAllSelectedNowOffersDB: " + e.getMessage());
            return false;
        }
    }

    private boolean checkSelectedNowOffersCapacity(){
        for(Offering offer : this.selectedNowOffers.keySet()){
            if(offer.getCapacity() <= 0 && this.selectedNowOffers.get(offer) == OfferStatus.NOT_FINALIZED){
                return false;
            }
        }
        return true;
    }

    private boolean checkSelectedNowOffersCapacityDB(){
        try {
            return BolBolRepo.getInstance().checkSelectedNowOffersCapacityDB(this.studentId);
        }catch (SQLException e){
            System.out.println("Error in checkSelectedNowOffersCapacityDB: " + e.getMessage());
            return false;
        }
    }

    private String checkNumberUnitsSelectedNow(){
        long totalNumUnits = calculateTotalUnitsSelectedNowOffersDB();

        if(totalNumUnits >= 12 && totalNumUnits <= 20){
            return "";
        }
        else if(totalNumUnits < 12){
            return "You have chosen less than 12 units!";
        }
        return "You have chosen more than 20 units!";
    }

    public boolean checkHasPassedPrerequisitesAllSelectedNowOffers(){
        for(Offering offering : this.selectedNowOffers.keySet()){
            if (!checkHasPassedPrerequisitesSingleOffer(offering.getPrerequisites())) {
                return false;
            }
        }
        return true;
    }

    public boolean checkHasPassedPrerequisitesAllSelectedNowOffersDB(){
        ArrayList<String> studentSelectedNow;
        String[] codeAndClassCode; Offering offering;
        try {
            studentSelectedNow = BolBolRepo.getInstance().getStudentSelectedNowOffersDB(this.studentId);
            for(String offerCodeAndClassCode : studentSelectedNow){
                codeAndClassCode = offerCodeAndClassCode.split("-");
                offering = BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(codeAndClassCode[0] , codeAndClassCode[1]);
                if (!checkHasPassedPrerequisitesSingleOffer(offering.getPrerequisites())) {
                    return false;
                }
            }
            return true;
        }catch (SQLException e){
            System.out.println("Error in checkHasPassedPrerequisitesAllSelectedNowOffersDB: " + e.getMessage());
            return false;
        }
    }

    public boolean checkHasPassedBeforeAllSelectedNowOffers(){
        for(Offering offering : this.selectedNowOffers.keySet()){
            if (!checkHasPassedBeforeSingleOffer(offering)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkHasPassedBeforeAllSelectedNowOffersDB(){
        ArrayList<String> studentSelectedNow;
        String[] codeAndClassCode; Offering offering;
        try {
            studentSelectedNow = BolBolRepo.getInstance().getStudentSelectedNowOffersDB(this.studentId);
            for(String offerCodeAndClassCode : studentSelectedNow){
                codeAndClassCode = offerCodeAndClassCode.split("-");
                offering = BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(codeAndClassCode[0] , codeAndClassCode[1]);
                if (!checkHasPassedBeforeSingleOffer(offering)) {
                    return false;
                }
            }
            return true;
        }catch (SQLException e){
            System.out.println("Error in checkHasPassedPrerequisitesAllSelectedNowOffersDB: " + e.getMessage());
            return false;
        }
    }

    private String checkNumberTakenUnits(){
        JSONObject resultMessage;
        long totalNumUnits = calculateTotalUnitsOffers();

        if(totalNumUnits >= 12 && totalNumUnits <= 20){
            return "";
        }
        else if(totalNumUnits < 12){
//            resultMessage = new JSONObject();
//            resultMessage.put("success" , false);
//            resultMessage.put("error" , "MinimumUnitsError");
//            System.out.println(resultMessage);
            return "You have chosen less than 12 units!";
        }
        return "You have chosen more than 20 units!";
    }

    private boolean checkStudentCourseCapacity(){
        for(Offering offer : this.studentCourses.keySet()){
            if(offer.getCapacity() <= 0){
//                JSONObject resultMessage = new JSONObject();
//                resultMessage.put("success" , false);
//                resultMessage.put("error" , "CapacityError " + offer.getCode());
//                System.out.println(resultMessage);
                return false;
            }
        }
        return true;
    }

    private boolean checkTwoCourseTime(Offering offer_1 , Offering offer_2){
        String[] time_offer_1 = offer_1.getTime().split("-" , 2);
        String[] time_offer_2 = offer_2.getTime().split("-" , 2);

        int startOffer1 , endOffer1 , startOffer2 , endOffer2;
        startOffer1 = calculateTimeValue(time_offer_1[0]); endOffer1 = calculateTimeValue(time_offer_1[1]);
        startOffer2 = calculateTimeValue(time_offer_2[0]); endOffer2 = calculateTimeValue(time_offer_2[1]);

        if(startOffer1 < endOffer2 && startOffer2 < endOffer1){
//            JSONObject resultMessage = new JSONObject();
//            resultMessage.put("success" , false);
//            resultMessage.put("error" , "ClassTimeCollisionError " + offer_1.getCode() + " " + offer_2.getCode());
//            System.out.println(resultMessage);
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
        for(Offering offer_i : this.studentCourses.keySet()){
            for(Offering offer_j : this.studentCourses.keySet()){
                if(offer_i == offer_j){
                    continue;
                }
                if(!checkTwoCourseDays(offer_i , offer_j)){
                    return false;
                }
            }
        }
        return true;
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
//                JSONObject resultMessage = new JSONObject();
//                resultMessage.put("success" , false);
//                resultMessage.put("error" , "ExamTimeCollisionError " + offer1.getCode() + " " + offer2.getCode());
//                System.out.println(resultMessage);
                return false;
            }
        }
        return true;
    }

    private boolean checkExamTime(){
        for(Offering offer_i : this.studentCourses.keySet()){
            for(Offering offer_j : this.studentCourses.keySet()){
                if(offer_i == offer_j){
                    continue;
                }
                if(!checkTwoCourseExam(offer_i , offer_j)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkHasPassedPrerequisitesAllCourses(){
        for(Offering offering : this.studentCourses.keySet()){
            if (!checkHasPassedPrerequisitesSingleOffer(offering.getPrerequisites())) {
                return false;
            }
        }
        return true;
    }

    private boolean checkHasPassedBeforeSingleOffer(Offering offer){
        try{
            BolBolRepo.getInstance().getStudentPassedCoursesNotFailedFromCodeDB(this.studentId , offer.getCode());
        }catch (SQLException e){
            System.out.println("Concern in checkHasPassedPrerequisitesSingleOffer: " + e.getMessage());
            // There was no foundPreCourses
            return false;
        }
        return true;
    }

    public boolean checkHasPassedBeforeAllOffers(){
        for(Offering offering : this.studentCourses.keySet()){
            if (!checkHasPassedBeforeSingleOffer(offering)) {
                return false;
            }
        }
        return true;
    }


    // Calculators
    public int calculateTotalUnitsSelectedNowOffers(){
        int numTotalUnits = 0;
        for(Offering offer : this.selectedNowOffers.keySet()){
            numTotalUnits += offer.getUnits();
        }
        return numTotalUnits;
    }

    public int calculateTotalUnitsSelectedNowOffersDB(){
        try{
            ArrayList<String> selectedNowOffers = BolBolRepo.getInstance().getStudentSelectedNowOffersDB(this.studentId);
            String[] codeAndClassCode;
            int numTotalUnits = 0;
            for(String offerCodeAndClassCode : selectedNowOffers){
                codeAndClassCode = offerCodeAndClassCode.split("-");
                numTotalUnits += BolBolRepo.getInstance().getOfferUnitsFromCodeDB(codeAndClassCode[0]);
            }
            return numTotalUnits;
        }catch (SQLException e){
            System.out.println("Error in calculateTotalUnitsSelectedNowOffersDB: " + e.getMessage());
            return -1;
        }
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

    private int calculateExamDateValue(String date){
        // Each month is 30 days!
        String[] dateDetails = date.split("-" , 3);
        return (Integer.parseInt(dateDetails[0]) * 365) + (Integer.parseInt(dateDetails[1]) * 30) + (Integer.parseInt(dateDetails[2]));
    }

    private int calculateExamTimeValue(String time){
        String[] timeDetails = time.split(":" , 3);
        return (Integer.parseInt(timeDetails[0]) * 3600) + (Integer.parseInt(timeDetails[1]) * 60) + (Integer.parseInt(timeDetails[2]));

    }

    public double calculateGPA(){
        double gpaSum = 0;
        int numTotalUnits = 0;
        for(PassedCourse passedCourse : this.studentPassedCourses){
            gpaSum += passedCourse.getGrade() * passedCourse.getUnits();
            numTotalUnits += passedCourse.getUnits();
        }
        return gpaSum / numTotalUnits;
    }

    public double calculateGPA_DB(){
        double gpaSum = 0;
        int numTotalUnits = 0;

        ArrayList<PassedCourse> passedCourses;
        try {
            passedCourses = BolBolRepo.getInstance().getStudentPassedCoursesDB(this.studentId);
        }catch (SQLException e){
            System.out.println("Error in calculateGPA_DB: " + e.getMessage());
            return -1;
        }
        for(PassedCourse passedCourse : passedCourses){
            gpaSum += passedCourse.getGrade() * passedCourse.getUnits();
            numTotalUnits += passedCourse.getUnits();
        }
        return gpaSum / numTotalUnits;
    }

    public double calculateSemesterGPA(List<PassedCourse> semesterPassedCourses){
        double gpaSum = 0;
        int numTotalUnits = 0;
        for(PassedCourse passedCourse : semesterPassedCourses){
            gpaSum += passedCourse.getGrade() * passedCourse.getUnits();
            numTotalUnits += passedCourse.getUnits();
        }
        return gpaSum / numTotalUnits;
    }

    public int calculateTotalNumPassedUnits(){
        int numTotalUnits = 0;
        for(PassedCourse passedCourse : this.studentPassedCourses){
            numTotalUnits += passedCourse.getUnits();
        }
        return numTotalUnits;
    }

    public int calculateTotalNumPassedUnitsDB(){
        int numTotalUnits = 0;

        ArrayList<PassedCourse> passedCourses;
        try {
            passedCourses = BolBolRepo.getInstance().getStudentPassedCoursesDB(this.studentId);
        }catch (SQLException e){
            System.out.println("Error in calculateTotalNumPassedUnitsDB: " + e.getMessage());
            return -1;
        }

        for(PassedCourse passedCourse : passedCourses){
            numTotalUnits += passedCourse.getUnits();
        }
        return numTotalUnits;
    }

    public int calculateTotalUnitsOffers(){
        int numTotalUnits = 0;
        for(Offering offer : this.studentCourses.keySet()){
            numTotalUnits += offer.getUnits();
        }
        return numTotalUnits;
    }

    private int calculateTotalUnitsFinalized(){
        int totalUnits = 0;
        for(Offering offer : this.studentCourses.keySet()){
            if(this.studentCourses.get(offer) == OfferStatus.FINALIZED){
                totalUnits += offer.getUnits();
            }
        }
        return totalUnits;
    }


    // Tools
    private HashMap<Offering , Boolean> createVisitedOfferingsHashMap(){
        HashMap<Offering , Boolean> visitedOfferings = new HashMap<>();
        for(Offering offering : this.studentCourses.keySet()){
            visitedOfferings.put(offering , false);
        }
        return visitedOfferings;
    }

    private void handleSubmitOffersCapacityAndNumSignedUp(){
        HashMap<Offering , Boolean> visitedOfferings = createVisitedOfferingsHashMap();

        // Handling new offers and existing ones respectively
        Iterator<HashMap.Entry<Offering , OfferStatus>> selectedNowOffersIter = this.selectedNowOffers.entrySet().iterator();
        while(selectedNowOffersIter.hasNext()){
            Map.Entry<Offering , OfferStatus> selectedNowOffersTuple = selectedNowOffersIter.next();
            if(!this.studentCourses.containsKey(selectedNowOffersTuple.getKey())){
                selectedNowOffersTuple.getKey().decrementCapacity();
            }
            else{
                visitedOfferings.put(selectedNowOffersTuple.getKey() , true);
            }
        }

        // Handling removed offers
        Iterator<HashMap.Entry<Offering , OfferStatus>> studentCoursesIter = this.studentCourses.entrySet().iterator();
        while(studentCoursesIter.hasNext()){
            Map.Entry<Offering , OfferStatus> studentCoursesTuple = studentCoursesIter.next();
            if(!visitedOfferings.get(studentCoursesTuple.getKey())){
                studentCoursesTuple.getKey().incrementCapacity();
                studentCoursesTuple.getKey().decrementNumSignedUp();
            }
        }
    }

    private HashMap<String , Boolean> createVisitedOfferingsHashMapDB(){
        // Key: code-classCode
        HashMap<String , Boolean> visitedOfferings = new HashMap<>();
        try{
            for(Offering offering : BolBolRepo.getInstance().getStudentCoursesDB(this.studentId).keySet()){
                visitedOfferings.put(offering.getCode() + "-" + offering.getClassCode() , false);
            }
            return visitedOfferings;
        }catch (SQLException e){
            System.out.println("Error in createVisitedOfferingsHashMapDB: " + e.getMessage());
            return null;
        }
    }

    private void handleSubmitOffersCapacityAndNumSignedUpDB(){
        HashMap<String , Boolean> visitedOfferings = createVisitedOfferingsHashMapDB();
        ArrayList<String> takenCourseInfo;

        // Handling new offers and existing ones respectively
        try {
            Iterator<HashMap.Entry<Offering , OfferStatus>> selectedNowOffersIter = BolBolRepo.getInstance().getStudentSelectedNowHashMapFormatDB(this.studentId).entrySet().iterator();
            while(selectedNowOffersIter.hasNext()){
                Map.Entry<Offering , OfferStatus> selectedNowOffersTuple = selectedNowOffersIter.next();
                try {
                    takenCourseInfo = BolBolRepo.getInstance().findTakenCourseFromStudentIdCodeClassCodeDB(this.studentId,
                                                                                            selectedNowOffersTuple.getKey().getCode(),
                                                                                            selectedNowOffersTuple.getKey().getClassCode());
                    visitedOfferings.put(takenCourseInfo.get(1) + "-" + takenCourseInfo.get(2) , true);
                }catch (SQLException e){
                    try {
                        BolBolRepo.getInstance().decrementCourseCapacityDB(selectedNowOffersTuple.getKey().getCode() , selectedNowOffersTuple.getKey().getClassCode());
                    }catch (SQLException err){
                        System.out.println("Error in decrementCourseCapacityDB: " + e.getMessage());
                        return;
                    }
                }
            }
        }catch (SQLException e){
            System.out.println("Error in Handling new offers and existing ones respectively: " + e.getMessage());
        }

        // Handling removed offers
        try{
            Iterator<HashMap.Entry<Offering , OfferStatus>> studentCoursesIter = BolBolRepo.getInstance().getStudentCoursesDB(this.studentId).entrySet().iterator();
            while(studentCoursesIter.hasNext()){
                Map.Entry<Offering , OfferStatus> studentCoursesTuple = studentCoursesIter.next();
                if(!visitedOfferings.get(studentCoursesTuple.getKey().getCode() + "-" + studentCoursesTuple.getKey().getClassCode())){
                    try{
                        BolBolRepo.getInstance().incrementCourseCapacityDB(studentCoursesTuple.getKey().getCode() , studentCoursesTuple.getKey().getClassCode());
                    }catch (SQLException err){
                        System.out.println("Error in handleSubmitOffersCapacityAndNumSignedUpDB incrementCapacity : " + err.getMessage());
                    }

                    try{
                        BolBolRepo.getInstance().decrementNumSignedUpDB(studentCoursesTuple.getKey().getCode() , studentCoursesTuple.getKey().getClassCode());
                    }catch (SQLException err){
                        System.out.println("Error in  handleSubmitOffersCapacityAndNumSignedUpDB decrementNumSignedUp : " + err.getMessage());
                    }
                }
            }
        }catch (SQLException e){
            System.out.println("Concern in Handling removed offers: " + e.getMessage());
        }
    }

    // Student Methods
    public boolean addToWeeklyScheduleOfStudent(Offering offer){
//        if(offer.getCapacity() <= 0){
////            JSONObject resultMessage = new JSONObject();
////            resultMessage.put("success" , false);
////            resultMessage.put("error" , "CapacityError " + offer.getCode());
////            System.out.println(resultMessage);
//            return false;
//        }

        for(Offering offering : this.studentCourses.keySet()){
            if(offering.getCode().equals(offer.getCode())){
//                JSONObject resultMessage = new JSONObject();
//                resultMessage.put("success" , false);
//                resultMessage.put("error" , "Offer already exists in schedule!");
//                System.out.println(resultMessage);
                return false;
            }
        }

//        if(!checkHasPassedPrerequisitesSingleOffer(offer.getPrerequisites())){
//            return false;
//        }
        /*
        if(!checkSingleCourseTimeWithOthers(offer)){
            return false;
        }
        if(!checkSingleExamTimeWithOthers(offer)){
            return false;
        }

         */

        this.studentCourses.put(offer , OfferStatus.NOT_FINALIZED);

//        // Success Message
//        JSONObject resultMessage = new JSONObject();
//        resultMessage.put("success" , true);
//        resultMessage.put("data" , "Offer was added to student's weekly schedule successfully!");
//        System.out.println(resultMessage);
        return true;
    }

    public String addToSelectedNowOffers(Offering selectedOffer){
        for(Offering offering : this.selectedNowOffers.keySet()){
            if(offering.getCode().equals(selectedOffer.getCode())){
                return "Offering already exists!";
            }
        }

        if(!checkSingleCourseTimeWithAllSelectedNowOffers(selectedOffer)){
            return "Offering has class time collision!";
        }
        if(!checkSingleExamTimeWithAllSelectedNowOffers(selectedOffer)){
            return "Offering has exam time collision!";
        }

        this.selectedNowOffers.put(selectedOffer , OfferStatus.NOT_FINALIZED);

        return "";
    }

    public String addToSelectedNowOffersDB(Offering selectedOffer){
        try{
            BolBolRepo.getInstance().findSelectedNowOfferFromStudentIdCodeClassCodeDB(this.studentId , selectedOffer.getCode() , selectedOffer.getClassCode());
            return "Offering already exists!";
        }catch (SQLException e){
            System.out.println("Offer not already existing so no problem for adding to student's selected now offers");
        }

        if(!checkSingleCourseTimeWithAllSelectedNowOffersDB(selectedOffer)){
            return "Offering has class time collision!";
        }
        if(!checkSingleExamTimeWithAllSelectedNowOffersDB(selectedOffer)){
            return "Offering has exam time collision!";
        }

        try{
            BolBolRepo.getInstance().insertOfferToSelectedNowDB(this.studentId , selectedOffer.getCode() , selectedOffer.getClassCode() , OfferStatus.NOT_FINALIZED);

            return "";
        }catch (SQLException e){
            System.out.println("Error in addToSelectedNowOffersDB: " + e.getMessage());
            return "FAILED";
        }
    }

    public boolean addToPassedCourses(PassedCourse passedCourse){
        if(!Objects.isNull(findPassedCourseFromCodeAndTerm(passedCourse.getCode() , passedCourse.getTerm()))){
            JSONObject resultMessage = new JSONObject();
            resultMessage.put("success" , false);
            resultMessage.put("error" , "Passed course already exists!");
            System.out.println(resultMessage);
            return false;
        }

        this.studentPassedCourses.add(passedCourse);

//        JSONObject resultMessage = new JSONObject();
//        resultMessage.put("success" , true);
//        resultMessage.put("data" , "Passed Course was added successfully!");
//        System.out.println(resultMessage);
        return true;
    }

    public boolean removeFromWeeklyScheduleOfStudent(String code , String classCode){
        JSONObject resultMessage;
        for(Offering offering : this.studentCourses.keySet()){
            if(offering.getCode().equals(code) && offering.getClassCode().equals(classCode)){
//                if(this.studentCourses.get(offering) == OfferStatus.FINALIZED){
//                    if(calculateTotalUnitsFinalized() - offering.getUnits() < 12){
////                        resultMessage = new JSONObject();
////                        resultMessage.put("success" , false);
////                        resultMessage.put("error" , "MinimumUnitsError");
////                        System.out.println(resultMessage);
//                        return false;
//                    }
//                    offering.incrementCapacity();
//                }
                this.studentCourses.remove(offering);

                // Success Message
//                resultMessage = new JSONObject();
//                resultMessage.put("success" , true);
//                resultMessage.put("data" , "Offer was removed from student's weekly schedule successfully!");
//                System.out.println(resultMessage);
                return true;
            }
        }

//        resultMessage = new JSONObject();
//        resultMessage.put("success" , false);
//        resultMessage.put("error" , "OfferingNotFound");
//        System.out.println(resultMessage);
        return false;
    }

    public boolean removeFromSelectedNowOffers(String code , String classCode){
        Iterator<HashMap.Entry<Offering , OfferStatus>> selectedNowOfferIter = this.selectedNowOffers.entrySet().iterator();
        while(selectedNowOfferIter.hasNext()){
            Map.Entry<Offering , OfferStatus> selectedNowOfferTuple = selectedNowOfferIter.next();
            if(selectedNowOfferTuple.getKey().getCode().equals(code) && selectedNowOfferTuple.getKey().getClassCode().equals(classCode)){
                selectedNowOfferIter.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeFromSelectedNowOffersDB(String code , String classCode){
        try {
            BolBolRepo.getInstance().removeOfferFromSelectedNowDB(this.studentId , code , classCode);
            return true;
        }catch (SQLException e){
            System.out.println("Error in removeFromSelectedNowOffersDB: " + e.getMessage());
            return false;
        }
    }


    public boolean getWeeklyScheduleOfStudent(){
        JSONObject resultMessage = new JSONObject();
        resultMessage.put("success" , true);

        JSONObject weeklyScheduleJSON = new JSONObject();

        JSONObject courseJSON , classTime , examTime;
        JSONArray arrCoursesJSON = new JSONArray();

        for(Offering offering : this.studentCourses.keySet()){
            classTime = new JSONObject();
            classTime.put("days" , offering.getDays());
            classTime.put("time" , offering.getTime());

            examTime = new JSONObject();
            examTime.put("start" , offering.getExamTimeStart());
            examTime.put("end" , offering.getExamTimeEnd());

            courseJSON = new JSONObject();
            courseJSON.put("code" , offering.getCode());
            courseJSON.put("name" , offering.getName());
            courseJSON.put("classTime" , classTime);
            courseJSON.put("examTime" , examTime);
            if(this.studentCourses.get(offering) == OfferStatus.FINALIZED){
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

    public String finalizeScheduleOfStudent(){
        String checkNumberTakenUnitsString = checkNumberTakenUnits();
        if(!checkNumberTakenUnitsString.equals("")){
            return checkNumberTakenUnitsString;
        }
        if(!checkStudentCourseCapacity()){
            return "Course has no capacity!";
        }
//        if(!checkCourseTime()){
//            return false;
//        }
//        if(!checkExamTime()){
//            return false;
//        }
        if(!checkHasPassedPrerequisitesAllCourses()){
            return "Hasn't passed prerequisites!";
        }

        if(!checkHasPassedBeforeAllOffers()){
            return "Had already passed course before!";
        }

//        for(Offering offering : this.studentCourses.keySet()){
//            offering.decrementCapacity();
//            this.studentCourses.put(offering , OfferStatus.FINALIZED);
//        }


//        JSONObject resultMessage = new JSONObject();
//        resultMessage.put("success" , true);
//        resultMessage.put("data" , "Offers were finalized!");
//        System.out.println(resultMessage);
        return "";
    }

    public String submitSelectedNowOffers(){
        String checkNumberTakenUnitsString = checkNumberUnitsSelectedNow();
        if(!checkNumberTakenUnitsString.equals("")){
            return checkNumberTakenUnitsString;
        }
        if(!checkSelectedNowOffersCapacityDB()){
            return "Course has no capacity!";
        }
        if(!checkHasPassedPrerequisitesAllSelectedNowOffersDB()){
            return "Hasn't passed prerequisites!";
        }

        if(!checkHasPassedBeforeAllSelectedNowOffersDB()){
            return "Had already passed course before!";
        }

//        handleSubmitOffersCapacityAndNumSignedUp();
        handleSubmitOffersCapacityAndNumSignedUpDB();

//        this.studentCourses.clear();
        try {
            BolBolRepo.getInstance().clearStudentCoursesDB(this.studentId);

            Iterator<HashMap.Entry<Offering , OfferStatus>> selectedNowOffersIter = BolBolRepo.getInstance().getStudentSelectedNowHashMapFormatDB(this.studentId).entrySet().iterator();
            while(selectedNowOffersIter.hasNext()){
                Map.Entry<Offering , OfferStatus> selectedNowOffersTuple = selectedNowOffersIter.next();
                if(selectedNowOffersTuple.getValue() == OfferStatus.NOT_FINALIZED){
//                    this.studentCourses.put(selectedNowOffersTuple.getKey() , OfferStatus.WAITING);
                    try{
                        BolBolRepo.getInstance().insertTakenCourseDB(this.studentId , selectedNowOffersTuple.getKey().getCode() ,
                                                                     selectedNowOffersTuple.getKey().getClassCode() , OfferStatus.WAITING);
                    }catch (SQLException e){
                        System.out.println("Error in submitSelectedNowOffers insertTakenCourseDB NOT_FINALIZED: " + e.getMessage());
                    }
                }else{
//                    this.studentCourses.put(selectedNowOffersTuple.getKey() , selectedNowOffersTuple.getValue());
                    try {
                        BolBolRepo.getInstance().insertTakenCourseDB(this.studentId , selectedNowOffersTuple.getKey().getCode() ,
                                                               selectedNowOffersTuple.getKey().getClassCode() , selectedNowOffersTuple.getValue());
                    }catch (SQLException e){
                        System.out.println("Error in submitSelectedNowOffers insertTakenCourseDB ELSE: " + e.getMessage());
                    }
                }
//                selectedNowOffersTuple.getKey().addStudentToOfferWaitingList(this.studentId);
                try {
                    BolBolRepo.getInstance().insertStudentAndOfferToWaitingListDB(selectedNowOffersTuple.getKey().getCode(),
                                                                                  selectedNowOffersTuple.getKey().getClassCode(),
                                                                                  this.studentId);
                }catch (SQLException e){
                    System.out.println("Error in submitSelectedNowOffers insertStudentAndOfferToWaitingListDB: " + e.getMessage());
                }
            }
            return "";
        }catch (SQLException e){
            System.out.println("Error in submitSelectedNowOffers: " + e.getMessage());
            return "FAILED";
        }
    }

    public boolean restoreSelectedNowOffers(){
//        this.selectedNowOffers.clear();

//        Iterator<HashMap.Entry<Offering , OfferStatus>> studentCoursesIter = this.studentCourses.entrySet().iterator();
//        while(studentCoursesIter.hasNext()){
//            Map.Entry<Offering , OfferStatus> studentCoursesTuple = studentCoursesIter.next();
//            if(studentCoursesTuple.getValue() == OfferStatus.FINALIZED || studentCoursesTuple.getValue() == OfferStatus.WAITING){
//                this.selectedNowOffers.put(studentCoursesTuple.getKey() , studentCoursesTuple.getValue());
//            }
//        }
//        return true;

        try{
            BolBolRepo.getInstance().clearStudentSelectedNowOffersDB(this.studentId);
            Iterator<HashMap.Entry<Offering , OfferStatus>> studentCoursesIter = BolBolRepo.getInstance().getStudentCoursesFinalizedOrWaitingDB(this.studentId).entrySet().iterator();
            while(studentCoursesIter.hasNext()){
                Map.Entry<Offering , OfferStatus> studentCoursesTuple = studentCoursesIter.next();

                try{
                    BolBolRepo.getInstance().insertOfferToSelectedNowDB(this.studentId , studentCoursesTuple.getKey().getCode() ,
                                                                studentCoursesTuple.getKey().getClassCode() , studentCoursesTuple.getValue());
                }catch (SQLException err){
                    System.out.println("Concern in restoreSelectedNowOffers: " + err.getMessage());
                }
            }
            return true;
        }catch (SQLException e){
            System.out.println("Error in restoreSelectedNowOffers: " + e.getMessage());
            return false;
        }
    }

    public boolean changeWaitingOfferToFinalized(String code , String classCode){
        Iterator<HashMap.Entry<Offering , OfferStatus>> studentCoursesIter = this.studentCourses.entrySet().iterator();
        while(studentCoursesIter.hasNext()){
            Map.Entry<Offering , OfferStatus> studentCoursesTuple = studentCoursesIter.next();
            if(studentCoursesTuple.getKey().getCode().equals(code) && studentCoursesTuple.getKey().getClassCode().equals(classCode) &&
                studentCoursesTuple.getValue() == OfferStatus.WAITING){
                studentCoursesTuple.setValue(OfferStatus.FINALIZED);
                studentCoursesTuple.getKey().incrementNumSignedUp();
                return true;
            }
        }
        return false;
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public ArrayList<PassedCourse> getStudentPassedCourses() {
        return studentPassedCourses;
    }

    public String getName() {
        return name;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public HashMap<Offering, OfferStatus> getStudentCourses() {
        return studentCourses;
    }

    public HashMap<Offering, OfferStatus> getSelectedNowOffers() {
        return selectedNowOffers;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getField() {
        return field;
    }

    public String getLevel() {
        return level;
    }

    public String getStatus() {
        return status;
    }

    public String getImgAddress() {
        return imgAddress;
    }

    public String getStudentCourseNameBasedOnDayAndTime(String day , String time){
        for(Offering offering : this.studentCourses.keySet()){
            if(offering.getDays().contains(day) && offering.getTime().equals(time) && this.studentCourses.get(offering) == OfferStatus.FINALIZED){
                return offering.getName();
            }
        }
        return "";
    }
}