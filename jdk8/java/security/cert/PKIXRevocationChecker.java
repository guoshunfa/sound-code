package java.security.cert;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PKIXRevocationChecker extends PKIXCertPathChecker {
   private URI ocspResponder;
   private X509Certificate ocspResponderCert;
   private List<Extension> ocspExtensions = Collections.emptyList();
   private Map<X509Certificate, byte[]> ocspResponses = Collections.emptyMap();
   private Set<PKIXRevocationChecker.Option> options = Collections.emptySet();

   protected PKIXRevocationChecker() {
   }

   public void setOcspResponder(URI var1) {
      this.ocspResponder = var1;
   }

   public URI getOcspResponder() {
      return this.ocspResponder;
   }

   public void setOcspResponderCert(X509Certificate var1) {
      this.ocspResponderCert = var1;
   }

   public X509Certificate getOcspResponderCert() {
      return this.ocspResponderCert;
   }

   public void setOcspExtensions(List<Extension> var1) {
      this.ocspExtensions = (List)(var1 == null ? Collections.emptyList() : new ArrayList(var1));
   }

   public List<Extension> getOcspExtensions() {
      return Collections.unmodifiableList(this.ocspExtensions);
   }

   public void setOcspResponses(Map<X509Certificate, byte[]> var1) {
      if (var1 == null) {
         this.ocspResponses = Collections.emptyMap();
      } else {
         HashMap var2 = new HashMap(var1.size());
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            var2.put(var4.getKey(), ((byte[])var4.getValue()).clone());
         }

         this.ocspResponses = var2;
      }

   }

   public Map<X509Certificate, byte[]> getOcspResponses() {
      HashMap var1 = new HashMap(this.ocspResponses.size());
      Iterator var2 = this.ocspResponses.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.put(var3.getKey(), ((byte[])var3.getValue()).clone());
      }

      return var1;
   }

   public void setOptions(Set<PKIXRevocationChecker.Option> var1) {
      this.options = (Set)(var1 == null ? Collections.emptySet() : new HashSet(var1));
   }

   public Set<PKIXRevocationChecker.Option> getOptions() {
      return Collections.unmodifiableSet(this.options);
   }

   public abstract List<CertPathValidatorException> getSoftFailExceptions();

   public PKIXRevocationChecker clone() {
      PKIXRevocationChecker var1 = (PKIXRevocationChecker)super.clone();
      var1.ocspExtensions = new ArrayList(this.ocspExtensions);
      var1.ocspResponses = new HashMap(this.ocspResponses);
      Iterator var2 = var1.ocspResponses.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         byte[] var4 = (byte[])var3.getValue();
         var3.setValue(var4.clone());
      }

      var1.options = new HashSet(this.options);
      return var1;
   }

   public static enum Option {
      ONLY_END_ENTITY,
      PREFER_CRLS,
      NO_FALLBACK,
      SOFT_FAIL;
   }
}
