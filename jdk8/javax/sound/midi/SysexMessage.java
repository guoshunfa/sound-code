package javax.sound.midi;

public class SysexMessage extends MidiMessage {
   public static final int SYSTEM_EXCLUSIVE = 240;
   public static final int SPECIAL_SYSTEM_EXCLUSIVE = 247;

   public SysexMessage() {
      this(new byte[2]);
      this.data[0] = -16;
      this.data[1] = -9;
   }

   public SysexMessage(byte[] var1, int var2) throws InvalidMidiDataException {
      super((byte[])null);
      this.setMessage(var1, var2);
   }

   public SysexMessage(int var1, byte[] var2, int var3) throws InvalidMidiDataException {
      super((byte[])null);
      this.setMessage(var1, var2, var3);
   }

   protected SysexMessage(byte[] var1) {
      super(var1);
   }

   public void setMessage(byte[] var1, int var2) throws InvalidMidiDataException {
      int var3 = var1[0] & 255;
      if (var3 != 240 && var3 != 247) {
         throw new InvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(var3));
      } else {
         super.setMessage(var1, var2);
      }
   }

   public void setMessage(int var1, byte[] var2, int var3) throws InvalidMidiDataException {
      if (var1 != 240 && var1 != 247) {
         throw new InvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(var1));
      } else if (var3 >= 0 && var3 <= var2.length) {
         this.length = var3 + 1;
         if (this.data == null || this.data.length < this.length) {
            this.data = new byte[this.length];
         }

         this.data[0] = (byte)(var1 & 255);
         if (var3 > 0) {
            System.arraycopy(var2, 0, this.data, 1, var3);
         }

      } else {
         throw new IndexOutOfBoundsException("length out of bounds: " + var3);
      }
   }

   public byte[] getData() {
      byte[] var1 = new byte[this.length - 1];
      System.arraycopy(this.data, 1, var1, 0, this.length - 1);
      return var1;
   }

   public Object clone() {
      byte[] var1 = new byte[this.length];
      System.arraycopy(this.data, 0, var1, 0, var1.length);
      SysexMessage var2 = new SysexMessage(var1);
      return var2;
   }
}
