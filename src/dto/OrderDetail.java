package dto;

public class OrderDetail {
    private int detailId, num, price, brandId, orderId, productId;
    private String brandName, productName;

    public OrderDetail() {};
    public OrderDetail(int detailId, int num, int price, int brandId, String brandName, int orderId, int productId, String productName) {
        this.detailId = detailId;
        this.num = num;
        this.price = price;
        this.brandId = brandId;
        this.orderId = orderId;
        this.productId = productId;
        this.brandName = brandName;
        this.productName = productName;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
