package dao;

import common.DBManager;
import dto.Product;
import dto.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    // 모든 제품을 로드
    public List<Product> loadProducts(String userId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, b.brandname FROM product p JOIN brand b ON p.brand_brandid = b.brandid; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getInt("productid"));
                product.setPdname(resultSet.getString("pdname"));
                product.setContent(resultSet.getString("content"));
                product.setImage(resultSet.getString("image"));
                product.setBrandName(resultSet.getString("brandname")); // 브랜드명 조인
                product.setPrice(resultSet.getInt("price"));

                boolean flag = checkPurchased(userId, product.getProductId());

                if (flag) {
                    product.setHas("보유중");
                } else {
                    product.setHas("구매가능");
                }

                list.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return list;
    }

    // 검색어로 제품을 검색하는 메서드
    public List<Product> searchProducts(String searchTerm, String userId) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT p.*, b.brandname FROM product p JOIN brand b ON p.brand_brandid = b.brandid WHERE p.pdname LIKE ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + searchTerm + "%");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getInt("productid"));
                product.setPdname(resultSet.getString("pdname"));
                product.setPrice(resultSet.getInt("price"));
                product.setContent(resultSet.getString("content"));
                product.setImage(resultSet.getString("image"));
                product.setBrandName(resultSet.getString("brandname"));

                boolean flag = checkPurchased(userId, product.getProductId());

                if (flag) {
                    product.setHas("보유중");
                } else {
                    product.setHas("구매가능");
                }

                productList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return productList;
    }

    // 카테고리로 제품을 검색하는 메서드
    public List<Product> searchProductsCategory(String topCategory, String subCategory, String userId) {
        List<Product> productList = new ArrayList<>();
        String sql = "WITH RECURSIVE CategoryHierarchy AS (" +
                "  SELECT categoryid, name, parent_categoryid " + // 입력한 카테고리 이름 받아와서 조회
                "  FROM category " +
                "  WHERE name = ? " +
                "  UNION ALL " +
                "  SELECT c.categoryid, c.name, c.parent_categoryid " + // 재귀 - 부모 카테고리를 조회
                "  FROM category c " +
                "  JOIN CategoryHierarchy ch ON c.parent_categoryid = ch.categoryid " +
                ") " +
                "SELECT p.*, b.brandname FROM product p " + // 부모와 자식 카테고리를 통해 상품 검색
                "JOIN brand b ON p.brand_brandid = b.brandid " +
                "JOIN CategoryHierarchy ch ON p.category_categoryid = ch.categoryid " +
                "WHERE ch.name = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, topCategory);
            preparedStatement.setString(2, subCategory);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getInt("productid"));
                product.setPdname(resultSet.getString("pdname"));
                product.setPrice(resultSet.getInt("price"));
                product.setContent(resultSet.getString("content"));
                product.setImage(resultSet.getString("image"));
                product.setBrandName(resultSet.getString("brandname"));

                boolean flag = checkPurchased(userId, product.getProductId());

                if (flag) {
                    product.setHas("보유중");
                } else {
                    product.setHas("구매가능");
                }

                productList.add(product);
            }

        } catch (
                SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return productList;
    }

    public List<String> getTopCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM category WHERE parent_categoryid IS NULL";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categories.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return categories;
    }

    public List<String> getSubCategories(String parentCategory) {
        List<String> subCategories = new ArrayList<>();
        String sql = "SELECT c.name FROM category c JOIN category p ON c.parent_categoryid = p.categoryid WHERE p.name = ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, parentCategory);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                subCategories.add(resultSet.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return subCategories;
    }

    // 상품 아이디로 상품 정보 가져오기(상세페이지)
    public Product getProductById(int productId) {
        Product product = null;
        String sql = "SELECT p.*, b.brandname FROM product p JOIN brand b ON p.brand_brandid = b.brandid WHERE productid = ?; ";

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
                product.setProductId(resultSet.getInt("productid"));
                product.setPdname(resultSet.getString("pdname"));
                product.setPrice(resultSet.getInt("price"));
                product.setContent(resultSet.getString("content"));
                product.setImage(resultSet.getString("image"));
                product.setBrandName(resultSet.getString("brandname"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return product;
    }

    // 상품 자체에 좋아요가 총 몇 개 달렸는지 확인하기 위해
    public int countLikesProduct(int productId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE product_productid = ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

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

        return 0;
    }

    // 사용자가 좋아요를 눌렀는지 안 눌렀는지 확인하기 위해
    public boolean checkLikeProduct(String customerId, int productId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE customer_custid = ? AND product_productid = ?; ";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, customerId); // user_liked 쿼리의 customer_id 파라미터
            preparedStatement.setInt(2, productId); // productid 파라미터

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return false;
    }

    public boolean addlikeProduct(String customerId, int productId) {
        String sql = "INSERT INTO likes (customer_custid, product_productid) VALUES (?, ?)";
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

    public boolean deletelikeProduct(String customerId, int productId) {
        String sql = "DELETE FROM likes WHERE customer_custid = ? AND product_productid = ?; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int ret = -1;
        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, customerId);
            preparedStatement.setInt(2, productId);

            ret = preparedStatement.executeUpdate();

            return ret > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(preparedStatement, connection);
        }

        return false;
    }

    // 사용자가 특정 상품을 구매했는지 확인하는 메서드
    public boolean checkPurchased(String userId, int productId) {
        String sql = "SELECT COUNT(*) FROM orderlist ol JOIN orderdetail od ON ol.orderid = od.order_orderid WHERE ol.customer_custid = ? AND od.product_productid = ?";
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
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return false;
    }

    // 리뷰
    public List<Review> getReviewsByProductId(int productId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE product_productid = ?; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, productId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Review review = new Review();
                review.setReviewId(resultSet.getInt("idreviews"));
                review.setCustId(resultSet.getString("customer_custid"));
                review.setProductId(resultSet.getInt("product_productid"));
                review.setContent(resultSet.getString("content"));
                review.setStars(resultSet.getInt("stars"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }

        return reviews;
    }

    // 리뷰 추가 메서드
    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (customer_custid, product_productid, content, stars) VALUES (?, ?, ?, ?); ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, review.getCustId());
            preparedStatement.setInt(2, review.getProductId());
            preparedStatement.setString(3, review.getContent());
            preparedStatement.setInt(4, review.getStars());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(preparedStatement, connection);
        }
        return false;
    }

    // 리뷰 삭제 메서드
    public boolean deleteReview(String userId, int productId) {
        String sql = "DELETE FROM reviews WHERE customer_custid = ? AND product_productid = ?; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            preparedStatement.setInt(2, productId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(preparedStatement, connection);
        }
        return false;
    }

    // 판매자 상품 등록
    public int addProduct(Product product) {
        String sql = "INSERT INTO product (pdname, content, image, price, brand_brandid) VALUES (?, ?, ?, ?, ?); ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int ret = -1;

        try {
            int brandId = getBrandIdByName(product.getBrandName());

            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, product.getPdname());
            preparedStatement.setString(2, product.getContent());
            preparedStatement.setString(3, product.getImage());
            preparedStatement.setInt(4, product.getPrice());
            preparedStatement.setInt(5, brandId);

            ret = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(preparedStatement, connection);
        }
        return ret;
    }

    public int getBrandIdByName(String brandName) {
        String sql = "SELECT brandid FROM brand WHERE brandname = ?; ";
        int brandId = -1;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, brandName);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                brandId = resultSet.getInt("brandid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(resultSet, preparedStatement, connection);
        }
        return brandId;
    }

    public List<String> getAllCategories(String categoryName) {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT categoryid FROM category where categoryname = ?; ";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, categoryName);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categories.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

}
