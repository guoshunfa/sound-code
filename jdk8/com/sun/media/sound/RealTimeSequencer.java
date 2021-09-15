package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

final class RealTimeSequencer extends AbstractMidiDevice implements Sequencer, AutoConnectSequencer {
   private static final boolean DEBUG_PUMP = false;
   private static final boolean DEBUG_PUMP_ALL = false;
   private static final Map<ThreadGroup, EventDispatcher> dispatchers = new WeakHashMap();
   static final RealTimeSequencer.RealTimeSequencerInfo info = new RealTimeSequencer.RealTimeSequencerInfo();
   private static final Sequencer.SyncMode[] masterSyncModes;
   private static final Sequencer.SyncMode[] slaveSyncModes;
   private static final Sequencer.SyncMode masterSyncMode;
   private static final Sequencer.SyncMode slaveSyncMode;
   private Sequence sequence = null;
   private double cacheTempoMPQ = -1.0D;
   private float cacheTempoFactor = -1.0F;
   private boolean[] trackMuted = null;
   private boolean[] trackSolo = null;
   private final MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();
   private boolean running = false;
   private RealTimeSequencer.PlayThread playThread;
   private boolean recording = false;
   private final List recordingTracks = new ArrayList();
   private long loopStart = 0L;
   private long loopEnd = -1L;
   private int loopCount = 0;
   private final ArrayList metaEventListeners = new ArrayList();
   private final ArrayList controllerEventListeners = new ArrayList();
   private boolean autoConnect = false;
   private boolean doAutoConnectAtNextOpen = false;
   Receiver autoConnectedReceiver = null;

   RealTimeSequencer() throws MidiUnavailableException {
      super(info);
   }

   public synchronized void setSequence(Sequence var1) throws InvalidMidiDataException {
      if (var1 != this.sequence) {
         if (this.sequence != null && var1 == null) {
            this.setCaches();
            this.stop();
            this.trackMuted = null;
            this.trackSolo = null;
            this.loopStart = 0L;
            this.loopEnd = -1L;
            this.loopCount = 0;
            if (this.getDataPump() != null) {
               this.getDataPump().setTickPos(0L);
               this.getDataPump().resetLoopCount();
            }
         }

         if (this.playThread != null) {
            this.playThread.setSequence(var1);
         }

         this.sequence = var1;
         if (var1 != null) {
            this.tempoCache.refresh(var1);
            this.setTickPosition(0L);
            this.propagateCaches();
         }
      } else if (var1 != null) {
         this.tempoCache.refresh(var1);
         if (this.playThread != null) {
            this.playThread.setSequence(var1);
         }
      }

   }

   public synchronized void setSequence(InputStream var1) throws IOException, InvalidMidiDataException {
      if (var1 == null) {
         this.setSequence((Sequence)null);
      } else {
         Sequence var2 = MidiSystem.getSequence(var1);
         this.setSequence(var2);
      }
   }

   public Sequence getSequence() {
      return this.sequence;
   }

   public synchronized void start() {
      if (!this.isOpen()) {
         throw new IllegalStateException("sequencer not open");
      } else if (this.sequence == null) {
         throw new IllegalStateException("sequence not set");
      } else if (!this.running) {
         this.implStart();
      }
   }

   public synchronized void stop() {
      if (!this.isOpen()) {
         throw new IllegalStateException("sequencer not open");
      } else {
         this.stopRecording();
         if (this.running) {
            this.implStop();
         }
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   public void startRecording() {
      if (!this.isOpen()) {
         throw new IllegalStateException("Sequencer not open");
      } else {
         this.start();
         this.recording = true;
      }
   }

   public void stopRecording() {
      if (!this.isOpen()) {
         throw new IllegalStateException("Sequencer not open");
      } else {
         this.recording = false;
      }
   }

   public boolean isRecording() {
      return this.recording;
   }

   public void recordEnable(Track var1, int var2) {
      if (!this.findTrack(var1)) {
         throw new IllegalArgumentException("Track does not exist in the current sequence");
      } else {
         synchronized(this.recordingTracks) {
            RealTimeSequencer.RecordingTrack var4 = RealTimeSequencer.RecordingTrack.get(this.recordingTracks, var1);
            if (var4 != null) {
               var4.channel = var2;
            } else {
               this.recordingTracks.add(new RealTimeSequencer.RecordingTrack(var1, var2));
            }

         }
      }
   }

   public void recordDisable(Track var1) {
      synchronized(this.recordingTracks) {
         RealTimeSequencer.RecordingTrack var3 = RealTimeSequencer.RecordingTrack.get(this.recordingTracks, var1);
         if (var3 != null) {
            this.recordingTracks.remove(var3);
         }

      }
   }

   private boolean findTrack(Track var1) {
      boolean var2 = false;
      if (this.sequence != null) {
         Track[] var3 = this.sequence.getTracks();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var1 == var3[var4]) {
               var2 = true;
               break;
            }
         }
      }

      return var2;
   }

   public float getTempoInBPM() {
      return (float)MidiUtils.convertTempo((double)this.getTempoInMPQ());
   }

   public void setTempoInBPM(float var1) {
      if (var1 <= 0.0F) {
         var1 = 1.0F;
      }

      this.setTempoInMPQ((float)MidiUtils.convertTempo((double)var1));
   }

   public float getTempoInMPQ() {
      if (this.needCaching()) {
         if (this.cacheTempoMPQ != -1.0D) {
            return (float)this.cacheTempoMPQ;
         } else {
            return this.sequence != null ? this.tempoCache.getTempoMPQAt(this.getTickPosition()) : 500000.0F;
         }
      } else {
         return this.getDataPump().getTempoMPQ();
      }
   }

