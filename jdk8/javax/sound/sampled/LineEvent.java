package javax.sound.sampled;

import java.util.EventObject;

public class LineEvent extends EventObject {
   private final LineEvent.Type type;
   private final long position;

   public LineEvent(Line var1, LineEvent.Type var2, long var3) {
      super(var1);
      this.type = var2;
      this.position = var3;
   }

   public final Line getLine() {
      return (Line)this.getSource();
   }

   public final LineEvent.Type getType() {
      return this.type;
   }

   public final long getFramePosition() {
      return this.position;
   }

   public String toString() {
      String var1 = "";
      if (this.type != null) {
         var1 = this.type.toString() + " ";
      }

      String var2;
      if (this.getLine() == null) {
         var2 = "null";
      } else {
         var2 = this.getLine().toString();
      }

      return new String(var1 + "event from line " + var2);
   }

   public static class Type {
      private String name;
      public static final LineEvent.Type OPEN = new LineEvent.Type("Open");
      public static final LineEvent.Type CLOSE = new LineEvent.Type("Close");
      public static final LineEvent.Type START = new LineEvent.Type("Start");
      public static final LineEvent.Type STOP = new LineEvent.Type("Stop");

      protected Type(String var1) {
         this.name = var1;
      }

      public final boolean equals(Object var1) {
         return super.equals(var1);
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public String toString() {
         return this.name;
      }
   }
}
