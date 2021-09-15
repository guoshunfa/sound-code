package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import java.io.PrintStream;
import java.security.PublicKey;

public class KeyUtils {
   private KeyUtils() {
   }

   public static void prinoutKeyInfo(KeyInfo var0, PrintStream var1) throws XMLSecurityException {
      int var2;
      for(var2 = 0; var2 < var0.lengthKeyName(); ++var2) {
         KeyName var3 = var0.itemKeyName(var2);
         var1.println("KeyName(" + var2 + ")=\"" + var3.getKeyName() + "\"");
      }

      for(var2 = 0; var2 < var0.lengthKeyValue(); ++var2) {
         KeyValue var5 = var0.itemKeyValue(var2);
         PublicKey var4 = var5.getPublicKey();
         var1.println("KeyValue Nr. " + var2);
         var1.println((Object)var4);
      }

      for(var2 = 0; var2 < var0.lengthMgmtData(); ++var2) {
         MgmtData var6 = var0.itemMgmtData(var2);
         var1.println("MgmtData(" + var2 + ")=\"" + var6.getMgmtData() + "\"");
      }

      for(var2 = 0; var2 < var0.lengthX509Data(); ++var2) {
         X509Data var7 = var0.itemX509Data(var2);
         var1.println("X509Data(" + var2 + ")=\"" + (var7.containsCertificate() ? "Certificate " : "") + (var7.containsIssuerSerial() ? "IssuerSerial " : "") + "\"");
      }

   }
}
