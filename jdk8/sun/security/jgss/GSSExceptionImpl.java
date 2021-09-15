package sun.security.jgss;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public class GSSExceptionImpl extends GSSException {
   private static final long serialVersionUID = 4251197939069005575L;
   private String majorMessage;

   GSSExceptionImpl(int var1, Oid var2) {
      super(var1);
      this.majorMessage = super.getMajorString() + ": " + var2;
   }

   public GSSExceptionImpl(int var1, String var2) {
      super(var1);
      this.majorMessage = var2;
   }

   public GSSExceptionImpl(int var1, Exception var2) {
      super(var1);
      this.initCause(var2);
   }

   public GSSExceptionImpl(int var1, String var2, Exception var3) {
      this(var1, var2);
      this.initCause(var3);
   }

   public String getMessage() {
      return this.majorMessage != null ? this.majorMessage : super.getMessage();
   }
}
