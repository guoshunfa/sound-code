package javax.sound.midi;

public class MetaMessage extends MidiMessage {
   public static final int META = 255;
   private int dataLength;
   private static final long mask = 127L;

   public MetaMessage() {
      this(new byte[]{-1, 0});
   }

   public MetaMessage(int var1, byte[] var2, int var3) throws InvalidMidiDataException {
      super((byte[])null);
      this.dataLength = 0;
      this.setMessage(var1, var2, var3);
   }

   protected MetaMessage(byte[] var1) {
      super(var1);
      this.dataLength = 0;
      if (var1.length >= 3) {
         this.dataLength = var1.length - 3;

         for(int var2 = 2; var2 < var1.length && (var1[var2] & 128) != 0; ++var2) {
            --this.dataLength;
         }
      }

   }

   public void setMessage(int var1, byte[] var2, int var3) throws InvalidMidiDataException {
      if (var1 < 128 && var1 >= 0) {
         if ((var3 <= 0 || var3 <= var2.length) && var3 >= 0) {
            this.length = 2 + this.getVarIntLength((long)var3) + var3;
            this.dataLength = var3;
            this.data = new byte[this.length];
            this.data[0] = -1;
            this.data[1] = (byte)var1;
            this.writeVarInt(this.data, 2, (long)var3);
            if (var3 > 0) {
               System.arraycopy(var2, 0, this.data, this.length - this.dataLength, this.dataLength);
            }

         } else {
            throw new InvalidMidiDataException("length out of bounds: " + var3);
         }
      } else {
         throw new InvalidMidiDataException("Invalid meta event with type " + var1);
      }
   }

   public int getType() {
      return this.length >= 2 ? this.data[1] & 255 : 0;
   }

   public byte[] getData() {
      byte[] var1 = new byte[this.dataLength];
      System.arraycopy(this.data, this.length - this.dataLength, var1, 0, this.dataLength);
      return var1;
   }

   public Object clone() {
      byte[] var1 = new byte[this.length];
      System.arraycopy(this.data, 0, var1, 0, var1.length);
      MetaMessage var2 = new MetaMessage(var1);
      return var2;
   }

   private int getVarIntLength(long var1) {
      int var3 = 0;

      do {
         var1 >>= 7;
         ++var3;
      } while(var1 > 0L);

      return var3;
   }

   private void writeVarInt(byte[] var1, int var2, long var3) {
      int var5;
      for(var5 = 63; var5 > 0 && (var3 & 127L << var5) == 0L; var5 -= 7) {
      }

      while(var5 > 0) {
         var1[var2++] = (byte)((int)((var3 & 127L << var5) >> var5 | 128L));
         var5 -= 7;
      }

      var1[var2] = (byte)((int)(var3 & 127L));
   }
}
