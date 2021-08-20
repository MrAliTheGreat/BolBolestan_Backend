package BolBolCodes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class WeeklyScheduleTest {
    private BolBolInterface bolbolInterface;
    private String[] commands;

    private String getContentsFile(String address){
        String contentsSetupCommands;
        try{
            contentsSetupCommands = new String(Files.readAllBytes(Paths.get(address)));
        }catch (IOException exp){
            contentsSetupCommands = "";
        }
        return contentsSetupCommands;
    }

    @Before
    public void setup(){
        bolbolInterface = new BolBolInterface();
        this.commands = getContentsFile("src/test/resources/setupCommands.txt").split("\n");

        for(String command : this.commands){
            this.bolbolInterface.runBolBolInterface(command);
        }
    }

    @Test
    public void testMixFinalNotFinal(){
        this.commands = getContentsFile("src/test/resources/testMixFinalNotFinalCommands.txt").split("\n");

        for(String command : this.commands){
            this.bolbolInterface.runBolBolInterface(command);
        }
        System.out.println(this.commands[3]);
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getFinalizedOfferingsForTEST().get(0) , true);
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getFinalizedOfferingsForTEST().get(1) , true);
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getFinalizedOfferingsForTEST().get(2) , false);

    }

    @Test
    public void testOfferDetails(){
        this.commands = getContentsFile("src/test/resources/testOfferNamesCommands.txt").split("\n");

        for(String command : this.commands){
            this.bolbolInterface.runBolBolInterface(command);
        }

        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(0).getName() , "OS");
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(1).getName() , "Internet Engineering");

        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(0).getInstructor() , "Mohammad Razi");
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(1).getInstructor() , "Reza Ahmadi");

        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(0).getUnits() , 10);
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(1).getUnits() , 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testOfferDays(){
        this.commands = getContentsFile("src/test/resources/testOfferDaysCommands.txt").split("\n");

        for(String command : this.commands){
            this.bolbolInterface.runBolBolInterface(command);
        }

        this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(0).getDays().get(2);

    }

    @Test
    public void testOfferDaysMatch(){
        this.commands = getContentsFile("src/test/resources/testOfferDaysCommands.txt").split("\n");

        for(String command : this.commands){
            this.bolbolInterface.runBolBolInterface(command);
        }

        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(0).getDays().get(0) , "Wednesday");
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(0).getDays().get(1) , "Monday");

        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(1).getDays().get(0) , "Wednesday");
        assertEquals(this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getStudentCoursesForTEST().get(1).getDays().get(1) , "Tuesday");

    }

    @After
    public void clear(){
        this.bolbolInterface = null;
        this.commands = null;
    }
}
