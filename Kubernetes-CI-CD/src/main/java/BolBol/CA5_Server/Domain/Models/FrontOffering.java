package BolBol.CA5_Server.Domain.Models;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.BolBolRepo;

import java.sql.SQLException;

public class FrontOffering extends Offering {
    String offerStatus;

    public FrontOffering(Offering offering , String studentId){
        super(offering.getCode() , offering.getName() , offering.getInstructor(), offering.getUnits(), offering.getDays(),
                offering.getTime(), offering.getExamTimeStart(), offering.getExamTimeEnd(), offering.getCapacity(),
                offering.getPrerequisites(), offering.getClassCode(), offering.getType());

        setNumSignedUp(offering.getNumSignedUp());
        setNumberStudents(offering.getNumberStudents());

        try {
            Student targetStudent = BolBolRepo.getInstance().findStudentFromIdDB(studentId);
            if(targetStudent.getStudentCourses().get(offering) == Student.OfferStatus.WAITING){
                this.offerStatus = "waiting";
            }
            else{
                this.offerStatus = "add";
            }
        }catch (SQLException e){
            System.out.println("Error in checkUserExistence: " + e.getMessage());
        }
    }

    public String getOfferStatus() {
        return offerStatus;
    }
}
