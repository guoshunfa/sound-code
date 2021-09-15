package sun.security.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.DomainLoadStoreParameter;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import sun.security.util.PolicyUtil;

abstract class DomainKeyStore extends KeyStoreSpi {
   private static final String ENTRY_NAME_SEPARATOR = "entrynameseparator";
   private static final String KEYSTORE_PROVIDER_NAME = "keystoreprovidername";
   private static final String KEYSTORE_TYPE = "keystoretype";
   private static final String KEYSTORE_URI = "keystoreuri";
   private static final String KEYSTORE_PASSWORD_ENV = "keystorepasswordenv";
   private static final String REGEX_META = ".$|()[{^?*+\\";
   private static final String DEFAULT_STREAM_PREFIX = "iostream";
   private int streamCounter = 1;
   private String entryNameSeparator = " ";
   private String entryNameSeparatorRegEx = " ";
   private static final String DEFAULT_KEYSTORE_TYPE = KeyStore.getDefaultType();
   private final Map<String, KeyStore> keystores = new HashMap();

   abstract String convertAlias(String var1);

   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      AbstractMap.SimpleEntry var3 = this.getKeystoresForReading(var1);
      Key var4 = null;

      try {
         String var5 = (String)var3.getKey();
         Iterator var6 = ((Collection)var3.getValue()).iterator();

         while(var6.hasNext()) {
            KeyStore var7 = (KeyStore)var6.next();
            var4 = var7.getKey(var5, var2);
            if (var4 != null) {
               break;
            }
         }

         return var4;
      } catch (KeyStoreException var8) {
         throw new IllegalStateException(var8);
      }
   }

   public Certificate[] engineGetCertificateChain(String var1) {
      AbstractMap.SimpleEntry var2 = this.getKeystoresForReading(var1);
      Certificate[] var3 = null;

      try {
         String var4 = (String)var2.getKey();
         Iterator var5 = ((Collection)var2.getValue()).iterator();

         while(var5.hasNext()) {
            KeyStore var6 = (KeyStore)var5.next();
            var3 = var6.getCertificateChain(var4);
            if (var3 != null) {
               break;
            }
         }

         return var3;
      } catch (KeyStoreException var7) {
         throw new IllegalStateException(var7);
      }
   }

   public Certificate engineGetCertificate(String var1) {
      AbstractMap.SimpleEntry var2 = this.getKeystoresForReading(var1);
      Certificate var3 = null;

      try {
         String var4 = (String)var2.getKey();
         Iterator var5 = ((Collection)var2.getValue()).iterator();

         while(var5.hasNext()) {
            KeyStore var6 = (KeyStore)var5.next();
            var3 = var6.getCertificate(var4);
            if (var3 != null) {
               break;
            }
         }

         return var3;
      } catch (KeyStoreException var7) {
         throw new IllegalStateException(var7);
      }
   }

   public Date engineGetCreationDate(String var1) {
      AbstractMap.SimpleEntry var2 = this.getKeystoresForReading(var1);
      Date var3 = null;

      try {
         String var4 = (String)var2.getKey();
         Iterator var5 = ((Collection)var2.getValue()).iterator();

         while(var5.hasNext()) {
            KeyStore var6 = (KeyStore)var5.next();
            var3 = var6.getCreationDate(var4);
            if (var3 != null) {
               break;
            }
         }

         return var3;
      } catch (KeyStoreException var7) {
         throw new IllegalStateException(var7);
      }
   }

   public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      AbstractMap.SimpleEntry var5 = this.getKeystoreForWriting(var1);
      if (var5 == null) {
         throw new KeyStoreException("Error setting key entry for '" + var1 + "'");
      } else {
         String var6 = (String)var5.getKey();
         Map.Entry var7 = (Map.Entry)var5.getValue();
         ((KeyStore)var7.getValue()).setKeyEntry(var6, var2, var3, var4);
      }
   }

   public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      AbstractMap.SimpleEntry var4 = this.getKeystoreForWriting(var1);
      if (var4 == null) {
         throw new KeyStoreException("Error setting protected key entry for '" + var1 + "'");
      } else {
         String var5 = (String)var4.getKey();
         Map.Entry var6 = (Map.Entry)var4.getValue();
         ((KeyStore)var6.getValue()).setKeyEntry(var5, var2, var3);
      }
   }

   public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      AbstractMap.SimpleEntry var3 = this.getKeystoreForWriting(var1);
      if (var3 == null) {
         throw new KeyStoreException("Error setting certificate entry for '" + var1 + "'");
      } else {
         String var4 = (String)var3.getKey();
         Map.Entry var5 = (Map.Entry)var3.getValue();
         ((KeyStore)var5.getValue()).setCertificateEntry(var4, var2);
      }
   }

   public void engineDeleteEntry(String var1) throws KeyStoreException {
      AbstractMap.SimpleEntry var2 = this.getKeystoreForWriting(var1);
      if (var2 == null) {
         throw new KeyStoreException("Error deleting entry for '" + var1 + "'");
      } else {
         String var3 = (String)var2.getKey();
         Map.Entry var4 = (Map.Entry)var2.getValue();
         ((KeyStore)var4.getValue()).deleteEntry(var3);
      }
   }

   public Enumeration<String> engineAliases() {
      final Iterator var1 = this.keystores.entrySet().iterator();
      return new Enumeration<String>() {
         private int index = 0;
         private Map.Entry<String, KeyStore> keystoresEntry = null;
         private String prefix = null;
         private Enumeration<String> aliases = null;

         public boolean hasMoreElements() {
            try {
               if (this.aliases == null) {
                  if (!var1.hasNext()) {
                     return false;
                  }

                  this.keystoresEntry = (Map.Entry)var1.next();
                  this.prefix = (String)this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator;
                  this.aliases = ((KeyStore)this.keystoresEntry.getValue()).aliases();
               }

               if (this.aliases.hasMoreElements()) {
                  return true;
               }

               if (!var1.hasNext()) {
                  return false;
               }

               this.keystoresEntry = (Map.Entry)var1.next();
               this.prefix = (String)this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator;
               this.aliases = ((KeyStore)this.keystoresEntry.getValue()).aliases();
            } catch (KeyStoreException var2) {
               return false;
            }

            return this.aliases.hasMoreElements();
         }

         public String nextElement() {
            if (this.hasMoreElements()) {
               return this.prefix + (String)this.aliases.nextElement();
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   public boolean engineContainsAlias(String var1) {
      AbstractMap.SimpleEntry var2 = this.getKeystoresForReading(var1);

      try {
         String var3 = (String)var2.getKey();
         Iterator var4 = ((Collection)var2.getValue()).iterator();

         KeyStore var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (KeyStore)var4.next();
         } while(!var5.containsAlias(var3));

         return true;
      } catch (KeyStoreException var6) {
         throw new IllegalStateException(var6);
      }
   }

   public int engineSize() {
      int var1 = 0;

      try {
         KeyStore var3;
         for(Iterator var2 = this.keystores.values().iterator(); var2.hasNext(); var1 += var3.size()) {
            var3 = (KeyStore)var2.next();
         }

         return var1;
      } catch (KeyStoreException var4) {
         throw new IllegalStateException(var4);
      }
   }

   public boolean engineIsKeyEntry(String var1) {
      AbstractMap.SimpleEntry var2 = this.getKeystoresForReading(var1);

      try {
         String var3 = (String)var2.getKey();
         Iterator var4 = ((Collection)var2.getValue()).iterator();

         KeyStore var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (KeyStore)var4.next();
         } while(!var5.isKeyEntry(var3));

         return true;
      } catch (KeyStoreException var6) {
         throw new IllegalStateException(var6);
      }
   }

   public boolean engineIsCertificateEntry(String var1) {
      AbstractMap.SimpleEntry var2 = this.getKeystoresForReading(var1);

      try {
         String var3 = (String)var2.getKey();
         Iterator var4 = ((Collection)var2.getValue()).iterator();

         KeyStore var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (KeyStore)var4.next();
         } while(!var5.isCertificateEntry(var3));

         return true;
      } catch (KeyStoreException var6) {
         throw new IllegalStateException(var6);
      }
   }

   private AbstractMap.SimpleEntry<String, Collection<KeyStore>> getKeystoresForReading(String var1) {
      String[] var2 = var1.split(this.entryNameSeparatorRegEx, 2);
      if (var2.length == 2) {
         KeyStore var3 = (KeyStore)this.keystores.get(var2[0]);
         if (var3 != null) {
            return new AbstractMap.SimpleEntry(var2[1], Collections.singleton(var3));
         }
      } else if (var2.length == 1) {
         return new AbstractMap.SimpleEntry(var1, this.keystores.values());
      }

      return new AbstractMap.SimpleEntry("", Collections.emptyList());
   }

   private AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> getKeystoreForWriting(String var1) {
      String[] var2 = var1.split(this.entryNameSeparator, 2);
      if (var2.length == 2) {
         KeyStore var3 = (KeyStore)this.keystores.get(var2[0]);
         if (var3 != null) {
            return new AbstractMap.SimpleEntry(var2[1], new AbstractMap.SimpleEntry(var2[0], var3));
         }
      }

      return null;
   }

   public String engineGetCertificateAlias(Certificate var1) {
      try {
         String var2 = null;
         Iterator var3 = this.keystores.values().iterator();

         while(var3.hasNext()) {
            KeyStore var4 = (KeyStore)var3.next();
            if ((var2 = var4.getCertificateAlias(var1)) != null) {
               break;
            }
         }

         return var2;
      } catch (KeyStoreException var5) {
         throw new IllegalStateException(var5);
      }
   }

   public void engineStore(OutputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      try {
         if (this.keystores.size() == 1) {
            ((KeyStore)this.keystores.values().iterator().next()).store(var1, var2);
            return;
         }
      } catch (KeyStoreException var4) {
         throw new IllegalStateException(var4);
      }

      throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
   }

   public void engineStore(KeyStore.LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (!(var1 instanceof DomainLoadStoreParameter)) {
         throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
      } else {
         DomainLoadStoreParameter var2 = (DomainLoadStoreParameter)var1;
         List var3 = this.getBuilders(var2.getConfiguration(), var2.getProtectionParams());
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            DomainKeyStore.KeyStoreBuilderComponents var5 = (DomainKeyStore.KeyStoreBuilderComponents)var4.next();

            try {
               KeyStore.ProtectionParameter var6 = var5.protection;
               if (!(var6 instanceof KeyStore.PasswordProtection)) {
                  throw new KeyStoreException(new IllegalArgumentException("ProtectionParameter must be a KeyStore.PasswordProtection"));
               }

               char[] var7 = ((KeyStore.PasswordProtection)var5.protection).getPassword();
               KeyStore var8 = (KeyStore)this.keystores.get(var5.name);
               FileOutputStream var9 = new FileOutputStream(var5.file);
               Throwable var10 = null;

               try {
                  var8.store(var9, var7);
               } catch (Throwable var20) {
                  var10 = var20;
                  throw var20;
               } finally {
                  if (var9 != null) {
                     if (var10 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var19) {
                           var10.addSuppressed(var19);
                        }
                     } else {
                        var9.close();
                     }
                  }

               }
            } catch (KeyStoreException var22) {
               throw new IOException(var22);
            }
         }

      }
   }

   public void engineLoad(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      try {
         KeyStore var3 = null;

         try {
            var3 = KeyStore.getInstance("JKS");
            var3.load(var1, var2);
         } catch (Exception var5) {
            if ("JKS".equalsIgnoreCase(DEFAULT_KEYSTORE_TYPE)) {
               throw var5;
            }

            var3 = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
            var3.load(var1, var2);
         }

         String var4 = "iostream" + this.streamCounter++;
         this.keystores.put(var4, var3);
      } catch (Exception var6) {
         throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
      }
   }

   public void engineLoad(KeyStore.LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (!(var1 instanceof DomainLoadStoreParameter)) {
         throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
      } else {
         DomainLoadStoreParameter var2 = (DomainLoadStoreParameter)var1;
         List var3 = this.getBuilders(var2.getConfiguration(), var2.getProtectionParams());
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            DomainKeyStore.KeyStoreBuilderComponents var5 = (DomainKeyStore.KeyStoreBuilderComponents)var4.next();

            try {
               if (var5.file != null) {
                  this.keystores.put(var5.name, KeyStore.Builder.newInstance(var5.type, var5.provider, var5.file, var5.protection).getKeyStore());
               } else {
                  this.keystores.put(var5.name, KeyStore.Builder.newInstance(var5.type, var5.provider, var5.protection).getKeyStore());
               }
            } catch (KeyStoreException var7) {
               throw new IOException(var7);
            }
         }

      }
   }

   private List<DomainKeyStore.KeyStoreBuilderComponents> getBuilders(URI var1, Map<String, KeyStore.ProtectionParameter> var2) throws IOException {
      PolicyParser var3 = new PolicyParser(true);
      Collection var4 = null;
      ArrayList var5 = new ArrayList();
      String var6 = var1.getFragment();

      try {
         InputStreamReader var7 = new InputStreamReader(PolicyUtil.getInputStream(var1.toURL()), "UTF-8");
         Throwable var8 = null;

         try {
            var3.read(var7);
            var4 = var3.getDomainEntries();
         } catch (Throwable var30) {
            var8 = var30;
            throw var30;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var28) {
                     var8.addSuppressed(var28);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (MalformedURLException var32) {
         throw new IOException(var32);
      } catch (PolicyParser.ParsingException var33) {
         throw new IOException(var33);
      }

      Iterator var34 = var4.iterator();

      label242:
      while(var34.hasNext()) {
         PolicyParser.DomainEntry var35 = (PolicyParser.DomainEntry)var34.next();
         Map var9 = var35.getProperties();
         if (var6 == null || var6.equalsIgnoreCase(var35.getName())) {
            if (var9.containsKey("entrynameseparator")) {
               this.entryNameSeparator = (String)var9.get("entrynameseparator");
               boolean var10 = false;
               StringBuilder var11 = new StringBuilder();

               for(int var12 = 0; var12 < this.entryNameSeparator.length(); ++var12) {
                  char var36 = this.entryNameSeparator.charAt(var12);
                  if (".$|()[{^?*+\\".indexOf(var36) != -1) {
                     var11.append('\\');
                  }

                  var11.append(var36);
               }

               this.entryNameSeparatorRegEx = var11.toString();
            }

            Collection var37 = var35.getEntries();
            Iterator var38 = var37.iterator();

            while(true) {
               if (!var38.hasNext()) {
                  break label242;
               }

               PolicyParser.KeyStoreEntry var39 = (PolicyParser.KeyStoreEntry)var38.next();
               String var13 = var39.getName();
               HashMap var14 = new HashMap(var9);
               var14.putAll(var39.getProperties());
               String var15 = DEFAULT_KEYSTORE_TYPE;
               if (var14.containsKey("keystoretype")) {
                  var15 = (String)var14.get("keystoretype");
               }

               Provider var16 = null;
               if (var14.containsKey("keystoreprovidername")) {
                  String var17 = (String)var14.get("keystoreprovidername");
                  var16 = Security.getProvider(var17);
                  if (var16 == null) {
                     throw new IOException("Error locating JCE provider: " + var17);
                  }
               }

               File var40 = null;
               String var18;
               if (var14.containsKey("keystoreuri")) {
                  var18 = (String)var14.get("keystoreuri");

                  try {
                     if (var18.startsWith("file://")) {
                        var40 = new File(new URI(var18));
                     } else {
                        var40 = new File(var18);
                     }
                  } catch (IllegalArgumentException | URISyntaxException var29) {
                     throw new IOException("Error processing keystore property: keystoreURI=\"" + var18 + "\"", var29);
                  }
               }

               var18 = null;
               Object var41;
               if (var2.containsKey(var13)) {
                  var41 = (KeyStore.ProtectionParameter)var2.get(var13);
               } else if (var14.containsKey("keystorepasswordenv")) {
                  String var19 = (String)var14.get("keystorepasswordenv");
                  String var20 = System.getenv(var19);
                  if (var20 == null) {
                     throw new IOException("Error processing keystore property: keystorePasswordEnv=\"" + var19 + "\"");
                  }

                  var41 = new KeyStore.PasswordProtection(var20.toCharArray());
               } else {
                  var41 = new KeyStore.PasswordProtection((char[])null);
               }

               var5.add(new DomainKeyStore.KeyStoreBuilderComponents(var13, var15, var16, var40, (KeyStore.ProtectionParameter)var41));
            }
         }
      }

      if (var5.isEmpty()) {
         throw new IOException("Error locating domain configuration data for: " + var1);
      } else {
         return var5;
      }
   }

   class KeyStoreBuilderComponents {
      String name;
      String type;
      Provider provider;
      File file;
      KeyStore.ProtectionParameter protection;

      KeyStoreBuilderComponents(String var2, String var3, Provider var4, File var5, KeyStore.ProtectionParameter var6) {
         this.name = var2;
         this.type = var3;
         this.provider = var4;
         this.file = var5;
         this.protection = var6;
      }
   }

   public static final class DKS extends DomainKeyStore {
      String convertAlias(String var1) {
         return var1.toLowerCase(Locale.ENGLISH);
      }
   }
}
