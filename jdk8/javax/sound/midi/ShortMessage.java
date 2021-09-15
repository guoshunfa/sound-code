package javax.sound.midi;

public class ShortMessage extends MidiMessage {
   public static final int MIDI_TIME_CODE = 241;
   public static final int SONG_POSITION_POINTER = 242;
   public static final int SONG_SELECT = 243;
   public static final int TUNE_REQUEST = 246;
   public static final int END_OF_EXCLUSIVE = 247;
   public static final int TIMING_CLOCK = 248;
   public static final int START = 250;
   public static final int CONTINUE = 251;
   public static final int STOP = 252;
   public static final int ACTIVE_SENSING = 254;
   public static final int SYSTEM_RESET = 255;
   public static final int NOTE_OFF = 128;
   public static final int NOTE_ON = 144;
   public static final int POLY_PRESSURE = 160;
   public static final int CONTROL_CHANGE = 176;
   public static final int PROGRAM_CHANGE = 192;
   public static final int CHANNEL_PRESSURE = 208;
   public static final int PITCH_BEND = 224;

   public ShortMessage() {
      this(new byte[3]);
      this.data[0] = -112;
      this.data[1] = 64;
      this.data[2] = 127;
      this.length = 3;
   }

   public ShortMessage(int var1) throws InvalidMidiDataException {
      super((byte[])null);
      this.setMessage(var1);
   }

   public ShortMessage(int var1, int var2, int var3) throws InvalidMidiDataException {
      super((byte[])null);
      this.setMessage(var1, var2, var3);
   }

   public ShortMessage(int var1, int var2, int var3, int var4) throws InvalidMidiDataException {
      super((byte[])null);
      this.setMessage(var1, var2, var3, var4);
   }

   protected ShortMessage(byte[] var1) {
      super(var1);
   }

   public void setMessage(int var1) throws InvalidMidiDataException {
      int var2 = this.getDataLength(var1);
      if (var2 != 0) {
         throw new InvalidMidiDataException("Status byte; " + var1 + " requires " + var2 + " data bytes");
      } else {
         this.setMessage(var1, 0, 0);
      }
   }

   public void setMessage(int var1, int var2, int var3) throws InvalidMidiDataException {
      int var4 = this.getDataLength(var1);
      if (var4 > 0) {
         if (var2 < 0 || var2 > 127) {
            throw new InvalidMidiDataException("data1 out of range: " + var2);
         }

         if (var4 > 1 && (var3 < 0 || var3 > 127)) {
            throw new InvalidMidiDataException("data2 out of range: " + var3);
         }
      }

      this.length = var4 + 1;
      if (this.data == null || this.data.length < this.length) {
         this.data = new byte[3];
      }

      this.data[0] = (byte)(var1 & 255);
      if (this.length > 1) {
         this.data[1] = (byte)(var2 & 255);
         if (this.length > 2) {
            this.data[2] = (byte)(var3 & 255);
         }
      }

   }

   public void setMessage(int var1, int var2, int var3, int var4) throws InvalidMidiDataException {
      if (var1 < 240 && var1 >= 128) {
         if ((var2 & -16) != 0) {
            throw new InvalidMidiDataException("channel out of range: " + var2);
         } else {
            this.setMessage(var1 & 240 | var2 & 15, var3, var4);
         }
      } else {
         throw new InvalidMidiDataException("command out of range: 0x" + Integer.toHexString(var1));
      }
   }

   public int getChannel() {
      return this.getStatus() & 15;
   }

   public int getCommand() {
      return this.getStatus() & 240;
   }

   public int getData1() {
      return this.length > 1 ? this.data[1] & 255 : 0;
   }

   public int getData2() {
      return this.length > 2 ? this.data[2] & 255 : 0;
   }

   public Object clone() {
      byte[] var1 = new byte[this.length];
      System.arraycopy(this.data, 0, var1, 0, var1.length);
      ShortMessage var2 = new ShortMessage(var1);
      return var2;
   }

   protected final int getDataLength(int var1) throws InvalidMidiDataException {
      switch(var1) {
      case 241:
      case 243:
         return 1;
      case 242:
         return 2;
      case 244:
      case 245:
      default:
         switch(var1 & 240) {
         case 128:
         case 144:
         case 160:
         case 176:
         case 224:
            return 2;
         case 192:
         case 208:
            return 1;
         default:
            throw new InvalidMidiDataException("Invalid status byte: " + var1);
         }
      case 246:
      case 247:
      case 248:
      case 249:
      case 250:
      case 251:
      case 252:
      case 253:
      case 254:
      case 255:
         return 0;
      }
   }
}