   public void setTempoInMPQ(float var1) {
      if (var1 <= 0.0F) {
         var1 = 1.0F;
      }

      if (this.needCaching()) {
         this.cacheTempoMPQ = (double)var1;
      } else {
         this.getDataPump().setTempoMPQ(var1);
         this.cacheTempoMPQ = -1.0D;
      }

   }

   public void setTempoFactor(float var1) {
      if (var1 > 0.0F) {
         if (this.needCaching()) {
            this.cacheTempoFactor = var1;
         } else {
            this.getDataPump().setTempoFactor(var1);
            this.cacheTempoFactor = -1.0F;
         }

      }
   }

   public float getTempoFactor() {
      if (this.needCaching()) {
         return this.cacheTempoFactor != -1.0F ? this.cacheTempoFactor : 1.0F;
      } else {
         return this.getDataPump().getTempoFactor();
      }
   }

   public long getTickLength() {
      return this.sequence == null ? 0L : this.sequence.getTickLength();
   }

   public synchronized long getTickPosition() {
      return this.getDataPump() != null && this.sequence != null ? this.getDataPump().getTickPos() : 0L;
   }

   public synchronized void setTickPosition(long var1) {
      if (var1 >= 0L) {
         if (this.getDataPump() == null) {
            if (var1 != 0L) {
            }
         } else if (this.sequence == null) {
            if (var1 != 0L) {
            }
         } else {
            this.getDataPump().setTickPos(var1);
         }

      }
   }

   public long getMicrosecondLength() {
      return this.sequence == null ? 0L : this.sequence.getMicrosecondLength();
   }

   public long getMicrosecondPosition() {
      if (this.getDataPump() != null && this.sequence != null) {
         synchronized(this.tempoCache) {
            return MidiUtils.tick2microsecond(this.sequence, this.getDataPump().getTickPos(), this.tempoCache);
         }
      } else {
         return 0L;
      }
   }

   public void setMicrosecondPosition(long var1) {
      if (var1 >= 0L) {
         if (this.getDataPump() == null) {
            if (var1 != 0L) {
            }
         } else if (this.sequence == null) {
            if (var1 != 0L) {
            }
         } else {
            synchronized(this.tempoCache) {
               this.setTickPosition(MidiUtils.microsecond2tick(this.sequence, var1, this.tempoCache));
            }
         }

      }
   }

   public void setMasterSyncMode(Sequencer.SyncMode var1) {
   }

   public Sequencer.SyncMode getMasterSyncMode() {
      return masterSyncMode;
   }

   public Sequencer.SyncMode[] getMasterSyncModes() {
      Sequencer.SyncMode[] var1 = new Sequencer.SyncMode[masterSyncModes.length];
      System.arraycopy(masterSyncModes, 0, var1, 0, masterSyncModes.length);
      return var1;
   }

   public void setSlaveSyncMode(Sequencer.SyncMode var1) {
   }

   public Sequencer.SyncMode getSlaveSyncMode() {
      return slaveSyncMode;
   }

   public Sequencer.SyncMode[] getSlaveSyncModes() {
      Sequencer.SyncMode[] var1 = new Sequencer.SyncMode[slaveSyncModes.length];
      System.arraycopy(slaveSyncModes, 0, var1, 0, slaveSyncModes.length);
      return var1;
   }

   int getTrackCount() {
      Sequence var1 = this.getSequence();
      return var1 != null ? this.sequence.getTracks().length : 0;
   }

   public synchronized void setTrackMute(int var1, boolean var2) {
      int var3 = this.getTrackCount();
      if (var1 >= 0 && var1 < this.getTrackCount()) {
         this.trackMuted = ensureBoolArraySize(this.trackMuted, var3);
         this.trackMuted[var1] = var2;
         if (this.getDataPump() != null) {
            this.getDataPump().muteSoloChanged();
         }

      }
   }

   public synchronized boolean getTrackMute(int var1) {
      if (var1 >= 0 && var1 < this.getTrackCount()) {
         return this.trackMuted != null && this.trackMuted.length > var1 ? this.trackMuted[var1] : false;
      } else {
         return false;
      }
   }

   public synchronized void setTrackSolo(int var1, boolean var2) {
      int var3 = this.getTrackCount();
      if (var1 >= 0 && var1 < this.getTrackCount()) {
         this.trackSolo = ensureBoolArraySize(this.trackSolo, var3);
         this.trackSolo[var1] = var2;
         if (this.getDataPump() != null) {
            this.getDataPump().muteSoloChanged();
         }

      }
   }

   public synchronized boolean getTrackSolo(int var1) {
      if (var1 >= 0 && var1 < this.getTrackCount()) {
         return this.trackSolo != null && this.trackSolo.length > var1 ? this.trackSolo[var1] : false;
      } else {
         return false;
      }
   }

   public boolean addMetaEventListener(MetaEventListener var1) {
      synchronized(this.metaEventListeners) {
         if (!this.metaEventListeners.contains(var1)) {
            this.metaEventListeners.add(var1);
         }

         return true;
      }
   }

   public void removeMetaEventListener(MetaEventListener var1) {
      synchronized(this.metaEventListeners) {
         int var3 = this.metaEventListeners.indexOf(var1);
         if (var3 >= 0) {
            this.metaEventListeners.remove(var3);
         }

      }
   }

