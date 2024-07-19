package dto;

public class Cart {
    private int cartId;
    private int productId;
    private String pdName;
    private int quantity;
    private int price;
    private String userId;

    public Cart(){};
    public Cart(int cartId, int quantity, int productId, String pdName, int price, String userId) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.userId = userId;
        this.pdName = pdName;
        this.price = price;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPdName() {
        return pdName;
    }

    public void setPdName(String pdName) {
        this.pdName = pdName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
