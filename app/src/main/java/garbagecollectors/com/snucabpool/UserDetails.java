package garbagecollectors.com.snucabpool;

public class UserDetails {
    static String username = "";
    static String password = "";
    static String chatWith = "";

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserDetails.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserDetails.password = password;
    }

    public static String getChatWith() {
        return chatWith;
    }

    public static void setChatWith(String chatWith) {
        UserDetails.chatWith = chatWith;
    }
}