   public int[] addControllerEventListener(ControllerEventListener var1, int[] var2) {
      synchronized(this.controllerEventListeners) {
         RealTimeSequencer.ControllerListElement var4 = null;
         boolean var5 = false;

         for(int var6 = 0; var6 < this.controllerEventListeners.size(); ++var6) {
            var4 = (RealTimeSequencer.ControllerListElement)this.controllerEventListeners.get(var6);
            if (var4.listener.equals(var1)) {
               var4.addControllers(var2);
               var5 = true;
               break;
            }
         }

         if (!var5) {
            var4 = new RealTimeSequencer.ControllerListElement(var1, var2);
            this.controllerEventListeners.add(var4);
         }

         return var4.getControllers();
      }
   }

   public int[] removeControllerEventListener(ControllerEventListener var1, int[] var2) {
      synchronized(this.controllerEventListeners) {
         RealTimeSequencer.ControllerListElement var4 = null;
         boolean var5 = false;

         int var6;
         for(var6 = 0; var6 < this.controllerEventListeners.size(); ++var6) {
            var4 = (RealTimeSequencer.ControllerListElement)this.controllerEventListeners.get(var6);
            if (var4.listener.equals(var1)) {
               var4.removeControllers(var2);
               var5 = true;
               break;
            }
         }

         if (!var5) {
            return new int[0];
         } else if (var2 == null) {
            var6 = this.controllerEventListeners.indexOf(var4);
            if (var6 >= 0) {
               this.controllerEventListeners.remove(var6);
            }

            return new int[0];
         } else {
            return var4.getControllers();
         }
      }
   }

   public void setLoopStartPoint(long var1) {
      if (var1 <= this.getTickLength() && (this.loopEnd == -1L || var1 <= this.loopEnd) && var1 >= 0L) {
         this.loopStart = var1;
      } else {
         throw new IllegalArgumentException("invalid loop start point: " + var1);
      }
   }

   public long getLoopStartPoint() {
      return this.loopStart;
   }

   public void setLoopEndPoint(long var1) {
      if (var1 <= this.getTickLength() && (this.loopStart <= var1 || var1 == -1L) && var1 >= -1L) {
         this.loopEnd = var1;
      } else {
         throw new IllegalArgumentException("invalid loop end point: " + var1);
      }
   }

   public long getLoopEndPoint() {
      return this.loopEnd;
   }

   public void setLoopCount(int var1) {
      if (var1 != -1 && var1 < 0) {
         throw new IllegalArgumentException("illegal value for loop count: " + var1);
      } else {
         this.loopCount = var1;
         if (this.getDataPump() != null) {
            this.getDataPump().resetLoopCount();
         }

      }
   }

   public int getLoopCount() {
      return this.loopCount;
   }

   protected void implOpen() throws MidiUnavailableException {
      this.playThread = new RealTimeSequencer.PlayThread();
      if (this.sequence != null) {
         this.playThread.setSequence(this.sequence);
      }

      this.propagateCaches();
      if (this.doAutoConnectAtNextOpen) {
         this.doAutoConnect();
      }

   }

   private void doAutoConnect() {
      Receiver var1 = null;

      try {
         Synthesizer var2 = MidiSystem.getSynthesizer();
         if (var2 instanceof ReferenceCountingDevice) {
            var1 = ((ReferenceCountingDevice)var2).getReceiverReferenceCounting();
         } else {
            var2.open();

            try {
               var1 = var2.getReceiver();
            } finally {
               if (var1 == null) {
                  var2.close();
               }

            }
         }
      } catch (Exception var11) {
      }

      if (var1 == null) {
         try {
            var1 = MidiSystem.getReceiver();
         } catch (Exception var9) {
         }
      }

      if (var1 != null) {
         this.autoConnectedReceiver = var1;

         try {
            this.getTransmitter().setReceiver(var1);
         } catch (Exception var8) {
         }
      }

   }

   private synchronized void propagateCaches() {
      if (this.sequence != null && this.isOpen()) {
         if (this.cacheTempoFactor != -1.0F) {
            this.setTempoFactor(this.cacheTempoFactor);
         }

         if (this.cacheTempoMPQ == -1.0D) {
            this.setTempoInMPQ((new MidiUtils.TempoCache(this.sequence)).getTempoMPQAt(this.getTickPosition()));
         } else {
            this.setTempoInMPQ((float)this.cacheTempoMPQ);
         }
      }

   }

   private synchronized void setCaches() {
      this.cacheTempoFactor = this.getTempoFactor();
      this.cacheTempoMPQ = (double)this.getTempoInMPQ();
   }

   protected synchronized void implClose() {
      if (this.playThread != null) {
         this.playThread.close();
         this.playThread = null;
      }

      super.implClose();
      this.sequence = null;
      this.running = false;
      this.cacheTempoMPQ = -1.0D;
      this.cacheTempoFactor = -1.0F;
      this.trackMuted = null;
      this.trackSolo = null;
      this.loopStart = 0L;
      this.loopEnd = -1L;
      this.loopCount = 0;
      this.doAutoConnectAtNextOpen = this.autoConnect;
      if (this.autoConnectedReceiver != null) {
         try {
            this.autoConnectedReceiver.close();
         } catch (Exception var2) {
         }

         this.autoConnectedReceiver = null;
      }

   }

   void implStart() {
      if (this.playThread != null) {
         this.tempoCache.refresh(this.sequence);
         if (!this.running) {
            this.running = true;
            this.playThread.start();
         }

      }
   }

   void implStop() {
      if (this.playThread != null) {
         this.recording = false;
         if (this.running) {
            this.running = false;
            this.playThread.stop();
         }

      }
   }

   private static EventDispatcher getEventDispatcher() {
      ThreadGroup var0 = Thread.currentThread().getThreadGroup();
      synchronized(dispatchers) {
         EventDispatcher var2 = (EventDispatcher)dispatchers.get(var0);
         if (var2 == null) {
            var2 = new EventDispatcher();
            dispatchers.put(var0, var2);
            var2.start();
         }

         return var2;
      }
   }

