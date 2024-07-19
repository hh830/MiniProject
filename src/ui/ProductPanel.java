package ui;

import dto.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton, viewButton, cartButton, logoutButton;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JComboBox<String> topCategoryComboBox, subCategoryComboBox;

    public ProductPanel() {
        setLayout(new BorderLayout());

        searchField = new JTextField();
        searchButton = new JButton("검색");
        viewButton = new JButton("상세보기");
        cartButton = new JButton("내 장바구니");
        logoutButton = new JButton("로그아웃");

        // 상위 카테고리 콤보 박스
        topCategoryComboBox = new JComboBox<>();
        topCategoryComboBox.addItem("-");

        // 하위 카테고리 콤보 박스
        subCategoryComboBox = new JComboBox<>();
        subCategoryComboBox.addItem("-");

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

        productTableModel = new DefaultTableModel(new String[]{"상품ID", "상품명", "제작사", "가격", "보유여부"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are not editable
            }
        };
        productTable = new JTable(productTableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(viewButton);
        buttonPanel.add(cartButton);
        buttonPanel.add(logoutButton);

        add(searchPanel, BorderLayout.NORTH);
        add(productScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addViewListener(ActionListener listener) {
        viewButton.addActionListener(listener);
    }

    public void addCartListener(ActionListener listener) {
        cartButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public String getSearchTerm() {
        return searchField.getText();
    }

    public int getSelectedProductId() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            return -1;
        }
        return (int) productTableModel.getValueAt(selectedRow, 0);
    }

    public void updateProductList(List<Product> productList) {
        productTableModel.setRowCount(0);
        for (Product product : productList) {
            productTableModel.addRow(new Object[]{product.getProductId(), product.getPdname(), product.getBrandName(), product.getPrice(), product.getHas()});
        }
    }

    public void updateTopCategories(List<String> categories) {
        topCategoryComboBox.removeAllItems();
        topCategoryComboBox.addItem("-");
        for (String category : categories) {
            topCategoryComboBox.addItem(category);
        }
    }

    public void updateSubCategories(List<String> categories) {
        subCategoryComboBox.removeAllItems();
        subCategoryComboBox.addItem("-");
        for (String category : categories) {
            subCategoryComboBox.addItem(category);
        }
    }

    public String getSelectedTopCategory() {
        return (String) topCategoryComboBox.getSelectedItem();
    }

    public String getSelectedSubCategory() {
        return (String) subCategoryComboBox.getSelectedItem();
    }

    public JComboBox<String> getTopCategoryComboBox() {
        return topCategoryComboBox;
    }

    public JComboBox<String> getSubCategoryComboBox() {
        return subCategoryComboBox;
    }
}
