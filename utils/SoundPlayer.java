package sortingvisualizer.utils;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;

/**
 * High-performance Sound Player using pre-opened Clip Pools in RAM to achieve
 * zero-latency, perfectly synchronized audio feedback. Supports separate sounds
 * for comparison and swap events, falling back to MIDI if WAV files are missing.
 */
public class SoundPlayer {
    // WAV player fields with pre-opened Clip Pools for compare and swap sounds
    private Clip[] compareClipPool;
    private Clip[] swapClipPool;
    private int comparePoolIndex = 0;
    private int swapPoolIndex = 0;
    private AudioFormat compareFormat;
    private AudioFormat swapFormat;
    private boolean useWav = false;

    // MIDI player fields (fallback)
    private Synthesizer synth;
    private MidiChannel midiChannel;

    private boolean enabled = true;

    public SoundPlayer() {
        // Try loading piano.wav for compare sound and swap.wav for swap sound, pre-opening pools
        try {
            File compareFile = new File("piano.wav");
            File swapFile = new File("swap.wav");
            if (compareFile.exists() && swapFile.exists()) {
                // Load compare wav
                AudioInputStream compareIn = AudioSystem.getAudioInputStream(compareFile);
                compareFormat = compareIn.getFormat();
                byte[] compareBytes = readAllBytes(compareIn);
                compareIn.close();
                compareClipPool = new Clip[25];
                for (int i = 0; i < compareClipPool.length; i++) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(compareBytes);
                    AudioInputStream ais = new AudioInputStream(bais, compareFormat, compareBytes.length / compareFormat.getFrameSize());
                    compareClipPool[i] = AudioSystem.getClip();
                    compareClipPool[i].open(ais);
                }
                // Load swap wav
                AudioInputStream swapIn = AudioSystem.getAudioInputStream(swapFile);
                swapFormat = swapIn.getFormat();
                byte[] swapBytes = readAllBytes(swapIn);
                swapIn.close();
                swapClipPool = new Clip[10];
                for (int i = 0; i < swapClipPool.length; i++) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(swapBytes);
                    AudioInputStream ais = new AudioInputStream(bais, swapFormat, swapBytes.length / swapFormat.getFrameSize());
                    swapClipPool[i] = AudioSystem.getClip();
                    swapClipPool[i].open(ais);
                }
                useWav = true;
            }
        } catch (Exception ignored) {
            useWav = false;
            compareClipPool = null;
            swapClipPool = null;
        }

        // Setup MIDI fallback if WAV pool initialization fails
        if (!useWav) {
            try {
                synth = MidiSystem.getSynthesizer();
                synth.open();
                midiChannel = synth.getChannels()[0];
                midiChannel.programChange(0); // Acoustic Grand Piano
            } catch (Exception ignored) {}
        }
    }

    // Helper to read all bytes from an AudioInputStream
    private byte[] readAllBytes(AudioInputStream ais) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = ais.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * Plays a pitch‑modulated compare sound based on the element value.
     */
    public synchronized void playCompare(int value) {
        if (!enabled) return;
        if (useWav && compareClipPool != null) {
            try {
                Clip clip = compareClipPool[comparePoolIndex];
                comparePoolIndex = (comparePoolIndex + 1) % compareClipPool.length;
                clip.stop();
                clip.setFramePosition(0);
                if (clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
                    FloatControl rateCtrl = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
                    float baseRate = compareFormat.getSampleRate();
                    float factor = 0.5f + (value / 400.0f) * 1.5f; // 0.5x to 2x speed
                    float newRate = baseRate * factor;
                    newRate = Math.max(rateCtrl.getMinimum(), Math.min(rateCtrl.getMaximum(), newRate));
                    rateCtrl.setValue(newRate);
                }
                clip.start();
            } catch (Exception ignored) {}
        } else if (midiChannel != null) {
            int pitch = 40 + (int) ((value / 400.0) * 55);
            pitch = Math.max(40, Math.min(95, pitch));
            midiChannel.noteOn(pitch, 65);
        }
    }

    /**
     * Plays the swap sound (no pitch modulation).
     */
    public synchronized void playSwap() {
        if (!enabled) return;
        if (useWav && swapClipPool != null) {
            try {
                Clip clip = swapClipPool[swapPoolIndex];
                swapPoolIndex = (swapPoolIndex + 1) % swapClipPool.length;
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception ignored) {}
        } else if (midiChannel != null) {
            // Fixed pitch for swap fallback
            midiChannel.noteOn(60, 70);
        }
    }

    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            if (compareClipPool != null) {
                for (Clip clip : compareClipPool) {
                    if (clip != null && clip.isRunning()) {
                        clip.stop();
                    }
                }
            }
            if (swapClipPool != null) {
                for (Clip clip : swapClipPool) {
                    if (clip != null && clip.isRunning()) {
                        clip.stop();
                    }
                }
            }
            if (midiChannel != null) {
                midiChannel.allNotesOff();
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
