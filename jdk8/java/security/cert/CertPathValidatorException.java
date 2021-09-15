package java.security.cert;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;

public class CertPathValidatorException extends GeneralSecurityException {
   private static final long serialVersionUID = -3083180014971893139L;
   private int index;
   private CertPath certPath;
   private CertPathValidatorException.Reason reason;

   public CertPathValidatorException() {
      this((String)null, (Throwable)null);
   }

   public CertPathValidatorException(String var1) {
      this(var1, (Throwable)null);
   }

   public CertPathValidatorException(Throwable var1) {
      this(var1 == null ? null : var1.toString(), var1);
   }

   public CertPathValidatorException(String var1, Throwable var2) {
      this(var1, var2, (CertPath)null, -1);
   }

   public CertPathValidatorException(String var1, Throwable var2, CertPath var3, int var4) {
      this(var1, var2, var3, var4, CertPathValidatorException.BasicReason.UNSPECIFIED);
   }

   public CertPathValidatorException(String var1, Throwable var2, CertPath var3, int var4, CertPathValidatorException.Reason var5) {
      super(var1, var2);
      this.index = -1;
      this.reason = CertPathValidatorException.BasicReason.UNSPECIFIED;
      if (var3 == null && var4 != -1) {
         throw new IllegalArgumentException();
      } else if (var4 >= -1 && (var3 == null || var4 < var3.getCertificates().size())) {
         if (var5 == null) {
            throw new NullPointerException("reason can't be null");
         } else {
            this.certPath = var3;
            this.index = var4;
            this.reason = var5;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public CertPath getCertPath() {
      return this.certPath;
   }

   public int getIndex() {
      return this.index;
   }

   public CertPathValidatorException.Reason getReason() {
      return this.reason;
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      if (this.reason == null) {
         this.reason = CertPathValidatorException.BasicReason.UNSPECIFIED;
      }

      if (this.certPath == null && this.index != -1) {
         throw new InvalidObjectException("certpath is null and index != -1");
      } else if (this.index < -1 || this.certPath != null && this.index >= this.certPath.getCertificates().size()) {
         throw new InvalidObjectException("index out of range");
      }
   }

   public static enum BasicReason implements CertPathValidatorException.Reason {
      UNSPECIFIED,
      EXPIRED,
      NOT_YET_VALID,
      REVOKED,
      UNDETERMINED_REVOCATION_STATUS,
      INVALID_SIGNATURE,
      ALGORITHM_CONSTRAINED;
   }

   public interface Reason extends Serializable {
   }
}
