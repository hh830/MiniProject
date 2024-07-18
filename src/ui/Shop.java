package ui;

import dao.CartDao;
import dao.CustomerDao;
import dao.ProductDao;
import dto.CustomerDto;
import dto.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Shop extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField loginUserIdField, loginPasswordField;
    private JTextField registerUserIdField, registerPasswordField, registerNameField, registerAddressField, registerPhoneField, registerAgeField;

    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField searchField;
    private JComboBox<String> topCategoryComboBox, subCategoryComboBox;
    private CustomerDao customerDao = new CustomerDao();
    private ProductDao productDao = new ProductDao();
    private CartDao cartDao = new CartDao();
    private String userId;

    public Shop() {
        // 화면 UI 관련 설정
        setTitle("Mini steam");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // x표시 누를 때 종료해라
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        initComponents();
        add(mainPanel);
        cardLayout.show(mainPanel, "loginPanel");


    }

    private void initComponents() {
        // ** 로그인 **
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        loginUserIdField = new JTextField();
        loginPasswordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(loginUserIdField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(loginPasswordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);

        loginButton.addActionListener(e -> {
            userId = loginUserIdField.getText();
            String password = loginPasswordField.getText();

            if(customerDao.login(userId, password) == 1){
                JOptionPane.showMessageDialog(this, "로그인 성공", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "productPanel");
                listProduct();
            } else{
                JOptionPane.showMessageDialog(this, "로그인 실패", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "registerPanel"));

        // ** 회원가입 **
        JPanel registerPanel = new JPanel(new GridLayout(7, 2));
        registerUserIdField = new JTextField();
        registerPasswordField = new JPasswordField();
        registerNameField = new JTextField();
        registerAgeField = new JTextField();
        registerAddressField = new JTextField();
        registerPhoneField = new JTextField();
        JButton submitRegisterButton = new JButton("Register");
        JButton backToLoginButton = new JButton("Back to Login");

        registerPanel.add(new JLabel("Id:"));
        registerPanel.add(registerUserIdField);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(registerPasswordField);
        registerPanel.add(new JLabel("Name:"));
        registerPanel.add(registerNameField);
        registerPanel.add(new JLabel("Age:"));
        registerPanel.add(registerAgeField);
        registerPanel.add(new JLabel("Address:"));
        registerPanel.add(registerAddressField);
        registerPanel.add(new JLabel("Phone:"));
        registerPanel.add(registerPhoneField);
        registerPanel.add(submitRegisterButton);
        registerPanel.add(backToLoginButton);

        submitRegisterButton.addActionListener(e -> {
            String id = registerUserIdField.getText();
            String password = registerPasswordField.getText();
            String name = registerNameField.getText();
            int age = Integer.parseInt(registerAgeField.getText());
            String address = registerAddressField.getText();
            String phone = registerPhoneField.getText();

            int flag = customerDao.register(new CustomerDto(id, password, name, age, address, phone));
            if(flag == 1) {
                JOptionPane.showMessageDialog(this, "회원가입완료", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "loginPanel");
            }

        });
        backToLoginButton.addActionListener(e -> cardLayout.show(mainPanel, "loginPanel"));

        // ** 상품 보기 **
        JPanel productPanel = new JPanel(new BorderLayout());
        productTableModel = new DefaultTableModel(new String[]{"상품ID", "상품명", "제작사", "가격"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are not editable
            }
        };

        productTable = new JTable(productTableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        searchField = new JTextField();
        JButton searchButton = new JButton("검색");
        JButton viewButton = new JButton("자세히");
        JButton cartButton = new JButton("go cart");
        JButton logoutButton = new JButton("로그아웃");

        // 상위 카테고리 콤보 박스
        topCategoryComboBox = new JComboBox<>();
        topCategoryComboBox.addItem("전체");

        List<String> topCategories = productDao.getTopCategories();
        for (String category : topCategories) {
            topCategoryComboBox.addItem(category);
        }

        // 하위 카테고리 콤보 박스
        subCategoryComboBox = new JComboBox<>();
        subCategoryComboBox.addItem("전체");

        topCategoryComboBox.addActionListener(e -> {
            subCategoryComboBox.removeAllItems();
            String content = (String) topCategoryComboBox.getSelectedItem();
            List<String> subCategories = productDao.getSubCategories(content);
            for (String category : subCategories) {
                subCategoryComboBox.addItem(category);
            }
        });

        subCategoryComboBox.addActionListener(e1 -> {
            String topContent = (String) topCategoryComboBox.getSelectedItem();
            String content = (String) subCategoryComboBox.getSelectedItem();
            searchProductCategory(topContent, content);
        });


        // 검색창 부분
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        searchPanel.add(searchField, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 1;
        searchPanel.add(searchButton, gbc);

        gbc.gridx = 2;
        searchPanel.add(topCategoryComboBox, gbc);

        gbc.gridx = 3;
        searchPanel.add(subCategoryComboBox, gbc);

        searchButton.addActionListener(e -> {
            String text = searchField.getText();
            if(!text.isBlank())
                searchProduct(text);
        });

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(viewButton);
        buttonPanel.add(cartButton);
        buttonPanel.add(logoutButton);

        productPanel.add(searchPanel, BorderLayout.NORTH);
        productPanel.add(productScrollPane, BorderLayout.CENTER);
        productPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 각 행 더블클릭 하면 자세히 보기
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // double click
                if (e.getClickCount() == 2) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        viewProduct();
                    }
                }
            }
        });

        viewButton.addActionListener(e -> viewProduct());
        cartButton.addActionListener(e -> goToCart());
        logoutButton.addActionListener(e -> logout());

        mainPanel.add(loginPanel, "loginPanel");
        mainPanel.add(registerPanel, "registerPanel");
        mainPanel.add(productPanel, "productPanel");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Shop().setVisible(true));
    }

    private void clearTable(){
        productTableModel.setRowCount(0);
    }

    private void listProduct() {
        // 현재 tableModel을 정리하고
        clearTable();

        List<Product> productList = productDao.loadProducts();

        for (Product product : productList ) {
            productTableModel.addRow(new Object[] {product.getProductId(), product.getPdname(), product.getBrandName(), product.getPrice() });
        }
    }

    // 검색어로 검색
    private void searchProduct(String searchTerm) {
        clearTable();

        List<Product> productList = productDao.searchProducts(searchTerm);

        for (Product product : productList ) {
            productTableModel.addRow(new Object[] {product.getProductId(), product.getPdname(), product.getBrandName(), product.getPrice() });
        }
    }

    // 카테고리로 검색
    private void searchProductCategory(String topCategory, String category) {
        clearTable();

        List<Product> productList = productDao.searchProductsCategory(topCategory, category);

        for (Product product : productList ) {
            productTableModel.addRow(new Object[] {product.getProductId(), product.getPdname(), product.getBrandName(), product.getPrice() });
        }
    }

    // 자세히 보기
    private void viewProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to view", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }


        int productId = (Integer) productTableModel.getValueAt(selectedRow, 0);
        String customerId = loginUserIdField.getText(); // 현재 로그인한 사용자 ID를 가져옵니다.

        Product product = productDao.getProductById(productId);
        product.setLikeCount(countLikesProduct());
        product.setUserLiked(productDao.checkLikeProduct(customerId, productId));

        if (product != null) {
            DetailDialog detailDialog = new DetailDialog(this, product, userId);
            detailDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load product details", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    // 좋아요 추가 및 삭제
//    private void likeProduct() {
//        int selectedRow = productTable.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Select a product to like", "Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int productId = (int) productTableModel.getValueAt(selectedRow, 0);
//        String customerId = loginUserIdField.getText();
//
//        if (productDao.checkLikeProduct(customerId, productId)) {
//            // 이미 좋아요가 있으면 삭제
//            productDao.deletelikeProduct(customerId, productId);
//            JOptionPane.showMessageDialog(this, "좋아요 취소되었습니다", "delete", JOptionPane.INFORMATION_MESSAGE);
//        } else {
//            productDao.addlikeProduct(customerId, productId);
//            JOptionPane.showMessageDialog(this, "좋아요 추가되었습니다", "insert", JOptionPane.INFORMATION_MESSAGE);
//        }
//    }

    // 상품의 좋아요 개수 세기
    private int countLikesProduct(){
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to like", "Error", JOptionPane.WARNING_MESSAGE);
            return -1;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 0);

        return productDao.checkLikeProduct(productId);
    }

    private void goToCart() {
        CartDialog cartDialog = new CartDialog(this, userId);
        cartDialog.setVisible(true);

    }

    private void logout() {
        cardLayout.show(mainPanel, "loginPanel");
        loginUserIdField.setText("");
        loginPasswordField.setText("");
    }
}
