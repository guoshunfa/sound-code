package javax.sound.midi;

public abstract class Instrument extends SoundbankResource {
   private final Patch patch;

   protected Instrument(Soundbank var1, Patch var2, String var3, Class<?> var4) {
      super(var1, var3, var4);
      this.patch = var2;
   }

   public Patch getPatch() {
      return this.patch;
   }
}
