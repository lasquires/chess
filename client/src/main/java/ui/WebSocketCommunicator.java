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
//                System.out.println("Sent message: " + message);
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


    }
}

