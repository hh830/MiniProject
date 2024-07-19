package ui;

import dto.CustomerDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterPanel extends JPanel {
    private JTextField registerUserIdField;
    private JTextField registerPasswordField;
    private JTextField registerNameField;
    private JTextField registerAgeField;
    private JTextField registerAddressField;
    private JTextField registerPhoneField;
    private JButton submitRegisterButton;
    private JButton backToLoginButton;

    public RegisterPanel() {
        setLayout(new GridLayout(7, 2));

        registerUserIdField = new JTextField();
        registerPasswordField = new JPasswordField();
        registerNameField = new JTextField();
        registerAgeField = new JTextField();
        registerAddressField = new JTextField();
        registerPhoneField = new JTextField();
        submitRegisterButton = new JButton("회원가입");
        backToLoginButton = new JButton("로그인으로");

        add(new JLabel("아이디:"));
        add(registerUserIdField);
        add(new JLabel("비밀번호:"));
        add(registerPasswordField);
        add(new JLabel("이름:"));
        add(registerNameField);
        add(new JLabel("나이:"));
        add(registerAgeField);
        add(new JLabel("주소:"));
        add(registerAddressField);
        add(new JLabel("전화번호:"));
        add(registerPhoneField);
        add(submitRegisterButton);
        add(backToLoginButton);
    }

    public CustomerDto getCustomerDto() {
        String id = registerUserIdField.getText();
        String password = registerPasswordField.getText();
        String name = registerNameField.getText();
        int age = Integer.parseInt(registerAgeField.getText());
        String address = registerAddressField.getText();
        String phone = registerPhoneField.getText();
        return new CustomerDto(id, password, name, age, address, phone);
    }

    public void addSubmitRegisterListener(ActionListener listener) {
        submitRegisterButton.addActionListener(listener);
    }

    public void addBackToLoginListener(ActionListener listener) {
        backToLoginButton.addActionListener(listener);
    }
}
