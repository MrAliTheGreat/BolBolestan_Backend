package BolBol.CA5_Server.Repository.Offering;

import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
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
import java.util.HashMap;
import java.util.List;

public class OfferingMapper extends Mapper<Offering , MultipleStrings> {

    private static final String COLUMNS = " code, classCode, name, units, type, instructor, capacity , numSignedUp , classTime, examTimeStart , examTimeEnd ";
    private static final String TABLE_NAME = "OFFERS";

    @Override
    protected String getInsertStatement(Offering offering) {
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s, %s, %d, %s, %s, %d, %d, %s, %s, %s);", TABLE_NAME, COLUMNS,
                StringUtils.quoteWrapper(offering.getCode()) , StringUtils.quoteWrapper(offering.getClassCode()) ,
                StringUtils.quoteWrapper(offering.getName()) , offering.getUnits(), StringUtils.quoteWrapper(offering.getType()) ,
                StringUtils.quoteWrapper(offering.getInstructor()) , offering.getCapacity() , offering.getNumSignedUp() ,
                StringUtils.quoteWrapper(offering.getTime()) , StringUtils.quoteWrapper(offering.getExamTimeStart()) ,
                StringUtils.quoteWrapper(offering.getExamTimeEnd()));
    }

    @Override
    protected String getFindStatement(MultipleStrings id) {
        return String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "code",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "classCode", StringUtils.quoteWrapper(id.getStrings().get(1)) );
    }

    @Override
    protected String getDeleteStatement(MultipleStrings id) {
        return String.format("DELETE FROM %s WHERE %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "code",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "classCode", StringUtils.quoteWrapper(id.getStrings().get(1)) );
    }

    @Override
    protected Offering convertResultSetToObject(ResultSet rs) throws SQLException {
        return new Offering(rs.getString("code"), rs.getString("name") , rs.getString("instructor"),
                            rs.getInt("units"),
                            BolBolRepo.getInstance().getOfferClassDaysDB(rs.getString("code") , rs.getString("classCode")),
                            rs.getString("classTime") , rs.getString("examTimeStart") , rs.getString("examTimeEnd"),
                            rs.getInt("capacity") , rs.getInt("numSignedUp"),
                            BolBolRepo.getInstance().getOfferPrerequisitesDB(rs.getString("code")),
                            rs.getString("classCode") , rs.getString("type"));
    }


    public boolean checkOfferExistenceFromCode(String code) throws SQLException {
        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "code", code);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement)
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                resultSet.next();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public int getOfferUnitsFromCode(String code) throws SQLException{
        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "code", code);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement)
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                resultSet.next();
                return resultSet.getInt("units");
            } catch (SQLException e) {
                System.out.println("getOfferUnitsFromCode: Offer with code " + code + " wasn't found!");
                return -1;
            }
        }
    }

    public String findOfferNameByCode(String code) throws SQLException{
        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "code", code);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement)
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                resultSet.next();
                return resultSet.getString("name");
            } catch (SQLException e) {
                System.out.println("findOfferNameByCode Offer with code " + code + " wasn't found!");
                return "";
            }
        }
    }

    public void decrementCourseCapacity(String code , String classCode) throws SQLException{
        String statement = String.format("UPDATE %s SET capacity = capacity - 1 WHERE %s.%s = %s and %s.%s = %s;", TABLE_NAME,
                                TABLE_NAME, "code", StringUtils.quoteWrapper(code), TABLE_NAME ,"classCode", StringUtils.quoteWrapper(classCode));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. decrementCourseCapacity query!");
                throw ex;
            }
        }
    }

    public void incrementCourseCapacity(String code , String classCode) throws SQLException{
        String statement = String.format("UPDATE %s SET capacity = capacity + 1 WHERE %s.%s = %s and %s.%s = %s;", TABLE_NAME,
                TABLE_NAME, "code", StringUtils.quoteWrapper(code), TABLE_NAME ,"classCode", StringUtils.quoteWrapper(classCode));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. incrementCourseCapacity query!");
                throw ex;
            }
        }
    }

    public void decrementNumSignedUp(String code , String classCode) throws SQLException{
        String statement = String.format("UPDATE %s SET numSignedUp = numSignedUp - 1 WHERE %s.%s = %s and %s.%s = %s;", TABLE_NAME,
                TABLE_NAME, "code", StringUtils.quoteWrapper(code), TABLE_NAME ,"classCode", StringUtils.quoteWrapper(classCode));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. decrementNumSignedUp query!");
                throw ex;
            }
        }
    }

    public void incrementNumSignedUp(String code , String classCode) throws SQLException{
        String statement = String.format("UPDATE %s SET numSignedUp = numSignedUp + 1 WHERE %s.%s = %s and %s.%s = %s;", TABLE_NAME,
                TABLE_NAME, "code", StringUtils.quoteWrapper(code), TABLE_NAME ,"classCode", StringUtils.quoteWrapper(classCode));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. incrementNumSignedUp query!");
                throw ex;
            }
        }
    }

    public ArrayList<Offering> getSearchedOffers(String name) throws SQLException {
        ArrayList<Offering> result = new ArrayList<>();

        String statement = String.format("SELECT * FROM %s WHERE name LIKE %s" , TABLE_NAME , StringUtils.quoteWrapper("%" + name + "%"));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.add(this.convertResultSetToObject(resultSet));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getSearchedOffers query!");
                throw ex;
            }
        }
    }

    public HashMap<String , String> getOffersByName() throws SQLException {
        HashMap<String , String> result = new HashMap<>();

        String statement = String.format("SELECT code, name FROM %s" , TABLE_NAME);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.put(resultSet.getString("code") , resultSet.getString("name"));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getOffersByName query!");
                throw ex;
            }
        }
    }

    public ArrayList<Offering> getAllOffers() throws SQLException {
        ArrayList<Offering> result = new ArrayList<>();

        String statement = String.format("SELECT * FROM %s" , TABLE_NAME);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.add(this.convertResultSetToObject(resultSet));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getAllOffers query!");
                throw ex;
            }
        }
    }

    public ArrayList<Offering> getAllOffersByType(String type) throws SQLException {
        ArrayList<Offering> result = new ArrayList<>();

        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s" , TABLE_NAME , TABLE_NAME , "type" , StringUtils.quoteWrapper(type));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.add(this.convertResultSetToObject(resultSet));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getAllOffersByType query!");
                throw ex;
            }
        }
    }
}