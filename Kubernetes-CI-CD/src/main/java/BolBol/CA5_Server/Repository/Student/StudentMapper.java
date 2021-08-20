package BolBol.CA5_Server.Repository.Student;

import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Utilities.StringUtils;

import java.sql.*;
import java.util.ArrayList;

public class StudentMapper extends Mapper<Student , String> {

    private static final String COLUMNS = " studentId, name, secondName, birthDate, field, faculty, level, status, img , email , password ";
    private static final String TABLE_NAME = "STUDENTS";

    public StudentMapper() throws SQLException{}

    public StudentMapper(boolean makeTable) throws SQLException{
        if(makeTable) {
            Connection con = ConnectionPool.getConnection();
            Statement st = con.createStatement();
            st.executeUpdate(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
            st.executeUpdate(String.format(
                    "create table %s (\n" +
                            "  studentId varchar(255) primary key,\n" +
                            "  name tinytext not null,\n" +
                            "  secondName tinytext not null,\n" +
                            "  birthDate tinytext not null,\n" +
                            "  field tinytext not null,\n" +
                            "  faculty tinytext not null,\n" +
                            "  level tinytext not null,\n" +
                            "  status tinytext not null,\n" +
                            "  img text not null,\n" +
                            "  email text not null,\n" +
                            "  password tinytext not null\n" +
                    ")\n" +
                    "CHARACTER SET utf8 COLLATE utf8_general_ci;",
            TABLE_NAME));
            st.close();
            con.close();
        }
    }

    @Override
    protected String getInsertStatement(Student student) {
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s , %s , %s);", TABLE_NAME, COLUMNS,
               StringUtils.quoteWrapper(student.getStudentId()), StringUtils.quoteWrapper(student.getName()),
               StringUtils.quoteWrapper(student.getSecondName()) , StringUtils.quoteWrapper(student.getBirthDate()),
               StringUtils.quoteWrapper(student.getField()) , StringUtils.quoteWrapper(student.getFaculty()),
               StringUtils.quoteWrapper(student.getLevel()) , StringUtils.quoteWrapper(student.getStatus()),
               StringUtils.quoteWrapper(student.getImgAddress()) , StringUtils.quoteWrapper(student.getEmail()),
               StringUtils.quoteWrapper(student.getPassword()) );
    }

    @Override
    protected String getFindStatement(String studentId) {
        return String.format("SELECT * FROM %s WHERE %s.%s = %s;", TABLE_NAME, TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId));
    }

    @Override
    protected String getDeleteStatement(String studentId) {
        return String.format("DELETE FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId));
    }

    @Override
    protected Student convertResultSetToObject(ResultSet rs) throws SQLException {
        return new Student(rs.getString("studentId") , rs.getString("name") , rs.getString("secondName") ,
                           rs.getString("birthDate") , rs.getString("field") , rs.getString("faculty") ,
                           rs.getString("level") , rs.getString("status") , rs.getString("img") ,
                           rs.getString("email") , rs.getString("password"));
    }

    public ArrayList<String> getAllStudentIDs() throws SQLException{
        ArrayList<String> result = new ArrayList<String>();

        String statement = String.format("SELECT studentId FROM %s", TABLE_NAME);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.add(resultSet.getString("studentId"));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getAllStudentIDs query!");
                throw ex;
            }
        }
    }

    public Student findStudentFromEmail(String email) throws SQLException{
        String statement = "SELECT * FROM " + TABLE_NAME + " WHERE " + TABLE_NAME + ".email = ?";
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(statement);
        st.setString(1 , email);
        ResultSet resultSet;
        try {
            resultSet = st.executeQuery();
            resultSet.next();
            Student foundStudent = this.convertResultSetToObject(resultSet);
            con.close();
            return foundStudent;
        } catch (SQLException e) {
            System.out.println("Concern in findStudentFromEmail: " + e.getMessage());
            con.close();
            st.close();
            throw e;
        }
    }

    public void changeToNewPasswordByStudentId(String newPassword , String studentId) throws SQLException {
        String statement = "UPDATE " + TABLE_NAME + " SET password = ? WHERE " + TABLE_NAME + ".studentId = ?";
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(statement);
        st.setString(1 , newPassword);
        st.setString(2 , studentId);
        try {
            st.executeUpdate();
            con.close();
            st.close();
        } catch (SQLException ex) {
            System.out.println("Error in Mapper. changeToNewPasswordByStudentId query!");
            con.close();
            st.close();
            throw ex;
        }
    }
}
