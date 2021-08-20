package BolBolCodes;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;


public class BolBolInterface {

    private BolBolSystem bolbolSystem;
    private boolean commandStatus;

    public BolBolInterface(){
        bolbolSystem = new BolBolSystem();
        commandStatus = false;
    }

    private ArrayList<String> JSONArrayToArrayList(JSONArray jsonArray){
        ArrayList<String> jsonString = new ArrayList<String>();
        for(int i = 0; i<jsonArray.size(); i++){
            jsonString.add((String) jsonArray.get(i));
        }
        return jsonString;
    }

    public void runBolBolInterface(String inputCommand){
        this.commandStatus = false;
        String command[] = inputCommand.split(" " , 2);
        String operation = command[0] , jsonData;
        JSONObject jsonObject = null;

        if(command.length == 2){
            jsonData = command[1];
            jsonObject = (JSONObject) JSONValue.parse(jsonData);
        }

        if(operation.equals("addOffering")){
            JSONObject classTime = (JSONObject) jsonObject.get("classTime");
            JSONObject examTime = (JSONObject) jsonObject.get("examTime");

            Offering offer = new Offering((String) jsonObject.get("code") , (String) jsonObject.get("name") ,
                    (String) jsonObject.get("Instructor") , (long) jsonObject.get("units") ,
                    JSONArrayToArrayList((JSONArray) classTime.get("days")) ,
                    (String) classTime.get("time") , (String) examTime.get("start") ,
                    (String) examTime.get("end") , (long) jsonObject.get("capacity") ,
                    JSONArrayToArrayList((JSONArray) jsonObject.get("prerequisites")));

            this.commandStatus = bolbolSystem.addOffer(offer);

        }
        else if(operation.equals("addStudent")){
            Student student = new Student((String) jsonObject.get("studentId") , (String) jsonObject.get("name") ,
                    (String) jsonObject.get("enteredAt") );

            this.commandStatus = bolbolSystem.addStudent(student);
        }
        else if(operation.equals("getOfferings")){
            this.commandStatus = bolbolSystem.getOfferings((String) jsonObject.get("StudentId"));
        }
        else if(operation.equals("getOffering")){
            this.commandStatus = bolbolSystem.getOffering((String) jsonObject.get("StudentId") , (String) jsonObject.get("code"));
        }
        else if(operation.equals("addToWeeklySchedule")){
            this.commandStatus = bolbolSystem.addToWeeklySchedule((String) jsonObject.get("StudentId") , (String) jsonObject.get("code"));
        }
        else if(operation.equals("removeFromWeeklySchedule")){
            this.commandStatus = bolbolSystem.removeFromWeeklySchedule((String) jsonObject.get("StudentId") , (String) jsonObject.get("code"));
        }
        else if(operation.equals("getWeeklySchedule")){
            this.commandStatus = bolbolSystem.getWeeklySchedule((String) jsonObject.get("StudentId"));
        }
        else if(operation.equals("finalize")){
            this.commandStatus = bolbolSystem.finalizeSchedule((String) jsonObject.get("StudentId"));
        }
        else{
            this.commandStatus = bolbolSystem.executeUnknownCommand();
        }
    }

    protected BolBolSystem getbolbolSystemForTEST() {
        return this.bolbolSystem;
    }

    protected boolean getCommandStatusForTEST() {
        return this.commandStatus;
    }
}
