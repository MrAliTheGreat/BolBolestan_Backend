package BolBol.CA5_Server.Domain.BolBolCodes;

public class PassedCourse {
    private String code;
    private long grade;
    private long units;
    private long term;

    public PassedCourse(String code , long grade , long units , long term){
        this.code = code;
        this.grade = grade;
        this.units = units;
        this.term = term;
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

    public long getTerm() {
        return term;
    }
}
