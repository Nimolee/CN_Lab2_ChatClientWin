package sample;

public class message {
    private String date;
    private String name;
    private String message;

    public message(String msg) {
        this.date = msg.split("\n")[1];
        this.name = msg.split("\n")[2];
        this.message = msg.split("\n")[3];
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