   void sendMetaEvents(MidiMessage var1) {
      if (this.metaEventListeners.size() != 0) {
         getEventDispatcher().sendAudioEvents(var1, this.metaEventListeners);
      }
   }

   void sendControllerEvents(MidiMessage var1) {
      int var2 = this.controllerEventListeners.size();
      if (var2 != 0) {
         if (var1 instanceof ShortMessage) {
            ShortMessage var3 = (ShortMessage)var1;
            int var4 = var3.getData1();
            ArrayList var5 = new ArrayList();

            for(int var6 = 0; var6 < var2; ++var6) {
               RealTimeSequencer.ControllerListElement var7 = (RealTimeSequencer.ControllerListElement)this.controllerEventListeners.get(var6);

               for(int var8 = 0; var8 < var7.controllers.length; ++var8) {
                  if (var7.controllers[var8] == var4) {
                     var5.add(var7.listener);
                     break;
                  }
               }
            }

            getEventDispatcher().sendAudioEvents(var1, var5);
         }
      }
   }

   private boolean needCaching() {
      return !this.isOpen() || this.sequence == null || this.playThread == null;
   }

   private RealTimeSequencer.DataPump getDataPump() {
      return this.playThread != null ? this.playThread.getDataPump() : null;
   }

   private MidiUtils.TempoCache getTempoCache() {
      return this.tempoCache;
   }

   private static boolean[] ensureBoolArraySize(boolean[] var0, int var1) {
      if (var0 == null) {
         return new boolean[var1];
      } else if (var0.length < var1) {
         boolean[] var2 = new boolean[var1];
         System.arraycopy(var0, 0, var2, 0, var0.length);
         return var2;
      } else {
         return var0;
      }
   }

   protected boolean hasReceivers() {
      return true;
   }

   protected Receiver createReceiver() throws MidiUnavailableException {
      return new RealTimeSequencer.SequencerReceiver();
   }

   protected boolean hasTransmitters() {
      return true;
   }

   protected Transmitter createTransmitter() throws MidiUnavailableException {
      return new RealTimeSequencer.SequencerTransmitter();
   }

   public void setAutoConnect(Receiver var1) {
      this.autoConnect = var1 != null;
      this.autoConnectedReceiver = var1;
   }

   static {
      masterSyncModes = new Sequencer.SyncMode[]{Sequencer.SyncMode.INTERNAL_CLOCK};
      slaveSyncModes = new Sequencer.SyncMode[]{Sequencer.SyncMode.NO_SYNC};
      masterSyncMode = Sequencer.SyncMode.INTERNAL_CLOCK;
      slaveSyncMode = Sequencer.SyncMode.NO_SYNC;
   }

   private class DataPump {
      private float currTempo;
      private float tempoFactor;
      private float inverseTempoFactor;
      private long ignoreTempoEventAt;
      private int resolution;
      private float divisionType;
      private long checkPointMillis;
      private long checkPointTick;
      private int[] noteOnCache;
      private Track[] tracks;
      private boolean[] trackDisabled;
      private int[] trackReadPos;
      private long lastTick;
      private boolean needReindex = false;
      private int currLoopCounter = 0;

      DataPump() {
         this.init();
      }

      synchronized void init() {
         this.ignoreTempoEventAt = -1L;
         this.tempoFactor = 1.0F;
         this.inverseTempoFactor = 1.0F;
         this.noteOnCache = new int[128];
         this.tracks = null;
         this.trackDisabled = null;
      }

      synchronized void setTickPos(long var1) {
         this.lastTick = var1;
         if (RealTimeSequencer.this.running) {
            this.notesOff(false);
         }

         if (!RealTimeSequencer.this.running && var1 <= 0L) {
            this.needReindex = true;
         } else {
            this.chaseEvents(var1, var1);
         }

         if (!this.hasCachedTempo()) {
            this.setTempoMPQ(RealTimeSequencer.this.getTempoCache().getTempoMPQAt(this.lastTick, this.currTempo));
            this.ignoreTempoEventAt = -1L;
         }

         this.checkPointMillis = 0L;
      }

      long getTickPos() {
         return this.lastTick;
      }

      boolean hasCachedTempo() {
         if (this.ignoreTempoEventAt != this.lastTick) {
            this.ignoreTempoEventAt = -1L;
         }

         return this.ignoreTempoEventAt >= 0L;
      }

      synchronized void setTempoMPQ(float var1) {
         if (var1 > 0.0F && var1 != this.currTempo) {
            this.ignoreTempoEventAt = this.lastTick;
            this.currTempo = var1;
            this.checkPointMillis = 0L;
         }

      }

      float getTempoMPQ() {
         return this.currTempo;
      }

      synchronized void setTempoFactor(float var1) {
         if (var1 > 0.0F && var1 != this.tempoFactor) {
            this.tempoFactor = var1;
            this.inverseTempoFactor = 1.0F / var1;
            this.checkPointMillis = 0L;
         }

      }

      float getTempoFactor() {
         return this.tempoFactor;
      }

      synchronized void muteSoloChanged() {
         boolean[] var1 = this.makeDisabledArray();
         if (RealTimeSequencer.this.running) {
            this.applyDisabledTracks(this.trackDisabled, var1);
         }

         this.trackDisabled = var1;
      }

      synchronized void setSequence(Sequence var1) {
         if (var1 == null) {
            this.init();
         } else {
            this.tracks = var1.getTracks();
            this.muteSoloChanged();
            this.resolution = var1.getResolution();
            this.divisionType = var1.getDivisionType();
            this.trackReadPos = new int[this.tracks.length];
            this.checkPointMillis = 0L;
            this.needReindex = true;
         }
      }

