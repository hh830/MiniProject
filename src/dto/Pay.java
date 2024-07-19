package dto;

public class Pay {
    private int payId;
    private int price;
    private String payInfo;
    private int status;

    private int orderId;

    public Pay() {};
    public Pay(int payId, int price, String payInfo, int status, int orderId) {
        this.payId = payId;
        this.price = price;
        this.payInfo = payInfo;
        this.status = status;
        this.orderId = orderId;
    }

    public int getPayId() {
        return payId;
    }

    public void setPayId(int payId) {
        this.payId = payId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
