package com.example.websocketissue;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.neovisionaries.ws.client.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoAsyncStuff().execute();
            }
        });
    }
}

class DoAsyncStuff extends AsyncTask<String, Void, Void> {

    private static Logger _logger;

    public DoAsyncStuff() {
        _logger = Logger.getLogger("mylogger");
    }

    protected Void doInBackground(String... urls) {

        try {
            WebSocket ws = connect();
            ws.sendText("Marco");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static WebSocket connect() throws Exception {
        return new WebSocketFactory()
                .setConnectionTimeout(5000)
                .createSocket("wss://webapp-ws-test.azurewebsites.net/api/websocket")
                //.createSocket("wss://31445626.ngrok.io/api/websocket")
                .addListener(new WebSocketAdapter() {
                    // A text message arrived from the server.
                    public void onTextMessage(WebSocket websocket, String message) {
                        _logger.log(Level.INFO, message);
                    }

                    public void onDisconnected(WebSocket websocket,
                                               WebSocketFrame serverCloseFrame,
                                               WebSocketFrame clientCloseFrame,
                                               boolean closedByServer) throws Exception {
                        _logger.log(Level.INFO, "On Disconnect called.");
                    }

                    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        _logger.log(Level.INFO, "Received close frame.");
                        _logger.log(Level.INFO, frame.getPayloadText());
                    }

                    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                        _logger.log(Level.SEVERE, "An exception happened during the websocket operation");
                        _logger.log(Level.SEVERE, String.format("On Frame error %s", cause.getCause()));
                    }

                    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
                        _logger.log(Level.INFO, String.format("New state %s", newState.toString()));
                    }

                    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                        _logger.log(Level.SEVERE, String.format("On Frame error %s", cause.getCause()));
                    }

                })

                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }
}
