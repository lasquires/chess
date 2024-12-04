package ui;

import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
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
//            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
//                @Override
//                public void onMessage(String message) {
//
//                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
//
//                    observer.notify(notification);
//
//                }
//            });
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
//                    try {


                        // Parse the base ServerMessage to determine the type
                        ServerMessage baseMessage = new Gson().fromJson(message, ServerMessage.class);

                        switch (baseMessage.getServerMessageType()) {
                            case NOTIFICATION -> {
                                NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                                observer.notify(notification);
                            }
                            case ERROR -> {
                                ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
                                observer.notify(error);
                            }
                            case LOAD_GAME -> {
                                LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
                                observer.notify(loadGame);
                            }
                            default -> {
                                System.out.println("Unknown message type received: " + baseMessage.getServerMessageType());
                            }
                        }
//                    } catch (Exception e) {
//                        System.err.println("Failed to process message: " + message);
//                        e.printStackTrace();
//                    }
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

