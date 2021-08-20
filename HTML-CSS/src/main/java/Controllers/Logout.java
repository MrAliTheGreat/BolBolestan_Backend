package Controllers;

import BolBolCodes.BolBolSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "Logout", value = "/logout")
public class Logout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BolBolSystem.getBolBolSystemInstance().logoutOnlineStudent();
        response.sendRedirect("/login");
    }
}
