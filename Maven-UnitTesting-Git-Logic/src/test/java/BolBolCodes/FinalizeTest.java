package BolBolCodes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FinalizeTest {
    private BolBolInterface bolbolInterface;
    private String[] setupCommands;

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
        this.setupCommands = getContentsFile("src/test/resources/setupCommands.txt").split("\n");

        for(String command : this.setupCommands){
            this.bolbolInterface.runBolBolInterface(command);
        }
    }

    @Test
    public void testCourseFull(){
        String[] courseFullCommands = getContentsFile("src/test/resources/testCourseFullCommands.txt").split("\n");
        for(String command : courseFullCommands){
            this.bolbolInterface.runBolBolInterface(command);
        }
        assertEquals(this.bolbolInterface.getCommandStatusForTEST() , false);
    }

    @Test
    public void testMinMaxUnits(){
        String[] minMaxUnitsCommands = getContentsFile("src/test/resources/testMinMaxUnitsCommands.txt").split("\n");
        for(String command : minMaxUnitsCommands){
            this.bolbolInterface.runBolBolInterface(command);
        }
        assertEquals(this.bolbolInterface.getCommandStatusForTEST() , true);
    }

    @Test
    public void testTimeCollision(){
        String[] timeCollisionCommands = getContentsFile("src/test/resources/testTimeCollisionCommands.txt").split("\n");
        for(String command : timeCollisionCommands){
            this.bolbolInterface.runBolBolInterface(command);
        }
        assertEquals(this.bolbolInterface.getCommandStatusForTEST() , false);
    }

    @Test
    public void testExamTimeCollision(){
        String[] examTimeCollisionCommands = getContentsFile("src/test/resources/testExamTimeColCommands.txt").split("\n");
        for(String command : examTimeCollisionCommands){
            this.bolbolInterface.runBolBolInterface(command);
        }
        assertEquals(this.bolbolInterface.getCommandStatusForTEST() , false);
    }

    @Test
    public void testCorrectFinalize(){
        String[] correctFinalizeCommands = getContentsFile("src/test/resources/testCorrectFinalize.txt").split("\n");
        for(String command : correctFinalizeCommands){
            this.bolbolInterface.runBolBolInterface(command);
        }
        boolean areFinalized = true;
        for(boolean finalized : this.bolbolInterface.getbolbolSystemForTEST().getUserStudentForTEST().getFinalizedOfferingsForTEST()){
            if(!finalized){
                areFinalized = false;
            }
        }
        assertEquals(areFinalized , true);
    }

    @After
    public void clear(){
        this.bolbolInterface = null;
        this.setupCommands = null;
    }

}
