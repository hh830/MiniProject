package dao;

import common.DBManager;
import dto.Cart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static common.DBManager.getConnection;
import static common.DBManager.releaseConnection;

public class CartDao {
    public boolean addToCart(String customerId, int productId) {
        String sql = "INSERT INTO cart_item (customer_custid, product_productid, quantity) VALUES (?, ?, 1); ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, customerId);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(preparedStatement, connection);
        }
        return false;
    }

    // 장바구니 목록 가져오기
    public List<Cart> getCartItems(String customerId) {
        List<Cart> cartItems = new ArrayList<>();
        String sql = "SELECT p.*, c.quantity FROM cart_item c JOIN product p ON c.product_productid = p.productid WHERE c.customer_custid = ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, customerId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Cart cart = new Cart();
                cart.setProductId(resultSet.getInt("productid"));
                cart.setPdName(resultSet.getString("pdname"));
                cart.setPrice(resultSet.getInt("price"));
                cart.setQuantity(resultSet.getInt("quantity"));
                cartItems.add(cart);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }
        return cartItems;
    }

    // 장바구니에서 삭제
    public boolean removeProductFromCart(String customerId, int productId) {
        String sql = "DELETE FROM cart_item WHERE customer_custid = ? AND product_productid = ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, customerId);
            preparedStatement.setInt(2, productId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            DBManager.releaseConnection(preparedStatement, connection);
        }
        return false;
    }

    // 상품 개당 가격
    public int getProductPrice(int productId) {
        String sql = "SELECT price FROM product WHERE productid = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, productId);
            resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("price");
                }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }
        return 0;
    }

    // 수량 업데이트
    public int updateQuantity(int productId, int num){
        int ret = -1;
        String sql = "UPDATE cart_item SET quantity = ? where product_productid = ?; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setInt(2, productId);

            ret = preparedStatement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally{
            DBManager.releaseConnection(preparedStatement, connection);
        }
        return ret;
    }


    public void removeOrderDetail(Cart cart, String userId) {
        String sql = "DELETE FROM cart_item WHERE customer_custid = ? AND product_productid = ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, userId);
            preparedStatement.setInt(2, cart.getProductId());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            DBManager.releaseConnection(preparedStatement, connection);
        }
    }
}
