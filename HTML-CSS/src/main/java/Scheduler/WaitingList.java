package Scheduler;

import BolBolCodes.BolBolSystem;
import BolBolCodes.Offering;
import BolBolCodes.Student;

public class WaitingList implements Runnable {

    @Override
    public void run() {
        System.out.println("It's time for checking waiting lists!!!");
        Student targetStudent;

        for(Offering offering : BolBolSystem.getBolBolSystemInstance().getOfferings()){
            for(String studentId : offering.getOfferWaitingList()){
                targetStudent = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentId);
                targetStudent.changeWaitingOfferToFinalized(offering.getCode() , offering.getClassCode());
            }
        }
    }
}