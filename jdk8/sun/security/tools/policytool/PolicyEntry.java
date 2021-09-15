package sun.security.tools.policytool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ListIterator;
import sun.security.provider.PolicyParser;

class PolicyEntry {
   private CodeSource codesource;
   private PolicyTool tool;
   private PolicyParser.GrantEntry grantEntry;
   private boolean testing = false;

   PolicyEntry(PolicyTool var1, PolicyParser.GrantEntry var2) throws MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
      this.tool = var1;
      URL var3 = null;
      if (var2.codeBase != null) {
         var3 = new URL(var2.codeBase);
      }

      this.codesource = new CodeSource(var3, (Certificate[])null);
      if (this.testing) {
         System.out.println("Adding Policy Entry:");
         System.out.println("    CodeBase = " + var3);
         System.out.println("    Signers = " + var2.signedBy);
         System.out.println("    with " + var2.principals.size() + " Principals");
      }

      this.grantEntry = var2;
   }

   CodeSource getCodeSource() {
      return this.codesource;
   }

   PolicyParser.GrantEntry getGrantEntry() {
      return this.grantEntry;
   }

   String headerToString() {
      String var1 = this.principalsToString();
      return var1.length() == 0 ? this.codebaseToString() : this.codebaseToString() + ", " + var1;
   }

   String codebaseToString() {
      String var1 = new String();
      if (this.grantEntry.codeBase != null && !this.grantEntry.codeBase.equals("")) {
         var1 = var1.concat("CodeBase \"" + this.grantEntry.codeBase + "\"");
      }

      if (this.grantEntry.signedBy != null && !this.grantEntry.signedBy.equals("")) {
         var1 = var1.length() > 0 ? var1.concat(", SignedBy \"" + this.grantEntry.signedBy + "\"") : var1.concat("SignedBy \"" + this.grantEntry.signedBy + "\"");
      }

      return var1.length() == 0 ? new String("CodeBase <ALL>") : var1;
   }

   String principalsToString() {
      String var1 = "";
      if (this.grantEntry.principals != null && !this.grantEntry.principals.isEmpty()) {
         StringBuffer var2 = new StringBuffer(200);
         ListIterator var3 = this.grantEntry.principals.listIterator();

         while(var3.hasNext()) {
            PolicyParser.PrincipalEntry var4 = (PolicyParser.PrincipalEntry)var3.next();
            var2.append(" Principal " + var4.getDisplayClass() + " " + var4.getDisplayName(true));
            if (var3.hasNext()) {
               var2.append(", ");
            }
         }

         var1 = var2.toString();
      }

      return var1;
   }

   PolicyParser.PermissionEntry toPermissionEntry(Permission var1) {
      String var2 = null;
      if (var1.getActions() != null && var1.getActions().trim() != "") {
         var2 = var1.getActions();
      }

      PolicyParser.PermissionEntry var3 = new PolicyParser.PermissionEntry(var1.getClass().getName(), var1.getName(), var2);
      return var3;
   }
}
