package javax.sound.sampled;

public interface Port extends Line {
   public static class Info extends Line.Info {
      public static final Port.Info MICROPHONE = new Port.Info(Port.class, "MICROPHONE", true);
      public static final Port.Info LINE_IN = new Port.Info(Port.class, "LINE_IN", true);
      public static final Port.Info COMPACT_DISC = new Port.Info(Port.class, "COMPACT_DISC", true);
      public static final Port.Info SPEAKER = new Port.Info(Port.class, "SPEAKER", false);
      public static final Port.Info HEADPHONE = new Port.Info(Port.class, "HEADPHONE", false);
      public static final Port.Info LINE_OUT = new Port.Info(Port.class, "LINE_OUT", false);
      private String name;
      private boolean isSource;

      public Info(Class<?> var1, String var2, boolean var3) {
         super(var1);
         this.name = var2;
         this.isSource = var3;
      }

      public String getName() {
         return this.name;
      }

      public boolean isSource() {
         return this.isSource;
      }

      public boolean matches(Line.Info var1) {
         if (!super.matches(var1)) {
            return false;
         } else if (!this.name.equals(((Port.Info)var1).getName())) {
            return false;
         } else {
            return this.isSource == ((Port.Info)var1).isSource();
         }
      }

      public final boolean equals(Object var1) {
         return super.equals(var1);
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public final String toString() {
         return this.name + (this.isSource ? " source" : " target") + " port";
      }
   }
}
