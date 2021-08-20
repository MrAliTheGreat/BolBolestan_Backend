package BolBol.CA5_Server.Domain.BolBolCodes;

import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Offering {
    private String code;
    private String name;
    private String instructor;
    private long units;
    private ArrayList<String> days;
    private String time;
    private String examTimeStart;
    private String examTimeEnd;
    private long capacity;
    private ArrayList<String> prerequisites;
    private String classCode;
    private String type;
    private long numSignedUp;

    private int numberStudents;

    private ArrayList<String> offerWaitingList = new ArrayList<String>();

    public Offering(String code , String name , String instructor , long units , ArrayList<String> days , String time , String examTimeStart ,
                    String examTimeEnd , long capacity , ArrayList<String> prerequisites , String classCode , String type){
        this.code = code;
        this.name = name;
        this.instructor = instructor;
        this.units = units;
        this.days = days;
        this.time = time;
        this.examTimeStart = examTimeStart;
        this.examTimeEnd = examTimeEnd;
        this.capacity = capacity;
        this.prerequisites = prerequisites;
        this.classCode = classCode;
        this.type = type;
        this.numSignedUp = 0;

        this.numberStudents = 0;
    }

    public void incrementCapacity(){
        this.capacity++;
    }

    public void decrementCapacity(){
        this.capacity--;
    }

    public void incrementNumSignedUp(){
        this.numSignedUp++;
    }

    public void decrementNumSignedUp(){
        this.numSignedUp--;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getInstructor() {
        return instructor;
    }

    public long getUnits() {
        return units;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public String getTime() {
        return time;
    }

    public String getExamTimeStart() {
        return examTimeStart;
    }

    public String getExamTimeEnd() {
        return examTimeEnd;
    }

    public long getCapacity() {
        return capacity;
    }

    public ArrayList<String> getPrerequisites() {
        return prerequisites;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getType() {
        return type;
    }

    public long getNumSignedUp() {
        return numSignedUp;
    }

    public int getNumberStudents() {
        return numberStudents;
    }

    public void addStudentToOfferWaitingList(String studentId){
        this.offerWaitingList.add(studentId);
    }

    public ArrayList<String> getOfferWaitingList() {
        return offerWaitingList;
    }

    public void setNumSignedUp(long numSignedUp) {
        this.numSignedUp = numSignedUp;
    }

    public void setNumberStudents(int numberStudents) {
        this.numberStudents = numberStudents;
    }
}
