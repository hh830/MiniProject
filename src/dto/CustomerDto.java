package dto;

public class CustomerDto {
    private String userId;
    private String password;
    private String name;

    private int age;
    private String address;
    private String phone;

    public CustomerDto(){};
    public CustomerDto(String userId, String password, String name, int age, String address, String phone) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.age = age;
        this.address = address;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
