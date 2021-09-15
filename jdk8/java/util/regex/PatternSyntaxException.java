package java.util.regex;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class PatternSyntaxException extends IllegalArgumentException {
   private static final long serialVersionUID = -3864639126226059218L;
   private final String desc;
   private final String pattern;
   private final int index;
   private static final String nl = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("line.separator")));

   public PatternSyntaxException(String var1, String var2, int var3) {
      this.desc = var1;
      this.pattern = var2;
      this.index = var3;
   }

   public int getIndex() {
      return this.index;
   }

   public String getDescription() {
      return this.desc;
   }

   public String getPattern() {
      return this.pattern;
   }

   public String getMessage() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.desc);
      if (this.index >= 0) {
         var1.append(" near index ");
         var1.append(this.index);
      }

      var1.append(nl);
      var1.append(this.pattern);
      if (this.index >= 0 && this.pattern != null && this.index < this.pattern.length()) {
         var1.append(nl);

         for(int var2 = 0; var2 < this.index; ++var2) {
            var1.append(' ');
         }

         var1.append('^');
      }

      return var1.toString();
   }
}
