package BolBol.CA5_Server.Repository;

import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.PassedCourse;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Domain.Scheduler.WaitingList;
import BolBol.CA5_Server.Repository.Offering.DaysMapper;
import BolBol.CA5_Server.Repository.Offering.OfferingMapper;
import BolBol.CA5_Server.Repository.Offering.PreMapper;
import BolBol.CA5_Server.Repository.Offering.WaitingListMapper;
import BolBol.CA5_Server.Repository.PassedCourse.PassedCourseMapper;
import BolBol.CA5_Server.Repository.SelectedNow.SelectedNowMapper;
import BolBol.CA5_Server.Repository.Student.StudentMapper;
import BolBol.CA5_Server.Repository.TakenCourses.TakenCoursesMapper;
import BolBol.CA5_Server.Utilities.MultipleStrings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BolBolRepo{
    private static BolBolRepo instance;

    private BolBolRepo(){

    }

    public static BolBolRepo getInstance() {
        if (Objects.isNull(instance)){
            instance = new BolBolRepo();
        }
        return instance;
    }

    public void insertOfferDaysDB(String code , String classCode , ArrayList<String> days) throws SQLException {
        ArrayList<String> info;
        for(String day : days){
            info = new ArrayList<>();
            info.add(code); info.add(classCode); info.add(day);
            DaysMapper daysMapper = new DaysMapper();
            daysMapper.insert(info);
        }
    }

    public void insertOfferPrerequisitesDB(String code , ArrayList<String> preCodes) throws SQLException {
        if(!this.checkOfferExistenceForPrerequisitesDB(code)){
            System.out.println("Offer with code " + code + " doesn't exist in offers DB!");
            return;
        }

        ArrayList<String> info;
        for(String preCode : preCodes){
            info = new ArrayList<>();
            info.add(code); info.add(preCode);
            PreMapper preMapper = new PreMapper();
            preMapper.insert(info);
        }
    }

    public void insertOfferDB(Offering offering) throws SQLException{
        OfferingMapper offeringMapper = new OfferingMapper();
        offeringMapper.insert(offering);
    }

    public void insertStudentDB(Student student) throws SQLException {
        StudentMapper studentMapper = new StudentMapper();
        studentMapper.insert(student);
    }

    public void insertPassedCourseDB(PassedCourse passedCourse , String studentId) throws SQLException {
        PassedCourseMapper passedCourseMapper = new PassedCourseMapper(studentId);
        passedCourseMapper.insert(passedCourse);
    }

    private ArrayList<String> getStudentCoursesArrayList(String studentId , String code , String classCode , Student.OfferStatus offerStatus){
        ArrayList<String> info = new ArrayList<>(); info.add(studentId); info.add(code); info.add(classCode);
        if(offerStatus == Student.OfferStatus.FINALIZED){
            info.add("FINALIZED");
        }
        else if(offerStatus == Student.OfferStatus.WAITING){
            info.add("WAITING");
        }
        else{
            info.add("NOT_FINALIZED");
        }
        return info;
    }

    public void insertTakenCourseDB(String studentId , String code , String classCode , Student.OfferStatus offerStatus) throws SQLException {
        TakenCoursesMapper takenCoursesMapper = new TakenCoursesMapper();
        takenCoursesMapper.insert(getStudentCoursesArrayList(studentId , code , classCode , offerStatus));
    }

    public void insertOfferToSelectedNowDB(String studentId , String code , String classCode , Student.OfferStatus offerStatus) throws SQLException {
        SelectedNowMapper selectedNowMapper = new SelectedNowMapper();
        selectedNowMapper.insert(getStudentCoursesArrayList(studentId , code , classCode , offerStatus));
    }

    public void insertStudentAndOfferToWaitingListDB(String code , String classCode , String studentId) throws SQLException {
        ArrayList<String> info = new ArrayList<>(); info.add(code); info.add(classCode); info.add(studentId);
        WaitingListMapper waitingListMapper = new WaitingListMapper();
        waitingListMapper.insert(info);
    }

    public Student findStudentFromIdDB(String studentId) throws SQLException{
        return new StudentMapper().find(studentId);
    }

    public Student findStudentFromEmailDB(String email) throws SQLException{
        return new StudentMapper().findStudentFromEmail(email);
    }

    public Offering findOfferFromCodeAndClassCodeDB(String code , String classCode) throws SQLException{
        List<String> info = new ArrayList<>(); info.add(code); info.add(classCode);
        return new OfferingMapper().find(new MultipleStrings(info));
    }

    public ArrayList<String> findTakenCourseFromStudentIdCodeClassCodeDB(String studentId , String code , String classCode) throws SQLException{
        List<String> info = new ArrayList<>(); info.add(studentId); info.add(code); info.add(classCode);
        return new TakenCoursesMapper().find(new MultipleStrings(info));
    }

    public ArrayList<String> findSelectedNowOfferFromStudentIdCodeClassCodeDB(String studentId , String code , String classCode) throws SQLException{
        List<String> info = new ArrayList<>(); info.add(studentId); info.add(code); info.add(classCode);
        return new SelectedNowMapper().find(new MultipleStrings(info));
    }

    public ArrayList<String> getOfferClassDaysDB(String code , String classCode) throws SQLException{
        return new DaysMapper().getOfferClassDays(code , classCode);
    }

    public ArrayList<String> getOfferPrerequisitesDB(String code) throws SQLException{
        return new PreMapper().getOfferPrerequisites(code);
    }

    public boolean checkOfferExistenceForPrerequisitesDB(String code) throws SQLException{
        return new OfferingMapper().checkOfferExistenceFromCode(code);
    }

    public ArrayList<String> getAllStudentIDsDB() throws SQLException{
        return new StudentMapper().getAllStudentIDs();
    }

    public int getOfferUnitsFromCodeDB(String code) throws SQLException{
        return new OfferingMapper().getOfferUnitsFromCode(code);
    }

    public ArrayList<PassedCourse> getStudentPassedCoursesDB(String studentId) throws SQLException{
        return new PassedCourseMapper(studentId).getStudentPassedCourses(studentId);
    }

    public ArrayList<PassedCourse> getStudentPassedCoursesNotFailedFromCodeDB(String studentId , String code) throws SQLException{
        return new PassedCourseMapper(studentId).getStudentPassedCoursesNotFailedFromCode(studentId , code);
    }

    public String findOfferNameByCodeDB(String code){
        try {
            return new OfferingMapper().findOfferNameByCode(code);
        }catch (SQLException e){
            System.out.println("Error in findOfferNameByCodeDB: " + e.getMessage());
            return "";
        }
    }

    public int getLastTermNumberStudentDB(String studentId) throws SQLException{
        return new PassedCourseMapper(studentId).getLastTermNumberStudent(studentId);
    }

    public HashMap<Offering , Student.OfferStatus> getStudentCoursesFinalizedOrWaitingDB(String studentId) throws SQLException{
        return new TakenCoursesMapper().getStudentCoursesFinalizedOrWaiting(studentId);
    }

    public HashMap<Offering , Student.OfferStatus> getStudentCoursesDB(String studentId) throws SQLException{
        return new TakenCoursesMapper().getStudentCourses(studentId);
    }

    public void decrementCourseCapacityDB(String code , String classCode)throws SQLException {
        new OfferingMapper().decrementCourseCapacity(code , classCode);
    }

    public void incrementCourseCapacityDB(String code , String classCode)throws SQLException {
        new OfferingMapper().incrementCourseCapacity(code , classCode);
    }

    public void decrementNumSignedUpDB(String code , String classCode)throws SQLException {
        new OfferingMapper().decrementNumSignedUp(code , classCode);
    }

    public void clearStudentCoursesDB(String studentId) throws SQLException {
        new TakenCoursesMapper().clearStudentCourses(studentId);
    }

    public void clearStudentSelectedNowOffersDB(String studentId) throws SQLException {
        new SelectedNowMapper().clearStudentSelectedNowOffers(studentId);
    }

    public ArrayList<Offering> getSearchedOffersDB(String name) throws SQLException {
        return new OfferingMapper().getSearchedOffers(name);
    }

    public HashMap<String , String> getOffersByNameDB() throws SQLException {
        return new OfferingMapper().getOffersByName();
    }

    public ArrayList<Offering> getAllOffersDB() throws SQLException {
        return new OfferingMapper().getAllOffers();
    }

    public ArrayList<Offering> getAllOffersByTypeDB(String type) throws SQLException {
        return new OfferingMapper().getAllOffersByType(type);
    }

    public ArrayList<String> getStudentSelectedNowOffersDB(String studentId) throws SQLException {
        return new SelectedNowMapper().getStudentSelectedNowOffers(studentId);
    }

    public void removeOfferFromSelectedNowDB(String studentId , String code , String classCode) throws SQLException {
        List<String> info = new ArrayList<>(); info.add(studentId); info.add(code); info.add(classCode);
        SelectedNowMapper selectedNowMapper = new SelectedNowMapper();
        selectedNowMapper.delete(new MultipleStrings(info));
    }

    public boolean checkSelectedNowOffersCapacityDB(String studentId) throws SQLException {
        return new SelectedNowMapper().checkSelectedNowOffersCapacity(studentId);
    }

    public HashMap<Offering , Student.OfferStatus> getStudentSelectedNowHashMapFormatDB(String studentId) throws SQLException {
        return new SelectedNowMapper().getStudentSelectedNowHashMapFormat(studentId);
    }

    public HashMap<String , String> getStudentSelectedNowStatusHashMapFormatDB(String studentId) throws SQLException {
        return new SelectedNowMapper().getStudentSelectedNowStatusHashMapFormat(studentId);
    }

    public ArrayList<MultipleStrings> getWaitingListOffersDB() throws SQLException {
        return new WaitingListMapper().getWaitingListOffers();
    }

    public void changeWaitingOfferToFinalizedDB(String studentId , String code , String classCode) throws SQLException {
        ArrayList<String> willBeFinalizedOfferCodeAndClassCodes = new TakenCoursesMapper().getWillBeFinalizedOffers(studentId , code , classCode);
        String[] codeAndClassCode;
        for(String offerCodeAndClassCode : willBeFinalizedOfferCodeAndClassCodes){
            codeAndClassCode = offerCodeAndClassCode.split("-");
            new OfferingMapper().incrementNumSignedUp(codeAndClassCode[0] , codeAndClassCode[1]);
        }
        new TakenCoursesMapper().changeWaitingOfferToFinalized(studentId , code , classCode);

        // Clearing Finalized Offers From Waiting list
        List<String> info = new ArrayList<>(); info.add(code); info.add(classCode); info.add(studentId);
        new WaitingListMapper().delete(new MultipleStrings(info));
    }

    public void changeToNewPasswordByStudentIdDB(String newPassword , String studentId)throws SQLException {
        new StudentMapper().changeToNewPasswordByStudentId(newPassword , studentId);
    }
}
