package dao;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import common.DBManager;
import dto.CustomerDto;
import dto.Product;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    public int login(String userId, String password) {
        CustomerDto customerDto = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM customer WHERE custid=? AND password=?; ";
        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return 1;
            } else {
                return 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(preparedStatement, connection);
        }
        return -1;
    }

    public int register(CustomerDto customerDto) {
        Connection connection = null;
        PreparedStatement statement = null;

        String userId = customerDto.getUserId();
        String password = customerDto.getPassword();
        String name = customerDto.getName();
        Integer age = customerDto.getAge();
        String address = customerDto.getAddress();
        String phone = customerDto.getPhone();

        try {
            connection = DBManager.getConnection();
            statement = connection.prepareStatement("INSERT INTO customer (custid, password, name, age, address, phone) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, userId);
            statement.setString(2, password);
            statement.setString(3, name);
            statement.setInt(4, age);
            statement.setString(5, address);
            statement.setString(6, phone);
            statement.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            DBManager.releaseConnection(statement, connection);
        }
        return -1;
    }

    // 사용자의 역할을 확인 - 구매자1 판매자2
    public String getUserRole(String userId) {
        String sql = "SELECT r.rolename FROM customer c JOIN role r ON c.role_roleid = r.roleid WHERE c.custid = ?; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String role = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                role = resultSet.getString("rolename");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }
        return role;
    }
}
