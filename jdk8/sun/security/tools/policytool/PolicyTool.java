package sun.security.tools.policytool;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Permission;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.security.auth.login.LoginException;
import javax.security.auth.x500.X500Principal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.security.provider.PolicyParser;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;

public class PolicyTool {
   static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.policytool.Resources");
   static final Collator collator = Collator.getInstance();
   Vector<String> warnings;
   boolean newWarning;
   boolean modified;
   private static final boolean testing = false;
   private static final Class<?>[] TWOPARAMS;
   private static final Class<?>[] ONEPARAMS;
   private static final Class<?>[] NOPARAMS;
   private static String policyFileName;
   private Vector<PolicyEntry> policyEntries;
   private PolicyParser parser;
   private KeyStore keyStore;
   private String keyStoreName;
   private String keyStoreType;
   private String keyStoreProvider;
   private String keyStorePwdURL;
   private static final String P11KEYSTORE = "PKCS11";
   private static final String NONE = "NONE";

   private PolicyTool() {
      this.newWarning = false;
      this.modified = false;
      this.policyEntries = null;
      this.parser = null;
      this.keyStore = null;
      this.keyStoreName = " ";
      this.keyStoreType = " ";
      this.keyStoreProvider = " ";
      this.keyStorePwdURL = " ";
      this.policyEntries = new Vector();
      this.parser = new PolicyParser();
      this.warnings = new Vector();
   }

   String getPolicyFileName() {
      return policyFileName;
   }

   void setPolicyFileName(String var1) {
      policyFileName = var1;
   }

   void clearKeyStoreInfo() {
      this.keyStoreName = null;
      this.keyStoreType = null;
      this.keyStoreProvider = null;
      this.keyStorePwdURL = null;
      this.keyStore = null;
   }

   String getKeyStoreName() {
      return this.keyStoreName;
   }

   String getKeyStoreType() {
      return this.keyStoreType;
   }

   String getKeyStoreProvider() {
      return this.keyStoreProvider;
   }

   String getKeyStorePwdURL() {
      return this.keyStorePwdURL;
   }

   void openPolicy(String var1) throws FileNotFoundException, PolicyParser.ParsingException, KeyStoreException, CertificateException, InstantiationException, MalformedURLException, IOException, NoSuchAlgorithmException, IllegalAccessException, NoSuchMethodException, UnrecoverableKeyException, NoSuchProviderException, ClassNotFoundException, PropertyExpander.ExpandException, InvocationTargetException {
      this.newWarning = false;
      this.policyEntries = new Vector();
      this.parser = new PolicyParser();
      this.warnings = new Vector();
      this.setPolicyFileName((String)null);
      this.clearKeyStoreInfo();
      if (var1 == null) {
         this.modified = false;
      } else {
         this.setPolicyFileName(var1);
         this.parser.read(new FileReader(var1));
         this.openKeyStore(this.parser.getKeyStoreUrl(), this.parser.getKeyStoreType(), this.parser.getKeyStoreProvider(), this.parser.getStorePassURL());
         Enumeration var2 = this.parser.grantElements();

         label83:
         while(var2.hasMoreElements()) {
            PolicyParser.GrantEntry var3 = (PolicyParser.GrantEntry)var2.nextElement();
            MessageFormat var7;
            Object[] var8;
            if (var3.signedBy != null) {
               String[] var4 = this.parseSigners(var3.signedBy);

               for(int var5 = 0; var5 < var4.length; ++var5) {
                  PublicKey var6 = this.getPublicKeyAlias(var4[var5]);
                  if (var6 == null) {
                     this.newWarning = true;
                     var7 = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
                     var8 = new Object[]{var4[var5]};
                     this.warnings.addElement(var7.format(var8));
                  }
               }
            }

            ListIterator var15 = var3.principals.listIterator(0);

            while(var15.hasNext()) {
               PolicyParser.PrincipalEntry var16 = (PolicyParser.PrincipalEntry)var15.next();

               try {
                  this.verifyPrincipal(var16.getPrincipalClass(), var16.getPrincipalName());
               } catch (ClassNotFoundException var14) {
                  this.newWarning = true;
                  var7 = new MessageFormat(getMessage("Warning.Class.not.found.class"));
                  var8 = new Object[]{var16.getPrincipalClass()};
                  this.warnings.addElement(var7.format(var8));
               }
            }

            Enumeration var17 = var3.permissionElements();

            while(true) {
               PolicyParser.PermissionEntry var18;
               do {
                  if (!var17.hasMoreElements()) {
                     PolicyEntry var19 = new PolicyEntry(this, var3);
                     this.policyEntries.addElement(var19);
                     continue label83;
                  }

                  var18 = (PolicyParser.PermissionEntry)var17.nextElement();

                  Object[] var9;
                  MessageFormat var21;
                  try {
                     this.verifyPermission(var18.permission, var18.name, var18.action);
                  } catch (ClassNotFoundException var12) {
                     this.newWarning = true;
                     var21 = new MessageFormat(getMessage("Warning.Class.not.found.class"));
                     var9 = new Object[]{var18.permission};
                     this.warnings.addElement(var21.format(var9));
                  } catch (InvocationTargetException var13) {
                     this.newWarning = true;
                     var21 = new MessageFormat(getMessage("Warning.Invalid.argument.s.for.constructor.arg"));
                     var9 = new Object[]{var18.permission};
                     this.warnings.addElement(var21.format(var9));
                  }
               } while(var18.signedBy == null);

               String[] var20 = this.parseSigners(var18.signedBy);

               for(int var22 = 0; var22 < var20.length; ++var22) {
                  PublicKey var23 = this.getPublicKeyAlias(var20[var22]);
                  if (var23 == null) {
                     this.newWarning = true;
                     MessageFormat var10 = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
                     Object[] var11 = new Object[]{var20[var22]};
                     this.warnings.addElement(var10.format(var11));
                  }
               }
            }
         }

         this.modified = false;
      }
   }

