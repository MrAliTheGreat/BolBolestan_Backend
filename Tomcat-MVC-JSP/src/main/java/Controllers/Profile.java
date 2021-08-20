package Controllers;

import BolBolCodes.BolBolSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "Profile", value = "/profile")
public class Profile extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(Objects.isNull(BolBolSystem.getBolBolSystemInstance().getOnlineStudent())){
            response.sendRedirect("/login");
        }
        else{
            request.getRequestDispatcher("profile.jsp").forward(request , response);
        }
    }
}
