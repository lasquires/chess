package communication;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void help(){
        System.out.println("♕ Welcome to Chess. Sign in to start. ♕");
        System.out.println("Options:");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
    }
}