   void savePolicy(String var1) throws FileNotFoundException, IOException {
      this.parser.setKeyStoreUrl(this.keyStoreName);
      this.parser.setKeyStoreType(this.keyStoreType);
      this.parser.setKeyStoreProvider(this.keyStoreProvider);
      this.parser.setStorePassURL(this.keyStorePwdURL);
      this.parser.write(new FileWriter(var1));
      this.modified = false;
   }

   void openKeyStore(String var1, String var2, String var3, String var4) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, NoSuchProviderException, PropertyExpander.ExpandException {
      if (var1 == null && var2 == null && var3 == null && var4 == null) {
         this.keyStoreName = null;
         this.keyStoreType = null;
         this.keyStoreProvider = null;
         this.keyStorePwdURL = null;
      } else {
         URL var5 = null;
         if (policyFileName != null) {
            File var6 = new File(policyFileName);
            var5 = new URL("file:" + var6.getCanonicalPath());
         }

         if (var1 != null && var1.length() > 0) {
            var1 = PropertyExpander.expand(var1).replace(File.separatorChar, '/');
         }

         if (var2 == null || var2.length() == 0) {
            var2 = KeyStore.getDefaultType();
         }

         if (var4 != null && var4.length() > 0) {
            var4 = PropertyExpander.expand(var4).replace(File.separatorChar, '/');
         }

         try {
            this.keyStore = PolicyUtil.getKeyStore(var5, var1, var2, var3, var4, (Debug)null);
         } catch (IOException var9) {
            String var7 = "no password provided, and no callback handler available for retrieving password";
            Throwable var8 = var9.getCause();
            if (var8 != null && var8 instanceof LoginException && var7.equals(var8.getMessage())) {
               throw new IOException(var7);
            }

            throw var9;
         }

         this.keyStoreName = var1;
         this.keyStoreType = var2;
         this.keyStoreProvider = var3;
         this.keyStorePwdURL = var4;
      }
   }

   boolean addEntry(PolicyEntry var1, int var2) {
      if (var2 < 0) {
         this.policyEntries.addElement(var1);
         this.parser.add(var1.getGrantEntry());
      } else {
         PolicyEntry var3 = (PolicyEntry)this.policyEntries.elementAt(var2);
         this.parser.replace(var3.getGrantEntry(), var1.getGrantEntry());
         this.policyEntries.setElementAt(var1, var2);
      }

      return true;
   }

