package com.biz.navimate.misc;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import com.biz.navimate.debug.Dbg;

import java.io.IOException;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class RingtoneHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "RINGTONE_HELPER";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static MediaPlayer mediaPlayer         = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public static void PlayNotificationSound(Context context)
    {
        if (mediaPlayer == null)
        {
            mediaPlayer = new MediaPlayer();
        }

        // Check if ringtone is already playing
        if (mediaPlayer.isPlaying())
        {
            Dbg.error(TAG, "Alarm already playing. not starting again");
            return;
        }

        // Reset media player
        mediaPlayer.reset();

        try
        {
            // Init ringtone properties
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mediaPlayer.setDataSource(context, ringtoneUri);

            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
            {
                // Play sound
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }
        catch (IOException e)
        {
            Dbg.error(TAG, "IO exception while setting initData source for media player");
            Dbg.stack(e);
        }
    }

    // ----------------------- Private APIs ----------------------- //
}
