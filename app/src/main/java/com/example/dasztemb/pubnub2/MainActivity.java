package com.example.dasztemb.pubnub2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    String channelName = "chees_info";
    public Activity activity;
    Button sendMessageButton;
    EditText messageEditText;
    boolean sentMessage = false;
    boolean canSend = true;
    PubNub pubnub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        activity = this;
            PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setSubscribeKey("sub-c-6dc72230-1758-11e9-af54-8afa0e558510");
            pnConfiguration.setPublishKey("pub-c-408aa89e-8631-4767-b8fc-0b694d7902ea");
            pnConfiguration.setSecure(false);

            pubnub = new PubNub(pnConfiguration);
            messageEditText = findViewById(R.id.messageInput);
            sendMessageButton = findViewById(R.id.sendMessage);
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (canSend) {
                        sentMessage = true;
                        publish(pubnub);
                    }
                }
            });

            subscribe(pubnub);

    }

    private void publish(PubNub pubnub) {
        JsonObject data = new JsonObject();

        try {
            String text = messageEditText.getText().toString();
            data.addProperty("msg", text);
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        pubnub.publish()
                .message(data)
                .channel(channelName)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status)
                    {

                    }
                });
    }

    private void subscribe(PubNub pubnub) {

        /* Subscribe to the demo_tutorial channel */
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status)  {
                if (status.getCategory() == PNStatusCategory.PNUnknownCategory) {
                    System.out.println(status.getErrorData());
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                final JsonElement receivedMessageObject = message.getMessage();
                System.out.println("Received message content: " + receivedMessageObject.toString());
                // extract desired parts of the payload, using Gson

                String msg = "";
                try {
                    // extract desired parts of the payload, using Gson

                    msg = message.getMessage().getAsJsonObject().get("msg").getAsString();
                }
                catch (NullPointerException e){

                }


                final String finalMsg = msg;
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if(sentMessage) { //his own message
                            canSend = false;
                            sentMessage = false;
                            return;
                        }

                        else{
                            canSend = true;
                            Toast.makeText(getApplication().getBaseContext(), finalMsg,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubnub.subscribe()
                .channels(Arrays.asList(channelName))
                .execute();
    }


}
