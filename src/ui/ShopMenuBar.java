package ui;


import javax.swing.*;
import java.awt.event.ActionListener;

public class ShopMenuBar extends JMenuBar {
    private JMenu customerMenu;
    private JMenu sellerMenu;

    public ShopMenuBar() {
        // 고객 메뉴 생성
        customerMenu = new JMenu("구매자");

        // 판매자 메뉴 생성
        sellerMenu = new JMenu("판매자");

        // 메뉴바에 메뉴 추가
        add(customerMenu);
        add(sellerMenu);
    }

    // 이벤트 리스너 추가 메서드
    public void addCustomerMenuListener(ActionListener listener) {
        customerMenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                listener.actionPerformed(null);
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {}

            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });
    }

    public void addSellerMenuListener(ActionListener listener) {
        sellerMenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                listener.actionPerformed(null);
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {}

            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });
    }
}
