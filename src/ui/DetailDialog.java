package ui;

import dao.CartDao;
import dao.ProductDao;
import dto.Product;
import dto.Review;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DetailDialog extends JDialog {
    private JLabel nameLabel;
    private JLabel brandLabel;
    private JLabel priceLabel;
    private JLabel contentLabel;
    private JLabel imageLabel, likeNumLabel, heartLabel;
    private ProductDao productDao = new ProductDao();
    private String userId;
    private DefaultTableModel reviewTableModel;

    public DetailDialog(Frame parent, Product product, String userId) {
        super(parent, "Product Details", true);
        setSize(600, 800);
        setLocationRelativeTo(parent);
        this.userId = userId;

        initComponents(product);
    }

    private void initComponents(Product product) {
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameLabel = new JLabel("<html><h2>게임명: " + product.getPdname() + "</h2></html>");
        brandLabel = new JLabel("<html><h3>제작사: " + product.getBrandName() + "</h3></html>");
        priceLabel = new JLabel("<html><h3>가격: " + product.getPrice() + " 원</h3></html>");
        contentLabel = new JLabel("<html><h4>소개</h4><p>" + product.getContent() + "</p></html>");

        heartLabel = new JLabel(product.getUserLiked() ? "♥" : "♡");
        likeNumLabel = new JLabel("좋아요 수: " + product.getLikeCount());

        String imagePath = product.getImage();
        ImageIcon imageIcon = new ImageIcon(imagePath);
        imageLabel = new JLabel(imageIcon);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(imageLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(nameLabel);
        infoPanel.add(brandLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(contentLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(heartLabel);
        infoPanel.add(likeNumLabel);

        JButton likeButton = new JButton("좋아요");
        JButton cartButton = new JButton("장바구니 추가");
        JButton addReviewButton = new JButton("댓글 등록");
        JButton deleteReviewButton = new JButton("댓글 삭제");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(likeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cartButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(addReviewButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(deleteReviewButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        buttonPanel.add(Box.createHorizontalGlue());

        // 좋아요 리스너
        likeButton.addActionListener(e -> {
            int count = productDao.countLikesProduct(product.getProductId());

            if (product.getUserLiked()) {
                // 이미 좋아요 눌렀으면
                if (productDao.deletelikeProduct(userId, product.getProductId())) {
                    heartLabel.setText("♡");
                    product.setUserLiked(false);
                    product.setLikeCount(product.getLikeCount() - 1);
                    likeNumLabel.setText("좋아요 수: " + product.getLikeCount());
                    JOptionPane.showMessageDialog(this, "좋아요 삭제 완료", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "좋아요 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (productDao.addlikeProduct(userId, product.getProductId())) {
                    heartLabel.setText("♥");
                    product.setUserLiked(true);
                    product.setLikeCount(product.getLikeCount() + 1);
                    likeNumLabel.setText("좋아요 수: " + product.getLikeCount());
                    JOptionPane.showMessageDialog(this, "좋아요 추가 완료", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "좋아요 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 장바구니 추가 리스너
        cartButton.addActionListener(e -> {
            CartDao cartDao = new CartDao();
            if (cartDao.addToCart(userId, product.getProductId())) {
                JOptionPane.showMessageDialog(this, "장바구니 추가 완료", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "장바구니 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 리뷰 테이블
        String[] columnNames = {"작성자", "내용", "별점"};
        reviewTableModel = new DefaultTableModel(columnNames, 0);
        JTable reviewTable = new JTable(reviewTableModel);
        JScrollPane reviewScrollPane = new JScrollPane(reviewTable);
        loadReviews(product.getProductId());

        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        detailsPanel.add(reviewScrollPane, BorderLayout.CENTER);
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(detailsPanel);

        // 리뷰 등록 리스너
        addReviewButton.addActionListener(e -> {
            String reviewContent = JOptionPane.showInputDialog(this, "댓글 내용을 입력하세요:", "댓글 등록", JOptionPane.PLAIN_MESSAGE);
            String stars = JOptionPane.showInputDialog(this, "별점을 입력하세요 (1-5):", "댓글 등록", JOptionPane.PLAIN_MESSAGE);

            if (reviewContent != null && stars != null && !reviewContent.isEmpty() && !stars.isEmpty()) {
                int starRating = Integer.parseInt(stars);
                Review review = new Review();
                review.setCustId(userId);
                review.setProductId(product.getProductId());
                review.setContent(reviewContent);
                review.setStars(starRating);

                if (productDao.addReview(review)) {
                    reviewTableModel.addRow(new Object[]{userId, reviewContent, starRating});
                    JOptionPane.showMessageDialog(this, "댓글 등록 완료", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "댓글 등록 실패", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 리뷰 삭제 리스너
        deleteReviewButton.addActionListener(e -> {
            int selectedRow = reviewTable.getSelectedRow();
            if (selectedRow != -1) {
                String reviewerId = (String) reviewTableModel.getValueAt(selectedRow, 0);
                if (reviewerId.equals(userId)) {
                    int productId = product.getProductId();
                    if (productDao.deleteReview(userId, productId)) {
                        reviewTableModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "댓글 삭제 완료", "성공", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "댓글 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "본인 댓글만 삭제 가능", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "삭제할 댓글 선택하세요", "오류", JOptionPane.WARNING_MESSAGE);
            }
        });


    }


    private void loadReviews(int productId) {
        List<Review> reviews = productDao.getReviewsByProductId(productId);
        for (Review review : reviews) {
            reviewTableModel.addRow(new Object[]{review.getCustId(), review.getContent(), review.getStars()});
        }
    }
}
