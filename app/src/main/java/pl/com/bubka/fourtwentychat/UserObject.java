package pl.com.bubka.fourtwentychat;

public class UserObject {

    private String name;
    private String phoneNumber;
    private String uuid;

    public UserObject(String name, String phoneNumber, String uuid) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