      synchronized void resetLoopCount() {
         this.currLoopCounter = RealTimeSequencer.this.loopCount;
      }

      void clearNoteOnCache() {
         for(int var1 = 0; var1 < 128; ++var1) {
            this.noteOnCache[var1] = 0;
         }

      }

      void notesOff(boolean var1) {
         int var2 = 0;

         for(int var3 = 0; var3 < 16; ++var3) {
            int var4 = 1 << var3;

            for(int var5 = 0; var5 < 128; ++var5) {
               if ((this.noteOnCache[var5] & var4) != 0) {
                  int[] var10000 = this.noteOnCache;
                  var10000[var5] ^= var4;
                  RealTimeSequencer.this.getTransmitterList().sendMessage(144 | var3 | var5 << 8, -1L);
                  ++var2;
               }
            }

            RealTimeSequencer.this.getTransmitterList().sendMessage(176 | var3 | 31488, -1L);
            RealTimeSequencer.this.getTransmitterList().sendMessage(176 | var3 | 16384, -1L);
            if (var1) {
               RealTimeSequencer.this.getTransmitterList().sendMessage(176 | var3 | 30976, -1L);
               ++var2;
            }
         }

      }

      private boolean[] makeDisabledArray() {
         if (this.tracks == null) {
            return null;
         } else {
            boolean[] var1 = new boolean[this.tracks.length];
            boolean[] var2;
            boolean[] var3;
            synchronized(RealTimeSequencer.this) {
               var3 = RealTimeSequencer.this.trackMuted;
               var2 = RealTimeSequencer.this.trackSolo;
            }

            boolean var4 = false;
            int var5;
            if (var2 != null) {
               for(var5 = 0; var5 < var2.length; ++var5) {
                  if (var2[var5]) {
                     var4 = true;
                     break;
                  }
               }
            }

            if (var4) {
               for(var5 = 0; var5 < var1.length; ++var5) {
                  var1[var5] = var5 >= var2.length || !var2[var5];
               }
            } else {
               for(var5 = 0; var5 < var1.length; ++var5) {
                  var1[var5] = var3 != null && var5 < var3.length && var3[var5];
               }
            }

            return var1;
         }
      }

      private void sendNoteOffIfOn(Track var1, long var2) {
         int var4 = var1.size();
         int var5 = 0;

         try {
            for(int var6 = 0; var6 < var4; ++var6) {
               MidiEvent var7 = var1.get(var6);
               if (var7.getTick() > var2) {
                  break;
               }

               MidiMessage var8 = var7.getMessage();
               int var9 = var8.getStatus();
               int var10 = var8.getLength();
               if (var10 == 3 && (var9 & 240) == 144) {
                  int var11 = -1;
                  if (var8 instanceof ShortMessage) {
                     ShortMessage var12 = (ShortMessage)var8;
                     if (var12.getData2() > 0) {
                        var11 = var12.getData1();
                     }
                  } else {
                     byte[] var14 = var8.getMessage();
                     if ((var14[2] & 127) > 0) {
                        var11 = var14[1] & 127;
                     }
                  }

                  if (var11 >= 0) {
                     int var15 = 1 << (var9 & 15);
                     if ((this.noteOnCache[var11] & var15) != 0) {
                        RealTimeSequencer.this.getTransmitterList().sendMessage(var9 | var11 << 8, -1L);
                        int[] var10000 = this.noteOnCache;
                        var10000[var11] &= '\uffff' ^ var15;
                        ++var5;
                     }
                  }
               }
            }
         } catch (ArrayIndexOutOfBoundsException var13) {
         }

      }

      private void applyDisabledTracks(boolean[] var1, boolean[] var2) {
         byte[][] var3 = (byte[][])null;
         synchronized(RealTimeSequencer.this) {
            for(int var5 = 0; var5 < var2.length; ++var5) {
               if ((var1 == null || var5 >= var1.length || !var1[var5]) && var2[var5]) {
                  if (this.tracks.length > var5) {
                     this.sendNoteOffIfOn(this.tracks[var5], this.lastTick);
                  }
               } else if (var1 != null && var5 < var1.length && var1[var5] && !var2[var5]) {
                  if (var3 == null) {
                     var3 = new byte[128][16];
                  }

                  this.chaseTrackEvents(var5, 0L, this.lastTick, true, var3);
               }
            }

         }
      }

