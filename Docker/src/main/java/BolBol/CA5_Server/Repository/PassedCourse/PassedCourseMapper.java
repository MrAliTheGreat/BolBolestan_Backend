package BolBol.CA5_Server.Repository.PassedCourse;

import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.PassedCourse;
import BolBol.CA5_Server.Repository.BolBolRepo;
import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Utilities.MultipleStrings;
import BolBol.CA5_Server.Utilities.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassedCourseMapper extends Mapper<PassedCourse , MultipleStrings> {
    // MultipleStrings => studentId, code

    private static final String COLUMNS = " studentId, code , grade , term";
    private static final String TABLE_NAME = "GRADES";

    private String studentId;

    public PassedCourseMapper(String studentId){
        this.studentId = studentId;
    }

    @Override
    protected String getInsertStatement(PassedCourse passedCourse) {
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s, %d, %d);", TABLE_NAME, COLUMNS,
                             StringUtils.quoteWrapper(this.studentId) , StringUtils.quoteWrapper(passedCourse.getCode()) ,
                             passedCourse.getGrade() , passedCourse.getTerm());
    }

    @Override
    protected String getFindStatement(MultipleStrings id) {
        return String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "code", StringUtils.quoteWrapper(id.getStrings().get(1)) );
    }

    @Override
    protected String getDeleteStatement(MultipleStrings id) {
        return String.format("DELETE FROM %s WHERE %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "code", StringUtils.quoteWrapper(id.getStrings().get(1)) );
    }

    @Override
    protected PassedCourse convertResultSetToObject(ResultSet rs) throws SQLException {
        return new PassedCourse(rs.getString("code"), rs.getInt("grade") ,
                                BolBolRepo.getInstance().getOfferUnitsFromCodeDB(rs.getString("code")), rs.getInt("term"));
    }

    public ArrayList<PassedCourse> getStudentPassedCourses(String studentId) throws SQLException{
        ArrayList<PassedCourse> passedCourses = new ArrayList<>();

        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    passedCourses.add(this.convertResultSetToObject(resultSet));
                }
                con.close();
                return passedCourses;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getStudentPassedCourses query!");
                throw ex;
            }
        }
    }

    public ArrayList<PassedCourse> getStudentPassedCoursesNotFailedFromCode(String studentId , String code) throws SQLException{
        ArrayList<PassedCourse> passedCourses = new ArrayList<>();

        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s = %s and %s.%s >= 10", TABLE_NAME,
                                                                                      TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId),
                                                                                      TABLE_NAME , "code" , StringUtils.quoteWrapper(code),
                                                                                      TABLE_NAME , "grade");

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    passedCourses.add(this.convertResultSetToObject(resultSet));
                }
                con.close();
                return passedCourses;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getStudentPassedCourseFromCode query!");
                throw ex;
            }
        }
    }

    public int getLastTermNumberStudent(String studentId) throws SQLException{

        String statement = String.format("SELECT MAX(%s) FROM %s WHERE %s.%s = %s", "term" , TABLE_NAME, TABLE_NAME, "studentId",
                                         StringUtils.quoteWrapper(studentId));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                resultSet.next();
                return resultSet.getInt("MAX(term)");
            } catch (SQLException e) {
                System.out.println("Error in Mapper. getLastTermNumberStudent query!");
                throw e;
            }
        }
    }

}
