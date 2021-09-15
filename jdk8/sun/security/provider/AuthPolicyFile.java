package sun.security.provider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.security.auth.AuthPermission;
import javax.security.auth.Policy;
import javax.security.auth.PrivateCredentialPermission;
import javax.security.auth.Subject;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;

/** @deprecated */
@Deprecated
public class AuthPolicyFile extends Policy {
   static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private static final Debug debug = Debug.getInstance("policy", "\t[Auth Policy]");
   private static final String AUTH_POLICY = "java.security.auth.policy";
   private static final String SECURITY_MANAGER = "java.security.manager";
   private static final String AUTH_POLICY_URL = "auth.policy.url.";
   private Vector<AuthPolicyFile.PolicyEntry> policyEntries;
   private Hashtable<Object, Object> aliasMapping;
   private boolean initialized = false;
   private boolean expandProperties = true;
   private boolean ignoreIdentityScope = true;
   private static final Class<?>[] PARAMS = new Class[]{String.class, String.class};

   public AuthPolicyFile() {
      String var1 = System.getProperty("java.security.auth.policy");
      if (var1 == null) {
         var1 = System.getProperty("java.security.manager");
      }

      if (var1 != null) {
         this.init();
      }

   }

   private synchronized void init() {
      if (!this.initialized) {
         this.policyEntries = new Vector();
         this.aliasMapping = new Hashtable(11);
         this.initPolicyFile();
         this.initialized = true;
      }
   }

