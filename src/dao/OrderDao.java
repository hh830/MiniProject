package dao;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import common.DBManager;
import dto.*;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class OrderDao {

    // 주문 등록
    public int addOrder(Order order) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO orderlist (totalprice, customer_custid, pay_idpay, date) VALUES (?, ?, ?, ?); ";
        ResultSet resultSet = null;
        int ret = -1;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getTotalPrice());
            preparedStatement.setString(2, order.getCustomerId());
            preparedStatement.setInt(3, order.getPayId());

            Date date = java.sql.Date.valueOf(order.getDate()); // localdate -> date 형 변환
            preparedStatement.setDate(4, (java.sql.Date) date);

            ret = preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1); //orderId 값 가져오기
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return ret;
    }

    // 주문 상세 등록
    public void addOrderDetail(Cart cart, int orderId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO orderdetail (num, price, product_brand_brandid, order_orderid, product_productid) VALUES (?, ?, ?, ?, ?); ";
        int brandId = getBrandId(cart.getProductId());

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, cart.getQuantity());
            preparedStatement.setInt(2, cart.getPrice());
            preparedStatement.setInt(3, brandId);
            preparedStatement.setInt(4, orderId);
            preparedStatement.setInt(5, cart.getProductId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(preparedStatement, connection);
        }
    }

    // 상품 아이디로 브랜드 아이디 가져오기
    private int getBrandId(int productId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql = "SELECT brand_brandid FROM PRODUCT WHERE productid = ?; ";
        int ret = -1;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, productId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return ret;
    }

    // 고객에 맞게 주문 리스트 찾기
    public List<Order> getOrdersByUserId(String userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orderlist WHERE customer_custid = ?; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("orderid"));
                order.setTotalPrice(resultSet.getInt("totalprice"));
                order.setDate(resultSet.getDate("date").toLocalDate());

                String status = getPayStatus(resultSet.getInt("pay_idpay"));
                order.setStatus(status);

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return orders;
    }

    // 주문 상세
    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT * FROM orderdetail WHERE order_orderid = ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, orderId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(resultSet.getInt("order_orderid"));
                orderDetail.setPrice(resultSet.getInt("price"));
                orderDetail.setNum(resultSet.getInt("num"));
                orderDetail.setBrandId(resultSet.getInt("product_brand_brandid"));

                Product product = getProductById(resultSet.getInt("product_productid"));
                orderDetail.setProductName(product.getPdname());
                orderDetail.setBrandName(product.getBrandName());

                orderDetails.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return orderDetails;
    }

    public String getPayStatus(int payId) {
        String value = "";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql = "SELECT s.statusvalue FROM pay p JOIN status s ON p.status_statusid = s.statusid WHERE p.idpay = ?; ";
        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, payId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                value = resultSet.getString("statusvalue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return value;
    }

    // 브랜드 이름, 상품명 가져오기
    private Product getProductById(int productId) {
        Product product = null;
        String sql = "SELECT p.pdname, b.brandname FROM product p JOIN brand b ON p.brand_brandid = b.brandid WHERE p.productid = ? ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, productId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                product = new Product();
                product.setPdname(resultSet.getString("pdname"));
                product.setBrandName(resultSet.getString("brandname"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }
}
