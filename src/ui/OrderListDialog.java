package ui;

import dao.OrderDao;
import dto.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderListDialog extends JDialog {
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private String userId;
    private OrderDao orderDao = new OrderDao();

    public OrderListDialog(PayDialog parent, String userId) {
        super(parent, "주문 내역", true);
        this.userId = userId;

        setSize(600, 400);
        setLocationRelativeTo(parent);

        initComponents();
        loadOrderItems();
    }

    private void initComponents() {
        orderTableModel = new DefaultTableModel(new String[]{"주문ID", "총 가격", "주문 날짜", "상태"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 수정 불가
            }
        };
        orderTable = new JTable(orderTableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        JButton viewDetailButton = new JButton("상세 보기");
        viewDetailButton.addActionListener(e -> viewOrderDetail());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewDetailButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadOrderItems() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderDao.getOrdersByUserId(userId);
        for (Order order : orders) {
            orderTableModel.addRow(new Object[]{order.getOrderId(), order.getTotalPrice(), order.getDate(), order.getStatus()});
        }
    }

    private void viewOrderDetail() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "주문을 선택하세요.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);
        OrderDetailDialog orderDetailDialog = new OrderDetailDialog(this, orderId);
        orderDetailDialog.setVisible(true);
    }


}
