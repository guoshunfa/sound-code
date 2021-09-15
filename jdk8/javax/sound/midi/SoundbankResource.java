package javax.sound.midi;

public abstract class SoundbankResource {
   private final Soundbank soundBank;
   private final String name;
   private final Class dataClass;

   protected SoundbankResource(Soundbank var1, String var2, Class<?> var3) {
      this.soundBank = var1;
      this.name = var2;
      this.dataClass = var3;
   }

   public Soundbank getSoundbank() {
      return this.soundBank;
   }

   public String getName() {
      return this.name;
   }

   public Class<?> getDataClass() {
      return this.dataClass;
   }

   public abstract Object getData();
}
