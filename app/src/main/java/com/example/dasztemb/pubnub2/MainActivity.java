package com.example.dasztemb.pubnub2;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    String channelName = "chees_info";
    public Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-83372660-173f-11e9-923b-9ef472141036");
        pnConfiguration.setPublishKey("pub-c-58619992-ec49-4a13-841f-73312bcb4af1");
        pnConfiguration.setSecure(false);

        PubNub pubnub = new PubNub(pnConfiguration);


        subscribe(pubnub);
        publish(pubnub);


    }

    private void publish(PubNub pubnub) {
        JsonObject data = new JsonObject();

        try {
            data.addProperty("msg", "blue");
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
                        Toast.makeText(getApplication().getBaseContext(), finalMsg,Toast.LENGTH_SHORT).show();
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
