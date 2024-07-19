package dto;

public class Review {
    private int reviewId, productId, stars;
    private String content, custId;

    public Review() {};
    public Review(int reviewId, int productId, String content, int stars, String custId) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.content = content;
        this.stars = stars;
        this.custId = custId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
}
