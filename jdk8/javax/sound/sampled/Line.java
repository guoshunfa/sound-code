package javax.sound.sampled;

public interface Line extends AutoCloseable {
   Line.Info getLineInfo();

   void open() throws LineUnavailableException;

   void close();

   boolean isOpen();

   Control[] getControls();

   boolean isControlSupported(Control.Type var1);

   Control getControl(Control.Type var1);

   void addLineListener(LineListener var1);

   void removeLineListener(LineListener var1);

   public static class Info {
      private final Class lineClass;

      public Info(Class<?> var1) {
         if (var1 == null) {
            this.lineClass = Line.class;
         } else {
            this.lineClass = var1;
         }

      }

      public Class<?> getLineClass() {
         return this.lineClass;
      }

      public boolean matches(Line.Info var1) {
         if (!this.getClass().isInstance(var1)) {
            return false;
         } else {
            return this.getLineClass().isAssignableFrom(var1.getLineClass());
         }
      }

      public String toString() {
         String var1 = "javax.sound.sampled.";
         String var2 = new String(this.getLineClass().toString());
         int var4 = var2.indexOf(var1);
         String var3;
         if (var4 != -1) {
            var3 = var2.substring(0, var4) + var2.substring(var4 + var1.length(), var2.length());
         } else {
            var3 = var2;
         }

         return var3;
      }
   }
}
