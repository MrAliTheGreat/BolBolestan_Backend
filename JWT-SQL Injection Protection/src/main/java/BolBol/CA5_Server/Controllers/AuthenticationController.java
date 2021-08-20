package BolBol.CA5_Server.Controllers;

import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.BolBolRepo;
import BolBol.CA5_Server.Security.FilterJWT;
import BolBol.CA5_Server.Utilities.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @PostMapping(path = "/signup" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String signup(@RequestBody String jsonString, final HttpServletResponse response) {
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        try{
            BolBolRepo.getInstance().findStudentFromIdDB((String) receivedJSON.get("studentId"));
            System.out.println("User Already Exists: Duplicate StudentId!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "User Already Exists: Duplicate StudentId!";
        }catch (SQLException e){
            try {
                BolBolRepo.getInstance().findStudentFromEmailDB((String) receivedJSON.get("signUpEmail"));
                System.out.println("User Already Exists: Duplicate Email!");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "User Already Exists: Duplicate Email!";
            }catch (SQLException err){
                if(StringUtils.hasIllegalChars((String) receivedJSON.get("studentId")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("name")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("secondName")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("birthDate")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("field")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("faculty")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("level")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("signUpEmail")) ||
                   StringUtils.hasIllegalChars((String) receivedJSON.get("signUpPassword"))){

                    System.out.println("Error: Illegal Characters Found!");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return "Error: Illegal Characters Found!";
                }

                Student signingUpStudent = new Student((String) receivedJSON.get("studentId") , (String) receivedJSON.get("name") ,
                        (String) receivedJSON.get("secondName"), (String) receivedJSON.get("birthDate") ,
                        (String) receivedJSON.get("field") , (String) receivedJSON.get("faculty") ,
                        (String) receivedJSON.get("level") , "مشغول به تحصیل" ,
                        "http://138.197.181.131:5200/img/mike_wazowski.jpg" , (String) receivedJSON.get("signUpEmail") ,
                        StringUtils.hashPassword((String) receivedJSON.get("signUpPassword")) );

                try{
                    BolBolRepo.getInstance().insertStudentDB(signingUpStudent);
                    System.out.println("Student " + signingUpStudent.getStudentId() + " was inserted into students DB!");
                }catch(SQLException error){}

                return FilterJWT.getTokenJWT(signingUpStudent.getEmail());
            }
        }
    }

    @PostMapping(path = "/login" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@RequestBody String jsonString, final HttpServletResponse response){
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        if(StringUtils.hasIllegalChars((String) receivedJSON.get("signInEmail")) ||
           StringUtils.hasIllegalChars((String) receivedJSON.get("signInPassword")) ){

            System.out.println("Error: Illegal Characters Found!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Error: Illegal Characters Found!";
        }
        try{
            Student foundStudent =  BolBolRepo.getInstance().findStudentFromEmailDB((String) receivedJSON.get("signInEmail"));
            if(foundStudent.getPassword().equals(StringUtils.hashPassword((String) receivedJSON.get("signInPassword")) ) ){
                return FilterJWT.getTokenJWT(foundStudent.getEmail());
            }
            else {
                System.out.println("Incorrect Password!");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "Incorrect Password!";
            }
        }catch (SQLException e){
            System.out.println("User Not Found!");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "User Not Found!";
        }
    }

    @PostMapping(path = "/resetPassword" , consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public String resetPassword(@RequestBody String jsonString, final HttpServletResponse response) {
        JSONObject receivedJSON = (JSONObject) JSONValue.parse(jsonString);
        if(StringUtils.hasIllegalChars((String) receivedJSON.get("forgetPasswordEmail")) ){

            System.out.println("Error: Illegal Characters Found!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Error: Illegal Characters Found!";
        }

        try{
            Student targetStudent = BolBolRepo.getInstance().findStudentFromEmailDB((String) receivedJSON.get("forgetPasswordEmail"));
            String url = "http://localhost:3000/changePassword/" + FilterJWT.getTokenJWT(targetStudent.getEmail()).substring(7);
            String sendEmailResponse = this.sendEmail(targetStudent.getEmail() , url);
            if(sendEmailResponse.equals("null")){
                return "OK";
            }
            return "Possible Error in resetPassword!";
        }catch (SQLException e){
            System.out.println("User With This Email Not Found!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "User With This Email Not Found!";
        }
    }

    private String sendEmail(String email , String url){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email" , email);
        jsonObject.put("url" , url);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://138.197.181.131:5200/api/send_mail");

        try{
            StringEntity params = new StringEntity(jsonObject.toJSONString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            httpclient.close();
            if (entity != null) {
                return EntityUtils.toString(response.getEntity());
            }
            else{
                return "";
            }
        }catch (IOException e){
            System.out.println("Error in sendEmail: " + e.getMessage());
            try {
                httpclient.close();
            }catch (IOException err){}
            return "";
        }
    }
}

