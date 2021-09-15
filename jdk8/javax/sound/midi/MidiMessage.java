package javax.sound.midi;

public abstract class MidiMessage implements Cloneable {
   protected byte[] data;
   protected int length = 0;

   protected MidiMessage(byte[] var1) {
      this.data = var1;
      if (var1 != null) {
         this.length = var1.length;
      }

   }

   protected void setMessage(byte[] var1, int var2) throws InvalidMidiDataException {
      if (var2 < 0 || var2 > 0 && var2 > var1.length) {
         throw new IndexOutOfBoundsException("length out of bounds: " + var2);
      } else {
         this.length = var2;
         if (this.data == null || this.data.length < this.length) {
            this.data = new byte[this.length];
         }

         System.arraycopy(var1, 0, this.data, 0, var2);
      }
   }

   public byte[] getMessage() {
      byte[] var1 = new byte[this.length];
      System.arraycopy(this.data, 0, var1, 0, this.length);
      return var1;
   }

   public int getStatus() {
      return this.length > 0 ? this.data[0] & 255 : 0;
   }

   public int getLength() {
      return this.length;
   }

   public abstract Object clone();
}
