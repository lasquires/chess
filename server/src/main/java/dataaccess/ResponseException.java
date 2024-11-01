package dataaccess;

public class ResponseException extends RuntimeException {
  final private int statusCode;

  public ResponseException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

}
