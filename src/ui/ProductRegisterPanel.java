package ui;

import dto.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductRegisterPanel extends JPanel {
    private JTextField nameTextField;
    private JTextField priceTextField, imageTextField;
    private JTextArea contentTextArea;

    private JTextField brandTextField;
    private String selectedImagePath;
    private JButton registerButton;

    public ProductRegisterPanel(Frame parent) {
        setLayout(new GridLayout(9, 2));

        add(new JLabel("상품명:"));
        nameTextField = new JTextField();
        add(nameTextField);

        add(new JLabel("가격:"));
        priceTextField = new JTextField();
        add(priceTextField);

        add(new JLabel("설명:"));
        contentTextArea = new JTextArea();
        add(contentTextArea);

        add(new JLabel("브랜드:"));
        brandTextField = new JTextField();
        add(brandTextField);

        JButton imageButton = new JButton("이미지 선택");
        imageButton.addActionListener(e -> chooseImage());
        add(imageButton);

        registerButton = new JButton("등록");
        add(registerButton);
    }

    public void addRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public Product getProductDetails() {
        String productName = nameTextField.getText();
        String brandName = brandTextField.getText();

        int price = Integer.parseInt(priceTextField.getText());
        String content = contentTextArea.getText();
        String imagePath = selectedImagePath;

        Product product = new Product();
        product.setPdname(productName);
        product.setContent(content);
        product.setImage(imagePath);
        product.setPrice(price);
        product.setBrandName(brandName);

        return product;
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
        }
    }
}
