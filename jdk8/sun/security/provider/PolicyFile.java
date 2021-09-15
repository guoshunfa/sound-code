package sun.security.provider;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.Random;
import java.util.StringTokenizer;
import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.SharedSecrets;
import sun.net.www.ParseUtil;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;
import sun.security.util.SecurityConstants;

public class PolicyFile extends Policy {
   private static final Debug debug = Debug.getInstance("policy");
   private static final String NONE = "NONE";
   private static final String P11KEYSTORE = "PKCS11";
   private static final String SELF = "${{self}}";
   private static final String X500PRINCIPAL = "javax.security.auth.x500.X500Principal";
   private static final String POLICY = "java.security.policy";
   private static final String SECURITY_MANAGER = "java.security.manager";
   private static final String POLICY_URL = "policy.url.";
   private static final String AUTH_POLICY = "java.security.auth.policy";
   private static final String AUTH_POLICY_URL = "auth.policy.url.";
   private static final int DEFAULT_CACHE_SIZE = 1;
   private volatile PolicyFile.PolicyInfo policyInfo;
   private boolean constructed = false;
   private boolean expandProperties = true;
   private boolean ignoreIdentityScope = true;
   private boolean allowSystemProperties = true;
   private boolean notUtf8 = false;
   private URL url;
   private static final Class[] PARAMS0 = new Class[0];
   private static final Class[] PARAMS1 = new Class[]{String.class};
   private static final Class[] PARAMS2 = new Class[]{String.class, String.class};

   public PolicyFile() {
      this.init((URL)null);
   }

   public PolicyFile(URL var1) {
      this.url = var1;
      this.init(var1);
   }

