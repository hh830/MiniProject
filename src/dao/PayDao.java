package dao;

import common.DBManager;
import dto.Pay;

import java.sql.*;

public class PayDao {
    public int addPayment(Pay pay) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "INSERT INTO pay (price, payinfo, status_statusid) VALUES (?, ?, ?)";
        int payId = -1;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // RETURN_GENERATED_KEYS 사용
            preparedStatement.setInt(1, pay.getPrice());
            preparedStatement.setString(2, pay.getPayInfo());
            preparedStatement.setInt(3, pay.getStatus());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }

            resultSet = preparedStatement.getGeneratedKeys(); // 생성된 키 가져오기
            if (resultSet.next()) {
                payId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return payId;
    }


    public String getStatusName(int statusId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String value = "";
        String sql = "SELECT statusvalue FROM status WHERE statusid = ?; ";

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, statusId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                value = String.valueOf(resultSet.getInt("statusid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return value;
    }

    // 사용자가 특정 상품을 구매했는지 확인하는 메서드
    public boolean checkPurchased(String userId, int productId) {
        String sql = "SELECT od.product_productid FROM orderlist ol JOIN orderdetail od ON ol.orderid = od.order_orderid WHERE ol.customer_custid = ? AND od.product_productid = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, userId);
            preparedStatement.setInt(2, productId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true; // 구매함
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
