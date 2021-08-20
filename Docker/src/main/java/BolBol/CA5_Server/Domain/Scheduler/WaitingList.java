package BolBol.CA5_Server.Domain.Scheduler;

import BolBol.CA5_Server.Domain.BolBolCodes.*;
import BolBol.CA5_Server.Repository.BolBolRepo;
import BolBol.CA5_Server.Utilities.MultipleStrings;

import java.sql.SQLException;
import java.util.ArrayList;

public class WaitingList implements Runnable {

    @Override
    public void run() {
        System.out.println("It's time for checking waiting lists!!!");

        try {
            ArrayList<MultipleStrings> waitingOffers = BolBolRepo.getInstance().getWaitingListOffersDB();
            for(MultipleStrings multipleStrings : waitingOffers){
                String code = multipleStrings.getStrings().get(0);
                String classCode = multipleStrings.getStrings().get(1);
                String studentId = multipleStrings.getStrings().get(2);

                BolBolRepo.getInstance().changeWaitingOfferToFinalizedDB(studentId , code , classCode);
            }
        }catch (SQLException e){
            System.out.println("Error in waiting list offers: " + e.getMessage());
        }
    }
}