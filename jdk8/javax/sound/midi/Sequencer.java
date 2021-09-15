package javax.sound.midi;

import java.io.IOException;
import java.io.InputStream;

public interface Sequencer extends MidiDevice {
   int LOOP_CONTINUOUSLY = -1;

   void setSequence(Sequence var1) throws InvalidMidiDataException;

   void setSequence(InputStream var1) throws IOException, InvalidMidiDataException;

   Sequence getSequence();

   void start();

   void stop();

   boolean isRunning();

   void startRecording();

   void stopRecording();

   boolean isRecording();

   void recordEnable(Track var1, int var2);

   void recordDisable(Track var1);

   float getTempoInBPM();

   void setTempoInBPM(float var1);

   float getTempoInMPQ();

   void setTempoInMPQ(float var1);

   void setTempoFactor(float var1);

   float getTempoFactor();

   long getTickLength();

   long getTickPosition();

   void setTickPosition(long var1);

   long getMicrosecondLength();

   long getMicrosecondPosition();

   void setMicrosecondPosition(long var1);

   void setMasterSyncMode(Sequencer.SyncMode var1);

   Sequencer.SyncMode getMasterSyncMode();

   Sequencer.SyncMode[] getMasterSyncModes();

   void setSlaveSyncMode(Sequencer.SyncMode var1);

   Sequencer.SyncMode getSlaveSyncMode();

   Sequencer.SyncMode[] getSlaveSyncModes();

   void setTrackMute(int var1, boolean var2);

   boolean getTrackMute(int var1);

   void setTrackSolo(int var1, boolean var2);

   boolean getTrackSolo(int var1);

   boolean addMetaEventListener(MetaEventListener var1);

   void removeMetaEventListener(MetaEventListener var1);

   int[] addControllerEventListener(ControllerEventListener var1, int[] var2);

   int[] removeControllerEventListener(ControllerEventListener var1, int[] var2);

   void setLoopStartPoint(long var1);

   long getLoopStartPoint();

   void setLoopEndPoint(long var1);

   long getLoopEndPoint();

   void setLoopCount(int var1);

   int getLoopCount();

   public static class SyncMode {
      private String name;
      public static final Sequencer.SyncMode INTERNAL_CLOCK = new Sequencer.SyncMode("Internal Clock");
      public static final Sequencer.SyncMode MIDI_SYNC = new Sequencer.SyncMode("MIDI Sync");
      public static final Sequencer.SyncMode MIDI_TIME_CODE = new Sequencer.SyncMode("MIDI Time Code");
      public static final Sequencer.SyncMode NO_SYNC = new Sequencer.SyncMode("No Timing");

      protected SyncMode(String var1) {
         this.name = var1;
      }

      public final boolean equals(Object var1) {
         return super.equals(var1);
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public final String toString() {
         return this.name;
      }
   }
}
