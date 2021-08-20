package BolBol.CA5_Server.Repository.Offering;

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

public class WaitingListMapper extends Mapper<ArrayList<String> , MultipleStrings> {
    // ArrayList => code , classCode , studentId

    private static final String COLUMNS = " code , classCode , studentId";
    private static final String TABLE_NAME = "WAITINGLIST";

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
