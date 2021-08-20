package BolBol.CA5_Server.Repository.Offering;

import BolBol.CA5_Server.Domain.BolBolCodes.Student;
import BolBol.CA5_Server.Repository.ConnectionPool;
import BolBol.CA5_Server.Repository.Mapper;
import BolBol.CA5_Server.Utilities.MultipleStrings;
import BolBol.CA5_Server.Utilities.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaysMapper extends Mapper<ArrayList<String> , MultipleStrings> {
    // First ArrayList => code, classCode, day
    // Second MultipleStrings => code, classCode

    private static final String COLUMNS = " code, classCode, day";
    private static final String TABLE_NAME = "OFFERDAYS";

    public DaysMapper() throws SQLException {}

    public DaysMapper(Boolean makeTable) throws SQLException {
        if(makeTable) {
            Connection con = ConnectionPool.getConnection();
            Statement st = con.createStatement();
            st.executeUpdate(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
            st.executeUpdate(String.format(
                    "create table %s (\n" +
                            "  code varchar(255) not null,\n" +
                            "  classCode varchar(255) not null,\n" +
                            "  day varchar(255) not null,\n" +
                            "  primary key (code , classCode , day),\n" +
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
        return String.format("INSERT INTO %s( %s ) VALUES(%s, %s, %s);", TABLE_NAME, COLUMNS,
                StringUtils.quoteWrapper(strings.get(0)), StringUtils.quoteWrapper(strings.get(1)),
                StringUtils.quoteWrapper(strings.get(2)) );
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
    protected ArrayList<String> convertResultSetToObject(ResultSet rs) throws SQLException {
        ArrayList<String> info = new ArrayList<>();
        info.add(rs.getString("code")); info.add(rs.getString("classCode")); info.add(rs.getString("day"));
        return info;
    }


    public ArrayList<String> getOfferClassDays(String code , String classCode) throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        List<String> id = new ArrayList<>(); id.add(code); id.add(classCode);

        String statement = this.getFindStatement(new MultipleStrings(id));

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(statement);
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                while (resultSet.next()){
                    result.add(this.convertResultSetToObject(resultSet).get(2));
                }
                con.close();
                return result;
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. getOfferClassDays query!");
                throw ex;
            }
        }
    }

}
