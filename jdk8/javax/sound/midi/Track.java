package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.ArrayList;
import java.util.HashSet;

public class Track {
   private ArrayList eventsList = new ArrayList();
   private HashSet set = new HashSet();
   private MidiEvent eotEvent;

   Track() {
      Track.ImmutableEndOfTrack var1 = new Track.ImmutableEndOfTrack();
      this.eotEvent = new MidiEvent(var1, 0L);
      this.eventsList.add(this.eotEvent);
      this.set.add(this.eotEvent);
   }

   public boolean add(MidiEvent var1) {
      if (var1 == null) {
         return false;
      } else {
         synchronized(this.eventsList) {
            if (this.set.contains(var1)) {
               return false;
            } else {
               int var3 = this.eventsList.size();
               MidiEvent var4 = null;
               if (var3 > 0) {
                  var4 = (MidiEvent)this.eventsList.get(var3 - 1);
               }

               if (var4 != this.eotEvent) {
                  if (var4 != null) {
                     this.eotEvent.setTick(var4.getTick());
                  } else {
                     this.eotEvent.setTick(0L);
                  }

                  this.eventsList.add(this.eotEvent);
                  this.set.add(this.eotEvent);
                  var3 = this.eventsList.size();
               }

               if (MidiUtils.isMetaEndOfTrack(var1.getMessage())) {
                  if (var1.getTick() > this.eotEvent.getTick()) {
                     this.eotEvent.setTick(var1.getTick());
                  }

                  return true;
               } else {
                  this.set.add(var1);

                  int var5;
                  for(var5 = var3; var5 > 0 && var1.getTick() < ((MidiEvent)this.eventsList.get(var5 - 1)).getTick(); --var5) {
                  }

                  if (var5 == var3) {
                     this.eventsList.set(var3 - 1, var1);
                     if (this.eotEvent.getTick() < var1.getTick()) {
                        this.eotEvent.setTick(var1.getTick());
                     }

                     this.eventsList.add(this.eotEvent);
                  } else {
                     this.eventsList.add(var5, var1);
                  }

                  return true;
               }
            }
         }
      }
   }

   public boolean remove(MidiEvent var1) {
      synchronized(this.eventsList) {
         if (this.set.remove(var1)) {
            int var3 = this.eventsList.indexOf(var1);
            if (var3 >= 0) {
               this.eventsList.remove(var3);
               return true;
            }
         }

         return false;
      }
   }

   public MidiEvent get(int var1) throws ArrayIndexOutOfBoundsException {
      try {
         synchronized(this.eventsList) {
            return (MidiEvent)this.eventsList.get(var1);
         }
      } catch (IndexOutOfBoundsException var5) {
         throw new ArrayIndexOutOfBoundsException(var5.getMessage());
      }
   }

   public int size() {
      synchronized(this.eventsList) {
         return this.eventsList.size();
      }
   }

   public long ticks() {
      long var1 = 0L;
      synchronized(this.eventsList) {
         if (this.eventsList.size() > 0) {
            var1 = ((MidiEvent)this.eventsList.get(this.eventsList.size() - 1)).getTick();
         }

         return var1;
      }
   }

   private static class ImmutableEndOfTrack extends MetaMessage {
      private ImmutableEndOfTrack() {
         super(new byte[3]);
         this.data[0] = -1;
         this.data[1] = 47;
         this.data[2] = 0;
      }

      public void setMessage(int var1, byte[] var2, int var3) throws InvalidMidiDataException {
         throw new InvalidMidiDataException("cannot modify end of track message");
      }

      // $FF: synthetic method
      ImmutableEndOfTrack(Object var1) {
         this();
      }
   }
}
