package ui;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.*;

@ClientEndpoint
public class WebSocketCommunicator extends Endpoint{
    private URI serverUri;
    private Session session;
    private ServerMessageObserver observer;


    public WebSocketCommunicator(String baseUrl, ServerMessageObserver observer) {
        this.observer = observer;
        String url = baseUrl;
        try {
            url = url.replace("http", "ws");
            serverUri = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, serverUri);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    observer.notify(notification);
                }
            });



        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection opened.");
    }


    public void sendMessage(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
                System.out.println("Sent message: " + message);
            } else {
                throw new IllegalStateException("WebSocket session is not connected.");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void close() throws IOException {
        if (session != null) {
            session.close();
            System.out.println("WebSocket connection closed.");
        }
//    public String sendWSMessage(String message){
//
//            return this.sendMessage(message);
//
//    }

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
//    public void connect()  {
//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        //TODO: if this throws errors, remove try/catch
//        try {
//            session = container.connectToServer(this, serverUri);
//        } catch (DeploymentException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
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
//        System.err.println("WebSocket error occurred:");
//        throwable.printStackTrace();
//    }
//
//    public void sendMessage(String message) {
//        if (session != null && session.isOpen()) {
//            session.getAsyncRemote().sendText(message);
//            System.out.println("Sent message: " + message);
//        } else {
//            throw new IllegalStateException("WebSocket session is not connected.");
//        }
//    }
////
////    public void addObserver(ServerMessageObserver observer) {
////        observers.add(observer);
////    }
//
//    private void notifyObservers(ServerMessage message) {
////        for (ServerMessageObserver observer : observers) {
//            observer.notify(message);
////        }
//    }

    }
}

