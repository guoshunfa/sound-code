package sun.dc.pr;

public class PRException extends Exception {
   public static final String BAD_COORD_setOutputArea = "setOutputArea: alpha coordinate out of bounds";
   public static final String ALPHA_ARRAY_SHORT = "writeAlpha: alpha destination array too short";
   public static final String DUMMY = "";

   public PRException() {
   }

   public PRException(String var1) {
      super(var1);
   }
}
