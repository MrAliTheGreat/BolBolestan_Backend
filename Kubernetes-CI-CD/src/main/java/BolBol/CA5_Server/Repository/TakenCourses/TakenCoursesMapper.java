package BolBol.CA5_Server.Repository.TakenCourses;

import BolBol.CA5_Server.Domain.BolBolCodes.Offering;
import BolBol.CA5_Server.Repository.BolBolRepo;
import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Repository.Offering.OfferingMapper;
import BolBol.CA5_Server.Utilities.MultipleStrings;
import BolBol.CA5_Server.Utilities.StringUtils;
import BolBol.CA5_Server.Domain.BolBolCodes.Student.OfferStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TakenCoursesMapper extends Mapper<ArrayList<String>, MultipleStrings> {
    // ArrayList => studentId , code , classCode , status

    private static final String COLUMNS = " studentId, code , classCode , status";
    private static final String TABLE_NAME = "TAKENCOURSES";

    public TakenCoursesMapper() throws SQLException{}

    public TakenCoursesMapper(boolean makeTable) throws SQLException{
        if(makeTable) {
            Connection con = ConnectionPool.getConnection();
            Statement st = con.createStatement();
            st.executeUpdate(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
            st.executeUpdate(String.format(
                    "create table %s (\n" +
                            "  studentId varchar(255) not null,\n" +
                            "  code varchar(255) not null,\n" +
                            "  classCode varchar(255) not null,\n" +
                            "  status varchar(255) default 'NOT_FINALIZED',\n" +
                            "  primary key (studentId , code , classCode),\n" +
                            "  foreign key (studentId) references STUDENTS(studentId),\n" +
                            "  foreign key (code , classCode) references OFFERS(code , classCode)\n" +
                    ")\n" +
                    "CHARACTER SET utf8 COLLATE utf8_general_ci;",
                    TABLE_NAME));
            st.close();
            con.close();
        }
    }


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
        ArrayList<String> info = new ArrayList<>();
        info.add(rs.getString("studentId")); info.add(rs.getString("code")); info.add(rs.getString("classCode"));
        info.add(rs.getString("status"));
        return info;
    }

    private HashMap<Offering , OfferStatus> getDesiredCoursesHashMap(String statement , String funcName) throws SQLException {
        HashMap<Offering , OfferStatus> result = new HashMap<>();
        OfferStatus offerStatus;

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();

                while (resultSet.next()){
                    if(resultSet.getString("status").equals("FINALIZED")){
                        offerStatus = OfferStatus.FINALIZED;
                    }
                    else if(resultSet.getString("status").equals("WAITING")){
                        offerStatus = OfferStatus.WAITING;
                    }
                    else{
                        offerStatus = OfferStatus.NOT_FINALIZED;
                    }
                    result.put(BolBolRepo.getInstance().findOfferFromCodeAndClassCodeDB(resultSet.getString("code") , resultSet.getString("classCode")),
                            offerStatus);
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. " + funcName + " query!");
                throw ex;
            }
        }
    }

    public HashMap<Offering , OfferStatus> getStudentCourses(String studentId) throws SQLException{
        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId));

        return getDesiredCoursesHashMap(statement , "getStudentCourses");
    }

    public HashMap<Offering , OfferStatus> getStudentCoursesFinalizedOrWaiting(String studentId) throws SQLException{
        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s in (%s,%s)", TABLE_NAME, TABLE_NAME, "studentId" ,
                                         StringUtils.quoteWrapper(studentId), TABLE_NAME , "status" , StringUtils.quoteWrapper("FINALIZED") ,
                                         StringUtils.quoteWrapper("WAITING") );

        return getDesiredCoursesHashMap(statement , "getStudentCoursesFinalizedOrWaiting");
    }

    public void clearStudentCourses(String studentId) throws SQLException{
        String statement = String.format("DELETE FROM %s WHERE %s.%s = %s", TABLE_NAME, TABLE_NAME, "studentId", studentId);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement)
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. clearStudentCourses query!");
                throw ex;
            }
        }
    }

    public ArrayList<String> getWillBeFinalizedOffers(String studentId , String code , String classCode) throws SQLException{
        ArrayList<String> result = new ArrayList<String>();

        String statement = String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s = %s and %s.%s = %s and %s.%s = %s", TABLE_NAME,
                                         TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId),
                                         TABLE_NAME, "code", StringUtils.quoteWrapper(code),
                                         TABLE_NAME, "classCode", StringUtils.quoteWrapper(classCode),
                                         TABLE_NAME, "status", StringUtils.quoteWrapper("WAITING"));

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
                System.out.println("Error in Mapper. getWillBeFinalizedOffers query!");
                throw ex;
            }
        }
    }

    public void changeWaitingOfferToFinalized(String studentId , String code , String classCode) throws SQLException{
        String statement = String.format("UPDATE %s SET status = %s WHERE %s.%s = %s and %s.%s = %s and %s.%s = %s and %s.%s = %s", TABLE_NAME , StringUtils.quoteWrapper("FINALIZED"),
                TABLE_NAME, "studentId", StringUtils.quoteWrapper(studentId), TABLE_NAME ,"code", StringUtils.quoteWrapper(code),
                TABLE_NAME ,"classCode", StringUtils.quoteWrapper(classCode), TABLE_NAME ,"status", StringUtils.quoteWrapper("WAITING"));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement)
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. changeWaitingOfferToFinalized query!");
                throw ex;
            }
        }

    }
}
