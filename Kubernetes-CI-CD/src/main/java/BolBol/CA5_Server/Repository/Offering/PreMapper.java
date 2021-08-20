package BolBol.CA5_Server.Repository.Offering;

import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Utilities.MultipleStrings;
import BolBol.CA5_Server.Utilities.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreMapper extends Mapper<ArrayList<String>, String> {
    // First ArrayList => code, prerequisitesCode
    // Second String => code

    private static final String COLUMNS = " code, preCode";
    private static final String TABLE_NAME = "PREREQUISITES";

    public PreMapper() throws SQLException{}

    public PreMapper(boolean makeTable) throws SQLException{
        if(makeTable) {
            Connection con = ConnectionPool.getConnection();
            Statement st = con.createStatement();
            st.executeUpdate(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
            st.executeUpdate(String.format(
                    "create table %s (\n" +
                            "  code varchar(255) not null,\n" +
                            "  preCode varchar(255) not null,\n" +
                            "  primary key (code , preCode)\n" +
                    ")\n" +
                    "CHARACTER SET utf8 COLLATE utf8_general_ci;",
                    TABLE_NAME));
            st.close();
            con.close();
        }
    }

    @Override
    protected String getInsertStatement(ArrayList<String> strings) {
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s);", TABLE_NAME, COLUMNS,
                StringUtils.quoteWrapper(strings.get(0)), StringUtils.quoteWrapper(strings.get(1)));
    }

    @Override
    protected String getFindStatement(String id) {
        return String.format("SELECT * FROM %s WHERE %s.%s = %s;", TABLE_NAME, TABLE_NAME, "code", id);
    }

    @Override
    protected String getDeleteStatement(String id) {
        return String.format("DELETE FROM %s WHERE %s.%s = %s;", TABLE_NAME, TABLE_NAME, "code", id);
    }

    @Override
    protected ArrayList<String> convertResultSetToObject(ResultSet rs) throws SQLException {
        ArrayList<String> info = new ArrayList<>();
        info.add(rs.getString("code")); info.add(rs.getString("preCode"));
        return info;
    }

    public ArrayList<String> getOfferPrerequisites(String code) throws SQLException {
        ArrayList<String> result = new ArrayList<String>();

        String statement = this.getFindStatement(code);

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.add(this.convertResultSetToObject(resultSet).get(1));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getOfferPrerequisites query!");
                throw ex;
            }
        }
    }
}
