package ui;

import dao.CustomerDao;
import dao.ProductDao;
import dto.CustomerDto;
import dto.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Shop extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private ProductPanel productPanel;
    private ProductRegisterPanel productRegisterPanel;
    private ShopMenuBar menuBar;
    private CustomerDao customerDao = new CustomerDao();
    private ProductDao productDao = new ProductDao();
    private String userId;

    public Shop() {
        setTitle("Mini steam");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();
        productPanel = new ProductPanel();
        productRegisterPanel = new ProductRegisterPanel(this);

        menuBar = new ShopMenuBar();
        setJMenuBar(menuBar);

        initListeners();

        mainPanel.add(loginPanel, "loginPanel");
        mainPanel.add(registerPanel, "registerPanel");
        mainPanel.add(productPanel, "productPanel");
        mainPanel.add(productRegisterPanel, "productRegisterPanel");

        add(mainPanel);
        cardLayout.show(mainPanel, "loginPanel");

        updateTopCategories();  // 카테고리 목록 초기화
    }

    private void initListeners() {
        // 메뉴바 - 구매자 기능 선택 ( 판매자도 구매 가능 )
        menuBar.addCustomerMenuListener(e -> cardLayout.show(mainPanel, "loginPanel"));

        // 메뉴바 - 판매자 기능 선택 - role이 판매자인 사람만 가능
        menuBar.addSellerMenuListener(e -> {
            String role = customerDao.getUserRole(userId);
            if ("판매자".equals(role)) {
                JOptionPane.showMessageDialog(this, "판매자님 안녕하세요 :)", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "productRegisterPanel");
                updateTopCategories(); // 카테고리 목록 초기화
            } else if ("구매자".equals(role)) {
                JOptionPane.showMessageDialog(this, "판매자 권한이 아닙니다 :(", "ERROR", JOptionPane.ERROR_MESSAGE);
                listProduct();
                cardLayout.show(mainPanel, "productPanel");
            } else {
                JOptionPane.showMessageDialog(this, "로그인 해주세요 :)", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginPanel.addLoginListener(e -> {
            userId = loginPanel.getUserId();
            String password = loginPanel.getPassword();
            if (customerDao.login(userId, password) == 1) {
                String role = customerDao.getUserRole(userId);
                if ("구매자".equals(role)) {
                    JOptionPane.showMessageDialog(this, "구매자 - 로그인 성공", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "productPanel");
                    listProduct();
                } else if ("판매자".equals(role)) {
                    JOptionPane.showMessageDialog(this, "판매자 - 로그인 성공", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "productRegisterPanel");
                    updateTopCategories(); // 카테고리 목록 초기화
                }
            } else {
                JOptionPane.showMessageDialog(this, "로그인 실패", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginPanel.addRegisterListener(e -> cardLayout.show(mainPanel, "registerPanel"));

        registerPanel.addSubmitRegisterListener(e -> {
            CustomerDto customerDto = registerPanel.getCustomerDto();
            int flag = customerDao.register(customerDto);
            if (flag == 1) {
                JOptionPane.showMessageDialog(this, "회원가입 완료", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "loginPanel");
            }
        });

        registerPanel.addBackToLoginListener(e -> cardLayout.show(mainPanel, "loginPanel"));

        productRegisterPanel.addRegisterListener(e -> {
            Product product = productRegisterPanel.getProductDetails();
            if (productDao.addProduct(product) != -1) {
                JOptionPane.showMessageDialog(this, "상품 등록 완료", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "상품 등록 실패", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        productPanel.addSearchListener(e -> searchProduct(productPanel.getSearchTerm()));
        productPanel.addViewListener(e -> viewProduct());
        productPanel.addCartListener(e -> goToCart());
        productPanel.addLogoutListener(e -> logout());

        productPanel.getTopCategoryComboBox().addActionListener(e -> updateSubCategories());
        productPanel.getSubCategoryComboBox().addActionListener(e -> searchProductByCategory());
    }

    private void listProduct() {
        List<Product> productList = productDao.loadProducts(userId);
        productPanel.updateProductList(productList);
    }

    private void searchProduct(String searchTerm) {
        List<Product> productList = productDao.searchProducts(searchTerm, userId);
        productPanel.updateProductList(productList);
    }

    private void searchProductByCategory() {
        String topCategory = productPanel.getSelectedTopCategory();
        String subCategory = productPanel.getSelectedSubCategory();
        List<Product> productList = productDao.searchProductsCategory(topCategory, subCategory, userId);
        productPanel.updateProductList(productList);
    }

    // 상품 조회부분 부모 카테고리
    private void updateTopCategories() {
        List<String> topCategories = productDao.getTopCategories();
        productPanel.updateTopCategories(topCategories);
        //productRegisterPanel.updateCategories(topCategories); // productRegisterPanel에 카테고리 업데이트
    }

    // 상품 조회부분 서브 카테고리
    private void updateSubCategories() {
        String selectedTopCategory = productPanel.getSelectedTopCategory();
        List<String> subCategories = productDao.getSubCategories(selectedTopCategory);
        productPanel.updateSubCategories(subCategories);
    }

    private void viewProduct() {
        int productId = productPanel.getSelectedProductId();
        if (productId == -1) {
            JOptionPane.showMessageDialog(this, "행 선택해주세요", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = productDao.getProductById(productId);
        product.setLikeCount(productDao.countLikesProduct(productId));
        product.setUserLiked(productDao.checkLikeProduct(userId, productId));

        if (product != null) {
            DetailDialog detailDialog = new DetailDialog(this, product, userId);
            detailDialog.setVisible(true);
        }
    }

    private void goToCart() {
        CartDialog cartDialog = new CartDialog(this, userId);
        cartDialog.setVisible(true);
    }

    private void logout() {
        cardLayout.show(mainPanel, "loginPanel");
        loginPanel.setUserId("");
        loginPanel.setPassword("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Shop().setVisible(true));
    }
}
