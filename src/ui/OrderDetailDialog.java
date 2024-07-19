package ui;

import dao.OrderDao;
import dto.OrderDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderDetailDialog extends JDialog {
    private JTable orderDetailTable;
    private DefaultTableModel orderDetailTableModel;
    private int orderId;
    private OrderDao orderDao = new OrderDao();

    public OrderDetailDialog(JDialog parent, int orderId) {
        super(parent, "주문 상세 정보", true);
        this.orderId = orderId;
        setSize(600, 400);
        setLocationRelativeTo(parent);

        initComponents();
        loadOrderDetailItems();
    }

    private void initComponents() {
        orderDetailTableModel = new DefaultTableModel(new String[]{"상품명", "제작사", "가격", "수량"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 수정 불가
            }
        };
        orderDetailTable = new JTable(orderDetailTableModel);
        JScrollPane scrollPane = new JScrollPane(orderDetailTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadOrderDetailItems() {
        orderDetailTableModel.setRowCount(0);

        List<OrderDetail> orderDetails = orderDao.getOrderDetails(orderId);

        for (OrderDetail orderDetail : orderDetails) {
            orderDetailTableModel.addRow(new Object[]{
                    orderDetail.getProductName(),
                    orderDetail.getBrandName(),
                    orderDetail.getPrice(),
                    orderDetail.getNum()
            });
        }
    }
}
