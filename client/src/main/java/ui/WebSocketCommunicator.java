package ui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.*;


public class WebSocketCommunicator {
    private URI serverUri;
    private Session session;
    private ServerMessageObserver observer;


    public WebSocketCommunicator(String baseUrl, ServerMessageObserver serverMessageObserver) {
        observer = serverMessageObserver;
        String url = baseUrl;
        try {
            url = url.replace("http", "ws");
            serverUri = new URI(url + "/ws");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }


//    private void connectWebSocket(){
//        String url = serverUrl;
//        try {
//            url = url.replace("http", "ws");
//            URI uri = new URI(url + "/ws");
//            webSocketCommunicator = new WebSocketCommunicator(uri);
//            webSocketCommunicator.connect();
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }

//
//
    public void connect()  {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        //TODO: if this throws errors, remove try/catch
        try {
            session = container.connectToServer(this, serverUri);
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("Connected to server");
//    }
//
//    @OnMessage
//    public void onMessage(String message) {
//        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//        notifyObservers(serverMessage);
//    }
//
//    @OnClose
//    public void onClose(Session session, CloseReason reason) {
//        System.out.println("Connection closed: " + reason.getReasonPhrase());
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        throwable.printStackTrace();
//    }
//
    public void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }
//
//    public void addObserver(ServerMessageObserver observer) {
//        observers.add(observer);
//    }

//    private void notifyObservers(ServerMessage message) {
//        for (ServerMessageObserver observer : observers) {
//            observer.notify(message);
//        }
//    }

}

