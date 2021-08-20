package BolBol.CA5_Server.Repository.Offering;

import BolBol.CA5_Server.Domain.Scheduler.WaitingList;
import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Utilities.MultipleStrings;
import BolBol.CA5_Server.Utilities.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WaitingListMapper extends Mapper<ArrayList<String> , MultipleStrings> {
    // ArrayList => code , classCode , studentId

    private static final String COLUMNS = " code , classCode , studentId";
    private static final String TABLE_NAME = "WAITINGLIST";

    public WaitingListMapper() throws SQLException{}

    public WaitingListMapper(boolean makeTable) throws SQLException{
        Connection con = ConnectionPool.getConnection();
        Statement st = con.createStatement();
        st.executeUpdate(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
        st.executeUpdate(String.format(
                "create table %s (\n" +
                        "  code varchar(255) not null,\n" +
                        "  classCode varchar(255) not null,\n" +
                        "  studentId varchar(255) not null,\n" +
                        "  primary key (code , classCode , studentId),\n" +
                        "  foreign key (studentId) references STUDENTS(studentId),\n" +
                        "  foreign key (code , classCode) references OFFERS(code , classCode)\n" +
                ")\n" +
                "CHARACTER SET utf8 COLLATE utf8_general_ci;",
                TABLE_NAME));
        st.close();
        con.close();
    }


    @Override
    protected String getInsertStatement(ArrayList<String> strings) {
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s, %s);", TABLE_NAME, COLUMNS,
                StringUtils.quoteWrapper(strings.get(0)), StringUtils.quoteWrapper(strings.get(1)),
                StringUtils.quoteWrapper(strings.get(2)));
    }

    @Override
    protected String getFindStatement(MultipleStrings id) {
        return String.format("SELECT * FROM %s WHERE %s.%s = %s and %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "code",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "classCode", StringUtils.quoteWrapper(id.getStrings().get(1)),
                TABLE_NAME, "studentId", StringUtils.quoteWrapper(id.getStrings().get(2)));
    }

    @Override
    protected String getDeleteStatement(MultipleStrings id) {
        return String.format("DELETE FROM %s WHERE %s.%s = %s and %s.%s = %s and %s.%s = %s", TABLE_NAME, TABLE_NAME, "code",
                StringUtils.quoteWrapper(id.getStrings().get(0)), TABLE_NAME, "classCode", StringUtils.quoteWrapper(id.getStrings().get(1)),
                TABLE_NAME, "studentId", StringUtils.quoteWrapper(id.getStrings().get(2)));
    }

    @Override
    protected ArrayList<String> convertResultSetToObject(ResultSet rs) throws SQLException {
        ArrayList<String> info = new ArrayList<>();
        info.add(rs.getString("code")); info.add(rs.getString("classCode")); info.add(rs.getString("studentId"));
        return info;
    }

    public ArrayList<MultipleStrings> getWaitingListOffers() throws SQLException {
        ArrayList<MultipleStrings> result = new ArrayList<>();
        MultipleStrings multipleStrings;
        List<String> info; // code, classCode, studentId

        String statement = String.format("SELECT * FROM %s" , TABLE_NAME);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    info = new ArrayList<>();
                    info.add(resultSet.getString("code"));
                    info.add(resultSet.getString("classCode"));
                    info.add(resultSet.getString("studentId"));
                    multipleStrings = new MultipleStrings(info);
                    result.add(multipleStrings);
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getWaitingListOffers query!");
                throw ex;
            }
        }
    }
}
