import ServerCodes.ServerInterface;

import java.util.Scanner;

public class main {
    public static void main(String args[]){
        String coursesAddress = "http://138.197.181.131:5000/api/courses";
        String studentsAddress = "http://138.197.181.131:5000/api/students";
        String gradesAddressBASE = "http://138.197.181.131:5000/api/grades/";
        // Student ID must be appended to give the grades for each student

        int infoServerPort = 5000;

        ServerInterface serverInterface = new ServerInterface();
        serverInterface.start(coursesAddress , studentsAddress , gradesAddressBASE , infoServerPort);
    }
}