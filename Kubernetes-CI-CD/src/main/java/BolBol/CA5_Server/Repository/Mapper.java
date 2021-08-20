package BolBol.CA5_Server.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Mapper<T, I> implements IMapper<T, I> {

    abstract protected String getInsertStatement(T t);

    abstract protected String getFindStatement(I id);

    abstract protected String getDeleteStatement(I id);

    abstract protected T convertResultSetToObject(ResultSet rs) throws SQLException;

    public T find(I id) throws SQLException {
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(getFindStatement(id))
        ){
            ResultSet resultSet;
            try {
                resultSet = st.executeQuery();
                resultSet.next();
                return convertResultSetToObject(resultSet);
            } catch (SQLException e) {
                System.out.println("Concern in Mapper. FindByID query! " + e.getMessage());
                throw e;
            }
        }
    }

    public void insert(T obj) throws SQLException {
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(getInsertStatement(obj))
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. Insert query!");
                throw ex;
            }
        }
    }

    public void delete(I id) throws SQLException {
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement st = con.prepareStatement(getDeleteStatement(id))
        ) {
            try {
                st.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error in Mapper. Delete query!");
                throw ex;
            }
        }
    }
}