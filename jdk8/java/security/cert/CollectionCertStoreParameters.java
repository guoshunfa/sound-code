package java.security.cert;

import java.util.Collection;
import java.util.Collections;

public class CollectionCertStoreParameters implements CertStoreParameters {
   private Collection<?> coll;

   public CollectionCertStoreParameters(Collection<?> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.coll = var1;
      }
   }

   public CollectionCertStoreParameters() {
      this.coll = Collections.EMPTY_SET;
   }

   public Collection<?> getCollection() {
      return this.coll;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("CollectionCertStoreParameters: [\n");
      var1.append("  collection: " + this.coll + "\n");
      var1.append("]");
      return var1.toString();
   }
}
