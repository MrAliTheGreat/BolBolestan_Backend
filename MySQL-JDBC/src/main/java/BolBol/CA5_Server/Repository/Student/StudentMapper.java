package BolBol.CA5_Server.Repository.Student;

import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Utilities.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentMapper extends Mapper<Student , String> {

    private static final String COLUMNS = " studentId, name, secondName, birthDate, field, faculty, level, status, img ";
    private static final String TABLE_NAME = "STUDENTS";

    @Override
    protected String getInsertStatement(Student student) {
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s);", TABLE_NAME, COLUMNS,
               StringUtils.quoteWrapper(student.getStudentId()), StringUtils.quoteWrapper(student.getName()),
               StringUtils.quoteWrapper(student.getSecondName()) , StringUtils.quoteWrapper(student.getBirthDate()),
               StringUtils.quoteWrapper(student.getField()) , StringUtils.quoteWrapper(student.getFaculty()),
               StringUtils.quoteWrapper(student.getLevel()) , StringUtils.quoteWrapper(student.getStatus()),
               StringUtils.quoteWrapper(student.getImgAddress()) );
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
                           rs.getString("level") , rs.getString("status") , rs.getString("img"));
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
}
