package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Domain.Models.FrontOffering;
import BolBol.CA5_Server.Domain.Models.FrontStudent;
import BolBol.CA5_Server.Repository.BolBolRepo;
import jdk.jshell.execution.Util;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/courses")
public class CoursesController {

    @GetMapping("/{studentId}/offers")
    public ArrayList<FrontOffering> getFrontOffers(@PathVariable String studentId){
        BolBolSystem.getBolBolSystemInstance().setSearchOffersDB(BolBolSystem.getBolBolSystemInstance().getSearchName());
        ArrayList<Offering> offerings = BolBolSystem.getBolBolSystemInstance().getSearchOffers();
        ArrayList<FrontOffering> frontOfferings = new ArrayList<FrontOffering>();

        for(Offering offering : offerings){
            frontOfferings.add(new FrontOffering(offering , studentId));
        }

        BolBolSystem.getBolBolSystemInstance().setCategorized(false);

        return frontOfferings;
    }

    @GetMapping("/offers")
    public ArrayList<FrontOffering> getFrontOffersJWT(){
        String studentId = getStudentIdFromKeyJWT("getFrontOffersJWT");
        return getFrontOffers(studentId);
    }

    @GetMapping("/offersNames")
    public HashMap<String , String> getOffersByName(){
        try{
            return BolBolRepo.getInstance().getOffersByNameDB();
        }catch (SQLException e){
            System.out.println("Error in getOffersByName: " + e.getMessage());
            return new HashMap<>();
        }
    }

