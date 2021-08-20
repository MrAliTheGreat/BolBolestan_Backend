package Controllers;

import BolBolCodes.BolBolSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "Login", value = "/login")
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(Objects.isNull(BolBolSystem.getBolBolSystemInstance().getOnlineStudent())) {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        else{
            response.sendRedirect("/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String studentId = request.getParameter("std_id");
        if(!BolBolSystem.getBolBolSystemInstance().setOnlineStudent(studentId) ){
            request.getRequestDispatcher("404.jsp").forward(request , response);
        }
        else{
            response.sendRedirect("/");
        }
    }
}