   private void init(URL var1) {
      String var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            PolicyFile.this.expandProperties = "true".equalsIgnoreCase(Security.getProperty("policy.expandProperties"));
            PolicyFile.this.ignoreIdentityScope = "true".equalsIgnoreCase(Security.getProperty("policy.ignoreIdentityScope"));
            PolicyFile.this.allowSystemProperties = "true".equalsIgnoreCase(Security.getProperty("policy.allowSystemProperty"));
            PolicyFile.this.notUtf8 = "false".equalsIgnoreCase(System.getProperty("sun.security.policy.utf8"));
            return System.getProperty("sun.security.policy.numcaches");
         }
      });
      int var3;
      if (var2 != null) {
         try {
            var3 = Integer.parseInt(var2);
         } catch (NumberFormatException var5) {
            var3 = 1;
         }
      } else {
         var3 = 1;
      }

      PolicyFile.PolicyInfo var4 = new PolicyFile.PolicyInfo(var3);
      this.initPolicyFile(var4, var1);
      this.policyInfo = var4;
   }

   private void initPolicyFile(final PolicyFile.PolicyInfo var1, final URL var2) {
      if (var2 != null) {
         if (debug != null) {
            debug.println("reading " + var2);
         }

         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               if (!PolicyFile.this.init(var2, var1)) {
                  PolicyFile.this.initStaticPolicy(var1);
               }

               return null;
            }
         });
      } else {
         boolean var3 = this.initPolicyFile("java.security.policy", "policy.url.", var1);
         if (!var3) {
            this.initStaticPolicy(var1);
         }

         this.initPolicyFile("java.security.auth.policy", "auth.policy.url.", var1);
      }

   }

   private boolean initPolicyFile(final String var1, final String var2, final PolicyFile.PolicyInfo var3) {
      Boolean var4 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            boolean var1x = false;
            URL var4;
            if (PolicyFile.this.allowSystemProperties) {
               String var2x = System.getProperty(var1);
               if (var2x != null) {
                  boolean var3x = false;
                  if (var2x.startsWith("=")) {
                     var3x = true;
                     var2x = var2x.substring(1);
                  }

                  try {
                     var2x = PropertyExpander.expand(var2x);
                     File var5 = new File(var2x);
                     if (var5.exists()) {
                        var4 = ParseUtil.fileToEncodedURL(new File(var5.getCanonicalPath()));
                     } else {
                        var4 = new URL(var2x);
                     }

                     if (PolicyFile.debug != null) {
                        PolicyFile.debug.println("reading " + var4);
                     }

                     if (PolicyFile.this.init(var4, var3)) {
                        var1x = true;
                     }
                  } catch (Exception var7) {
                     if (PolicyFile.debug != null) {
                        PolicyFile.debug.println("caught exception: " + var7);
                     }
                  }

                  if (var3x) {
                     if (PolicyFile.debug != null) {
                        PolicyFile.debug.println("overriding other policies!");
                     }

                     return var1x;
                  }
               }
            }

            String var9;
            for(int var8 = 1; (var9 = Security.getProperty(var2 + var8)) != null; ++var8) {
               try {
                  var4 = null;
                  String var10 = PropertyExpander.expand(var9).replace(File.separatorChar, '/');
                  if (!var9.startsWith("file:${java.home}/") && !var9.startsWith("file:${user.home}/")) {
                     var4 = (new URI(var10)).toURL();
                  } else {
                     var4 = (new File(var10.substring(5))).toURI().toURL();
                  }

                  if (PolicyFile.debug != null) {
                     PolicyFile.debug.println("reading " + var4);
                  }

                  if (PolicyFile.this.init(var4, var3)) {
                     var1x = true;
                  }
               } catch (Exception var6) {
                  if (PolicyFile.debug != null) {
                     PolicyFile.debug.println("error reading policy " + var6);
                     var6.printStackTrace();
                  }
               }
            }

            return var1x;
         }
      });
      return var4;
   }

   private boolean init(URL var1, PolicyFile.PolicyInfo var2) {
      boolean var3 = false;
      PolicyParser var4 = new PolicyParser(this.expandProperties);
      InputStreamReader var5 = null;

      try {
         if (this.notUtf8) {
            var5 = new InputStreamReader(PolicyUtil.getInputStream(var1));
         } else {
            var5 = new InputStreamReader(PolicyUtil.getInputStream(var1), "UTF-8");
         }

         var4.read(var5);
         KeyStore var6 = null;

         try {
            var6 = PolicyUtil.getKeyStore(var1, var4.getKeyStoreUrl(), var4.getKeyStoreType(), var4.getKeyStoreProvider(), var4.getStorePassURL(), debug);
         } catch (Exception var20) {
            if (debug != null) {
               var20.printStackTrace();
            }
         }

         Enumeration var24 = var4.grantElements();

         while(var24.hasMoreElements()) {
            PolicyParser.GrantEntry var25 = (PolicyParser.GrantEntry)var24.nextElement();
            this.addGrantEntry(var25, var6, var2);
         }
      } catch (PolicyParser.ParsingException var21) {
         MessageFormat var7 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.parsing.policy.message"));
         Object[] var8 = new Object[]{var1, var21.getLocalizedMessage()};
         System.err.println(var7.format(var8));
         if (debug != null) {
            var21.printStackTrace();
         }
      } catch (Exception var22) {
         if (debug != null) {
            debug.println("error parsing " + var1);
            debug.println(var22.toString());
            var22.printStackTrace();
         }
      } finally {
         if (var5 != null) {
            try {
               var5.close();
               var3 = true;
            } catch (IOException var19) {
            }
         } else {
            var3 = true;
         }

      }

      return var3;
   }

   private void initStaticPolicy(final PolicyFile.PolicyInfo var1) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            PolicyFile.PolicyEntry var1x = new PolicyFile.PolicyEntry(new CodeSource((URL)null, (Certificate[])null));
            var1x.add(SecurityConstants.LOCAL_LISTEN_PERMISSION);
            var1x.add(new PropertyPermission("java.version", "read"));
            var1x.add(new PropertyPermission("java.vendor", "read"));
            var1x.add(new PropertyPermission("java.vendor.url", "read"));
            var1x.add(new PropertyPermission("java.class.version", "read"));
            var1x.add(new PropertyPermission("os.name", "read"));
            var1x.add(new PropertyPermission("os.version", "read"));
            var1x.add(new PropertyPermission("os.arch", "read"));
            var1x.add(new PropertyPermission("file.separator", "read"));
            var1x.add(new PropertyPermission("path.separator", "read"));
            var1x.add(new PropertyPermission("line.separator", "read"));
            var1x.add(new PropertyPermission("java.specification.version", "read"));
            var1x.add(new PropertyPermission("java.specification.vendor", "read"));
            var1x.add(new PropertyPermission("java.specification.name", "read"));
            var1x.add(new PropertyPermission("java.vm.specification.version", "read"));
            var1x.add(new PropertyPermission("java.vm.specification.vendor", "read"));
            var1x.add(new PropertyPermission("java.vm.specification.name", "read"));
            var1x.add(new PropertyPermission("java.vm.version", "read"));
            var1x.add(new PropertyPermission("java.vm.vendor", "read"));
            var1x.add(new PropertyPermission("java.vm.name", "read"));
            var1.policyEntries.add(var1x);
            String[] var2 = PolicyParser.parseExtDirs("${{java.ext.dirs}}", 0);
            if (var2 != null && var2.length > 0) {
               for(int var3 = 0; var3 < var2.length; ++var3) {
                  try {
                     var1x = new PolicyFile.PolicyEntry(PolicyFile.this.canonicalizeCodebase(new CodeSource(new URL(var2[var3]), (Certificate[])null), false));
                     var1x.add(SecurityConstants.ALL_PERMISSION);
                     var1.policyEntries.add(var1x);
                  } catch (Exception var5) {
                  }
               }
            }

            return null;
         }
      });
   }

   private CodeSource getCodeSource(PolicyParser.GrantEntry var1, KeyStore var2, PolicyFile.PolicyInfo var3) throws MalformedURLException {
      Certificate[] var4 = null;
      if (var1.signedBy != null) {
         var4 = this.getCertificates(var2, var1.signedBy, var3);
         if (var4 == null) {
            if (debug != null) {
               debug.println("  -- No certs for alias '" + var1.signedBy + "' - ignoring entry");
            }

            return null;
         }
      }

      URL var5;
      if (var1.codeBase != null) {
         var5 = new URL(var1.codeBase);
      } else {
         var5 = null;
      }

      return this.canonicalizeCodebase(new CodeSource(var5, var4), false);
   }

   private void addGrantEntry(PolicyParser.GrantEntry var1, KeyStore var2, PolicyFile.PolicyInfo var3) {
      if (debug != null) {
         debug.println("Adding policy entry: ");
         debug.println("  signedBy " + var1.signedBy);
         debug.println("  codeBase " + var1.codeBase);
         if (var1.principals != null) {
            Iterator var4 = var1.principals.iterator();

            while(var4.hasNext()) {
               PolicyParser.PrincipalEntry var5 = (PolicyParser.PrincipalEntry)var4.next();
               debug.println("  " + var5.toString());
            }
         }
      }

      try {
         CodeSource var15 = this.getCodeSource(var1, var2, var3);
         if (var15 == null) {
            return;
         }

         if (!this.replacePrincipals(var1.principals, var2)) {
            return;
         }

         PolicyFile.PolicyEntry var17 = new PolicyFile.PolicyEntry(var15, var1.principals);
         Enumeration var18 = var1.permissionElements();

         while(var18.hasMoreElements()) {
            PolicyParser.PermissionEntry var7 = (PolicyParser.PermissionEntry)var18.nextElement();

            MessageFormat var9;
            Object[] var10;
            Certificate[] var19;
            try {
               this.expandPermissionName(var7, var2);
               if (var7.permission.equals("javax.security.auth.PrivateCredentialPermission") && var7.name.endsWith(" self")) {
                  var7.name = var7.name.substring(0, var7.name.indexOf("self")) + "${{self}}";
               }

               Object var8;
               if (var7.name != null && var7.name.indexOf("${{self}}") != -1) {
                  if (var7.signedBy != null) {
                     var19 = this.getCertificates(var2, var7.signedBy, var3);
                  } else {
                     var19 = null;
                  }

                  var8 = new PolicyFile.SelfPermission(var7.permission, var7.name, var7.action, var19);
               } else {
                  var8 = getInstance(var7.permission, var7.name, var7.action);
               }

               var17.add((Permission)var8);
               if (debug != null) {
                  debug.println("  " + var8);
               }
            } catch (ClassNotFoundException var11) {
               if (var7.signedBy != null) {
                  var19 = this.getCertificates(var2, var7.signedBy, var3);
               } else {
                  var19 = null;
               }

               if (var19 != null || var7.signedBy == null) {
                  UnresolvedPermission var20 = new UnresolvedPermission(var7.permission, var7.name, var7.action, var19);
                  var17.add(var20);
                  if (debug != null) {
                     debug.println("  " + var20);
                  }
               }
            } catch (InvocationTargetException var12) {
               var9 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
               var10 = new Object[]{var7.permission, var12.getTargetException().toString()};
               System.err.println(var9.format(var10));
            } catch (Exception var13) {
               var9 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
               var10 = new Object[]{var7.permission, var13.toString()};
               System.err.println(var9.format(var10));
            }
         }

         var3.policyEntries.add(var17);
      } catch (Exception var14) {
         MessageFormat var16 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Entry.message"));
         Object[] var6 = new Object[]{var14.toString()};
         System.err.println(var16.format(var6));
      }

      if (debug != null) {
         debug.println();
      }

   }

   private static final Permission getInstance(String var0, String var1, String var2) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      Class var3 = Class.forName(var0, false, (ClassLoader)null);
      Permission var4 = getKnownInstance(var3, var1, var2);
      if (var4 != null) {
         return var4;
      } else if (!Permission.class.isAssignableFrom(var3)) {
         throw new ClassCastException(var0 + " is not a Permission");
      } else {
         Constructor var5;
         Constructor var6;
         if (var1 == null && var2 == null) {
            try {
               var5 = var3.getConstructor(PARAMS0);
               return (Permission)var5.newInstance();
            } catch (NoSuchMethodException var9) {
               try {
                  var6 = var3.getConstructor(PARAMS1);
                  return (Permission)var6.newInstance(var1);
               } catch (NoSuchMethodException var8) {
                  Constructor var7 = var3.getConstructor(PARAMS2);
                  return (Permission)var7.newInstance(var1, var2);
               }
            }
         } else if (var1 != null && var2 == null) {
            try {
               var5 = var3.getConstructor(PARAMS1);
               return (Permission)var5.newInstance(var1);
            } catch (NoSuchMethodException var10) {
               var6 = var3.getConstructor(PARAMS2);
               return (Permission)var6.newInstance(var1, var2);
            }
         } else {
            var5 = var3.getConstructor(PARAMS2);
            return (Permission)var5.newInstance(var1, var2);
         }
      }
   }

   private static final Permission getKnownInstance(Class<?> var0, String var1, String var2) {
      if (var0.equals(FilePermission.class)) {
         return new FilePermission(var1, var2);
      } else if (var0.equals(SocketPermission.class)) {
         return new SocketPermission(var1, var2);
      } else if (var0.equals(RuntimePermission.class)) {
         return new RuntimePermission(var1, var2);
      } else if (var0.equals(PropertyPermission.class)) {
         return new PropertyPermission(var1, var2);
      } else if (var0.equals(NetPermission.class)) {
         return new NetPermission(var1, var2);
      } else {
         return var0.equals(AllPermission.class) ? SecurityConstants.ALL_PERMISSION : null;
      }
   }

   private Certificate[] getCertificates(KeyStore var1, String var2, PolicyFile.PolicyInfo var3) {
      ArrayList var4 = null;
      StringTokenizer var5 = new StringTokenizer(var2, ",");
      int var6 = 0;

      while(var5.hasMoreTokens()) {
         String var7 = var5.nextToken().trim();
         ++var6;
         Certificate var8 = null;
         synchronized(var3.aliasMapping) {
            var8 = (Certificate)var3.aliasMapping.get(var7);
            if (var8 == null && var1 != null) {
               try {
                  var8 = var1.getCertificate(var7);
               } catch (KeyStoreException var12) {
               }

               if (var8 != null) {
                  var3.aliasMapping.put(var7, var8);
                  var3.aliasMapping.put(var8, var7);
               }
            }
         }

         if (var8 != null) {
            if (var4 == null) {
               var4 = new ArrayList();
            }

            var4.add(var8);
         }
      }

      if (var4 != null && var6 == var4.size()) {
         Certificate[] var14 = new Certificate[var4.size()];
         var4.toArray(var14);
         return var14;
      } else {
         return null;
      }
   }

   public void refresh() {
      this.init(this.url);
   }

   public boolean implies(ProtectionDomain var1, Permission var2) {
      JavaSecurityProtectionDomainAccess.ProtectionDomainCache var3 = this.policyInfo.getPdMapping();
      PermissionCollection var4 = var3.get(var1);
      if (var4 != null) {
         return var4.implies(var2);
      } else {
         var4 = this.getPermissions(var1);
         if (var4 == null) {
            return false;
         } else {
            var3.put(var1, var4);
            return var4.implies(var2);
         }
      }
   }

   public PermissionCollection getPermissions(ProtectionDomain var1) {
      Permissions var2 = new Permissions();
      if (var1 == null) {
         return var2;
      } else {
         this.getPermissions(var2, var1);
         PermissionCollection var3 = var1.getPermissions();
         if (var3 != null) {
            synchronized(var3) {
               Enumeration var5 = var3.elements();

               while(var5.hasMoreElements()) {
                  var2.add((Permission)var5.nextElement());
               }
            }
         }

         return var2;
      }
   }

   public PermissionCollection getPermissions(CodeSource var1) {
      return this.getPermissions(new Permissions(), var1);
   }

   private PermissionCollection getPermissions(Permissions var1, ProtectionDomain var2) {
      if (debug != null) {
         debug.println("getPermissions:\n\t" + this.printPD(var2));
      }

      final CodeSource var3 = var2.getCodeSource();
      if (var3 == null) {
         return var1;
      } else {
         CodeSource var4 = (CodeSource)AccessController.doPrivileged(new PrivilegedAction<CodeSource>() {
            public CodeSource run() {
               return PolicyFile.this.canonicalizeCodebase(var3, true);
            }
         });
         return this.getPermissions(var1, var4, var2.getPrincipals());
      }
   }

   private PermissionCollection getPermissions(Permissions var1, final CodeSource var2) {
      if (var2 == null) {
         return var1;
      } else {
         CodeSource var3 = (CodeSource)AccessController.doPrivileged(new PrivilegedAction<CodeSource>() {
            public CodeSource run() {
               return PolicyFile.this.canonicalizeCodebase(var2, true);
            }
         });
         return this.getPermissions(var1, var3, (Principal[])null);
      }
   }

   private Permissions getPermissions(Permissions var1, CodeSource var2, Principal[] var3) {
      PolicyFile.PolicyInfo var4 = this.policyInfo;
      Iterator var5 = var4.policyEntries.iterator();

      while(var5.hasNext()) {
         PolicyFile.PolicyEntry var6 = (PolicyFile.PolicyEntry)var5.next();
         this.addPermissions(var1, var2, var3, var6);
      }

      synchronized(var4.identityPolicyEntries) {
         Iterator var11 = var4.identityPolicyEntries.iterator();

         while(true) {
            if (!var11.hasNext()) {
               break;
            }

            PolicyFile.PolicyEntry var7 = (PolicyFile.PolicyEntry)var11.next();
            this.addPermissions(var1, var2, var3, var7);
         }
      }

      if (!this.ignoreIdentityScope) {
         Certificate[] var10 = var2.getCertificates();
         if (var10 != null) {
            for(int var12 = 0; var12 < var10.length; ++var12) {
               Object var13 = var4.aliasMapping.get(var10[var12]);
               if (var13 == null && this.checkForTrustedIdentity(var10[var12], var4)) {
                  var1.add(SecurityConstants.ALL_PERMISSION);
               }
            }
         }
      }

      return var1;
   }

   private void addPermissions(Permissions var1, final CodeSource var2, Principal[] var3, final PolicyFile.PolicyEntry var4) {
      if (debug != null) {
         debug.println("evaluate codesources:\n\tPolicy CodeSource: " + var4.getCodeSource() + "\n\tActive CodeSource: " + var2);
      }

      Boolean var5 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return new Boolean(var4.getCodeSource().implies(var2));
         }
      });
      if (!var5) {
         if (debug != null) {
            debug.println("evaluation (codesource) failed");
         }

      } else {
         List var6 = var4.getPrincipals();
         if (debug != null) {
            ArrayList var7 = new ArrayList();
            if (var3 != null) {
               for(int var8 = 0; var8 < var3.length; ++var8) {
                  var7.add(new PolicyParser.PrincipalEntry(var3[var8].getClass().getName(), var3[var8].getName()));
               }
            }

            debug.println("evaluate principals:\n\tPolicy Principals: " + var6 + "\n\tActive Principals: " + var7);
         }

         if (var6 != null && !var6.isEmpty()) {
            if (var3 != null && var3.length != 0) {
               Iterator var16 = var6.iterator();

               PolicyParser.PrincipalEntry var17;
               do {
                  while(true) {
                     do {
                        if (!var16.hasNext()) {
                           if (debug != null) {
                              debug.println("evaluation (codesource/principals) passed");
                           }

                           this.addPerms(var1, var3, var4);
                           return;
                        }

                        var17 = (PolicyParser.PrincipalEntry)var16.next();
                     } while(var17.isWildcardClass());

                     if (var17.isWildcardName()) {
                        break;
                     }

                     HashSet var9 = new HashSet(Arrays.asList(var3));
                     Subject var10 = new Subject(true, var9, Collections.EMPTY_SET, Collections.EMPTY_SET);

                     try {
                        ClassLoader var11 = Thread.currentThread().getContextClassLoader();
                        Class var12 = Class.forName(var17.principalClass, false, var11);
                        if (!Principal.class.isAssignableFrom(var12)) {
                           throw new ClassCastException(var17.principalClass + " is not a Principal");
                        }

                        Constructor var13 = var12.getConstructor(PARAMS1);
                        Principal var14 = (Principal)var13.newInstance(var17.principalName);
                        if (debug != null) {
                           debug.println("found Principal " + var14.getClass().getName());
                        }

                        if (!var14.implies(var10)) {
                           if (debug != null) {
                              debug.println("evaluation (principal implies) failed");
                           }

                           return;
                        }
                     } catch (Exception var15) {
                        if (debug != null) {
                           var15.printStackTrace();
                        }

                        if (!var17.implies(var10)) {
                           if (debug != null) {
                              debug.println("evaluation (default principal implies) failed");
                           }

                           return;
                        }
                     }
                  }
               } while(wildcardPrincipalNameImplies(var17.principalClass, var3));

               if (debug != null) {
                  debug.println("evaluation (principal name wildcard) failed");
               }

            } else {
               if (debug != null) {
                  debug.println("evaluation (principals) failed");
               }

            }
         } else {
            this.addPerms(var1, var3, var4);
            if (debug != null) {
               debug.println("evaluation (codesource/principals) passed");
            }

         }
      }
   }

   private static boolean wildcardPrincipalNameImplies(String var0, Principal[] var1) {
      Principal[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Principal var5 = var2[var4];
         if (var0.equals(var5.getClass().getName())) {
            return true;
         }
      }

      return false;
   }

   private void addPerms(Permissions var1, Principal[] var2, PolicyFile.PolicyEntry var3) {
      for(int var4 = 0; var4 < var3.permissions.size(); ++var4) {
         Permission var5 = (Permission)var3.permissions.get(var4);
         if (debug != null) {
            debug.println("  granting " + var5);
         }

         if (var5 instanceof PolicyFile.SelfPermission) {
            this.expandSelf((PolicyFile.SelfPermission)var5, var3.getPrincipals(), var2, var1);
         } else {
            var1.add(var5);
         }
      }

   }

   private void expandSelf(PolicyFile.SelfPermission var1, List<PolicyParser.PrincipalEntry> var2, Principal[] var3, Permissions var4) {
      if (var2 != null && !var2.isEmpty()) {
         int var5 = 0;

         int var6;
         StringBuilder var7;
         for(var7 = new StringBuilder(); (var6 = var1.getSelfName().indexOf("${{self}}", var5)) != -1; var5 = var6 + "${{self}}".length()) {
            var7.append(var1.getSelfName().substring(var5, var6));
            Iterator var8 = var2.iterator();

            while(var8.hasNext()) {
               PolicyParser.PrincipalEntry var9 = (PolicyParser.PrincipalEntry)var8.next();
               String[][] var10 = this.getPrincipalInfo(var9, var3);

               for(int var11 = 0; var11 < var10.length; ++var11) {
                  if (var11 != 0) {
                     var7.append(", ");
                  }

                  var7.append(var10[var11][0] + " \"" + var10[var11][1] + "\"");
               }

               if (var8.hasNext()) {
                  var7.append(", ");
               }
            }
         }

         var7.append(var1.getSelfName().substring(var5));
         if (debug != null) {
            debug.println("  expanded:\n\t" + var1.getSelfName() + "\n  into:\n\t" + var7.toString());
         }

         try {
            var4.add(getInstance(var1.getSelfType(), var7.toString(), var1.getSelfActions()));
         } catch (ClassNotFoundException var17) {
            Class var19 = null;
            synchronized(var4) {
               Enumeration var21 = var4.elements();

               while(var21.hasMoreElements()) {
                  Permission var12 = (Permission)var21.nextElement();
                  if (var12.getClass().getName().equals(var1.getSelfType())) {
                     var19 = var12.getClass();
                     break;
                  }
               }
            }

            if (var19 == null) {
               var4.add(new UnresolvedPermission(var1.getSelfType(), var7.toString(), var1.getSelfActions(), var1.getCerts()));
            } else {
               try {
                  Constructor var20;
                  if (var1.getSelfActions() == null) {
                     try {
                        var20 = var19.getConstructor(PARAMS1);
                        var4.add((Permission)var20.newInstance(var7.toString()));
                     } catch (NoSuchMethodException var14) {
                        var20 = var19.getConstructor(PARAMS2);
                        var4.add((Permission)var20.newInstance(var7.toString(), var1.getSelfActions()));
                     }
                  } else {
                     var20 = var19.getConstructor(PARAMS2);
                     var4.add((Permission)var20.newInstance(var7.toString(), var1.getSelfActions()));
                  }
               } catch (Exception var15) {
                  if (debug != null) {
                     debug.println("self entry expansion  instantiation failed: " + var15.toString());
                  }
               }
            }
         } catch (Exception var18) {
            if (debug != null) {
               debug.println(var18.toString());
            }
         }

      } else {
         if (debug != null) {
            debug.println("Ignoring permission " + var1.getSelfType() + " with target name (" + var1.getSelfName() + ").  No Principal(s) specified in the grant clause.  SELF-based target names are only valid in the context of a Principal-based grant entry.");
         }

      }
   }

   private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry var1, Principal[] var2) {
      String[][] var3;
      if (!var1.isWildcardClass() && !var1.isWildcardName()) {
         var3 = new String[1][2];
         var3[0][0] = var1.principalClass;
         var3[0][1] = var1.principalName;
         return var3;
      } else {
         int var4;
         if (!var1.isWildcardClass() && var1.isWildcardName()) {
            ArrayList var8 = new ArrayList();

            for(var4 = 0; var4 < var2.length; ++var4) {
               if (var1.principalClass.equals(var2[var4].getClass().getName())) {
                  var8.add(var2[var4]);
               }
            }

            String[][] var9 = new String[var8.size()][2];
            int var5 = 0;

            for(Iterator var6 = var8.iterator(); var6.hasNext(); ++var5) {
               Principal var7 = (Principal)var6.next();
               var9[var5][0] = var7.getClass().getName();
               var9[var5][1] = var7.getName();
            }

            return var9;
         } else {
            var3 = new String[var2.length][2];

            for(var4 = 0; var4 < var2.length; ++var4) {
               var3[var4][0] = var2[var4].getClass().getName();
               var3[var4][1] = var2[var4].getName();
            }

            return var3;
         }
      }
   }

   protected Certificate[] getSignerCertificates(CodeSource var1) {
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
      String var3 = null;
      CodeSource var4 = var1;
      URL var5 = var1.getLocation();
      String var6;
      if (var5 != null) {
         if (var5.getProtocol().equals("jar")) {
            var6 = var5.getFile();
            int var7 = var6.indexOf("!/");
            if (var7 != -1) {
               try {
                  var5 = new URL(var6.substring(0, var7));
               } catch (MalformedURLException var9) {
               }
            }
         }

         if (var5.getProtocol().equals("file")) {
            boolean var11 = false;
            String var13 = var5.getHost();
            var11 = var13 == null || var13.equals("") || var13.equals("~") || var13.equalsIgnoreCase("localhost");
            if (var11) {
               var3 = var5.getFile().replace('/', File.separatorChar);
               var3 = ParseUtil.decode(var3);
            }
         }
      }

      if (var3 != null) {
         try {
            var6 = null;
            var3 = canonPath(var3);
            URL var12 = ParseUtil.fileToEncodedURL(new File(var3));
            if (var2) {
               var4 = new CodeSource(var12, this.getSignerCertificates(var1));
            } else {
               var4 = new CodeSource(var12, var1.getCertificates());
            }
         } catch (IOException var10) {
            if (var2) {
               var4 = new CodeSource(var1.getLocation(), this.getSignerCertificates(var1));
            }
         }
      } else if (var2) {
         var4 = new CodeSource(var1.getLocation(), this.getSignerCertificates(var1));
      }

      return var4;
   }

   private static String canonPath(String var0) throws IOException {
      if (var0.endsWith("*")) {
         var0 = var0.substring(0, var0.length() - 1) + "-";
         var0 = (new File(var0)).getCanonicalPath();
         return var0.substring(0, var0.length() - 1) + "*";
      } else {
         return (new File(var0)).getCanonicalPath();
      }
   }

   private String printPD(ProtectionDomain var1) {
      Principal[] var2 = var1.getPrincipals();
      String var3 = "<no principals>";
      if (var2 != null && var2.length > 0) {
         StringBuilder var4 = new StringBuilder("(principals ");

         for(int var5 = 0; var5 < var2.length; ++var5) {
            var4.append(var2[var5].getClass().getName() + " \"" + var2[var5].getName() + "\"");
            if (var5 < var2.length - 1) {
               var4.append(", ");
            } else {
               var4.append(")");
            }
         }

         var3 = var4.toString();
      }

      return "PD CodeSource: " + var1.getCodeSource() + "\n\tPD ClassLoader: " + var1.getClassLoader() + "\n\tPD Principals: " + var3;
   }

   private boolean replacePrincipals(List<PolicyParser.PrincipalEntry> var1, KeyStore var2) {
      if (var1 != null && !var1.isEmpty() && var2 != null) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            PolicyParser.PrincipalEntry var4 = (PolicyParser.PrincipalEntry)var3.next();
            if (var4.isReplaceName()) {
               String var5;
               if ((var5 = this.getDN(var4.principalName, var2)) == null) {
                  return false;
               }

               if (debug != null) {
                  debug.println("  Replacing \"" + var4.principalName + "\" with " + "javax.security.auth.x500.X500Principal" + "/\"" + var5 + "\"");
               }

               var4.principalClass = "javax.security.auth.x500.X500Principal";
               var4.principalName = var5;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private void expandPermissionName(PolicyParser.PermissionEntry var1, KeyStore var2) throws Exception {
      if (var1.name != null && var1.name.indexOf("${{", 0) != -1) {
         int var3 = 0;
         StringBuilder var6 = new StringBuilder();

         int var4;
         while((var4 = var1.name.indexOf("${{", var3)) != -1) {
            int var5 = var1.name.indexOf("}}", var4);
            if (var5 < 1) {
               break;
            }

            var6.append(var1.name.substring(var3, var4));
            String var7 = var1.name.substring(var4 + 3, var5);
            String var9 = var7;
            int var8;
            if ((var8 = var7.indexOf(":")) != -1) {
               var9 = var7.substring(0, var8);
            }

            if (var9.equalsIgnoreCase("self")) {
               var6.append(var1.name.substring(var4, var5 + 2));
               var3 = var5 + 2;
            } else {
               MessageFormat var11;
               Object[] var12;
               if (!var9.equalsIgnoreCase("alias")) {
                  var11 = new MessageFormat(ResourcesMgr.getString("substitution.value.prefix.unsupported"));
                  var12 = new Object[]{var9};
                  throw new Exception(var11.format(var12));
               }

               if (var8 == -1) {
                  var11 = new MessageFormat(ResourcesMgr.getString("alias.name.not.provided.pe.name."));
                  var12 = new Object[]{var1.name};
                  throw new Exception(var11.format(var12));
               }

               String var10 = var7.substring(var8 + 1);
               if ((var10 = this.getDN(var10, var2)) == null) {
                  var11 = new MessageFormat(ResourcesMgr.getString("unable.to.perform.substitution.on.alias.suffix"));
                  var12 = new Object[]{var7.substring(var8 + 1)};
                  throw new Exception(var11.format(var12));
               }

               var6.append("javax.security.auth.x500.X500Principal \"" + var10 + "\"");
               var3 = var5 + 2;
            }
         }

         var6.append(var1.name.substring(var3));
         if (debug != null) {
            debug.println("  Permission name expanded from:\n\t" + var1.name + "\nto\n\t" + var6.toString());
         }

         var1.name = var6.toString();
      }
   }

   private String getDN(String var1, KeyStore var2) {
      Certificate var3 = null;

      try {
         var3 = var2.getCertificate(var1);
      } catch (Exception var6) {
         if (debug != null) {
            debug.println("  Error retrieving certificate for '" + var1 + "': " + var6.toString());
         }

         return null;
      }

      if (var3 != null && var3 instanceof X509Certificate) {
         X509Certificate var4 = (X509Certificate)var3;
         X500Principal var5 = new X500Principal(var4.getSubjectX500Principal().toString());
         return var5.getName();
      } else {
         if (debug != null) {
            debug.println("  -- No certificate for '" + var1 + "' - ignoring entry");
         }

         return null;
      }
   }

   private boolean checkForTrustedIdentity(Certificate var1, PolicyFile.PolicyInfo var2) {
      return false;
   }

   private static class PolicyInfo {
      private static final boolean verbose = false;
      final List<PolicyFile.PolicyEntry> policyEntries = new ArrayList();
      final List<PolicyFile.PolicyEntry> identityPolicyEntries = Collections.synchronizedList(new ArrayList(2));
      final Map<Object, Object> aliasMapping = Collections.synchronizedMap(new HashMap(11));
      private final JavaSecurityProtectionDomainAccess.ProtectionDomainCache[] pdMapping;
      private Random random;

      PolicyInfo(int var1) {
         this.pdMapping = new JavaSecurityProtectionDomainAccess.ProtectionDomainCache[var1];
         JavaSecurityProtectionDomainAccess var2 = SharedSecrets.getJavaSecurityProtectionDomainAccess();

         for(int var3 = 0; var3 < var1; ++var3) {
            this.pdMapping[var3] = var2.getProtectionDomainCache();
         }

         if (var1 > 1) {
            this.random = new Random();
         }

      }

      JavaSecurityProtectionDomainAccess.ProtectionDomainCache getPdMapping() {
         if (this.pdMapping.length == 1) {
            return this.pdMapping[0];
         } else {
            int var1 = Math.abs(this.random.nextInt() % this.pdMapping.length);
            return this.pdMapping[var1];
         }
      }
   }

   private static class SelfPermission extends Permission {
      private static final long serialVersionUID = -8315562579967246806L;
      private String type;
      private String name;
      private String actions;
      private Certificate[] certs;

      public SelfPermission(String var1, String var2, String var3, Certificate[] var4) {
         super(var1);
         if (var1 == null) {
            throw new NullPointerException(ResourcesMgr.getString("type.can.t.be.null"));
         } else {
            this.type = var1;
            this.name = var2;
            this.actions = var3;
            if (var4 != null) {
               int var5;
               for(var5 = 0; var5 < var4.length; ++var5) {
                  if (!(var4[var5] instanceof X509Certificate)) {
                     this.certs = (Certificate[])var4.clone();
                     break;
                  }
               }

               if (this.certs == null) {
                  var5 = 0;

                  int var6;
                  for(var6 = 0; var5 < var4.length; ++var5) {
                     ++var6;

                     while(var5 + 1 < var4.length && ((X509Certificate)var4[var5]).getIssuerDN().equals(((X509Certificate)var4[var5 + 1]).getSubjectDN())) {
                        ++var5;
                     }
                  }

                  if (var6 == var4.length) {
                     this.certs = (Certificate[])var4.clone();
                  }

                  if (this.certs == null) {
                     ArrayList var7 = new ArrayList();

                     for(var5 = 0; var5 < var4.length; ++var5) {
                        var7.add(var4[var5]);

                        while(var5 + 1 < var4.length && ((X509Certificate)var4[var5]).getIssuerDN().equals(((X509Certificate)var4[var5 + 1]).getSubjectDN())) {
                           ++var5;
                        }
                     }

                     this.certs = new Certificate[var7.size()];
                     var7.toArray(this.certs);
                  }
               }
            }

         }
      }

      public boolean implies(Permission var1) {
         return false;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PolicyFile.SelfPermission)) {
            return false;
         } else {
            PolicyFile.SelfPermission var2 = (PolicyFile.SelfPermission)var1;
            if (this.type.equals(var2.type) && this.name.equals(var2.name) && this.actions.equals(var2.actions)) {
               if (this.certs.length != var2.certs.length) {
                  return false;
               } else {
                  int var3;
                  int var4;
                  boolean var5;
                  for(var3 = 0; var3 < this.certs.length; ++var3) {
                     var5 = false;

                     for(var4 = 0; var4 < var2.certs.length; ++var4) {
                        if (this.certs[var3].equals(var2.certs[var4])) {
                           var5 = true;
                           break;
                        }
                     }

                     if (!var5) {
                        return false;
                     }
                  }

                  for(var3 = 0; var3 < var2.certs.length; ++var3) {
                     var5 = false;

                     for(var4 = 0; var4 < this.certs.length; ++var4) {
                        if (var2.certs[var3].equals(this.certs[var4])) {
                           var5 = true;
                           break;
                        }
                     }

                     if (!var5) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         int var1 = this.type.hashCode();
         if (this.name != null) {
            var1 ^= this.name.hashCode();
         }

         if (this.actions != null) {
            var1 ^= this.actions.hashCode();
         }

         return var1;
      }

      public String getActions() {
         return "";
      }

      public String getSelfType() {
         return this.type;
      }

      public String getSelfName() {
         return this.name;
      }

      public String getSelfActions() {
         return this.actions;
      }

      public Certificate[] getCerts() {
         return this.certs;
      }

      public String toString() {
         return "(SelfPermission " + this.type + " " + this.name + " " + this.actions + ")";
      }
   }

   private static class PolicyEntry {
      private final CodeSource codesource;
      final List<Permission> permissions;
      private final List<PolicyParser.PrincipalEntry> principals;

      PolicyEntry(CodeSource var1, List<PolicyParser.PrincipalEntry> var2) {
         this.codesource = var1;
         this.permissions = new ArrayList();
         this.principals = var2;
      }

      PolicyEntry(CodeSource var1) {
         this(var1, (List)null);
      }

      List<PolicyParser.PrincipalEntry> getPrincipals() {
         return this.principals;
      }

      void add(Permission var1) {
         this.permissions.add(var1);
      }

      CodeSource getCodeSource() {
         return this.codesource;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(ResourcesMgr.getString("LPARAM"));
         var1.append((Object)this.getCodeSource());
         var1.append("\n");

         for(int var2 = 0; var2 < this.permissions.size(); ++var2) {
            Permission var3 = (Permission)this.permissions.get(var2);
            var1.append(ResourcesMgr.getString("SPACE"));
            var1.append(ResourcesMgr.getString("SPACE"));
            var1.append((Object)var3);
            var1.append(ResourcesMgr.getString("NEWLINE"));
         }

         var1.append(ResourcesMgr.getString("RPARAM"));
         var1.append(ResourcesMgr.getString("NEWLINE"));
         return var1.toString();
      }
   }
}