    @PostMapping(path = "/searchName" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchForOffersByName(@RequestBody String jsonString , final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        BolBolSystem.getBolBolSystemInstance().setSearchOffersDB((String) receivedJSON.get("searchText"));
        return "OK";
    }

    @PostMapping(path = "/searchType" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchForOffersByType(@RequestBody String jsonString , final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        BolBolSystem.getBolBolSystemInstance().setSearchOffersByTypeDB((String) receivedJSON.get("searchType"));
        BolBolSystem.getBolBolSystemInstance().setCategorized(true);
        return "OK";
    }

    @GetMapping("/{studentId}/selectedNowOffersStatus")
    public HashMap<String , String> getStudentSelectedNowCoursesStatus(@PathVariable String studentId){
        try {
            return BolBolRepo.getInstance().getStudentSelectedNowStatusHashMapFormatDB(studentId);
        }catch (SQLException e){
            System.out.println("Error in getStudentSelectedNowCoursesStatus: " + e.getMessage());
            return null;
        }
    }

    @GetMapping("/selectedNowOffersStatus")
    public HashMap<String , String> getStudentSelectedNowCoursesStatusJWT(){
        String studentId = getStudentIdFromKeyJWT("getStudentSelectedNowCoursesStatusJWT");
        return getStudentSelectedNowCoursesStatus(studentId);
    }

    @GetMapping("/{studentId}/selectedNowOffers")
    public List<Offering> getStudentSelectedNowCourses(@PathVariable String studentId){
        List<Offering> selectedNowCourses = new ArrayList<>();
//        Student targetStudent = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentId);

        try{
            Iterator<HashMap.Entry<Offering , Student.OfferStatus>> studentSelectedNowIter = BolBolRepo.getInstance().getStudentSelectedNowHashMapFormatDB(studentId).entrySet().iterator();
            while(studentSelectedNowIter.hasNext()){
                Map.Entry<Offering , Student.OfferStatus> studentSelectedNowTuple = studentSelectedNowIter.next();
                selectedNowCourses.add(studentSelectedNowTuple.getKey());
            }
            return selectedNowCourses;
        }catch (SQLException e){
            System.out.println("Error in getStudentSelectedNowCourses: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @GetMapping("/selectedNowOffers")
    public List<Offering> getStudentSelectedNowCoursesJWT(){
        String studentId = getStudentIdFromKeyJWT("getStudentSelectedNowCoursesJWT");
        return getStudentSelectedNowCourses(studentId);
    }

    @PostMapping(path = "/{studentId}/add" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String addOfferToSelectedNowOffers(@PathVariable String studentId , @RequestBody String jsonString , final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        String addResult = BolBolSystem.getBolBolSystemInstance().addToStudentSelectedNowOffers(studentId , (String) receivedJSON.get("offerCode") ,
                                                                                    (String) receivedJSON.get("offerClassCode"));
        if(addResult.equals("")){
            return "OK";
        }
        return addResult;
    }

    @PostMapping(path = "/add" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String addOfferToSelectedNowOffersJWT(@RequestBody String jsonString , final HttpServletResponse response){
        String studentId = getStudentIdFromKeyJWT("addOfferToSelectedNowOffersJWT");
        return addOfferToSelectedNowOffers(studentId , jsonString , response);
    }

    @DeleteMapping(path = "/{studentId}/remove" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeOfferFromSelectedNowOffers(@PathVariable String studentId , @RequestBody String jsonString , final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        BolBolSystem.getBolBolSystemInstance().removeFromStudentSelectedNowOffers(studentId , (String) receivedJSON.get("offerCode") ,
                (String) receivedJSON.get("offerClassCode"));
        return "OK";
    }

    @DeleteMapping(path = "/remove" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeOfferFromSelectedNowOffersJWT(@RequestBody String jsonString , final HttpServletResponse response){
        String studentId = getStudentIdFromKeyJWT("removeOfferFromSelectedNowOffersJWT");
        return removeOfferFromSelectedNowOffers(studentId , jsonString , response);
    }

    @GetMapping("/{studentId}/numSelectedNowOffersUnits")
    public String getNumUnitsSelectedNowOffers(@PathVariable String studentId){
        try{
            Student targetStudent = BolBolRepo.getInstance().findStudentFromIdDB(studentId);
            return Integer.toString(targetStudent.calculateTotalUnitsSelectedNowOffersDB());
        }catch (SQLException e){
            System.out.println("Error in getNumUnitsSelectedNowOffers: " + e.getMessage());
            return "";
        }
    }

    @GetMapping("/numSelectedNowOffersUnits")
    public String getNumUnitsSelectedNowOffersJWT(){
        String studentId = getStudentIdFromKeyJWT("getNumUnitsSelectedNowOffersJWT");
        return getNumUnitsSelectedNowOffers(studentId);
    }

    @PostMapping(path = "/{studentId}/reset" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String resetSelectedNowOffers(@PathVariable String studentId , @RequestBody String jsonString , final HttpServletResponse response){
        try {
            Student targetStudent = BolBolRepo.getInstance().findStudentFromIdDB(studentId);
            targetStudent.restoreSelectedNowOffers();
            return "OK";
        }catch (SQLException e){
            System.out.println("Error in resetSelectedNowOffers: " + e.getMessage());
            return null;
        }
    }

    @PostMapping(path = "/reset" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String resetSelectedNowOffersJWT(@RequestBody String jsonString , final HttpServletResponse response){
        String studentId = getStudentIdFromKeyJWT("resetSelectedNowOffersJWT");
        return resetSelectedNowOffers(studentId , jsonString , response);
    }

    @PostMapping(path = "/{studentId}/submit" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String submitSelectedNowOffers(@PathVariable String studentId , @RequestBody String jsonString , final HttpServletResponse response){
        String submitResult = BolBolSystem.getBolBolSystemInstance().submitStudentSelectedNowOffers(studentId);
        if(submitResult.equals("")){
            return "OK";
        }
        return submitResult;
    }

    @PostMapping(path = "/submit" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String submitSelectedNowOffersJWT(@RequestBody String jsonString , final HttpServletResponse response){
        String studentId = getStudentIdFromKeyJWT("submitSelectedNowOffersJWT");
        return submitSelectedNowOffers(studentId , jsonString , response);
    }

    private String getStudentIdFromKeyJWT(String functionName){
        try{
            return BolBolRepo.getInstance().findStudentFromEmailDB(SecurityContextHolder.getContext().getAuthentication().getName()).getStudentId();
        }catch(SQLException e){
            System.out.println("Error in " + functionName + ": " + e.getMessage());
            return null;
        }
    }
}

