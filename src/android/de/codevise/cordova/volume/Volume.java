package de.codevise.cordova.volume;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.content.Context;
import android.media.AudioManager;
import android.database.ContentObserver;
import android.provider.Settings.System;

public class Volume extends CordovaPlugin {

    private static final int STREAM = AudioManager.STREAM_MUSIC;
    private int stream = STREAM;

    public class SettingsContentObserver extends ContentObserver {

        private double previousVolume;

        public SettingsContentObserver(Handler handler) {
            super(handler);
            previousVolume = currentVolume();
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            double currentVolume = currentVolume();

            double delta= previousVolume - currentVolume;

            if(delta != 0) {
                triggerChangedEvent(currentVolume);
            }

            previousVolume=currentVolume;
        }
    }

    private Context context;

    private CallbackContext changedEventCallback = null;

    @Override
    public void initialize (CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = super.cordova.getActivity().getApplicationContext();

        SettingsContentObserver observer = new SettingsContentObserver(new Handler());
        context.getContentResolver().registerContentObserver(System.CONTENT_URI, true, observer);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("setStream".equals(action)) {
            stream = args.length() > 0 ? args.getInt(0) : STREAM;
            return true;
        } else if ("getVolume".equals(action)) {
            triggerEvent(callbackContext, currentVolume(), false);
            return true;
        } else if ("setVolumenChangeCallback".equals(action)) {
            changedEventCallback = callbackContext;
            return true;
        }

        return false;
    }

    private double currentVolume() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(stream);
        if (volume == 0) {
            return 0.0;
        }
        return 1.0 * volume / audioManager.getStreamMaxVolume(stream);
    }

    private void triggerChangedEvent(double volume) {
        if (changedEventCallback != null) {
            triggerEvent(changedEventCallback, volume, true);
        }
    }

    private void triggerEvent(CallbackContext callback, double volume, boolean keepCallback) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, (float) volume);
        result.setKeepCallback(keepCallback);
        callback.sendPluginResult(result);
    }

}
