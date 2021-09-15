package java.nio.charset;

public class CodingErrorAction {
   private String name;
   public static final CodingErrorAction IGNORE = new CodingErrorAction("IGNORE");
   public static final CodingErrorAction REPLACE = new CodingErrorAction("REPLACE");
   public static final CodingErrorAction REPORT = new CodingErrorAction("REPORT");

   private CodingErrorAction(String var1) {
      this.name = var1;
   }

   public String toString() {
      return this.name;
   }
}
