package pl.com.bubka.fourtwentychat;

public class ChatObject {
    private String chatId;

    public ChatObject(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
