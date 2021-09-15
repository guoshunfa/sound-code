package javax.naming;

import java.util.Hashtable;

public abstract class ReferralException extends NamingException {
   private static final long serialVersionUID = -2881363844695698876L;

   protected ReferralException(String var1) {
      super(var1);
   }

   protected ReferralException() {
   }

   public abstract Object getReferralInfo();

   public abstract Context getReferralContext() throws NamingException;

   public abstract Context getReferralContext(Hashtable<?, ?> var1) throws NamingException;

   public abstract boolean skipReferral();

   public abstract void retryReferral();
}