   boolean addPrinEntry(PolicyEntry var1, PolicyParser.PrincipalEntry var2, int var3) {
      PolicyParser.GrantEntry var4 = var1.getGrantEntry();
      if (var4.contains(var2)) {
         return false;
      } else {
         LinkedList var5 = var4.principals;
         if (var3 != -1) {
            var5.set(var3, var2);
         } else {
            var5.add(var2);
         }

         this.modified = true;
         return true;
      }
   }

   boolean addPermEntry(PolicyEntry var1, PolicyParser.PermissionEntry var2, int var3) {
      PolicyParser.GrantEntry var4 = var1.getGrantEntry();
      if (var4.contains(var2)) {
         return false;
      } else {
         Vector var5 = var4.permissionEntries;
         if (var3 != -1) {
            var5.setElementAt(var2, var3);
         } else {
            var5.addElement(var2);
         }

         this.modified = true;
         return true;
      }
   }

   boolean removePermEntry(PolicyEntry var1, PolicyParser.PermissionEntry var2) {
      PolicyParser.GrantEntry var3 = var1.getGrantEntry();
      this.modified = var3.remove(var2);
      return this.modified;
   }

   boolean removeEntry(PolicyEntry var1) {
      this.parser.remove(var1.getGrantEntry());
      this.modified = true;
      return this.policyEntries.removeElement(var1);
   }

   PolicyEntry[] getEntry() {
      if (this.policyEntries.size() <= 0) {
         return null;
      } else {
         PolicyEntry[] var1 = new PolicyEntry[this.policyEntries.size()];

         for(int var2 = 0; var2 < this.policyEntries.size(); ++var2) {
            var1[var2] = (PolicyEntry)this.policyEntries.elementAt(var2);
         }

         return var1;
      }
   }

   PublicKey getPublicKeyAlias(String var1) throws KeyStoreException {
      if (this.keyStore == null) {
         return null;
      } else {
         Certificate var2 = this.keyStore.getCertificate(var1);
         if (var2 == null) {
            return null;
         } else {
            PublicKey var3 = var2.getPublicKey();
            return var3;
         }
      }
   }

   String[] getPublicKeyAlias() throws KeyStoreException {
      int var1 = 0;
      String[] var2 = null;
      if (this.keyStore == null) {
         return null;
      } else {
         Enumeration var3;
         for(var3 = this.keyStore.aliases(); var3.hasMoreElements(); ++var1) {
            var3.nextElement();
         }

         if (var1 > 0) {
            var2 = new String[var1];
            var1 = 0;

            for(var3 = this.keyStore.aliases(); var3.hasMoreElements(); ++var1) {
               var2[var1] = new String((String)var3.nextElement());
            }
         }

         return var2;
      }
   }

   String[] parseSigners(String var1) {
      String[] var2 = null;
      int var3 = 1;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      while(var5 >= 0) {
         var5 = var1.indexOf(44, var4);
         if (var5 >= 0) {
            ++var3;
            var4 = var5 + 1;
         }
      }

      var2 = new String[var3];
      var5 = 0;
      var4 = 0;

      while(var5 >= 0) {
         if ((var5 = var1.indexOf(44, var4)) >= 0) {
            var2[var6] = var1.substring(var4, var5).trim();
            ++var6;
            var4 = var5 + 1;
         } else {
            var2[var6] = var1.substring(var4).trim();
         }
      }

      return var2;
   }

   void verifyPrincipal(String var1, String var2) throws ClassNotFoundException, InstantiationException {
      if (!var1.equals("WILDCARD_PRINCIPAL_CLASS") && !var1.equals("PolicyParser.REPLACE_NAME")) {
         Class var3 = Class.forName("java.security.Principal");
         Class var4 = Class.forName(var1, true, Thread.currentThread().getContextClassLoader());
         if (!var3.isAssignableFrom(var4)) {
            MessageFormat var5 = new MessageFormat(getMessage("Illegal.Principal.Type.type"));
            Object[] var6 = new Object[]{var1};
            throw new InstantiationException(var5.format(var6));
         } else {
            if ("javax.security.auth.x500.X500Principal".equals(var4.getName())) {
               new X500Principal(var2);
            }

         }
      }
   }

