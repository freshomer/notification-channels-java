/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.notificationchannels;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Random;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static com.example.android.notificationchannels.MainActivity.NOTIFICATION_FOLLOW;

/** Helper class to manage notification channels, and create notifications. */
@SuppressLint("NewApi")
class NotificationHelper extends ContextWrapper {
    private NotificationManager mNotificationManager;
    public static final String FOLLOWERS_CHANNEL = "follower";
    public static final String DIRECT_MESSAGE_CHANNEL = "direct_message";

    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    private static BroadcastReceiver mBroadcastReceiver = new TestBroadcastReceiver();

    private Context mContext;

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param context The application context
     */
    public NotificationHelper(Context context) {
        super(context);

        mContext = context;

        // Create the channel object with the unique ID FOLLOWERS_CHANNEL
        NotificationChannel followersChannel =
                new NotificationChannel(
                        FOLLOWERS_CHANNEL,
                        getString(R.string.notification_channel_followers),
                        NotificationManager.IMPORTANCE_DEFAULT);

        // Configure the channel's initial settings
        followersChannel.setLightColor(Color.GREEN);
        followersChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        followersChannel.setShowBadge(true);

        // Submit the notification channel object to the notification manager
        getNotificationManager().createNotificationChannel(followersChannel);

        // Do the same for the Direct Message channel
        NotificationChannel dmChannel =
                new NotificationChannel(
                        DIRECT_MESSAGE_CHANNEL,
                        getString(R.string.notification_channel_direct_message),
                        NotificationManager.IMPORTANCE_HIGH);
        dmChannel.setLightColor(Color.BLUE);
        dmChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        dmChannel.setShowBadge(true);
        getNotificationManager().createNotificationChannel(dmChannel);
        IntentFilter filter = new IntentFilter("com.example.android.action_snooze");
        context.registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * Get a follow/un-follow notification
     *
     * <p>Provide the builder rather than the notification it's self as useful for making
     * notification changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotificationFollower(String title, String body) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), FOLLOWERS_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());


        String replyLabel = "reply";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        Intent snoozeIntent = new Intent("com.example.android.action_snooze");
//        snoozeIntent.setAction("com.example.android.action_snooze");
//        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
//        snoozeIntent.setClass(mContext, TestBroadcastReceiver.class);

        // Build a PendingIntent for the reply action to trigger.
        int requestID = 10001;
//        PendingIntent replyPendingIntent =
//                PendingIntent.getBroadcast(mContext,
//                        requestID,
//                        snoozeIntent,
//                        0);

        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(mContext,
                        requestID,
                        snoozeIntent,
                        0);

        Notification.Action action = new Notification.Action.Builder(R.drawable.ic_launcher, "reply",
                replyPendingIntent).addRemoteInput(remoteInput).build();
        return builder.addAction(action);
//        return
//                .addAction((new Notification.Action.Builder(R.drawable.tile, )))
    }

    /**
     * Get a direct message notification
     *
     * <p>Provide the builder rather than the notification it's self as useful for making
     * notification changes.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotificationDM(String title, String body) {
        return new Notification.Builder(getApplicationContext(), DIRECT_MESSAGE_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
    }

    /**
     * Create a PendingIntent for opening up the MainActivity when the notification is pressed
     *
     * @return A PendingIntent that opens the MainActivity
     */
    private PendingIntent getPendingIntent() {
        Intent openMainIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(openMainIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
    }

    private PendingIntent getPendingIntent2() {
        Intent openMainIntent = new Intent(this, TestBroadcastReceiver.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(openMainIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getNotificationManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {
        return android.R.drawable.stat_notify_chat;
    }

    /**
     * Get the notification mNotificationManager.
     *
     * <p>Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    /**
     * Get a random name string from resources to add personalization to the notification
     *
     * @return A random name
     */
    public String getRandomName() {
        String[] names = getApplicationContext().getResources().getStringArray(R.array.names_array);
        return names[new Random().nextInt(names.length)];
    }

    public static class TestBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            CharSequence messageText = getMessageText(intent);

//            CharSequence[] oldHistory = sbn.getNotification().extras
//                    .getCharSequenceArray(Notification.EXTRA_REMOTE_INPUT_HISTORY);
            CharSequence[] oldHistory = null;
            CharSequence[] newHistory;
            if (oldHistory == null) {
                newHistory = new CharSequence[1];
            } else {
                newHistory = new CharSequence[oldHistory.length + 1];
                System.arraycopy(oldHistory, 0, newHistory, 1, oldHistory.length);
            }
            newHistory[0] = String.valueOf(messageText);

            // Build a new notification, which informs the user that the system
            // handled their interaction with the previous notification.
            Notification repliedNotification = new Notification.Builder(context, FOLLOWERS_CHANNEL)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentText("Received")
                    .setRemoteInputHistory(newHistory)
                    .build();

            // Issue the new notification.
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_FOLLOW, repliedNotification);
        }

        private CharSequence getMessageText(Intent intent) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                return remoteInput.getCharSequence(KEY_TEXT_REPLY);
            }
            return null;
        }
    }
}
