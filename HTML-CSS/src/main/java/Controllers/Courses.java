package Controllers;

import BolBolCodes.BolBolSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "Courses", value = "/courses")
public class Courses extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(Objects.isNull(BolBolSystem.getBolBolSystemInstance().getOnlineStudent())){
            response.sendRedirect("/login");
        }
        else{
            BolBolSystem.getBolBolSystemInstance().getOnlineStudent().restoreSelectedNowOffers();
            BolBolCodes.BolBolSystem.getBolBolSystemInstance().setSearchOffers(BolBolSystem.getBolBolSystemInstance().getSearchName());
            request.getRequestDispatcher("courses.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String onlineStudentId = BolBolCodes.BolBolSystem.getBolBolSystemInstance().getOnlineStudent().getStudentId();

        // Button Action
        String action = request.getParameter("action");

        if(action.equals("remove")){
            BolBolCodes.BolBolSystem.getBolBolSystemInstance().removeFromStudentSelectedNowOffers(onlineStudentId ,
                                                                                                  request.getParameter("course_code") ,
                                                                                                  request.getParameter("class_code"));
            request.getRequestDispatcher("courses.jsp").forward(request, response);
        }
        else if(action.equals("submit")){
            String finalizeResult = BolBolCodes.BolBolSystem.getBolBolSystemInstance().submitStudentSelectedNowOffers(onlineStudentId);
            if(finalizeResult.equals("")){
                response.sendRedirect("/plan");
            }
            else{
                BolBolSystem.getBolBolSystemInstance().getOnlineStudent().restoreSelectedNowOffers();
                request.setAttribute("FinalizeResult" , finalizeResult);
                request.getRequestDispatcher("submit_failed.jsp").forward(request , response);
            }
        }
        else if(action.equals("reset")){
            BolBolSystem.getBolBolSystemInstance().getOnlineStudent().restoreSelectedNowOffers();
            request.getRequestDispatcher("courses.jsp").forward(request, response);
        }
        else if(action.equals("search")){
            BolBolCodes.BolBolSystem.getBolBolSystemInstance().setSearchOffers(request.getParameter("search"));
            request.getRequestDispatcher("courses.jsp").forward(request, response);
        }
        else if(action.equals("clear")){
            BolBolCodes.BolBolSystem.getBolBolSystemInstance().setSearchOffers("");
            request.getRequestDispatcher("courses.jsp").forward(request, response);
        }
        else if(action.equals("add")){
            String addResult = BolBolSystem.getBolBolSystemInstance().addToStudentSelectedNowOffers(onlineStudentId , request.getParameter("course_code") ,
                                                                                                    request.getParameter("class_code"));
            request.setAttribute("AddResult" , addResult);
            request.getRequestDispatcher("courses.jsp").forward(request, response);
        }
    }
}
