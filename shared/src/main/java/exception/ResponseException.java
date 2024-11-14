package exception;

public class ResponseException extends Exception {

    public ResponseException(int statusCode, String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
