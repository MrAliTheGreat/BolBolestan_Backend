package BolBol.CA5_Server.Repository.SelectedNow;

import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.BolBolRepo;
import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Utilities.MultipleStrings;
import BolBol.CA5_Server.Utilities.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectedNowMapper extends Mapper<ArrayList<String>, MultipleStrings> {
    // ArrayList => studentId , code , classCode , status

    private static final String COLUMNS = " studentId, code , classCode , status";
    private static final String TABLE_NAME = "SELECTEDNOW";

    @Override
    protected String getInsertStatement(ArrayList<String> strings) {
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s, %s , %s);", TABLE_NAME, COLUMNS,
                StringUtils.quoteWrapper(strings.get(0)), StringUtils.quoteWrapper(strings.get(1)),
                StringUtils.quoteWrapper(strings.get(2)), StringUtils.quoteWrapper(strings.get(3)));
    }

    @Override
    protected String getFindStatement(MultipleStrings id) {
        return String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "code", StringUtils.quoteWrapper(id.getStrings().get(1)),
                TABLE_NAME, "classCode", StringUtils.quoteWrapper(id.getStrings().get(2)));
    }

    @Override
    protected String getDeleteStatement(MultipleStrings id) {
        return String.format("DELETE FROM %s WHERE %s.%s = %s and %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "code", StringUtils.quoteWrapper(id.getStrings().get(1)),
                TABLE_NAME, "classCode", StringUtils.quoteWrapper(id.getStrings().get(2)));
    }

    @Override
    protected ArrayList<String> convertResultSetToObject(ResultSet rs) throws SQLException {
        ArrayList<String> info = new ArrayList<String>();
        info.add(rs.getString("studentId")); info.add(rs.getString("code")); info.add(rs.getString("classCode"));
        info.add(rs.getString("status"));
        return info;
    }

    public ArrayList<String> getStudentSelectedNowOffers(String studentId) throws SQLException {
        ArrayList<String> result = new ArrayList<String>();

        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.add(resultSet.getString("code") + "-" + resultSet.getString("classCode"));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getStudentSelectedNowOffers query!");
                throw ex;
            }
        }
    }

    public void clearStudentSelectedNowOffers(String studentId) throws SQLException{
        String statement = String.format("DELETE FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement)
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. clearStudentSelectedNowOffers query!");
                throw ex;
            }
        }
    }

    public boolean checkSelectedNowOffersCapacity(String studentId) throws SQLException {
        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME,
                                                                                             "studentId", StringUtils.quoteWrapper(studentId),
                                                                                             TABLE_NAME , "status" ,
                                                                                             StringUtils.quoteWrapper("NOT_FINALIZED"));
        Offering offering;
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    offering = BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(resultSet.getString("code"),
                                                                                        resultSet.getString("classCode"));
                    if(offering.getCapacity() <= 0){
                        return false;
                    }
                }
                con.close();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. checkSelectedNowOffersCapacity query!");
                throw ex;
            }
        }
        return true;
    }

    public HashMap<Offering , Student.OfferStatus> getStudentSelectedNowHashMapFormat(String studentId) throws SQLException {
        HashMap<Offering , Student.OfferStatus> result = new HashMap<>();

        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s" , TABLE_NAME , TABLE_NAME , "studentId" , StringUtils.quoteWrapper(studentId));

        Offering offering;
        Student.OfferStatus offerStatus;

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    offering = BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(resultSet.getString("code") , resultSet.getString("classCode"));
                    if(resultSet.getString("status").equals("FINALIZED")){
                        offerStatus = Student.OfferStatus.FINALIZED;
                    }
                    else if(resultSet.getString("status").equals("WAITING")){
                        offerStatus = Student.OfferStatus.WAITING;
                    }
                    else{
                        offerStatus = Student.OfferStatus.NOT_FINALIZED;
                    }
                    result.put(offering , offerStatus);
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getOffersByName query!");
                throw ex;
            }
        }
    }

    public HashMap<String , String> getStudentSelectedNowStatusHashMapFormat(String studentId) throws SQLException {
        HashMap<String , String> result = new HashMap<>();

        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s" , TABLE_NAME , TABLE_NAME , "studentId" , StringUtils.quoteWrapper(studentId));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.put(resultSet.getString("code") , resultSet.getString("status"));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getStudentSelectedNowStatusHashMapFormat query!");
                throw ex;
            }
        }
    }

}
