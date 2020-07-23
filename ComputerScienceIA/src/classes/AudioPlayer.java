package classes;

import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * AudioPlayer.java - Plays sound effects from mp3 files
 *
 * @author Darwin
 */
public class AudioPlayer {

    /**
     * Plays a sound effect from a file
     *
     * @param given sound file
     */
    void playSound(File sound) {
        try {
            // create an instance of a clip
            Clip clip = AudioSystem.getClip();
            // sound file is set as the clip
            clip.open(AudioSystem.getAudioInputStream(sound));
            // clip starts playing
            clip.start();
        } catch (Exception e) {
            // any errors become printed
            System.out.println(e);
        }
    }
}