      private void chaseTrackEvents(int var1, long var2, long var4, boolean var6, byte[][] var7) {
         if (var2 > var4) {
            var2 = 0L;
         }

         byte[] var8 = new byte[16];

         int var10;
         for(int var9 = 0; var9 < 16; ++var9) {
            var8[var9] = -1;

            for(var10 = 0; var10 < 128; ++var10) {
               var7[var10][var9] = -1;
            }
         }

         Track var18 = this.tracks[var1];
         var10 = var18.size();

         int var11;
         int var15;
         try {
            for(var11 = 0; var11 < var10; ++var11) {
               MidiEvent var12 = var18.get(var11);
               if (var12.getTick() >= var4) {
                  if (var6 && var1 < this.trackReadPos.length) {
                     this.trackReadPos[var1] = var11 > 0 ? var11 - 1 : 0;
                  }
                  break;
               }

               MidiMessage var13 = var12.getMessage();
               int var14 = var13.getStatus();
               var15 = var13.getLength();
               ShortMessage var16;
               byte[] var22;
               if (var15 == 3 && (var14 & 240) == 176) {
                  if (var13 instanceof ShortMessage) {
                     var16 = (ShortMessage)var13;
                     var7[var16.getData1() & 127][var14 & 15] = (byte)var16.getData2();
                  } else {
                     var22 = var13.getMessage();
                     var7[var22[1] & 127][var14 & 15] = var22[2];
                  }
               }

               if (var15 == 2 && (var14 & 240) == 192) {
                  if (var13 instanceof ShortMessage) {
                     var16 = (ShortMessage)var13;
                     var8[var14 & 15] = (byte)var16.getData1();
                  } else {
                     var22 = var13.getMessage();
                     var8[var14 & 15] = var22[1];
                  }
               }
            }
         } catch (ArrayIndexOutOfBoundsException var17) {
         }

         var11 = 0;

         for(int var19 = 0; var19 < 16; ++var19) {
            for(int var20 = 0; var20 < 128; ++var20) {
               byte var21 = var7[var20][var19];
               if (var21 >= 0) {
                  var15 = 176 | var19 | var20 << 8 | var21 << 16;
                  RealTimeSequencer.this.getTransmitterList().sendMessage(var15, -1L);
                  ++var11;
               }
            }

            if (var8[var19] >= 0) {
               RealTimeSequencer.this.getTransmitterList().sendMessage(192 | var19 | var8[var19] << 8, -1L);
            }

            if (var8[var19] >= 0 || var2 == 0L || var4 == 0L) {
               RealTimeSequencer.this.getTransmitterList().sendMessage(224 | var19 | 4194304, -1L);
               RealTimeSequencer.this.getTransmitterList().sendMessage(176 | var19 | 16384, -1L);
            }
         }

      }

      synchronized void chaseEvents(long var1, long var3) {
         byte[][] var5 = new byte[128][16];

         for(int var6 = 0; var6 < this.tracks.length; ++var6) {
            if (this.trackDisabled == null || this.trackDisabled.length <= var6 || !this.trackDisabled[var6]) {
               this.chaseTrackEvents(var6, var1, var3, true, var5);
            }
         }

      }

      private long getCurrentTimeMillis() {
         return System.nanoTime() / 1000000L;
      }

      private long millis2tick(long var1) {
         if (this.divisionType != 0.0F) {
            double var3 = (double)var1 * (double)this.tempoFactor * (double)this.divisionType * (double)this.resolution / 1000.0D;
            return (long)var3;
         } else {
            return MidiUtils.microsec2ticks(var1 * 1000L, (double)(this.currTempo * this.inverseTempoFactor), this.resolution);
         }
      }

      private long tick2millis(long var1) {
         if (this.divisionType != 0.0F) {
            double var3 = (double)var1 * 1000.0D / ((double)this.tempoFactor * (double)this.divisionType * (double)this.resolution);
            return (long)var3;
         } else {
            return MidiUtils.ticks2microsec(var1, (double)(this.currTempo * this.inverseTempoFactor), this.resolution) / 1000L;
         }
      }

      private void ReindexTrack(int var1, long var2) {
         if (var1 < this.trackReadPos.length && var1 < this.tracks.length) {
            this.trackReadPos[var1] = MidiUtils.tick2index(this.tracks[var1], var2);
         }

      }

      private boolean dispatchMessage(int var1, MidiEvent var2) {
         boolean var3 = false;
         MidiMessage var4 = var2.getMessage();
         int var5 = var4.getStatus();
         int var6 = var4.getLength();
         int var10;
         if (var5 == 255 && var6 >= 2) {
            if (var1 == 0) {
               var10 = MidiUtils.getTempoMPQ(var4);
               if (var10 > 0) {
                  if (var2.getTick() != this.ignoreTempoEventAt) {
                     this.setTempoMPQ((float)var10);
                     var3 = true;
                  }

                  this.ignoreTempoEventAt = -1L;
               }
            }

            RealTimeSequencer.this.sendMetaEvents(var4);
         } else {
            RealTimeSequencer.this.getTransmitterList().sendMessage(var4, -1L);
            int[] var10000;
            switch(var5 & 240) {
            case 128:
               var10 = ((ShortMessage)var4).getData1() & 127;
               var10000 = this.noteOnCache;
               var10000[var10] &= '\uffff' ^ 1 << (var5 & 15);
               break;
            case 144:
               ShortMessage var7 = (ShortMessage)var4;
               int var8 = var7.getData1() & 127;
               int var9 = var7.getData2() & 127;
               if (var9 > 0) {
                  var10000 = this.noteOnCache;
                  var10000[var8] |= 1 << (var5 & 15);
               } else {
                  var10000 = this.noteOnCache;
                  var10000[var8] &= '\uffff' ^ 1 << (var5 & 15);
               }
               break;
            case 176:
               RealTimeSequencer.this.sendControllerEvents(var4);
            }
         }

         return var3;
      }

