package Controllers;

import BolBolCodes.BolBolSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "Plan", value = "/plan")
public class Plan extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(Objects.isNull(BolBolSystem.getBolBolSystemInstance().getOnlineStudent())){
            response.sendRedirect("/login");
        }
        else{
            request.getRequestDispatcher("plan.jsp").forward(request , response);
        }
    }
}
