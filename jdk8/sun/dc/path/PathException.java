package sun.dc.path;

public class PathException extends Exception {
   public static final String BAD_PATH_endPath = "endPath: bad path";
   public static final String BAD_PATH_useProxy = "useProxy: bad path";
   public static final String DUMMY = "";

   public PathException() {
   }

   public PathException(String var1) {
      super(var1);
   }
}