      synchronized boolean pump() {
         long var3 = this.lastTick;
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;
         long var1 = this.getCurrentTimeMillis();
         boolean var9 = false;

         do {
            var6 = false;
            int var10;
            if (this.needReindex) {
               if (this.trackReadPos.length < this.tracks.length) {
                  this.trackReadPos = new int[this.tracks.length];
               }

               for(var10 = 0; var10 < this.tracks.length; ++var10) {
                  this.ReindexTrack(var10, var3);
               }

               this.needReindex = false;
               this.checkPointMillis = 0L;
            }

            if (this.checkPointMillis == 0L) {
               var1 = this.getCurrentTimeMillis();
               this.checkPointMillis = var1;
               var3 = this.lastTick;
               this.checkPointTick = var3;
            } else {
               var3 = this.checkPointTick + this.millis2tick(var1 - this.checkPointMillis);
               if (RealTimeSequencer.this.loopEnd != -1L && (RealTimeSequencer.this.loopCount > 0 && this.currLoopCounter > 0 || RealTimeSequencer.this.loopCount == -1) && this.lastTick <= RealTimeSequencer.this.loopEnd && var3 >= RealTimeSequencer.this.loopEnd) {
                  var3 = RealTimeSequencer.this.loopEnd - 1L;
                  var7 = true;
               }

               this.lastTick = var3;
            }

            int var16 = 0;

            for(var10 = 0; var10 < this.tracks.length; ++var10) {
               try {
                  boolean var11 = this.trackDisabled[var10];
                  Track var12 = this.tracks[var10];
                  int var13 = this.trackReadPos[var10];
                  int var14 = var12.size();

                  MidiEvent var5;
                  while(!var6 && var13 < var14 && (var5 = var12.get(var13)).getTick() <= var3) {
                     if (var13 == var14 - 1 && MidiUtils.isMetaEndOfTrack(var5.getMessage())) {
                        var13 = var14;
                        break;
                     }

                     ++var13;
                     if (!var11 || var10 == 0 && MidiUtils.isMetaTempo(var5.getMessage())) {
                        var6 = this.dispatchMessage(var10, var5);
                     }
                  }

                  if (var13 >= var14) {
                     ++var16;
                  }

                  this.trackReadPos[var10] = var13;
               } catch (Exception var15) {
                  if (var15 instanceof ArrayIndexOutOfBoundsException) {
                     this.needReindex = true;
                     var6 = true;
                  }
               }

               if (var6) {
                  break;
               }
            }

            var8 = var16 == this.tracks.length;
            if (var7 || (RealTimeSequencer.this.loopCount > 0 && this.currLoopCounter > 0 || RealTimeSequencer.this.loopCount == -1) && !var6 && RealTimeSequencer.this.loopEnd == -1L && var8) {
               long var17 = this.checkPointMillis;
               long var18 = RealTimeSequencer.this.loopEnd;
               if (var18 == -1L) {
                  var18 = this.lastTick;
               }

               if (RealTimeSequencer.this.loopCount != -1) {
                  --this.currLoopCounter;
               }

               this.setTickPos(RealTimeSequencer.this.loopStart);
               this.checkPointMillis = var17 + this.tick2millis(var18 - this.checkPointTick);
               this.checkPointTick = RealTimeSequencer.this.loopStart;
               this.needReindex = false;
               var6 = false;
               var7 = false;
               var8 = false;
            }
         } while(var6);

         return var8;
      }
   }

   final class PlayThread implements Runnable {
      private Thread thread;
      private final Object lock = new Object();
      boolean interrupted = false;
      boolean isPumping = false;
      private final RealTimeSequencer.DataPump dataPump = RealTimeSequencer.this.new DataPump();

      PlayThread() {
         byte var2 = 8;
         this.thread = JSSecurityManager.createThread(this, "Java Sound Sequencer", false, var2, true);
      }

      RealTimeSequencer.DataPump getDataPump() {
         return this.dataPump;
      }

      synchronized void setSequence(Sequence var1) {
         this.dataPump.setSequence(var1);
      }

      synchronized void start() {
         RealTimeSequencer.this.running = true;
         if (!this.dataPump.hasCachedTempo()) {
            long var1 = RealTimeSequencer.this.getTickPosition();
            this.dataPump.setTempoMPQ(RealTimeSequencer.this.tempoCache.getTempoMPQAt(var1));
         }

         this.dataPump.checkPointMillis = 0L;
         this.dataPump.clearNoteOnCache();
         this.dataPump.needReindex = true;
         this.dataPump.resetLoopCount();
         synchronized(this.lock) {
            this.lock.notifyAll();
         }
      }

      synchronized void stop() {
         this.playThreadImplStop();
         long var1 = System.nanoTime() / 1000000L;

         while(this.isPumping) {
            synchronized(this.lock) {
               try {
                  this.lock.wait(2000L);
               } catch (InterruptedException var6) {
               }
            }

            if (System.nanoTime() / 1000000L - var1 > 1900L) {
            }
         }

      }

      void playThreadImplStop() {
         RealTimeSequencer.this.running = false;
         synchronized(this.lock) {
            this.lock.notifyAll();
         }
      }

      void close() {
         Thread var1 = null;
         synchronized(this) {
            this.interrupted = true;
            var1 = this.thread;
            this.thread = null;
         }

         if (var1 != null) {
            synchronized(this.lock) {
               this.lock.notifyAll();
            }
         }

         if (var1 != null) {
            try {
               var1.join(2000L);
            } catch (InterruptedException var5) {
            }
         }

      }

      public void run() {
         while(!this.interrupted) {
            boolean var1 = false;
            boolean var2 = RealTimeSequencer.this.running;
            this.isPumping = !this.interrupted && RealTimeSequencer.this.running;

            while(!var1 && !this.interrupted && RealTimeSequencer.this.running) {
               var1 = this.dataPump.pump();

               try {
                  Thread.sleep(1L);
               } catch (InterruptedException var8) {
               }
            }

            this.playThreadImplStop();
            if (var2) {
               this.dataPump.notesOff(true);
            }

            if (var1) {
               this.dataPump.setTickPos(RealTimeSequencer.this.sequence.getTickLength());
               MetaMessage var3 = new MetaMessage();

               try {
                  var3.setMessage(47, new byte[0], 0);
               } catch (InvalidMidiDataException var7) {
               }

               RealTimeSequencer.this.sendMetaEvents(var3);
            }

            synchronized(this.lock) {
               this.isPumping = false;
               this.lock.notifyAll();

               while(!RealTimeSequencer.this.running && !this.interrupted) {
                  try {
                     this.lock.wait();
                  } catch (Exception var6) {
                  }
               }
            }
         }

      }
   }

