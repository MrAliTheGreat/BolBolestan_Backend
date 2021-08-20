package BolBolCodes;

public class PassedCourse {
    private String code;
    private long grade;
    private long units;

    public PassedCourse(String code , long grade , long units){
        this.code = code;
        this.grade = grade;
        this.units = units;
    }

    public String getCode() {
        return code;
    }

    public long getGrade() {
        return grade;
    }

    public long getUnits() {
        return units;
    }
}
