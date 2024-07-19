package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JTextField loginUserIdField;
    private JTextField loginPasswordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginPanel() {
        setSize(200, 100);
        setLayout(new GridLayout(3, 2));

        loginUserIdField = new JTextField();
        loginPasswordField = new JPasswordField();
        loginButton = new JButton("로그인");
        registerButton = new JButton("회원가입");

        add(new JLabel("아이디:"));
        add(loginUserIdField);
        add(new JLabel("비밀번호:"));
        add(loginPasswordField);
        add(loginButton);
        add(registerButton);
    }

    // 로그인 정보 저장
    public String getUserId() {
        return loginUserIdField.getText();
    }

    public String getPassword() {
        return loginPasswordField.getText();
    }

    public void setUserId(String userId) {
        loginUserIdField.setText(userId);
    }

    public void setPassword(String password) {
        loginPasswordField.setText(password);
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
}
