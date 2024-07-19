package ui;


import dao.CartDao;
import dto.Cart;
import dto.Pay;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CartDialog extends JDialog {
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private CartDao cartDao = new CartDao();
    private String userId;
    private int totalPrice=0;
    private JPanel pricePanel;
    private JLabel totalPriceLabel;

    public CartDialog(Frame parent, String userId) {
        super(parent, "장바구니", true);
        this.userId = userId;
        setSize(600, 400);
        setLocationRelativeTo(parent);

        initComponents();
    }

    private void initComponents() {
        cartTableModel = new DefaultTableModel(new String[]{"상품ID", "상품명", "가격", "수량"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (column == 3) ? true : false; //3열 수량 수정 가능
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        // TableModelListener 추가
        cartTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) { // 수량 열이 수정된 경우
                    int row = e.getFirstRow();
                    String quantityStr = cartTableModel.getValueAt(row, 3).toString(); // 수량 문자열
                    int quantity = Integer.parseInt(quantityStr);

                    int productId = Integer.parseInt(cartTableModel.getValueAt(row, 0).toString());

                    updateQuantity(productId, quantity); // 가격 업데이트

                    loadCartItems();
                    totalPriceLabel.setText("총 가격: " + totalPrice);
                }
            }
        });


        JButton removeButton = new JButton("삭제");
        JButton payButton = new JButton("주문");

        removeButton.addActionListener(e -> {
            removeSelectedProduct();
            totalPriceLabel.setText("총 가격: " + totalPrice);
        });

        payButton.addActionListener(e -> goToOrderDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(removeButton);
        buttonPanel.add(payButton);

        loadCartItems();

        pricePanel = new JPanel(new GridLayout(2,1));
        totalPriceLabel = new JLabel("총 가격: " + totalPrice);

        pricePanel.add(totalPriceLabel);
        pricePanel.add(buttonPanel);

        add(scrollPane, BorderLayout.CENTER);
        add(pricePanel, BorderLayout.SOUTH);

    }

    private void goToOrderDialog() {
        List<Cart> cartList = cartDao.getCartItems(userId);
        PayDialog orderDialog = new PayDialog(this, cartList, totalPrice, userId);
        orderDialog.setVisible(true);

    }

    private void loadCartItems() {
        totalPrice = 0;
        cartTableModel.setRowCount(0);
        List<Cart> cartItems = cartDao.getCartItems(userId);
        for (Cart cart : cartItems) {
            cartTableModel.addRow(new Object[]{cart.getProductId(), cart.getPdName(), cart.getPrice(), cart.getQuantity()});
            totalPrice += cart.getPrice() * cart.getQuantity();
        }

    }

    private void removeSelectedProduct() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "행 선택해주세요", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) cartTableModel.getValueAt(selectedRow, 0);
        if (cartDao.removeProductFromCart(userId, productId)) {
            loadCartItems();
        }
    }

    private void updateQuantity(int productId, int quantity) {
        cartDao.updateQuantity(productId, quantity);
    }

}
