package com.example.android.notificationchannels;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationStyleActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int NOTIFICATION_FOLLOW = 1100;
    private static final int NOTIFICATION_UNFOLLOW = 1101;
    private static final int NOTIFICATION_DM_FRIEND = 1200;
    private static final int NOTIFICATION_DM_COWORKER = 1201;

    /*
     * A view model for interacting with the UI elements.
     */
    private NotificationStyleActivity.MainUi mUIModel;

    /*
     * A helper class for initializing notification channels and sending notifications.
     */
    private NotificationHelper mNotificationHelper;

    public static NotificationStyleActivity sInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_style);
        sInstance = this;
        mNotificationHelper = new NotificationHelper(this);
        mUIModel = new NotificationStyleActivity.MainUi(findViewById(R.id.activity_main));
        if ("com.example.android.action_snooze".equals(getIntent().getAction())) {
            Toast.makeText(this, "CUSTOM ACTION RECEIVED", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("com.example.android.action_snooze".equals(intent.getAction())) {
            Toast.makeText(this, "CUSTOM ACTION RECEIVED", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Send activity notifications.
     *
     * @param id The ID of the notification to create
     */
    private void sendNotification(int id) {
        Notification.Builder notificationBuilder = null;
        switch (id) {
            case NOTIFICATION_FOLLOW:
                notificationBuilder =
                        mNotificationHelper.getNotificationFollower(
                                "Direct reply",
                                "Hi, please reply directly");
                break;

            case NOTIFICATION_UNFOLLOW:
                notificationBuilder =
                        mNotificationHelper.getNotificationFollower(
                                "Reply with choices",
                                "These are the choices",
                                true);
                break;

            case NOTIFICATION_DM_FRIEND:
                notificationBuilder =
                        mNotificationHelper.getNotificationDM(
                                getString(R.string.direct_message_title_notification),
                                getString(R.string.dm_friend_notification_body,
                                        mNotificationHelper.getRandomName()));
                break;
        }
        if (notificationBuilder != null) {
            mNotificationHelper.notify(NOTIFICATION_FOLLOW, notificationBuilder);
        }
    }

    /** Send Intent to load system Notification Settings for this app. */
    private void goToNotificationSettings() {
        Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(i);
    }

    /**
     * Send intent to load system Notification Settings UI for a particular channel.
     *
     * @param channel Name of channel to configure
     */
    private void goToNotificationChannelSettings(String channel) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
        startActivity(intent);
    }

    public void setReceivedMessage(CharSequence charSequence) {
        ((TextView) findViewById(R.id.received_message)).setText("Received text: " + charSequence);
    }

    /**
     * View model for interacting with Activity UI elements. (Keeps core logic for sample separate.)
     */
    class MainUi implements View.OnClickListener {

        private MainUi(View root) {

            // Setup the buttons
            (root.findViewById(R.id.notification_reply_button)).setOnClickListener(this);
            (root.findViewById(R.id.notification_with_choice_button)).setOnClickListener(this);
            (root.findViewById(R.id.action_test_button)).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.notification_reply_button:
                    sendNotification(NOTIFICATION_FOLLOW);
                    break;
                case R.id.notification_with_choice_button:
                    sendNotification(NOTIFICATION_UNFOLLOW);
                    break;
                case R.id.action_test_button:
                    sendNotification(NOTIFICATION_DM_FRIEND);
                    break;
            }
        }
    }
}
