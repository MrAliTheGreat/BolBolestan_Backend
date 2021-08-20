package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.BolBolSystem;
import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Domain.Models.FrontOffering;
import BolBol.CA5_Server.Domain.Models.FrontStudent;
import jdk.jshell.execution.Util;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/courses")
public class CoursesController {

    @GetMapping("/{studentId}/offers")
    public ArrayList<FrontOffering> getFrontOffers(@PathVariable String studentId){
        BolBolSystem.getBolBolSystemInstance().setSearchOffers(BolBolSystem.getBolBolSystemInstance().getSearchName());
        ArrayList<Offering> offerings = BolBolSystem.getBolBolSystemInstance().getSearchOffers();
        ArrayList<FrontOffering> frontOfferings = new ArrayList<FrontOffering>();

        for(Offering offering : offerings){
            frontOfferings.add(new FrontOffering(offering , studentId));
        }

        BolBolSystem.getBolBolSystemInstance().setCategorized(false);

        return frontOfferings;
    }

    @GetMapping("/offersNames")
    public HashMap<String , String> getOffersByName(){
        HashMap<String , String> offerNamesByCode = new HashMap<>();
        ArrayList<Offering> offerings = BolBolSystem.getBolBolSystemInstance().getOfferings();
        for(Offering offering : offerings){
            offerNamesByCode.put(offering.getCode() , offering.getName());
        }
        return offerNamesByCode;
    }

    @PostMapping(path = "/searchName" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchForOffersByName(@RequestBody String jsonString , final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        BolBolSystem.getBolBolSystemInstance().setSearchOffers((String) receivedJSON.get("searchText"));
        return "OK";
    }

    @PostMapping(path = "/searchType" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchForOffersByType(@RequestBody String jsonString , final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        BolBolSystem.getBolBolSystemInstance().setSearchOffersByType((String) receivedJSON.get("searchType"));
        BolBolSystem.getBolBolSystemInstance().setCategorized(true);
        return "OK";
    }

    @GetMapping("/{studentId}/selectedNowOffersStatus")
    public HashMap<String , String> getStudentSelectedNowCoursesStatus(@PathVariable String studentId){
        HashMap<String , String> selectedNowCourses = new HashMap<>();
        Student targetStudent = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentId);

        HashMap<Offering , Student.OfferStatus> studentSelectedNowOffers = targetStudent.getSelectedNowOffers();
        for(Offering offering : studentSelectedNowOffers.keySet()){
            if(studentSelectedNowOffers.get(offering) == Student.OfferStatus.FINALIZED){
                selectedNowCourses.put(offering.getCode() , "FINALIZED");
            }
            else if(studentSelectedNowOffers.get(offering) == Student.OfferStatus.NOT_FINALIZED){
                selectedNowCourses.put(offering.getCode() , "NOT_FINALIZED");
            }
            else if(studentSelectedNowOffers.get(offering) == Student.OfferStatus.WAITING){
                selectedNowCourses.put(offering.getCode() , "WAITING");
            }
        }

        return selectedNowCourses;
    }

    @GetMapping("/{studentId}/selectedNowOffers")
    public List<Offering> getStudentSelectedNowCourses(@PathVariable String studentId){
        List<Offering> selectedNowCourses = new ArrayList<>();
        Student targetStudent = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentId);

        Iterator<HashMap.Entry<Offering , Student.OfferStatus>> studentSelectedNowIter = targetStudent.getSelectedNowOffers().entrySet().iterator();
        while(studentSelectedNowIter.hasNext()){
            Map.Entry<Offering , Student.OfferStatus> studentSelectedNowTuple = studentSelectedNowIter.next();
            selectedNowCourses.add(studentSelectedNowTuple.getKey());
        }

        return selectedNowCourses;
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

    @DeleteMapping(path = "/{studentId}/remove" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeOfferFromSelectedNowOffers(@PathVariable String studentId , @RequestBody String jsonString , final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        BolBolSystem.getBolBolSystemInstance().removeFromStudentSelectedNowOffers(studentId , (String) receivedJSON.get("offerCode") ,
                (String) receivedJSON.get("offerClassCode"));
        return "OK";
    }

    @GetMapping("/{studentId}/numSelectedNowOffersUnits")
    public String getNumUnitsSelectedNowOffers(@PathVariable String studentId){
        Student targetStudent = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentId);
        return Integer.toString(targetStudent.calculateTotalUnitsSelectedNowOffers());
    }

    @PostMapping(path = "/{studentId}/reset" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String resetSelectedNowOffers(@PathVariable String studentId , @RequestBody String jsonString , final HttpServletResponse response){
        Student targetStudent = BolBolSystem.getBolBolSystemInstance().findStudentFromId(studentId);
        targetStudent.restoreSelectedNowOffers();
        return "OK";
    }

    @PostMapping(path = "/{studentId}/submit" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String submitSelectedNowOffers(@PathVariable String studentId , @RequestBody String jsonString , final HttpServletResponse response){
        String submitResult = BolBolSystem.getBolBolSystemInstance().submitStudentSelectedNowOffers(studentId);
        if(submitResult.equals("")){
            return "OK";
        }
        return submitResult;
    }
}

