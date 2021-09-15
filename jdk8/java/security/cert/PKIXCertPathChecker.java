package java.security.cert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public abstract class PKIXCertPathChecker implements CertPathChecker, Cloneable {
   protected PKIXCertPathChecker() {
   }

   public abstract void init(boolean var1) throws CertPathValidatorException;

   public abstract boolean isForwardCheckingSupported();

   public abstract Set<String> getSupportedExtensions();

   public abstract void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException;

   public void check(Certificate var1) throws CertPathValidatorException {
      this.check(var1, Collections.emptySet());
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }
}
