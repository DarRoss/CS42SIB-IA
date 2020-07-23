package classes;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * AudioPlayer.java - Plays sound effects
 *
 * @author d.ross2
 */
public class AudioPlayer {

    /**
     * Plays a sound effect from a file
     *
     * @param sound given sound file
     */
    void playSound(File sound) {
        try {
            Clip clip = AudioSystem.getClip();
            // sound file is set as the clip
            clip.open(AudioSystem.getAudioInputStream(sound));
            // clip starts playing
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            // any errors become printed
            System.out.println(e);
        }
    }
}