   public synchronized void refresh() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AuthPermission("refreshPolicy"));
      }

      this.initialized = false;
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            AuthPolicyFile.this.init();
            return null;
         }
      });
   }

   private KeyStore initKeyStore(URL var1, String var2, String var3) {
      if (var2 != null) {
         try {
            URL var4 = null;

            try {
               var4 = new URL(var2);
            } catch (MalformedURLException var7) {
               var4 = new URL(var1, var2);
            }

            if (debug != null) {
               debug.println("reading keystore" + var4);
            }

            BufferedInputStream var5 = new BufferedInputStream(PolicyUtil.getInputStream(var4));
            KeyStore var6;
            if (var3 != null) {
               var6 = KeyStore.getInstance(var3);
            } else {
               var6 = KeyStore.getInstance(KeyStore.getDefaultType());
            }

            var6.load(var5, (char[])null);
            var5.close();
            return var6;
         } catch (Exception var8) {
            if (debug != null) {
               var8.printStackTrace();
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private void initPolicyFile() {
      String var1 = Security.getProperty("policy.expandProperties");
      if (var1 != null) {
         this.expandProperties = var1.equalsIgnoreCase("true");
      }

      String var2 = Security.getProperty("policy.ignoreIdentityScope");
      if (var2 != null) {
         this.ignoreIdentityScope = var2.equalsIgnoreCase("true");
      }

      String var3 = Security.getProperty("policy.allowSystemProperty");
      boolean var5;
      if (var3 != null && var3.equalsIgnoreCase("true")) {
         String var4 = System.getProperty("java.security.auth.policy");
         if (var4 != null) {
            var5 = false;
            if (var4.startsWith("=")) {
               var5 = true;
               var4 = var4.substring(1);
            }

            try {
               var4 = PropertyExpander.expand(var4);
               File var7 = new File(var4);
               URL var6;
               if (var7.exists()) {
                  var6 = new URL("file:" + var7.getCanonicalPath());
               } else {
                  var6 = new URL(var4);
               }

               if (debug != null) {
                  debug.println("reading " + var6);
               }

               this.init(var6);
            } catch (Exception var9) {
               if (debug != null) {
                  debug.println("caught exception: " + var9);
               }
            }

            if (var5) {
               if (debug != null) {
                  debug.println("overriding other policies!");
               }

               return;
            }
         }
      }

      int var10 = 1;

      String var11;
      for(var5 = false; (var11 = Security.getProperty("auth.policy.url." + var10)) != null; ++var10) {
         try {
            var11 = PropertyExpander.expand(var11).replace(File.separatorChar, '/');
            if (debug != null) {
               debug.println("reading " + var11);
            }

            this.init(new URL(var11));
            var5 = true;
         } catch (Exception var8) {
            if (debug != null) {
               debug.println("error reading policy " + var8);
               var8.printStackTrace();
            }
         }
      }

      if (!var5) {
      }

   }

   private boolean checkForTrustedIdentity(Certificate var1) {
      return false;
   }

   private void init(URL var1) {
      PolicyParser var2 = new PolicyParser(this.expandProperties);

      try {
         InputStreamReader var3 = new InputStreamReader(PolicyUtil.getInputStream(var1));
         Throwable var4 = null;

         try {
            var2.read(var3);
            KeyStore var5 = this.initKeyStore(var1, var2.getKeyStoreUrl(), var2.getKeyStoreType());
            Enumeration var6 = var2.grantElements();

            while(var6.hasMoreElements()) {
               PolicyParser.GrantEntry var7 = (PolicyParser.GrantEntry)var6.nextElement();
               this.addGrantEntry(var7, var5);
            }
         } catch (Throwable var17) {
            var4 = var17;
            throw var17;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var16) {
                     var4.addSuppressed(var16);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (PolicyParser.ParsingException var19) {
         System.err.println("java.security.auth.policy" + rb.getString(".error.parsing.") + var1);
         System.err.println("java.security.auth.policy" + rb.getString("COLON") + var19.getMessage());
         if (debug != null) {
            var19.printStackTrace();
         }
      } catch (Exception var20) {
         if (debug != null) {
            debug.println("error parsing " + var1);
            debug.println(var20.toString());
            var20.printStackTrace();
         }
      }

   }

   CodeSource getCodeSource(PolicyParser.GrantEntry var1, KeyStore var2) throws MalformedURLException {
      Certificate[] var3 = null;
      if (var1.signedBy != null) {
         var3 = this.getCertificates(var2, var1.signedBy);
         if (var3 == null) {
            if (debug != null) {
               debug.println(" no certs for alias " + var1.signedBy + ", ignoring.");
            }

            return null;
         }
      }

      URL var4;
      if (var1.codeBase != null) {
         var4 = new URL(var1.codeBase);
      } else {
         var4 = null;
      }

      return var1.principals != null && var1.principals.size() != 0 ? this.canonicalizeCodebase(new SubjectCodeSource((Subject)null, var1.principals, var4, var3), false) : this.canonicalizeCodebase(new CodeSource(var4, var3), false);
   }

   private void addGrantEntry(PolicyParser.GrantEntry var1, KeyStore var2) {
      if (debug != null) {
         debug.println("Adding policy entry: ");
         debug.println("  signedBy " + var1.signedBy);
         debug.println("  codeBase " + var1.codeBase);
         if (var1.principals != null) {
            Iterator var3 = var1.principals.iterator();

            while(var3.hasNext()) {
               PolicyParser.PrincipalEntry var4 = (PolicyParser.PrincipalEntry)var3.next();
               debug.println("  " + var4.getPrincipalClass() + " " + var4.getPrincipalName());
            }
         }

         debug.println();
      }

      try {
         CodeSource var14 = this.getCodeSource(var1, var2);
         if (var14 == null) {
            return;
         }

         AuthPolicyFile.PolicyEntry var15 = new AuthPolicyFile.PolicyEntry(var14);
         Enumeration var5 = var1.permissionElements();

         while(var5.hasMoreElements()) {
            PolicyParser.PermissionEntry var6 = (PolicyParser.PermissionEntry)var5.nextElement();

            try {
               Permission var7;
               if (var6.permission.equals("javax.security.auth.PrivateCredentialPermission") && var6.name.endsWith(" self")) {
                  var7 = getInstance(var6.permission, var6.name + " \"self\"", var6.action);
               } else {
                  var7 = getInstance(var6.permission, var6.name, var6.action);
               }

               var15.add(var7);
               if (debug != null) {
                  debug.println("  " + var7);
               }
            } catch (ClassNotFoundException var10) {
               Certificate[] var8;
               if (var6.signedBy != null) {
                  var8 = this.getCertificates(var2, var6.signedBy);
               } else {
                  var8 = null;
               }

               if (var8 != null || var6.signedBy == null) {
                  UnresolvedPermission var9 = new UnresolvedPermission(var6.permission, var6.name, var6.action, var8);
                  var15.add(var9);
                  if (debug != null) {
                     debug.println("  " + var9);
                  }
               }
            } catch (InvocationTargetException var11) {
               System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Permission.") + var6.permission + rb.getString("SPACE") + var11.getTargetException());
            } catch (Exception var12) {
               System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Permission.") + var6.permission + rb.getString("SPACE") + var12);
            }
         }

         this.policyEntries.addElement(var15);
      } catch (Exception var13) {
         System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Entry.") + var1 + rb.getString("SPACE") + var13);
      }

      if (debug != null) {
         debug.println();
      }

   }

   private static final Permission getInstance(String var0, String var1, String var2) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      Class var3 = Class.forName(var0);
      Constructor var4 = var3.getConstructor(PARAMS);
      return (Permission)var4.newInstance(var1, var2);
   }

   Certificate[] getCertificates(KeyStore var1, String var2) {
      Vector var3 = null;
      StringTokenizer var4 = new StringTokenizer(var2, ",");
      int var5 = 0;

      while(var4.hasMoreTokens()) {
         String var6 = var4.nextToken().trim();
         ++var5;
         Certificate var7 = null;
         var7 = (Certificate)this.aliasMapping.get(var6);
         if (var7 == null && var1 != null) {
            try {
               var7 = var1.getCertificate(var6);
            } catch (KeyStoreException var9) {
            }

            if (var7 != null) {
               this.aliasMapping.put(var6, var7);
               this.aliasMapping.put(var7, var6);
            }
         }

         if (var7 != null) {
            if (var3 == null) {
               var3 = new Vector();
            }

            var3.addElement(var7);
         }
      }

      if (var3 != null && var5 == var3.size()) {
         Certificate[] var10 = new Certificate[var3.size()];
         var3.copyInto(var10);
         return var10;
      } else {
         return null;
      }
   }

   private final synchronized Enumeration<AuthPolicyFile.PolicyEntry> elements() {
      return this.policyEntries.elements();
   }

   public PermissionCollection getPermissions(final Subject var1, final CodeSource var2) {
      return (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
         public PermissionCollection run() {
            SubjectCodeSource var1x = new SubjectCodeSource(var1, (LinkedList)null, var2 == null ? null : var2.getLocation(), var2 == null ? null : var2.getCertificates());
            return (PermissionCollection)(AuthPolicyFile.this.initialized ? AuthPolicyFile.this.getPermissions((Permissions)(new Permissions()), var1x) : new PolicyPermissions(AuthPolicyFile.this, var1x));
         }
      });
   }

   PermissionCollection getPermissions(CodeSource var1) {
      return (PermissionCollection)(this.initialized ? this.getPermissions(new Permissions(), var1) : new PolicyPermissions(this, var1));
   }

   Permissions getPermissions(Permissions var1, CodeSource var2) {
      if (!this.initialized) {
         this.init();
      }

      CodeSource[] var3 = new CodeSource[]{this.canonicalizeCodebase(var2, true)};
      if (debug != null) {
         debug.println("evaluate(" + var3[0] + ")\n");
      }

      for(int var4 = 0; var4 < this.policyEntries.size(); ++var4) {
         AuthPolicyFile.PolicyEntry var5 = (AuthPolicyFile.PolicyEntry)this.policyEntries.elementAt(var4);
         if (debug != null) {
            debug.println("PolicyFile CodeSource implies: " + var5.codesource.toString() + "\n\n\t" + var3[0].toString() + "\n\n");
         }

         if (var5.codesource.implies(var3[0])) {
            for(int var6 = 0; var6 < var5.permissions.size(); ++var6) {
               Permission var7 = (Permission)var5.permissions.elementAt(var6);
               if (debug != null) {
                  debug.println("  granting " + var7);
               }

               if (!this.addSelfPermissions(var7, var5.codesource, var3[0], var1)) {
                  var1.add(var7);
               }
            }
         }
      }

      if (!this.ignoreIdentityScope) {
         Certificate[] var8 = var3[0].getCertificates();
         if (var8 != null) {
            for(int var9 = 0; var9 < var8.length; ++var9) {
               if (this.aliasMapping.get(var8[var9]) == null && this.checkForTrustedIdentity(var8[var9])) {
                  var1.add(new AllPermission());
               }
            }
         }
      }

      return var1;
   }

   private boolean addSelfPermissions(Permission var1, CodeSource var2, CodeSource var3, Permissions var4) {
      if (!(var1 instanceof PrivateCredentialPermission)) {
         return false;
      } else if (!(var2 instanceof SubjectCodeSource)) {
         return false;
      } else {
         PrivateCredentialPermission var5 = (PrivateCredentialPermission)var1;
         SubjectCodeSource var6 = (SubjectCodeSource)var2;
         String[][] var7 = var5.getPrincipals();
         if (var7.length > 0 && var7[0][0].equalsIgnoreCase("self") && var7[0][1].equalsIgnoreCase("self")) {
            if (var6.getPrincipals() == null) {
               return true;
            } else {
               Iterator var8 = var6.getPrincipals().iterator();

               while(var8.hasNext()) {
                  PolicyParser.PrincipalEntry var9 = (PolicyParser.PrincipalEntry)var8.next();
                  String[][] var10 = this.getPrincipalInfo(var9, var3);

                  for(int var11 = 0; var11 < var10.length; ++var11) {
                     PrivateCredentialPermission var12 = new PrivateCredentialPermission(var5.getCredentialClass() + " " + var10[var11][0] + " \"" + var10[var11][1] + "\"", "read");
                     if (debug != null) {
                        debug.println("adding SELF permission: " + var12.toString());
                     }

                     var4.add(var12);
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry var1, CodeSource var2) {
      if (!var1.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") && !var1.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) {
         String[][] var10 = new String[1][2];
         var10[0][0] = var1.getPrincipalClass();
         var10[0][1] = var1.getPrincipalName();
         return var10;
      } else {
         SubjectCodeSource var3;
         Set var4;
         String[][] var5;
         int var6;
         Iterator var7;
         Principal var8;
         if (!var1.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") && var1.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) {
            var3 = (SubjectCodeSource)var2;
            var4 = null;

            try {
               Class var11 = Class.forName(var1.getPrincipalClass(), false, ClassLoader.getSystemClassLoader());
               var4 = var3.getSubject().getPrincipals(var11);
            } catch (Exception var9) {
               if (debug != null) {
                  debug.println("problem finding Principal Class when expanding SELF permission: " + var9.toString());
               }
            }

            if (var4 == null) {
               return new String[0][0];
            } else {
               var5 = new String[var4.size()][2];
               var6 = 0;

               for(var7 = var4.iterator(); var7.hasNext(); ++var6) {
                  var8 = (Principal)var7.next();
                  var5[var6][0] = var8.getClass().getName();
                  var5[var6][1] = var8.getName();
               }

               return var5;
            }
         } else {
            var3 = (SubjectCodeSource)var2;
            var4 = var3.getSubject().getPrincipals();
            var5 = new String[var4.size()][2];
            var6 = 0;

            for(var7 = var4.iterator(); var7.hasNext(); ++var6) {
               var8 = (Principal)var7.next();
               var5[var6][0] = var8.getClass().getName();
               var5[var6][1] = var8.getName();
            }

            return var5;
         }
      }
   }

   Certificate[] getSignerCertificates(CodeSource var1) {
      Certificate[] var2 = null;
      if ((var2 = var1.getCertificates()) == null) {
         return null;
      } else {
         int var3;
         for(var3 = 0; var3 < var2.length; ++var3) {
            if (!(var2[var3] instanceof X509Certificate)) {
               return var1.getCertificates();
            }
         }

         var3 = 0;

         int var4;
         for(var4 = 0; var3 < var2.length; ++var3) {
            ++var4;

            while(var3 + 1 < var2.length && ((X509Certificate)var2[var3]).getIssuerDN().equals(((X509Certificate)var2[var3 + 1]).getSubjectDN())) {
               ++var3;
            }
         }

         if (var4 == var2.length) {
            return var2;
         } else {
            ArrayList var5 = new ArrayList();

            for(var3 = 0; var3 < var2.length; ++var3) {
               var5.add(var2[var3]);

               while(var3 + 1 < var2.length && ((X509Certificate)var2[var3]).getIssuerDN().equals(((X509Certificate)var2[var3 + 1]).getSubjectDN())) {
                  ++var3;
               }
            }

            Certificate[] var6 = new Certificate[var5.size()];
            var5.toArray(var6);
            return var6;
         }
      }
   }

   private CodeSource canonicalizeCodebase(CodeSource var1, boolean var2) {
      Object var3 = var1;
      if (var1.getLocation() != null && var1.getLocation().getProtocol().equalsIgnoreCase("file")) {
         SubjectCodeSource var5;
         try {
            String var10 = var1.getLocation().getFile().replace('/', File.separatorChar);
            var5 = null;
            if (!var10.endsWith("*")) {
               var10 = (new File(var10)).getCanonicalPath();
            } else {
               var10 = var10.substring(0, var10.length() - 1);
               boolean var6 = false;
               if (var10.endsWith(File.separator)) {
                  var6 = true;
               }

               if (var10.equals("")) {
                  var10 = System.getProperty("user.dir");
               }

               File var7 = new File(var10);
               var10 = var7.getCanonicalPath();
               StringBuffer var8 = new StringBuffer(var10);
               if (!var10.endsWith(File.separator) && (var6 || var7.isDirectory())) {
                  var8.append(File.separatorChar);
               }

               var8.append('*');
               var10 = var8.toString();
            }

            URL var11 = (new File(var10)).toURL();
            if (var1 instanceof SubjectCodeSource) {
               SubjectCodeSource var12 = (SubjectCodeSource)var1;
               if (var2) {
                  var3 = new SubjectCodeSource(var12.getSubject(), var12.getPrincipals(), var11, this.getSignerCertificates(var12));
               } else {
                  var3 = new SubjectCodeSource(var12.getSubject(), var12.getPrincipals(), var11, var12.getCertificates());
               }
            } else if (var2) {
               var3 = new CodeSource(var11, this.getSignerCertificates(var1));
            } else {
               var3 = new CodeSource(var11, var1.getCertificates());
            }
         } catch (IOException var9) {
            if (var2) {
               if (!(var1 instanceof SubjectCodeSource)) {
                  var3 = new CodeSource(var1.getLocation(), this.getSignerCertificates(var1));
               } else {
                  var5 = (SubjectCodeSource)var1;
                  var3 = new SubjectCodeSource(var5.getSubject(), var5.getPrincipals(), var5.getLocation(), this.getSignerCertificates(var5));
               }
            }
         }
      } else if (var2) {
         if (!(var1 instanceof SubjectCodeSource)) {
            var3 = new CodeSource(var1.getLocation(), this.getSignerCertificates(var1));
         } else {
            SubjectCodeSource var4 = (SubjectCodeSource)var1;
            var3 = new SubjectCodeSource(var4.getSubject(), var4.getPrincipals(), var4.getLocation(), this.getSignerCertificates(var4));
         }
      }

      return (CodeSource)var3;
   }

   private static class PolicyEntry {
      CodeSource codesource;
      Vector<Permission> permissions;

      PolicyEntry(CodeSource var1) {
         this.codesource = var1;
         this.permissions = new Vector();
      }

      void add(Permission var1) {
         this.permissions.addElement(var1);
      }

      CodeSource getCodeSource() {
         return this.codesource;
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();
         var1.append(AuthPolicyFile.rb.getString("LPARAM"));
         var1.append((Object)this.getCodeSource());
         var1.append("\n");

         for(int var2 = 0; var2 < this.permissions.size(); ++var2) {
            Permission var3 = (Permission)this.permissions.elementAt(var2);
            var1.append(AuthPolicyFile.rb.getString("SPACE"));
            var1.append(AuthPolicyFile.rb.getString("SPACE"));
            var1.append((Object)var3);
            var1.append(AuthPolicyFile.rb.getString("NEWLINE"));
         }

         var1.append(AuthPolicyFile.rb.getString("RPARAM"));
         var1.append(AuthPolicyFile.rb.getString("NEWLINE"));
         return var1.toString();
      }
   }
}
