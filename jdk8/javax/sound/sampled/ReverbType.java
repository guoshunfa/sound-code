package javax.sound.sampled;

public class ReverbType {
   private String name;
   private int earlyReflectionDelay;
   private float earlyReflectionIntensity;
   private int lateReflectionDelay;
   private float lateReflectionIntensity;
   private int decayTime;

   protected ReverbType(String var1, int var2, float var3, int var4, float var5, int var6) {
      this.name = var1;
      this.earlyReflectionDelay = var2;
      this.earlyReflectionIntensity = var3;
      this.lateReflectionDelay = var4;
      this.lateReflectionIntensity = var5;
      this.decayTime = var6;
   }

   public String getName() {
      return this.name;
   }

   public final int getEarlyReflectionDelay() {
      return this.earlyReflectionDelay;
   }

   public final float getEarlyReflectionIntensity() {
      return this.earlyReflectionIntensity;
   }

   public final int getLateReflectionDelay() {
      return this.lateReflectionDelay;
   }

   public final float getLateReflectionIntensity() {
      return this.lateReflectionIntensity;
   }

   public final int getDecayTime() {
      return this.decayTime;
   }

   public final boolean equals(Object var1) {
      return super.equals(var1);
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final String toString() {
      return this.name + ", early reflection delay " + this.earlyReflectionDelay + " ns, early reflection intensity " + this.earlyReflectionIntensity + " dB, late deflection delay " + this.lateReflectionDelay + " ns, late reflection intensity " + this.lateReflectionIntensity + " dB, decay time " + this.decayTime;
   }
}
