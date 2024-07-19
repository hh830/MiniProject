package ui;

import dao.CartDao;
import dao.OrderDao;
import dao.PayDao;
import dto.Cart;
import dto.Order;
import dto.Pay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PayDialog extends JDialog {
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private List<Cart> orderItems;

    private JButton payButton;
    private JPanel pricePanel;
    private JLabel totalPriceLabel;
    private OrderDao orderDao = new OrderDao();
    private PayDao payDao = new PayDao();
    private int totalPrice = 0;
    private String userId;
    private CartDao cartDao = new CartDao();


    public PayDialog(CartDialog parent, List<Cart> orderItems, int totalPrice, String userId) {
        super(parent, "주문 결제", true);
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.userId = userId;

        setSize(600, 400);
        setLocationRelativeTo(parent);

        initComponents();
    }

    private void initComponents() {
        orderTableModel = new DefaultTableModel(new String[]{"상품ID", "상품명", "가격", "수량"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 수정 불가
            }
        };

        orderTable = new JTable(orderTableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        loadOrderItems();

        totalPriceLabel = new JLabel("총 가격: " + totalPrice);

        payButton = new JButton("결제");

        pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.add(totalPriceLabel);

        pricePanel.add(Box.createRigidArea(new Dimension(0, 10))); // 간격 추가
        pricePanel.add(payButton);

        // 결제 유형
        JPanel paymentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JComboBox<String> jComboBox = new JComboBox<>(); // 카드 or 무통장 등
        jComboBox.addItem("카드");
        jComboBox.addItem("무통장 입금");

        paymentPanel.add(new JLabel("결제 유형"));
        paymentPanel.add(jComboBox);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(scrollPane);
        mainPanel.add(paymentPanel);
        mainPanel.add(pricePanel);

        payButton.addActionListener(e -> {
            if (checkPurchase(orderItems)) {
                JOptionPane.showMessageDialog(this, "이미 구매한 상품이 있습니다", "ERROR", JOptionPane.ERROR_MESSAGE);
            } else {
                String content = (String) jComboBox.getSelectedItem();

                Pay pay = new Pay();
                pay.setPayInfo(content);
                pay.setPrice(totalPrice);
                pay.setStatus(1); // 결제전1, 결제완료2, 결제실패3, 환불4

                int payId = payDao.addPayment(pay); // 결제 정보 추가하기
                if (payId != -1) {
                    JOptionPane.showMessageDialog(this, "결제가 완료되었습니다. 결제 ID: " + payId, "Success", JOptionPane.INFORMATION_MESSAGE);

                    Order order = new Order();
                    order.setTotalPrice(totalPrice);
                    order.setCustomerId(userId);
                    order.setDate(LocalDate.now());
                    order.setPayId(payId);

                    int orderId = orderDao.addOrder(order); // 주문 내역 추가하기

                    addOrderDetail(orderItems, orderId); // 주문 상세 내역 추가

                    // 구매 후 장바구니 내역 제거
                    deleteOrderDetail(orderItems, userId);

                    // 주문 내역 창으로 이동
                    OrderListDialog orderListDialog = new OrderListDialog(this, userId);
                    orderListDialog.setVisible(true);
                    dispose(); // 결제 창 닫기
                } else {
                    JOptionPane.showMessageDialog(this, "결제에 실패하였습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(mainPanel);
    }

    private void loadOrderItems() {
        orderTableModel.setRowCount(0);
        for (Cart cart : orderItems) {
            orderTableModel.addRow(new Object[]{cart.getProductId(), cart.getPdName(), cart.getPrice(), cart.getQuantity()});
        }
    }

    private void addOrderDetail(List<Cart> orderItems, int orderId) {
        for (Cart cart : orderItems) {
            orderDao.addOrderDetail(cart, orderId);
        }
    }

    private void deleteOrderDetail(List<Cart> orderItems, String userId) {
        // 장바구니 내역 제거
        for (Cart cart : orderItems) {
            cartDao.removeOrderDetail(cart, userId);
        }
    }

    private boolean checkPurchase(List<Cart> orderItems){
        for(Cart cart : orderItems){
            if(payDao.checkPurchased(cart.getUserId(), cart.getProductId())){
                // 구매한 상품 있음
                return true;
            }
        }
        return false;
    }
}
