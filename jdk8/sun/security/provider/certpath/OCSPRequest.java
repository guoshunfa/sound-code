package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.Extension;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.PKIXExtensions;

class OCSPRequest {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final boolean dump;
   private final List<CertId> certIds;
   private final List<Extension> extensions;
   private byte[] nonce;

   OCSPRequest(CertId var1) {
      this(Collections.singletonList(var1));
   }

   OCSPRequest(List<CertId> var1) {
      this.certIds = var1;
      this.extensions = Collections.emptyList();
   }

   OCSPRequest(List<CertId> var1, List<Extension> var2) {
      this.certIds = var1;
      this.extensions = var2;
   }

   byte[] encodeBytes() throws IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      Iterator var3 = this.certIds.iterator();

      while(var3.hasNext()) {
         CertId var4 = (CertId)var3.next();
         DerOutputStream var5 = new DerOutputStream();
         var4.encode(var5);
         var2.write((byte)48, (DerOutputStream)var5);
      }

      var1.write((byte)48, (DerOutputStream)var2);
      DerOutputStream var7;
      DerOutputStream var9;
      if (!this.extensions.isEmpty()) {
         var7 = new DerOutputStream();
         Iterator var8 = this.extensions.iterator();

         while(var8.hasNext()) {
            Extension var10 = (Extension)var8.next();
            var10.encode(var7);
            if (var10.getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
               this.nonce = var10.getValue();
            }
         }

         var9 = new DerOutputStream();
         var9.write((byte)48, (DerOutputStream)var7);
         var1.write(DerValue.createTag((byte)-128, true, (byte)2), var9);
      }

      var7 = new DerOutputStream();
      var7.write((byte)48, (DerOutputStream)var1);
      var9 = new DerOutputStream();
      var9.write((byte)48, (DerOutputStream)var7);
      byte[] var11 = var9.toByteArray();
      if (dump) {
         HexDumpEncoder var6 = new HexDumpEncoder();
         debug.println("OCSPRequest bytes...\n\n" + var6.encode(var11) + "\n");
      }

      return var11;
   }

   List<CertId> getCertIds() {
      return this.certIds;
   }

   byte[] getNonce() {
      return this.nonce;
   }

   static {
      dump = debug != null && Debug.isOn("ocsp");
   }
}
