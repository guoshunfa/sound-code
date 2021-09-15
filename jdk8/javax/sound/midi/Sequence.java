package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.Vector;

public class Sequence {
   public static final float PPQ = 0.0F;
   public static final float SMPTE_24 = 24.0F;
   public static final float SMPTE_25 = 25.0F;
   public static final float SMPTE_30DROP = 29.97F;
   public static final float SMPTE_30 = 30.0F;
   protected float divisionType;
   protected int resolution;
   protected Vector<Track> tracks = new Vector();

   public Sequence(float var1, int var2) throws InvalidMidiDataException {
      if (var1 == 0.0F) {
         this.divisionType = 0.0F;
      } else if (var1 == 24.0F) {
         this.divisionType = 24.0F;
      } else if (var1 == 25.0F) {
         this.divisionType = 25.0F;
      } else if (var1 == 29.97F) {
         this.divisionType = 29.97F;
      } else {
         if (var1 != 30.0F) {
            throw new InvalidMidiDataException("Unsupported division type: " + var1);
         }

         this.divisionType = 30.0F;
      }

      this.resolution = var2;
   }

   public Sequence(float var1, int var2, int var3) throws InvalidMidiDataException {
      if (var1 == 0.0F) {
         this.divisionType = 0.0F;
      } else if (var1 == 24.0F) {
         this.divisionType = 24.0F;
      } else if (var1 == 25.0F) {
         this.divisionType = 25.0F;
      } else if (var1 == 29.97F) {
         this.divisionType = 29.97F;
      } else {
         if (var1 != 30.0F) {
            throw new InvalidMidiDataException("Unsupported division type: " + var1);
         }

         this.divisionType = 30.0F;
      }

      this.resolution = var2;

      for(int var4 = 0; var4 < var3; ++var4) {
         this.tracks.addElement(new Track());
      }

   }

   public float getDivisionType() {
      return this.divisionType;
   }

   public int getResolution() {
      return this.resolution;
   }

   public Track createTrack() {
      Track var1 = new Track();
      this.tracks.addElement(var1);
      return var1;
   }

   public boolean deleteTrack(Track var1) {
      synchronized(this.tracks) {
         return this.tracks.removeElement(var1);
      }
   }

   public Track[] getTracks() {
      return (Track[])((Track[])this.tracks.toArray(new Track[this.tracks.size()]));
   }

   public long getMicrosecondLength() {
      return MidiUtils.tick2microsecond(this, this.getTickLength(), (MidiUtils.TempoCache)null);
   }

   public long getTickLength() {
      long var1 = 0L;
      synchronized(this.tracks) {
         for(int var4 = 0; var4 < this.tracks.size(); ++var4) {
            long var5 = ((Track)this.tracks.elementAt(var4)).ticks();
            if (var5 > var1) {
               var1 = var5;
            }
         }

         return var1;
      }
   }

   public Patch[] getPatchList() {
      return new Patch[0];
   }
}
