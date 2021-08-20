package BolBolCodes;

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
    private int numberStudents;

    public Offering(String code , String name , String instructor , long units , ArrayList<String> days , String time , String examTimeStart ,
                    String examTimeEnd , long capacity , ArrayList<String> prerequisites){
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
        this.numberStudents = 0;
    }

    public void incrementCapacity(){
        this.capacity++;
    }

    public void decrementCapacity(){
        this.capacity--;
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
}
