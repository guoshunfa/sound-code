package javax.sound.sampled;

public interface Mixer extends Line {
   Mixer.Info getMixerInfo();

   Line.Info[] getSourceLineInfo();

   Line.Info[] getTargetLineInfo();

   Line.Info[] getSourceLineInfo(Line.Info var1);

   Line.Info[] getTargetLineInfo(Line.Info var1);

   boolean isLineSupported(Line.Info var1);

   Line getLine(Line.Info var1) throws LineUnavailableException;

   int getMaxLines(Line.Info var1);

   Line[] getSourceLines();

   Line[] getTargetLines();

   void synchronize(Line[] var1, boolean var2);

   void unsynchronize(Line[] var1);

   boolean isSynchronizationSupported(Line[] var1, boolean var2);

   public static class Info {
      private final String name;
      private final String vendor;
      private final String description;
      private final String version;

      protected Info(String var1, String var2, String var3, String var4) {
         this.name = var1;
         this.vendor = var2;
         this.description = var3;
         this.version = var4;
      }

      public final boolean equals(Object var1) {
         return super.equals(var1);
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public final String getName() {
         return this.name;
      }

      public final String getVendor() {
         return this.vendor;
      }

      public final String getDescription() {
         return this.description;
      }

      public final String getVersion() {
         return this.version;
      }

      public final String toString() {
         return this.name + ", version " + this.version;
      }
   }
}