   void verifyPermission(String var1, String var2, String var3) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      Class var4 = Class.forName(var1, true, Thread.currentThread().getContextClassLoader());
      Constructor var5 = null;
      Vector var6 = new Vector(2);
      if (var2 != null) {
         var6.add(var2);
      }

      if (var3 != null) {
         var6.add(var3);
      }

      switch(var6.size()) {
      case 0:
         try {
            var5 = var4.getConstructor(NOPARAMS);
            break;
         } catch (NoSuchMethodException var10) {
            var6.add((Object)null);
         }
      case 1:
         try {
            var5 = var4.getConstructor(ONEPARAMS);
            break;
         } catch (NoSuchMethodException var9) {
            var6.add((Object)null);
         }
      case 2:
         var5 = var4.getConstructor(TWOPARAMS);
      }

      Object[] var7 = var6.toArray();
      Permission var8 = (Permission)var5.newInstance(var7);
   }

   static void parseArgs(String[] var0) {
      boolean var1 = false;

      for(int var5 = 0; var5 < var0.length && var0[var5].startsWith("-"); ++var5) {
         String var2 = var0[var5];
         if (collator.compare(var2, "-file") == 0) {
            ++var5;
            if (var5 == var0.length) {
               usage();
            }

            policyFileName = var0[var5];
         } else {
            MessageFormat var3 = new MessageFormat(getMessage("Illegal.option.option"));
            Object[] var4 = new Object[]{var2};
            System.err.println(var3.format(var4));
            usage();
         }
      }

   }

   static void usage() {
      System.out.println(getMessage("Usage.policytool.options."));
      System.out.println();
      System.out.println(getMessage(".file.file.policy.file.location"));
      System.out.println();
      System.exit(1);
   }

   public static void main(final String[] var0) {
      parseArgs(var0);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            ToolWindow var1 = new ToolWindow(new PolicyTool());
            var1.displayToolWindow(var0);
         }
      });
   }

   static String splitToWords(String var0) {
      return var0.replaceAll("([A-Z])", " $1");
   }

   static String getMessage(String var0) {
      return removeMnemonicAmpersand(rb.getString(var0));
   }

   static int getMnemonicInt(String var0) {
      String var1 = rb.getString(var0);
      return findMnemonicInt(var1);
   }

   static int getDisplayedMnemonicIndex(String var0) {
      String var1 = rb.getString(var0);
      return findMnemonicIndex(var1);
   }

   private static int findMnemonicInt(String var0) {
      for(int var1 = 0; var1 < var0.length() - 1; ++var1) {
         if (var0.charAt(var1) == '&') {
            if (var0.charAt(var1 + 1) != '&') {
               return KeyEvent.getExtendedKeyCodeForChar(var0.charAt(var1 + 1));
            }

            ++var1;
         }
      }

      return 0;
   }

   private static int findMnemonicIndex(String var0) {
      for(int var1 = 0; var1 < var0.length() - 1; ++var1) {
         if (var0.charAt(var1) == '&') {
            if (var0.charAt(var1 + 1) != '&') {
               return var1;
            }

            ++var1;
         }
      }

      return -1;
   }

   private static String removeMnemonicAmpersand(String var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 != '&' || var2 == var0.length() - 1 || var0.charAt(var2 + 1) == '&') {
            var1.append(var3);
         }
      }

      return var1.toString();
   }

   // $FF: synthetic method
   PolicyTool(Object var1) {
      this();
   }

   static {
      collator.setStrength(0);
      if (System.getProperty("apple.laf.useScreenMenuBar") == null) {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
      }

      System.setProperty("apple.awt.application.name", getMessage("Policy.Tool"));
      if (System.getProperty("swing.defaultlaf") == null) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception var1) {
         }
      }

      TWOPARAMS = new Class[]{String.class, String.class};
      ONEPARAMS = new Class[]{String.class};
      NOPARAMS = new Class[0];
      policyFileName = null;
   }
}