   static class RecordingTrack {
      private final Track track;
      private int channel;

      RecordingTrack(Track var1, int var2) {
         this.track = var1;
         this.channel = var2;
      }

      static RealTimeSequencer.RecordingTrack get(List var0, Track var1) {
         synchronized(var0) {
            int var3 = var0.size();

            for(int var4 = 0; var4 < var3; ++var4) {
               RealTimeSequencer.RecordingTrack var5 = (RealTimeSequencer.RecordingTrack)var0.get(var4);
               if (var5.track == var1) {
                  return var5;
               }
            }

            return null;
         }
      }

      static Track get(List var0, int var1) {
         synchronized(var0) {
            int var3 = var0.size();

            for(int var4 = 0; var4 < var3; ++var4) {
               RealTimeSequencer.RecordingTrack var5 = (RealTimeSequencer.RecordingTrack)var0.get(var4);
               if (var5.channel == var1 || var5.channel == -1) {
                  return var5.track;
               }
            }

            return null;
         }
      }
   }

   private class ControllerListElement {
      int[] controllers;
      final ControllerEventListener listener;

      private ControllerListElement(ControllerEventListener var2, int[] var3) {
         this.listener = var2;
         if (var3 == null) {
            var3 = new int[128];

            for(int var4 = 0; var4 < 128; var3[var4] = var4++) {
            }
         }

         this.controllers = var3;
      }

      private void addControllers(int[] var1) {
         if (var1 == null) {
            this.controllers = new int[128];

            for(int var7 = 0; var7 < 128; this.controllers[var7] = var7++) {
            }

         } else {
            int[] var2 = new int[this.controllers.length + var1.length];

            int var4;
            for(var4 = 0; var4 < this.controllers.length; ++var4) {
               var2[var4] = this.controllers[var4];
            }

            int var3 = this.controllers.length;

            for(var4 = 0; var4 < var1.length; ++var4) {
               boolean var5 = false;

               for(int var6 = 0; var6 < this.controllers.length; ++var6) {
                  if (var1[var4] == this.controllers[var6]) {
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  var2[var3++] = var1[var4];
               }
            }

            int[] var8 = new int[var3];

            for(int var9 = 0; var9 < var3; ++var9) {
               var8[var9] = var2[var9];
            }

            this.controllers = var8;
         }
      }

      private void removeControllers(int[] var1) {
         if (var1 == null) {
            this.controllers = new int[0];
         } else {
            int[] var2 = new int[this.controllers.length];
            int var3 = 0;

            for(int var4 = 0; var4 < this.controllers.length; ++var4) {
               boolean var5 = false;

               for(int var6 = 0; var6 < var1.length; ++var6) {
                  if (this.controllers[var4] == var1[var6]) {
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  var2[var3++] = this.controllers[var4];
               }
            }

            int[] var7 = new int[var3];

            for(int var8 = 0; var8 < var3; ++var8) {
               var7[var8] = var2[var8];
            }

            this.controllers = var7;
         }

      }

      private int[] getControllers() {
         if (this.controllers == null) {
            return null;
         } else {
            int[] var1 = new int[this.controllers.length];

            for(int var2 = 0; var2 < this.controllers.length; ++var2) {
               var1[var2] = this.controllers[var2];
            }

            return var1;
         }
      }

      // $FF: synthetic method
      ControllerListElement(ControllerEventListener var2, int[] var3, Object var4) {
         this(var2, var3);
      }
   }

   private static class RealTimeSequencerInfo extends MidiDevice.Info {
      private static final String name = "Real Time Sequencer";
      private static final String vendor = "Oracle Corporation";
      private static final String description = "Software sequencer";
      private static final String version = "Version 1.0";

      private RealTimeSequencerInfo() {
         super("Real Time Sequencer", "Oracle Corporation", "Software sequencer", "Version 1.0");
      }

      // $FF: synthetic method
      RealTimeSequencerInfo(Object var1) {
         this();
      }
   }

   final class SequencerReceiver extends AbstractMidiDevice.AbstractReceiver {
      SequencerReceiver() {
         super();
      }

      void implSend(MidiMessage var1, long var2) {
         if (RealTimeSequencer.this.recording) {
            long var4 = 0L;
            if (var2 < 0L) {
               var4 = RealTimeSequencer.this.getTickPosition();
            } else {
               synchronized(RealTimeSequencer.this.tempoCache) {
                  var4 = MidiUtils.microsecond2tick(RealTimeSequencer.this.sequence, var2, RealTimeSequencer.this.tempoCache);
               }
            }

            Track var6 = null;
            if (var1.getLength() > 1) {
               if (var1 instanceof ShortMessage) {
                  ShortMessage var7 = (ShortMessage)var1;
                  if ((var7.getStatus() & 240) != 240) {
                     var6 = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, var7.getChannel());
                  }
               } else {
                  var6 = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, -1);
               }

               if (var6 != null) {
                  Object var9;
                  if (var1 instanceof ShortMessage) {
                     var9 = new FastShortMessage((ShortMessage)var1);
                  } else {
                     var9 = (MidiMessage)var1.clone();
                  }

                  MidiEvent var10 = new MidiEvent((MidiMessage)var9, var4);
                  var6.add(var10);
               }
            }
         }

      }
   }

   private class SequencerTransmitter extends AbstractMidiDevice.BasicTransmitter {
      private SequencerTransmitter() {
         super();
      }

      // $FF: synthetic method
      SequencerTransmitter(Object var2) {
         this();
      }
   }
}
