package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Repository.BolBolRepo;
import BolBol.CA5_Server.Utilities.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

@RestController
@RequestMapping("/changePassword")
public class ChangePasswordController {

    @PostMapping(path = "" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String changeToNewPassword(@RequestBody String jsonString, final HttpServletResponse response) throws IOException {
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        if(StringUtils.hasIllegalChars((String) receivedJSON.get("newPassword")) ){
            System.out.println("Error: Illegal Characters Found!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Error: Illegal Characters Found!";
        }
        try{
            String studentId = getStudentIdFromKeyJWT("changeToNewPassword");
            if(Objects.isNull(studentId)){
                System.out.println("Problem in getting studentId from JWT");
                return "Can't get studentId from JWT!";
            }
            BolBolRepo.getInstance().changeToNewPasswordByStudentIdDB(StringUtils.hashPassword((String) receivedJSON.get("newPassword")),
                                                                      studentId);
            return "OK";
        }catch (SQLException e){
            System.out.println("Error in changeToNewPassword SQL Part: " + e.getMessage());
            return "Error in changeToNewPassword SQL Part";
        }
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
