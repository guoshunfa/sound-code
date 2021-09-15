package javax.swing.plaf.synth;

public class ColorType {
   public static final ColorType FOREGROUND = new ColorType("Foreground");
   public static final ColorType BACKGROUND = new ColorType("Background");
   public static final ColorType TEXT_FOREGROUND = new ColorType("TextForeground");
   public static final ColorType TEXT_BACKGROUND = new ColorType("TextBackground");
   public static final ColorType FOCUS = new ColorType("Focus");
   public static final int MAX_COUNT;
   private static int nextID;
   private String description;
   private int index;

   protected ColorType(String var1) {
      if (var1 == null) {
         throw new NullPointerException("ColorType must have a valid description");
      } else {
         this.description = var1;
         Class var2 = ColorType.class;
         synchronized(ColorType.class) {
            this.index = nextID++;
         }
      }
   }

   public final int getID() {
      return this.index;
   }

   public String toString() {
      return this.description;
   }

   static {
      MAX_COUNT = Math.max(FOREGROUND.getID(), Math.max(BACKGROUND.getID(), FOCUS.getID())) + 1;
   }
}
