package br.com.anderson.aplicativobase;

/**
 * Created by DevMaker on 7/12/16.
 */
public class Message {
    private String userFrom;
    private String userTo;
    private String message;

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
