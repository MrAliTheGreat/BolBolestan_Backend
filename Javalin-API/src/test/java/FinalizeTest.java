import BolBolCodes.Offering;
import BolBolCodes.Student;
import ExternalServicesHandlers.importHandler;
import ExternalServicesHandlers.templateHandler;
import ServerCodes.ServerInterface;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static org.junit.Assert.*;

public class FinalizeTest {

    private ServerInterface serverInterface;
    private String coursesAddress = "http://138.197.181.131:5000/api/courses";
    private String studentsAddress = "http://138.197.181.131:5000/api/students";
    private String gradesAddressBASE = "http://138.197.181.131:5000/api/grades/";
    private int infoServerPort = 5000;
    private boolean finalizeResult;

    private Student initializeSampleStudentFailure(){
        Student sampleStudent = this.serverInterface.getBolBolSystemStudentsForTEST().get(0);
        // 4 units in total
        Offering offer1 = this.serverInterface.getBolBolSystemOffersForTEST().get(3); // units = 3
        Offering offer2 = this.serverInterface.getBolBolSystemOffersForTEST().get(7); // units = 1
        sampleStudent.addToWeeklyScheduleOfStudent(offer1);
        sampleStudent.addToWeeklyScheduleOfStudent(offer2);
        return sampleStudent;
    }

    private Student initializeSampleStudentSuccess(){
        Student sampleStudent = this.serverInterface.getBolBolSystemStudentsForTEST().get(0);
        // 15 units in total
        Offering offer1 = this.serverInterface.getBolBolSystemOffersForTEST().get(3);
        Offering offer2 = this.serverInterface.getBolBolSystemOffersForTEST().get(7);
        Offering offer3 = this.serverInterface.getBolBolSystemOffersForTEST().get(9);
        Offering offer4 = this.serverInterface.getBolBolSystemOffersForTEST().get(12);
        Offering offer5 = this.serverInterface.getBolBolSystemOffersForTEST().get(15);
        Offering offer6 = this.serverInterface.getBolBolSystemOffersForTEST().get(16);

        sampleStudent.addToWeeklyScheduleOfStudent(offer1);
        sampleStudent.addToWeeklyScheduleOfStudent(offer2);
        sampleStudent.addToWeeklyScheduleOfStudent(offer3);
        sampleStudent.addToWeeklyScheduleOfStudent(offer4);
        sampleStudent.addToWeeklyScheduleOfStudent(offer5);
        sampleStudent.addToWeeklyScheduleOfStudent(offer6);

        return sampleStudent;
    }

    @Before
    public void setupServer(){
        this.serverInterface = new ServerInterface();
        this.serverInterface.start(this.coursesAddress , this.studentsAddress , this.gradesAddressBASE , this.infoServerPort);
    }


    @Test
    public void testFinalizedFailure(){
        Student sampleStudent = initializeSampleStudentFailure();
        this.finalizeResult = sampleStudent.finalizeScheduleOfStudent();
        assertEquals(this.finalizeResult , false);

        String response = importHandler.getResponseFromService("http://localhost:5000/submit/" + sampleStudent.getStudentId());
        String fillerResult = templateHandler.fillSubmitTemplate(sampleStudent.getStudentId() ,
                                                                 Integer.toString(sampleStudent.calculateTotalUnitsOffers()) ,
                                                                 templateHandler.getTemplate("submit.html"));
        assertEquals(response , fillerResult);


        int statusCode = importHandler.getStatusCodeFromServer("http://localhost:5000/submit/" + sampleStudent.getStudentId());
        assertEquals(statusCode , 200);


        String resultLink = importHandler.getPostResponseFromService("http://localhost:5000/submit/" + sampleStudent.getStudentId());
        assertEquals(resultLink , "/submit_failed");
    }

    @Test
    public void testFinalizedSuccess(){
        Student sampleStudent = initializeSampleStudentSuccess();
        this.finalizeResult = sampleStudent.finalizeScheduleOfStudent();
        assertEquals(this.finalizeResult , true);

        String response = importHandler.getResponseFromService("http://localhost:5000/submit/" + sampleStudent.getStudentId());
        String fillerResult = templateHandler.fillSubmitTemplate(sampleStudent.getStudentId() ,
                                                                 Integer.toString(sampleStudent.calculateTotalUnitsOffers()) ,
                                                                 templateHandler.getTemplate("submit.html"));
        assertEquals(response , fillerResult);


        int statusCode = importHandler.getStatusCodeFromServer("http://localhost:5000/submit/" + sampleStudent.getStudentId());
        assertEquals(statusCode , 200);


        String resultLink = importHandler.getPostResponseFromService("http://localhost:5000/submit/" + sampleStudent.getStudentId());
        assertEquals(resultLink , "/submit_ok");

    }

    @After
    public void stop(){
        this.serverInterface.stopServer();
    }
}
