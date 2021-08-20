package Controllers;

import BolBolCodes.BolBolSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "HomePage", value = "/")
public class HomePage extends HttpServlet {
    public void init() throws ServletException{
        BolBolSystem.getBolBolSystemInstance().setAllExternalServerInfo("http://138.197.181.131:5100/api/courses" ,
                                                                        "http://138.197.181.131:5100/api/students" ,
                                                                        "http://138.197.181.131:5100/api/grades/");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(Objects.isNull(BolBolSystem.getBolBolSystemInstance().getOnlineStudent())){
            response.sendRedirect("/login");
        }
        else {
            request.getRequestDispatcher("home.jsp").forward(request, response);
        }
    }

}
