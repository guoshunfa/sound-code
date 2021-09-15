package javax.sound.midi;

public class Patch {
   private final int bank;
   private final int program;

   public Patch(int var1, int var2) {
      this.bank = var1;
      this.program = var2;
   }

   public int getBank() {
      return this.bank;
   }

   public int getProgram() {
      return this.program;
   }
}
