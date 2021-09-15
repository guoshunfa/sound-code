package sun.security.tools.keytool;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.CodeSigner;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.Timestamp;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs10.PKCS10;
import sun.security.pkcs10.PKCS10Attribute;
import sun.security.provider.certpath.CertStoreHelper;
import sun.security.tools.KeyStoreUtil;
import sun.security.tools.PathList;
import sun.security.util.DerValue;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Password;
import sun.security.util.Pem;
import sun.security.util.SecurityProviderConstants;
import sun.security.x509.AccessDescription;
import sun.security.x509.AlgorithmId;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.CRLExtensions;
import sun.security.x509.CRLReasonCodeExtension;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.DNSName;
import sun.security.x509.DistributionPoint;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.Extension;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.IPAddressName;
import sun.security.x509.IssuerAlternativeNameExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.OIDName;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.RFC822Name;
import sun.security.x509.SerialNumber;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.SubjectInfoAccessExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.URIName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public final class Main {
   private static final byte[] CRLF = new byte[]{13, 10};
   private boolean debug = false;
   private Main.Command command = null;
   private String sigAlgName = null;
   private String keyAlgName = null;
   private boolean verbose = false;
   private int keysize = -1;
   private boolean rfc = false;
   private long validity = 90L;
   private String alias = null;
   private String dname = null;
   private String dest = null;
   private String filename = null;
   private String infilename = null;
   private String outfilename = null;
   private String srcksfname = null;
   private Set<Pair<String, String>> providers = null;
   private String storetype = null;
   private String srcProviderName = null;
   private String providerName = null;
   private String pathlist = null;
   private char[] storePass = null;
   private char[] storePassNew = null;
   private char[] keyPass = null;
   private char[] keyPassNew = null;
   private char[] newPass = null;
   private char[] destKeyPass = null;
   private char[] srckeyPass = null;
   private String ksfname = null;
   private File ksfile = null;
   private InputStream ksStream = null;
   private String sslserver = null;
   private String jarfile = null;
   private KeyStore keyStore = null;
   private boolean token = false;
   private boolean nullStream = false;
   private boolean kssave = false;
   private boolean noprompt = false;
   private boolean trustcacerts = false;
   private boolean nowarn = false;
   private boolean protectedPath = false;
   private boolean srcprotectedPath = false;
   private CertificateFactory cf = null;
   private KeyStore caks = null;
   private char[] srcstorePass = null;
   private String srcstoretype = null;
   private Set<char[]> passwords = new HashSet();
   private String startDate = null;
   private List<String> ids = new ArrayList();
   private List<String> v3ext = new ArrayList();
   private boolean inplaceImport = false;
   private String inplaceBackupName = null;
   private List<String> weakWarnings = new ArrayList();
   private static final DisabledAlgorithmConstraints DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
   private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET;
   private static final Class<?>[] PARAM_STRING;
   private static final String NONE = "NONE";
   private static final String P11KEYSTORE = "PKCS11";
   private static final String P12KEYSTORE = "PKCS12";
   private static final String keyAlias = "mykey";
   private static final ResourceBundle rb;
   private static final Collator collator;
   private static final String[] extSupported;

   private Main() {
   }

   public static void main(String[] var0) throws Exception {
      Main var1 = new Main();
      var1.run(var0, System.out);
   }

   private void run(String[] var1, PrintStream var2) throws Exception {
      boolean var10 = false;

      Iterator var3;
      char[] var4;
      Object var13;
      label164: {
         try {
            try {
               var10 = true;
               this.parseArgs(var1);
               if (this.command != null) {
                  this.doCommands(var2);
                  var10 = false;
               } else {
                  var10 = false;
               }
               break label164;
            } catch (Exception var11) {
               System.out.println(rb.getString("keytool.error.") + var11);
               if (this.verbose) {
                  var11.printStackTrace(System.out);
               }
            }

            if (this.debug) {
               throw var11;
            }

            System.exit(1);
            var10 = false;
         } finally {
            if (var10) {
               this.printWeakWarnings(false);
               Iterator var6 = this.passwords.iterator();

               while(var6.hasNext()) {
                  char[] var7 = (char[])var6.next();
                  if (var7 != null) {
                     Arrays.fill(var7, ' ');
                     Object var14 = null;
                  }
               }

               if (this.ksStream != null) {
                  this.ksStream.close();
               }

            }
         }

         this.printWeakWarnings(false);
         var3 = this.passwords.iterator();

         while(var3.hasNext()) {
            var4 = (char[])var3.next();
            if (var4 != null) {
               Arrays.fill(var4, ' ');
               var13 = null;
            }
         }

         if (this.ksStream != null) {
            this.ksStream.close();
         }

         return;
      }

      this.printWeakWarnings(false);
      var3 = this.passwords.iterator();

      while(var3.hasNext()) {
         var4 = (char[])var3.next();
         if (var4 != null) {
            Arrays.fill(var4, ' ');
            var13 = null;
         }
      }

      if (this.ksStream != null) {
         this.ksStream.close();
      }

   }

   void parseArgs(String[] var1) {
      boolean var2 = false;
      boolean var3 = var1.length == 0;

      int var12;
      for(var12 = 0; var12 < var1.length && var1[var12].startsWith("-"); ++var12) {
         String var4 = var1[var12];
         int var6;
         if (var12 == var1.length - 1) {
            Main.Option[] var5 = Main.Option.values();
            var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Main.Option var8 = var5[var7];
               if (collator.compare(var4, var8.toString()) == 0) {
                  if (var8.arg != null) {
                     this.errorNeedArgument(var4);
                  }
                  break;
               }
            }
         }

         String var13 = null;
         var6 = var4.indexOf(58);
         if (var6 > 0) {
            var13 = var4.substring(var6 + 1);
            var4 = var4.substring(0, var6);
         }

         boolean var14 = false;
         Main.Command[] var15 = Main.Command.values();
         int var9 = var15.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Main.Command var11 = var15[var10];
            if (collator.compare(var4, var11.toString()) == 0) {
               this.command = var11;
               var14 = true;
               break;
            }
         }

         if (!var14) {
            if (collator.compare(var4, "-export") == 0) {
               this.command = Main.Command.EXPORTCERT;
            } else if (collator.compare(var4, "-genkey") == 0) {
               this.command = Main.Command.GENKEYPAIR;
            } else if (collator.compare(var4, "-import") == 0) {
               this.command = Main.Command.IMPORTCERT;
            } else if (collator.compare(var4, "-importpassword") == 0) {
               this.command = Main.Command.IMPORTPASS;
            } else if (collator.compare(var4, "-help") == 0) {
               var3 = true;
            } else if (collator.compare(var4, "-nowarn") == 0) {
               this.nowarn = true;
            } else if (collator.compare(var4, "-keystore") != 0 && collator.compare(var4, "-destkeystore") != 0) {
               if (collator.compare(var4, "-storepass") != 0 && collator.compare(var4, "-deststorepass") != 0) {
                  if (collator.compare(var4, "-storetype") != 0 && collator.compare(var4, "-deststoretype") != 0) {
                     if (collator.compare(var4, "-srcstorepass") == 0) {
                        ++var12;
                        this.srcstorePass = this.getPass(var13, var1[var12]);
                        this.passwords.add(this.srcstorePass);
                     } else if (collator.compare(var4, "-srcstoretype") == 0) {
                        ++var12;
                        this.srcstoretype = KeyStoreUtil.niceStoreTypeName(var1[var12]);
                     } else if (collator.compare(var4, "-srckeypass") == 0) {
                        ++var12;
                        this.srckeyPass = this.getPass(var13, var1[var12]);
                        this.passwords.add(this.srckeyPass);
                     } else if (collator.compare(var4, "-srcprovidername") == 0) {
                        ++var12;
                        this.srcProviderName = var1[var12];
                     } else if (collator.compare(var4, "-providername") != 0 && collator.compare(var4, "-destprovidername") != 0) {
                        if (collator.compare(var4, "-providerpath") == 0) {
                           ++var12;
                           this.pathlist = var1[var12];
                        } else if (collator.compare(var4, "-keypass") == 0) {
                           ++var12;
                           this.keyPass = this.getPass(var13, var1[var12]);
                           this.passwords.add(this.keyPass);
                        } else if (collator.compare(var4, "-new") == 0) {
                           ++var12;
                           this.newPass = this.getPass(var13, var1[var12]);
                           this.passwords.add(this.newPass);
                        } else if (collator.compare(var4, "-destkeypass") == 0) {
                           ++var12;
                           this.destKeyPass = this.getPass(var13, var1[var12]);
                           this.passwords.add(this.destKeyPass);
                        } else if (collator.compare(var4, "-alias") != 0 && collator.compare(var4, "-srcalias") != 0) {
                           if (collator.compare(var4, "-dest") != 0 && collator.compare(var4, "-destalias") != 0) {
                              if (collator.compare(var4, "-dname") == 0) {
                                 ++var12;
                                 this.dname = var1[var12];
                              } else if (collator.compare(var4, "-keysize") == 0) {
                                 ++var12;
                                 this.keysize = Integer.parseInt(var1[var12]);
                              } else if (collator.compare(var4, "-keyalg") == 0) {
                                 ++var12;
                                 this.keyAlgName = var1[var12];
                              } else if (collator.compare(var4, "-sigalg") == 0) {
                                 ++var12;
                                 this.sigAlgName = var1[var12];
                              } else if (collator.compare(var4, "-startdate") == 0) {
                                 ++var12;
                                 this.startDate = var1[var12];
                              } else if (collator.compare(var4, "-validity") == 0) {
                                 ++var12;
                                 this.validity = Long.parseLong(var1[var12]);
                              } else if (collator.compare(var4, "-ext") == 0) {
                                 ++var12;
                                 this.v3ext.add(var1[var12]);
                              } else if (collator.compare(var4, "-id") == 0) {
                                 ++var12;
                                 this.ids.add(var1[var12]);
                              } else if (collator.compare(var4, "-file") == 0) {
                                 ++var12;
                                 this.filename = var1[var12];
                              } else if (collator.compare(var4, "-infile") == 0) {
                                 ++var12;
                                 this.infilename = var1[var12];
                              } else if (collator.compare(var4, "-outfile") == 0) {
                                 ++var12;
                                 this.outfilename = var1[var12];
                              } else if (collator.compare(var4, "-sslserver") == 0) {
                                 ++var12;
                                 this.sslserver = var1[var12];
                              } else if (collator.compare(var4, "-jarfile") == 0) {
                                 ++var12;
                                 this.jarfile = var1[var12];
                              } else if (collator.compare(var4, "-srckeystore") == 0) {
                                 ++var12;
                                 this.srcksfname = var1[var12];
                              } else if (collator.compare(var4, "-provider") != 0 && collator.compare(var4, "-providerclass") != 0) {
                                 if (collator.compare(var4, "-v") == 0) {
                                    this.verbose = true;
                                 } else if (collator.compare(var4, "-debug") == 0) {
                                    this.debug = true;
                                 } else if (collator.compare(var4, "-rfc") == 0) {
                                    this.rfc = true;
                                 } else if (collator.compare(var4, "-noprompt") == 0) {
                                    this.noprompt = true;
                                 } else if (collator.compare(var4, "-trustcacerts") == 0) {
                                    this.trustcacerts = true;
                                 } else if (collator.compare(var4, "-protected") != 0 && collator.compare(var4, "-destprotected") != 0) {
                                    if (collator.compare(var4, "-srcprotected") == 0) {
                                       this.srcprotectedPath = true;
                                    } else {
                                       System.err.println(rb.getString("Illegal.option.") + var4);
                                       this.tinyHelp();
                                    }
                                 } else {
                                    this.protectedPath = true;
                                 }
                              } else {
                                 if (this.providers == null) {
                                    this.providers = new HashSet(3);
                                 }

                                 ++var12;
                                 String var16 = var1[var12];
                                 String var17 = null;
                                 if (var1.length > var12 + 1) {
                                    var4 = var1[var12 + 1];
                                    if (collator.compare(var4, "-providerarg") == 0) {
                                       if (var1.length == var12 + 2) {
                                          this.errorNeedArgument(var4);
                                       }

                                       var17 = var1[var12 + 2];
                                       var12 += 2;
                                    }
                                 }

                                 this.providers.add(Pair.of(var16, var17));
                              }
                           } else {
                              ++var12;
                              this.dest = var1[var12];
                           }
                        } else {
                           ++var12;
                           this.alias = var1[var12];
                        }
                     } else {
                        ++var12;
                        this.providerName = var1[var12];
                     }
                  } else {
                     ++var12;
                     this.storetype = KeyStoreUtil.niceStoreTypeName(var1[var12]);
                  }
               } else {
                  ++var12;
                  this.storePass = this.getPass(var13, var1[var12]);
                  this.passwords.add(this.storePass);
               }
            } else {
               ++var12;
               this.ksfname = var1[var12];
            }
         }
      }

      if (var12 < var1.length) {
         System.err.println(rb.getString("Illegal.option.") + var1[var12]);
         this.tinyHelp();
      }

      if (this.command == null) {
         if (var3) {
            this.usage();
         } else {
            System.err.println(rb.getString("Usage.error.no.command.provided"));
            this.tinyHelp();
         }
      } else if (var3) {
         this.usage();
         this.command = null;
      }

   }

   boolean isKeyStoreRelated(Main.Command var1) {
      return var1 != Main.Command.PRINTCERT && var1 != Main.Command.PRINTCERTREQ;
   }

   void doCommands(PrintStream var1) throws Exception {
      if ("PKCS11".equalsIgnoreCase(this.storetype) || KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
         this.token = true;
         if (this.ksfname == null) {
            this.ksfname = "NONE";
         }
      }

      if ("NONE".equals(this.ksfname)) {
         this.nullStream = true;
      }

      if (this.token && !this.nullStream) {
         System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), this.storetype));
         System.err.println();
         this.tinyHelp();
      }

      if (this.token && (this.command == Main.Command.KEYPASSWD || this.command == Main.Command.STOREPASSWD)) {
         throw new UnsupportedOperationException(MessageFormat.format(rb.getString(".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}"), this.storetype));
      } else if (!this.token || this.keyPass == null && this.newPass == null && this.destKeyPass == null) {
         if (!this.protectedPath || this.storePass == null && this.keyPass == null && this.newPass == null && this.destKeyPass == null) {
            if (this.srcprotectedPath && (this.srcstorePass != null || this.srckeyPass != null)) {
               throw new IllegalArgumentException(rb.getString("if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified"));
            } else if (!KeyStoreUtil.isWindowsKeyStore(this.storetype) || this.storePass == null && this.keyPass == null && this.newPass == null && this.destKeyPass == null) {
               if (KeyStoreUtil.isWindowsKeyStore(this.srcstoretype) && (this.srcstorePass != null || this.srckeyPass != null)) {
                  throw new IllegalArgumentException(rb.getString("if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified"));
               } else if (this.validity <= 0L) {
                  throw new Exception(rb.getString("Validity.must.be.greater.than.zero"));
               } else {
                  String var5;
                  String var7;
                  if (this.providers != null) {
                     Object var2 = null;
                     if (this.pathlist != null) {
                        String var3 = null;
                        var3 = PathList.appendPath(var3, System.getProperty("java.class.path"));
                        var3 = PathList.appendPath(var3, System.getProperty("env.class.path"));
                        var3 = PathList.appendPath(var3, this.pathlist);
                        URL[] var4 = PathList.pathToURLs(var3);
                        var2 = new URLClassLoader(var4);
                     } else {
                        var2 = ClassLoader.getSystemClassLoader();
                     }

                     Iterator var242 = this.providers.iterator();

                     while(var242.hasNext()) {
                        Pair var243 = (Pair)var242.next();
                        var5 = (String)var243.fst;
                        Class var6;
                        if (var2 != null) {
                           var6 = ((ClassLoader)var2).loadClass(var5);
                        } else {
                           var6 = Class.forName(var5);
                        }

                        var7 = (String)var243.snd;
                        Object var8;
                        if (var7 == null) {
                           var8 = var6.newInstance();
                        } else {
                           Constructor var9 = var6.getConstructor(PARAM_STRING);
                           var8 = var9.newInstance(var7);
                        }

                        if (!(var8 instanceof Provider)) {
                           MessageFormat var257 = new MessageFormat(rb.getString("provName.not.a.provider"));
                           Object[] var10 = new Object[]{var5};
                           throw new Exception(var257.format(var10));
                        }

                        Security.addProvider((Provider)var8);
                     }
                  }

                  if (this.command == Main.Command.LIST && this.verbose && this.rfc) {
                     System.err.println(rb.getString("Must.not.specify.both.v.and.rfc.with.list.command"));
                     this.tinyHelp();
                  }

                  if (this.command == Main.Command.GENKEYPAIR && this.keyPass != null && this.keyPass.length < 6) {
                     throw new Exception(rb.getString("Key.password.must.be.at.least.6.characters"));
                  } else if (this.newPass != null && this.newPass.length < 6) {
                     throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
                  } else if (this.destKeyPass != null && this.destKeyPass.length < 6) {
                     throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
                  } else {
                     if (this.ksfname == null) {
                        this.ksfname = System.getProperty("user.home") + File.separator + ".keystore";
                     }

                     KeyStore var241 = null;
                     if (this.command == Main.Command.IMPORTKEYSTORE) {
                        this.inplaceImport = this.inplaceImportCheck();
                        if (this.inplaceImport) {
                           var241 = this.loadSourceKeyStore();
                           if (this.storePass == null) {
                              this.storePass = this.srcstorePass;
                           }
                        }
                     }

                     if (this.isKeyStoreRelated(this.command) && !this.nullStream && !this.inplaceImport) {
                        try {
                           this.ksfile = new File(this.ksfname);
                           if (this.ksfile.exists() && this.ksfile.length() == 0L) {
                              throw new Exception(rb.getString("Keystore.file.exists.but.is.empty.") + this.ksfname);
                           }

                           this.ksStream = new FileInputStream(this.ksfile);
                        } catch (FileNotFoundException var240) {
                           if (this.command != Main.Command.GENKEYPAIR && this.command != Main.Command.GENSECKEY && this.command != Main.Command.IDENTITYDB && this.command != Main.Command.IMPORTCERT && this.command != Main.Command.IMPORTPASS && this.command != Main.Command.IMPORTKEYSTORE && this.command != Main.Command.PRINTCRL) {
                              throw new Exception(rb.getString("Keystore.file.does.not.exist.") + this.ksfname);
                           }
                        }
                     }

                     if ((this.command == Main.Command.KEYCLONE || this.command == Main.Command.CHANGEALIAS) && this.dest == null) {
                        this.dest = this.getAlias("destination");
                        if ("".equals(this.dest)) {
                           throw new Exception(rb.getString("Must.specify.destination.alias"));
                        }
                     }

                     if (this.command == Main.Command.DELETE && this.alias == null) {
                        this.alias = this.getAlias((String)null);
                        if ("".equals(this.alias)) {
                           throw new Exception(rb.getString("Must.specify.alias"));
                        }
                     }

                     if (this.storetype == null) {
                        this.storetype = KeyStore.getDefaultType();
                     }

                     if (this.providerName == null) {
                        this.keyStore = KeyStore.getInstance(this.storetype);
                     } else {
                        this.keyStore = KeyStore.getInstance(this.storetype, this.providerName);
                     }

                     if (!this.nullStream) {
                        if (this.inplaceImport) {
                           this.keyStore.load((InputStream)null, this.storePass);
                        } else {
                           this.keyStore.load(this.ksStream, this.storePass);
                        }

                        if (this.ksStream != null) {
                           this.ksStream.close();
                        }
                     }

                     if ("PKCS12".equalsIgnoreCase(this.storetype) && this.command == Main.Command.KEYPASSWD) {
                        throw new UnsupportedOperationException(rb.getString(".keypasswd.commands.not.supported.if.storetype.is.PKCS12"));
                     } else {
                        if (this.nullStream && this.storePass != null) {
                           this.keyStore.load((InputStream)null, this.storePass);
                        } else if (!this.nullStream && this.storePass != null) {
                           if (this.ksStream == null && this.storePass.length < 6) {
                              throw new Exception(rb.getString("Keystore.password.must.be.at.least.6.characters"));
                           }
                        } else if (this.storePass == null) {
                           if (this.protectedPath || KeyStoreUtil.isWindowsKeyStore(this.storetype) || this.command != Main.Command.CERTREQ && this.command != Main.Command.DELETE && this.command != Main.Command.GENKEYPAIR && this.command != Main.Command.GENSECKEY && this.command != Main.Command.IMPORTCERT && this.command != Main.Command.IMPORTPASS && this.command != Main.Command.IMPORTKEYSTORE && this.command != Main.Command.KEYCLONE && this.command != Main.Command.CHANGEALIAS && this.command != Main.Command.SELFCERT && this.command != Main.Command.STOREPASSWD && this.command != Main.Command.KEYPASSWD && this.command != Main.Command.IDENTITYDB) {
                              if (!this.protectedPath && !KeyStoreUtil.isWindowsKeyStore(this.storetype) && this.isKeyStoreRelated(this.command) && this.command != Main.Command.PRINTCRL) {
                                 System.err.print(rb.getString("Enter.keystore.password."));
                                 System.err.flush();
                                 this.storePass = Password.readPassword(System.in);
                                 this.passwords.add(this.storePass);
                              }
                           } else {
                              int var244 = 0;

                              do {
                                 if (this.command == Main.Command.IMPORTKEYSTORE) {
                                    System.err.print(rb.getString("Enter.destination.keystore.password."));
                                 } else {
                                    System.err.print(rb.getString("Enter.keystore.password."));
                                 }

                                 System.err.flush();
                                 this.storePass = Password.readPassword(System.in);
                                 this.passwords.add(this.storePass);
                                 if (!this.nullStream && (this.storePass == null || this.storePass.length < 6)) {
                                    System.err.println(rb.getString("Keystore.password.is.too.short.must.be.at.least.6.characters"));
                                    this.storePass = null;
                                 }

                                 if (this.storePass != null && !this.nullStream && this.ksStream == null) {
                                    System.err.print(rb.getString("Re.enter.new.password."));
                                    char[] var245 = Password.readPassword(System.in);
                                    this.passwords.add(var245);
                                    if (!Arrays.equals(this.storePass, var245)) {
                                       System.err.println(rb.getString("They.don.t.match.Try.again"));
                                       this.storePass = null;
                                    }
                                 }

                                 ++var244;
                              } while(this.storePass == null && var244 < 3);

                              if (this.storePass == null) {
                                 System.err.println(rb.getString("Too.many.failures.try.later"));
                                 return;
                              }
                           }

                           if (this.nullStream) {
                              this.keyStore.load((InputStream)null, this.storePass);
                           } else if (this.ksStream != null) {
                              this.ksStream = new FileInputStream(this.ksfile);
                              this.keyStore.load(this.ksStream, this.storePass);
                              this.ksStream.close();
                           }
                        }

                        Object[] var246;
                        MessageFormat var247;
                        if (this.storePass != null && "PKCS12".equalsIgnoreCase(this.storetype)) {
                           var247 = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
                           if (this.keyPass != null && !Arrays.equals(this.storePass, this.keyPass)) {
                              var246 = new Object[]{"-keypass"};
                              System.err.println(var247.format(var246));
                              this.keyPass = this.storePass;
                           }

                           if (this.newPass != null && !Arrays.equals(this.storePass, this.newPass)) {
                              var246 = new Object[]{"-new"};
                              System.err.println(var247.format(var246));
                              this.newPass = this.storePass;
                           }

                           if (this.destKeyPass != null && !Arrays.equals(this.storePass, this.destKeyPass)) {
                              var246 = new Object[]{"-destkeypass"};
                              System.err.println(var247.format(var246));
                              this.destKeyPass = this.storePass;
                           }
                        }

                        if (this.command == Main.Command.PRINTCERT || this.command == Main.Command.IMPORTCERT || this.command == Main.Command.IDENTITYDB || this.command == Main.Command.PRINTCRL) {
                           this.cf = CertificateFactory.getInstance("X509");
                        }

                        if (this.command != Main.Command.IMPORTCERT) {
                           this.trustcacerts = false;
                        }

                        if (this.trustcacerts) {
                           this.caks = KeyStoreUtil.getCacertsKeyStore();
                        }

                        String var258;
                        PrintStream var249;
                        Throwable var251;
                        if (this.command == Main.Command.CERTREQ) {
                           if (this.filename != null) {
                              var249 = new PrintStream(new FileOutputStream(this.filename));
                              var251 = null;

                              try {
                                 this.doCertReq(this.alias, this.sigAlgName, var249);
                              } catch (Throwable var231) {
                                 var251 = var231;
                                 throw var231;
                              } finally {
                                 if (var249 != null) {
                                    if (var251 != null) {
                                       try {
                                          var249.close();
                                       } catch (Throwable var225) {
                                          var251.addSuppressed(var225);
                                       }
                                    } else {
                                       var249.close();
                                    }
                                 }

                              }
                           } else {
                              this.doCertReq(this.alias, this.sigAlgName, var1);
                           }

                           if (this.verbose && this.filename != null) {
                              var247 = new MessageFormat(rb.getString("Certification.request.stored.in.file.filename."));
                              var246 = new Object[]{this.filename};
                              System.err.println(var247.format(var246));
                              System.err.println(rb.getString("Submit.this.to.your.CA"));
                           }
                        } else if (this.command == Main.Command.DELETE) {
                           this.doDeleteEntry(this.alias);
                           this.kssave = true;
                        } else if (this.command == Main.Command.EXPORTCERT) {
                           if (this.filename != null) {
                              var249 = new PrintStream(new FileOutputStream(this.filename));
                              var251 = null;

                              try {
                                 this.doExportCert(this.alias, var249);
                              } catch (Throwable var230) {
                                 var251 = var230;
                                 throw var230;
                              } finally {
                                 if (var249 != null) {
                                    if (var251 != null) {
                                       try {
                                          var249.close();
                                       } catch (Throwable var224) {
                                          var251.addSuppressed(var224);
                                       }
                                    } else {
                                       var249.close();
                                    }
                                 }

                              }
                           } else {
                              this.doExportCert(this.alias, var1);
                           }

                           if (this.filename != null) {
                              var247 = new MessageFormat(rb.getString("Certificate.stored.in.file.filename."));
                              var246 = new Object[]{this.filename};
                              System.err.println(var247.format(var246));
                           }
                        } else if (this.command == Main.Command.GENKEYPAIR) {
                           if (this.keyAlgName == null) {
                              this.keyAlgName = "DSA";
                           }

                           this.doGenKeyPair(this.alias, this.dname, this.keyAlgName, this.keysize, this.sigAlgName);
                           this.kssave = true;
                        } else if (this.command == Main.Command.GENSECKEY) {
                           if (this.keyAlgName == null) {
                              this.keyAlgName = "DES";
                           }

                           this.doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
                           this.kssave = true;
                        } else if (this.command == Main.Command.IMPORTPASS) {
                           if (this.keyAlgName == null) {
                              this.keyAlgName = "PBE";
                           }

                           this.doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
                           this.kssave = true;
                        } else {
                           FileInputStream var252;
                           if (this.command == Main.Command.IDENTITYDB) {
                              if (this.filename != null) {
                                 var252 = new FileInputStream(this.filename);
                                 var251 = null;

                                 try {
                                    this.doImportIdentityDatabase(var252);
                                 } catch (Throwable var229) {
                                    var251 = var229;
                                    throw var229;
                                 } finally {
                                    if (var252 != null) {
                                       if (var251 != null) {
                                          try {
                                             var252.close();
                                          } catch (Throwable var223) {
                                             var251.addSuppressed(var223);
                                          }
                                       } else {
                                          var252.close();
                                       }
                                    }

                                 }
                              } else {
                                 this.doImportIdentityDatabase(System.in);
                              }
                           } else {
                              Object var254;
                              if (this.command == Main.Command.IMPORTCERT) {
                                 var254 = System.in;
                                 if (this.filename != null) {
                                    var254 = new FileInputStream(this.filename);
                                 }

                                 var258 = this.alias != null ? this.alias : "mykey";

                                 try {
                                    if (this.keyStore.entryInstanceOf(var258, KeyStore.PrivateKeyEntry.class)) {
                                       this.kssave = this.installReply(var258, (InputStream)var254);
                                       if (this.kssave) {
                                          System.err.println(rb.getString("Certificate.reply.was.installed.in.keystore"));
                                       } else {
                                          System.err.println(rb.getString("Certificate.reply.was.not.installed.in.keystore"));
                                       }
                                    } else if (!this.keyStore.containsAlias(var258) || this.keyStore.entryInstanceOf(var258, KeyStore.TrustedCertificateEntry.class)) {
                                       this.kssave = this.addTrustedCert(var258, (InputStream)var254);
                                       if (this.kssave) {
                                          System.err.println(rb.getString("Certificate.was.added.to.keystore"));
                                       } else {
                                          System.err.println(rb.getString("Certificate.was.not.added.to.keystore"));
                                       }
                                    }
                                 } finally {
                                    if (var254 != System.in) {
                                       ((InputStream)var254).close();
                                    }

                                 }
                              } else if (this.command == Main.Command.IMPORTKEYSTORE) {
                                 if (var241 == null) {
                                    var241 = this.loadSourceKeyStore();
                                 }

                                 this.doImportKeyStore(var241);
                                 this.kssave = true;
                              } else if (this.command == Main.Command.KEYCLONE) {
                                 this.keyPassNew = this.newPass;
                                 if (this.alias == null) {
                                    this.alias = "mykey";
                                 }

                                 if (!this.keyStore.containsAlias(this.alias)) {
                                    var247 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
                                    var246 = new Object[]{this.alias};
                                    throw new Exception(var247.format(var246));
                                 }

                                 if (!this.keyStore.entryInstanceOf(this.alias, KeyStore.PrivateKeyEntry.class)) {
                                    var247 = new MessageFormat(rb.getString("Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key"));
                                    var246 = new Object[]{this.alias};
                                    throw new Exception(var247.format(var246));
                                 }

                                 this.doCloneEntry(this.alias, this.dest, true);
                                 this.kssave = true;
                              } else if (this.command == Main.Command.CHANGEALIAS) {
                                 if (this.alias == null) {
                                    this.alias = "mykey";
                                 }

                                 this.doCloneEntry(this.alias, this.dest, false);
                                 if (this.keyStore.containsAlias(this.alias)) {
                                    this.doDeleteEntry(this.alias);
                                 }

                                 this.kssave = true;
                              } else if (this.command == Main.Command.KEYPASSWD) {
                                 this.keyPassNew = this.newPass;
                                 this.doChangeKeyPasswd(this.alias);
                                 this.kssave = true;
                              } else if (this.command == Main.Command.LIST) {
                                 if (this.storePass == null && !KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
                                    this.printNoIntegrityWarning();
                                 }

                                 if (this.alias != null) {
                                    this.doPrintEntry(rb.getString("the.certificate"), this.alias, var1);
                                 } else {
                                    this.doPrintEntries(var1);
                                 }
                              } else if (this.command == Main.Command.PRINTCERT) {
                                 this.doPrintCert(var1);
                              } else if (this.command == Main.Command.SELFCERT) {
                                 this.doSelfCert(this.alias, this.dname, this.sigAlgName);
                                 this.kssave = true;
                              } else if (this.command == Main.Command.STOREPASSWD) {
                                 this.storePassNew = this.newPass;
                                 if (this.storePassNew == null) {
                                    this.storePassNew = this.getNewPasswd("keystore password", this.storePass);
                                 }

                                 this.kssave = true;
                              } else if (this.command == Main.Command.GENCERT) {
                                 if (this.alias == null) {
                                    this.alias = "mykey";
                                 }

                                 var254 = System.in;
                                 if (this.infilename != null) {
                                    var254 = new FileInputStream(this.infilename);
                                 }

                                 PrintStream var259 = null;
                                 if (this.outfilename != null) {
                                    var259 = new PrintStream(new FileOutputStream(this.outfilename));
                                    var1 = var259;
                                 }

                                 try {
                                    this.doGenCert(this.alias, this.sigAlgName, (InputStream)var254, var1);
                                 } finally {
                                    if (var254 != System.in) {
                                       ((InputStream)var254).close();
                                    }

                                    if (var259 != null) {
                                       var259.close();
                                    }

                                 }
                              } else if (this.command == Main.Command.GENCRL) {
                                 if (this.alias == null) {
                                    this.alias = "mykey";
                                 }

                                 if (this.filename != null) {
                                    var249 = new PrintStream(new FileOutputStream(this.filename));
                                    var251 = null;

                                    try {
                                       this.doGenCRL(var249);
                                    } catch (Throwable var228) {
                                       var251 = var228;
                                       throw var228;
                                    } finally {
                                       if (var249 != null) {
                                          if (var251 != null) {
                                             try {
                                                var249.close();
                                             } catch (Throwable var221) {
                                                var251.addSuppressed(var221);
                                             }
                                          } else {
                                             var249.close();
                                          }
                                       }

                                    }
                                 } else {
                                    this.doGenCRL(var1);
                                 }
                              } else if (this.command == Main.Command.PRINTCERTREQ) {
                                 if (this.filename != null) {
                                    var252 = new FileInputStream(this.filename);
                                    var251 = null;

                                    try {
                                       this.doPrintCertReq(var252, var1);
                                    } catch (Throwable var227) {
                                       var251 = var227;
                                       throw var227;
                                    } finally {
                                       if (var252 != null) {
                                          if (var251 != null) {
                                             try {
                                                var252.close();
                                             } catch (Throwable var222) {
                                                var251.addSuppressed(var222);
                                             }
                                          } else {
                                             var252.close();
                                          }
                                       }

                                    }
                                 } else {
                                    this.doPrintCertReq(System.in, var1);
                                 }
                              } else if (this.command == Main.Command.PRINTCRL) {
                                 this.doPrintCRL(this.filename, var1);
                              }
                           }
                        }

                        if (this.kssave) {
                           if (this.verbose) {
                              var247 = new MessageFormat(rb.getString(".Storing.ksfname."));
                              var246 = new Object[]{this.nullStream ? "keystore" : this.ksfname};
                              System.err.println(var247.format(var246));
                           }

                           if (this.token) {
                              this.keyStore.store((OutputStream)null, (char[])null);
                           } else {
                              char[] var260 = this.storePassNew != null ? this.storePassNew : this.storePass;
                              if (this.nullStream) {
                                 this.keyStore.store((OutputStream)null, var260);
                              } else {
                                 ByteArrayOutputStream var262 = new ByteArrayOutputStream();
                                 this.keyStore.store(var262, var260);
                                 FileOutputStream var248 = new FileOutputStream(this.ksfname);
                                 Throwable var253 = null;

                                 try {
                                    var248.write(var262.toByteArray());
                                 } catch (Throwable var226) {
                                    var253 = var226;
                                    throw var226;
                                 } finally {
                                    if (var248 != null) {
                                       if (var253 != null) {
                                          try {
                                             var248.close();
                                          } catch (Throwable var220) {
                                             var253.addSuppressed(var220);
                                          }
                                       } else {
                                          var248.close();
                                       }
                                    }

                                 }
                              }
                           }
                        }

                        if (this.isKeyStoreRelated(this.command) && !this.token && !this.nullStream && this.ksfname != null) {
                           File var261 = new File(this.ksfname);
                           if (var261.exists()) {
                              var258 = this.keyStoreType(var261);
                              if (var258.equalsIgnoreCase("JKS") || var258.equalsIgnoreCase("JCEKS")) {
                                 boolean var250 = true;
                                 Iterator var255 = Collections.list(this.keyStore.aliases()).iterator();

                                 while(var255.hasNext()) {
                                    var7 = (String)var255.next();
                                    if (!this.keyStore.entryInstanceOf(var7, KeyStore.TrustedCertificateEntry.class)) {
                                       var250 = false;
                                       break;
                                    }
                                 }

                                 if (!var250) {
                                    this.weakWarnings.add(String.format(rb.getString("jks.storetype.warning"), var258, this.ksfname));
                                 }
                              }

                              if (this.inplaceImport) {
                                 var5 = this.keyStoreType(new File(this.inplaceBackupName));
                                 String var256 = var258.equalsIgnoreCase(var5) ? rb.getString("backup.keystore.warning") : rb.getString("migrate.keystore.warning");
                                 this.weakWarnings.add(String.format(var256, this.srcksfname, var5, this.inplaceBackupName, var258));
                              }
                           }
                        }

                     }
                  }
               }
            } else {
               throw new IllegalArgumentException(rb.getString("if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified"));
            }
         } else {
            throw new IllegalArgumentException(rb.getString("if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified"));
         }
      } else {
         throw new IllegalArgumentException(MessageFormat.format(rb.getString(".keypass.and.new.can.not.be.specified.if.storetype.is.{0}"), this.storetype));
      }
   }

   private String keyStoreType(File var1) throws IOException {
      int var2 = -17957139;
      int var3 = -825307442;
      DataInputStream var4 = new DataInputStream(new FileInputStream(var1));
      Throwable var5 = null;

      String var7;
      try {
         int var6 = var4.readInt();
         if (var6 != var2) {
            if (var6 == var3) {
               var7 = "JCEKS";
               return var7;
            }

            var7 = "Non JKS/JCEKS";
            return var7;
         }

         var7 = "JKS";
      } catch (Throwable var18) {
         var5 = var18;
         throw var18;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var17) {
                  var5.addSuppressed(var17);
               }
            } else {
               var4.close();
            }
         }

      }

      return var7;
   }

   private void doGenCert(String var1, String var2, InputStream var3, PrintStream var4) throws Exception {
      if (!this.keyStore.containsAlias(var1)) {
         MessageFormat var30 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
         Object[] var31 = new Object[]{var1};
         throw new Exception(var30.format(var31));
      } else {
         Certificate var5 = this.keyStore.getCertificate(var1);
         byte[] var6 = var5.getEncoded();
         X509CertImpl var7 = new X509CertImpl(var6);
         X509CertInfo var8 = (X509CertInfo)var7.get("x509.info");
         X500Name var9 = (X500Name)var8.get("subject.dname");
         Date var10 = getStartDate(this.startDate);
         Date var11 = new Date();
         var11.setTime(var10.getTime() + this.validity * 1000L * 24L * 60L * 60L);
         CertificateValidity var12 = new CertificateValidity(var10, var11);
         PrivateKey var13 = (PrivateKey)this.recoverKey(var1, this.storePass, this.keyPass).fst;
         if (var2 == null) {
            var2 = getCompatibleSigAlgName(var13.getAlgorithm());
         }

         Signature var14 = Signature.getInstance(var2);
         var14.initSign(var13);
         X509CertInfo var15 = new X509CertInfo();
         var15.set("validity", var12);
         var15.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & Integer.MAX_VALUE));
         var15.set("version", new CertificateVersion(2));
         var15.set("algorithmID", new CertificateAlgorithmId(AlgorithmId.get(var2)));
         var15.set("issuer", var9);
         BufferedReader var16 = new BufferedReader(new InputStreamReader(var3));
         boolean var17 = false;
         StringBuffer var18 = new StringBuffer();

         while(true) {
            String var19 = var16.readLine();
            if (var19 == null) {
               break;
            }

            if (var19.startsWith("-----BEGIN") && var19.indexOf("REQUEST") >= 0) {
               var17 = true;
            } else {
               if (var19.startsWith("-----END") && var19.indexOf("REQUEST") >= 0) {
                  break;
               }

               if (var17) {
                  var18.append(var19);
               }
            }
         }

         byte[] var32 = Pem.decode(new String(var18));
         PKCS10 var20 = new PKCS10(var32);
         this.checkWeak(rb.getString("the.certificate.request"), var20);
         var15.set("key", new CertificateX509Key(var20.getSubjectPublicKeyInfo()));
         var15.set("subject", this.dname == null ? var20.getSubjectName() : new X500Name(this.dname));
         CertificateExtensions var21 = null;
         Iterator var22 = var20.getAttributes().getAttributes().iterator();

         while(var22.hasNext()) {
            PKCS10Attribute var23 = (PKCS10Attribute)var22.next();
            if (var23.getAttributeId().equals((Object)PKCS9Attribute.EXTENSION_REQUEST_OID)) {
               var21 = (CertificateExtensions)var23.getAttributeValue();
            }
         }

         CertificateExtensions var33 = this.createV3Extensions(var21, (CertificateExtensions)null, this.v3ext, var20.getSubjectPublicKeyInfo(), var5.getPublicKey());
         var15.set("extensions", var33);
         X509CertImpl var24 = new X509CertImpl(var15);
         var24.sign(var13, var2);
         this.dumpCert(var24, var4);
         Certificate[] var25 = this.keyStore.getCertificateChain(var1);
         int var26 = var25.length;

         for(int var27 = 0; var27 < var26; ++var27) {
            Certificate var28 = var25[var27];
            if (var28 instanceof X509Certificate) {
               X509Certificate var29 = (X509Certificate)var28;
               if (!this.isSelfSigned(var29)) {
                  this.dumpCert(var29, var4);
               }
            }
         }

         this.checkWeak(rb.getString("the.issuer"), this.keyStore.getCertificateChain(var1));
         this.checkWeak(rb.getString("the.generated.certificate"), (Certificate)var24);
      }
   }

   private void doGenCRL(PrintStream var1) throws Exception {
      if (this.ids == null) {
         throw new Exception("Must provide -id when -gencrl");
      } else {
         Certificate var2 = this.keyStore.getCertificate(this.alias);
         byte[] var3 = var2.getEncoded();
         X509CertImpl var4 = new X509CertImpl(var3);
         X509CertInfo var5 = (X509CertInfo)var4.get("x509.info");
         X500Name var6 = (X500Name)var5.get("subject.dname");
         Date var7 = getStartDate(this.startDate);
         Date var8 = (Date)var7.clone();
         var8.setTime(var8.getTime() + this.validity * 1000L * 24L * 60L * 60L);
         new CertificateValidity(var7, var8);
         PrivateKey var10 = (PrivateKey)this.recoverKey(this.alias, this.storePass, this.keyPass).fst;
         if (this.sigAlgName == null) {
            this.sigAlgName = getCompatibleSigAlgName(var10.getAlgorithm());
         }

         X509CRLEntry[] var11 = new X509CRLEntry[this.ids.size()];

         for(int var12 = 0; var12 < this.ids.size(); ++var12) {
            String var13 = (String)this.ids.get(var12);
            int var14 = var13.indexOf(58);
            if (var14 >= 0) {
               CRLExtensions var15 = new CRLExtensions();
               var15.set("Reason", new CRLReasonCodeExtension(Integer.parseInt(var13.substring(var14 + 1))));
               var11[var12] = new X509CRLEntryImpl(new BigInteger(var13.substring(0, var14)), var7, var15);
            } else {
               var11[var12] = new X509CRLEntryImpl(new BigInteger((String)this.ids.get(var12)), var7);
            }
         }

         X509CRLImpl var16 = new X509CRLImpl(var6, var7, var8, var11);
         var16.sign(var10, this.sigAlgName);
         if (this.rfc) {
            var1.println("-----BEGIN X509 CRL-----");
            var1.println(Base64.getMimeEncoder(64, CRLF).encodeToString(var16.getEncodedInternal()));
            var1.println("-----END X509 CRL-----");
         } else {
            var1.write((byte[])var16.getEncodedInternal());
         }

         this.checkWeak(rb.getString("the.generated.crl"), (CRL)var16, var10);
      }
   }

   private void doCertReq(String var1, String var2, PrintStream var3) throws Exception {
      if (var1 == null) {
         var1 = "mykey";
      }

      Pair var4 = this.recoverKey(var1, this.storePass, this.keyPass);
      PrivateKey var5 = (PrivateKey)var4.fst;
      if (this.keyPass == null) {
         this.keyPass = (char[])var4.snd;
      }

      Certificate var6 = this.keyStore.getCertificate(var1);
      if (var6 == null) {
         MessageFormat var11 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
         Object[] var12 = new Object[]{var1};
         throw new Exception(var11.format(var12));
      } else {
         PKCS10 var7 = new PKCS10(var6.getPublicKey());
         CertificateExtensions var8 = this.createV3Extensions((CertificateExtensions)null, (CertificateExtensions)null, this.v3ext, var6.getPublicKey(), (PublicKey)null);
         var7.getAttributes().setAttribute("extensions", new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, var8));
         if (var2 == null) {
            var2 = getCompatibleSigAlgName(var5.getAlgorithm());
         }

         Signature var9 = Signature.getInstance(var2);
         var9.initSign(var5);
         X500Name var10 = this.dname == null ? new X500Name(((X509Certificate)var6).getSubjectDN().toString()) : new X500Name(this.dname);
         var7.encodeAndSign(var10, var9);
         var7.print(var3);
         this.checkWeak(rb.getString("the.generated.certificate.request"), var7);
      }
   }

   private void doDeleteEntry(String var1) throws Exception {
      if (!this.keyStore.containsAlias(var1)) {
         MessageFormat var2 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
         Object[] var3 = new Object[]{var1};
         throw new Exception(var2.format(var3));
      } else {
         this.keyStore.deleteEntry(var1);
      }
   }

   private void doExportCert(String var1, PrintStream var2) throws Exception {
      if (this.storePass == null && !KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
         this.printNoIntegrityWarning();
      }

      if (var1 == null) {
         var1 = "mykey";
      }

      if (!this.keyStore.containsAlias(var1)) {
         MessageFormat var6 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
         Object[] var7 = new Object[]{var1};
         throw new Exception(var6.format(var7));
      } else {
         X509Certificate var3 = (X509Certificate)this.keyStore.getCertificate(var1);
         if (var3 == null) {
            MessageFormat var4 = new MessageFormat(rb.getString("Alias.alias.has.no.certificate"));
            Object[] var5 = new Object[]{var1};
            throw new Exception(var4.format(var5));
         } else {
            this.dumpCert(var3, var2);
            this.checkWeak(rb.getString("the.certificate"), (Certificate)var3);
         }
      }
   }

   private char[] promptForKeyPass(String var1, String var2, char[] var3) throws Exception {
      if ("PKCS12".equalsIgnoreCase(this.storetype)) {
         return var3;
      } else {
         if (!this.token && !this.protectedPath) {
            int var4;
            for(var4 = 0; var4 < 3; ++var4) {
               MessageFormat var5 = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
               Object[] var6 = new Object[]{var1};
               System.err.println(var5.format(var6));
               if (var2 == null) {
                  System.err.print(rb.getString(".RETURN.if.same.as.keystore.password."));
               } else {
                  var5 = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
                  Object[] var7 = new Object[]{var2};
                  System.err.print(var5.format(var7));
               }

               System.err.flush();
               char[] var9 = Password.readPassword(System.in);
               this.passwords.add(var9);
               if (var9 == null) {
                  return var3;
               }

               if (var9.length >= 6) {
                  System.err.print(rb.getString("Re.enter.new.password."));
                  char[] var8 = Password.readPassword(System.in);
                  this.passwords.add(var8);
                  if (Arrays.equals(var9, var8)) {
                     return var9;
                  }

                  System.err.println(rb.getString("They.don.t.match.Try.again"));
               } else {
                  System.err.println(rb.getString("Key.password.is.too.short.must.be.at.least.6.characters"));
               }
            }

            if (var4 == 3) {
               if (this.command == Main.Command.KEYCLONE) {
                  throw new Exception(rb.getString("Too.many.failures.Key.entry.not.cloned"));
               }

               throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
            }
         }

         return null;
      }
   }

   private char[] promptForCredential() throws Exception {
      if (System.console() == null) {
         char[] var4 = Password.readPassword(System.in);
         this.passwords.add(var4);
         return var4;
      } else {
         int var1;
         for(var1 = 0; var1 < 3; ++var1) {
            System.err.print(rb.getString("Enter.the.password.to.be.stored."));
            System.err.flush();
            char[] var2 = Password.readPassword(System.in);
            this.passwords.add(var2);
            System.err.print(rb.getString("Re.enter.password."));
            char[] var3 = Password.readPassword(System.in);
            this.passwords.add(var3);
            if (Arrays.equals(var2, var3)) {
               return var2;
            }

            System.err.println(rb.getString("They.don.t.match.Try.again"));
         }

         if (var1 == 3) {
            throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
         } else {
            return null;
         }
      }
   }

   private void doGenSecretKey(String var1, String var2, int var3) throws Exception {
      if (var1 == null) {
         var1 = "mykey";
      }

      if (this.keyStore.containsAlias(var1)) {
         MessageFormat var9 = new MessageFormat(rb.getString("Secret.key.not.generated.alias.alias.already.exists"));
         Object[] var11 = new Object[]{var1};
         throw new Exception(var9.format(var11));
      } else {
         boolean var4 = true;
         SecretKey var5 = null;
         MessageFormat var7;
         Object[] var8;
         if (var2.toUpperCase(Locale.ENGLISH).startsWith("PBE")) {
            SecretKeyFactory var6 = SecretKeyFactory.getInstance("PBE");
            var5 = var6.generateSecret(new PBEKeySpec(this.promptForCredential()));
            if (!"PBE".equalsIgnoreCase(var2)) {
               var4 = false;
            }

            if (this.verbose) {
               var7 = new MessageFormat(rb.getString("Generated.keyAlgName.secret.key"));
               var8 = new Object[]{var4 ? "PBE" : var5.getAlgorithm()};
               System.err.println(var7.format(var8));
            }
         } else {
            KeyGenerator var10 = KeyGenerator.getInstance(var2);
            if (var3 == -1) {
               if ("DES".equalsIgnoreCase(var2)) {
                  var3 = 56;
               } else {
                  if (!"DESede".equalsIgnoreCase(var2)) {
                     throw new Exception(rb.getString("Please.provide.keysize.for.secret.key.generation"));
                  }

                  var3 = 168;
               }
            }

            var10.init(var3);
            var5 = var10.generateKey();
            if (this.verbose) {
               var7 = new MessageFormat(rb.getString("Generated.keysize.bit.keyAlgName.secret.key"));
               var8 = new Object[]{new Integer(var3), var5.getAlgorithm()};
               System.err.println(var7.format(var8));
            }
         }

         if (this.keyPass == null) {
            this.keyPass = this.promptForKeyPass(var1, (String)null, this.storePass);
         }

         if (var4) {
            this.keyStore.setKeyEntry(var1, var5, this.keyPass, (Certificate[])null);
         } else {
            this.keyStore.setEntry(var1, new KeyStore.SecretKeyEntry(var5), new KeyStore.PasswordProtection(this.keyPass, var2, (AlgorithmParameterSpec)null));
         }

      }
   }

   private static String getCompatibleSigAlgName(String var0) throws Exception {
      if ("DSA".equalsIgnoreCase(var0)) {
         return "SHA256WithDSA";
      } else if ("RSA".equalsIgnoreCase(var0)) {
         return "SHA256WithRSA";
      } else if ("EC".equalsIgnoreCase(var0)) {
         return "SHA256withECDSA";
      } else {
         throw new Exception(rb.getString("Cannot.derive.signature.algorithm"));
      }
   }

   private void doGenKeyPair(String var1, String var2, String var3, int var4, String var5) throws Exception {
      if (var4 == -1) {
         if ("EC".equalsIgnoreCase(var3)) {
            var4 = SecurityProviderConstants.DEF_EC_KEY_SIZE;
         } else if ("RSA".equalsIgnoreCase(var3)) {
            var4 = SecurityProviderConstants.DEF_RSA_KEY_SIZE;
         } else if ("DSA".equalsIgnoreCase(var3)) {
            var4 = SecurityProviderConstants.DEF_DSA_KEY_SIZE;
         }
      }

      if (var1 == null) {
         var1 = "mykey";
      }

      if (this.keyStore.containsAlias(var1)) {
         MessageFormat var13 = new MessageFormat(rb.getString("Key.pair.not.generated.alias.alias.already.exists"));
         Object[] var14 = new Object[]{var1};
         throw new Exception(var13.format(var14));
      } else {
         if (var5 == null) {
            var5 = getCompatibleSigAlgName(var3);
         }

         CertAndKeyGen var6 = new CertAndKeyGen(var3, var5, this.providerName);
         X500Name var7;
         if (var2 == null) {
            var7 = this.getX500Name();
         } else {
            var7 = new X500Name(var2);
         }

         var6.generate(var4);
         PrivateKey var8 = var6.getPrivateKey();
         CertificateExtensions var9 = this.createV3Extensions((CertificateExtensions)null, (CertificateExtensions)null, this.v3ext, var6.getPublicKeyAnyway(), (PublicKey)null);
         X509Certificate[] var10 = new X509Certificate[]{var6.getSelfCertificate(var7, getStartDate(this.startDate), this.validity * 24L * 60L * 60L, var9)};
         if (this.verbose) {
            MessageFormat var11 = new MessageFormat(rb.getString("Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for"));
            Object[] var12 = new Object[]{new Integer(var4), var8.getAlgorithm(), var10[0].getSigAlgName(), new Long(this.validity), var7};
            System.err.println(var11.format(var12));
         }

         if (this.keyPass == null) {
            this.keyPass = this.promptForKeyPass(var1, (String)null, this.storePass);
         }

         this.checkWeak(rb.getString("the.generated.certificate"), (Certificate)var10[0]);
         this.keyStore.setKeyEntry(var1, var8, this.keyPass, var10);
      }
   }

   private void doCloneEntry(String var1, String var2, boolean var3) throws Exception {
      if (var1 == null) {
         var1 = "mykey";
      }

      if (this.keyStore.containsAlias(var2)) {
         MessageFormat var7 = new MessageFormat(rb.getString("Destination.alias.dest.already.exists"));
         Object[] var8 = new Object[]{var2};
         throw new Exception(var7.format(var8));
      } else {
         Pair var4 = this.recoverEntry(this.keyStore, var1, this.storePass, this.keyPass);
         KeyStore.Entry var5 = (KeyStore.Entry)var4.fst;
         this.keyPass = (char[])var4.snd;
         KeyStore.PasswordProtection var6 = null;
         if (this.keyPass != null) {
            if (var3 && !"PKCS12".equalsIgnoreCase(this.storetype)) {
               if (this.keyPassNew == null) {
                  this.keyPassNew = this.promptForKeyPass(var2, var1, this.keyPass);
               }
            } else {
               this.keyPassNew = this.keyPass;
            }

            var6 = new KeyStore.PasswordProtection(this.keyPassNew);
         }

         this.keyStore.setEntry(var2, var5, var6);
      }
   }

   private void doChangeKeyPasswd(String var1) throws Exception {
      if (var1 == null) {
         var1 = "mykey";
      }

      Pair var2 = this.recoverKey(var1, this.storePass, this.keyPass);
      Key var3 = (Key)var2.fst;
      if (this.keyPass == null) {
         this.keyPass = (char[])var2.snd;
      }

      if (this.keyPassNew == null) {
         MessageFormat var4 = new MessageFormat(rb.getString("key.password.for.alias."));
         Object[] var5 = new Object[]{var1};
         this.keyPassNew = this.getNewPasswd(var4.format(var5), this.keyPass);
      }

      this.keyStore.setKeyEntry(var1, var3, this.keyPassNew, this.keyStore.getCertificateChain(var1));
   }

   private void doImportIdentityDatabase(InputStream var1) throws Exception {
      System.err.println(rb.getString("No.entries.from.identity.database.added"));
   }

   private void doPrintEntry(String var1, String var2, PrintStream var3) throws Exception {
      MessageFormat var4;
      Object[] var5;
      if (!this.keyStore.containsAlias(var2)) {
         var4 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
         var5 = new Object[]{var2};
         throw new Exception(var4.format(var5));
      } else {
         if (!this.verbose && !this.rfc && !this.debug) {
            if (!this.token) {
               var4 = new MessageFormat(rb.getString("alias.keyStore.getCreationDate.alias."));
               var5 = new Object[]{var2, this.keyStore.getCreationDate(var2)};
               var3.print(var4.format(var5));
            } else {
               var4 = new MessageFormat(rb.getString("alias."));
               var5 = new Object[]{var2};
               var3.print(var4.format(var5));
            }
         } else {
            var4 = new MessageFormat(rb.getString("Alias.name.alias"));
            var5 = new Object[]{var2};
            var3.println(var4.format(var5));
            if (!this.token) {
               var4 = new MessageFormat(rb.getString("Creation.date.keyStore.getCreationDate.alias."));
               Object[] var6 = new Object[]{this.keyStore.getCreationDate(var2)};
               var3.println(var4.format(var6));
            }
         }

         Object[] var8;
         if (this.keyStore.entryInstanceOf(var2, KeyStore.SecretKeyEntry.class)) {
            if (!this.verbose && !this.rfc && !this.debug) {
               var3.println("SecretKeyEntry, ");
            } else {
               var8 = new Object[]{"SecretKeyEntry"};
               var3.println((new MessageFormat(rb.getString("Entry.type.type."))).format(var8));
            }
         } else if (this.keyStore.entryInstanceOf(var2, KeyStore.PrivateKeyEntry.class)) {
            if (!this.verbose && !this.rfc && !this.debug) {
               var3.println("PrivateKeyEntry, ");
            } else {
               var8 = new Object[]{"PrivateKeyEntry"};
               var3.println((new MessageFormat(rb.getString("Entry.type.type."))).format(var8));
            }

            Certificate[] var9 = this.keyStore.getCertificateChain(var2);
            if (var9 != null) {
               if (!this.verbose && !this.rfc && !this.debug) {
                  var3.println(rb.getString("Certificate.fingerprint.SHA1.") + this.getCertFingerPrint("SHA1", var9[0]));
                  this.checkWeak(var1, var9[0]);
               } else {
                  var3.println(rb.getString("Certificate.chain.length.") + var9.length);

                  for(int var10 = 0; var10 < var9.length; ++var10) {
                     MessageFormat var12 = new MessageFormat(rb.getString("Certificate.i.1."));
                     Object[] var7 = new Object[]{new Integer(var10 + 1)};
                     var3.println(var12.format(var7));
                     if (this.verbose && var9[var10] instanceof X509Certificate) {
                        this.printX509Cert((X509Certificate)((X509Certificate)var9[var10]), var3);
                     } else if (this.debug) {
                        var3.println(var9[var10].toString());
                     } else {
                        this.dumpCert(var9[var10], var3);
                     }

                     this.checkWeak(var1, var9[var10]);
                  }
               }
            }
         } else if (this.keyStore.entryInstanceOf(var2, KeyStore.TrustedCertificateEntry.class)) {
            Certificate var11 = this.keyStore.getCertificate(var2);
            var5 = new Object[]{"trustedCertEntry"};
            String var13 = (new MessageFormat(rb.getString("Entry.type.type."))).format(var5) + "\n";
            if (this.verbose && var11 instanceof X509Certificate) {
               var3.println(var13);
               this.printX509Cert((X509Certificate)var11, var3);
            } else if (this.rfc) {
               var3.println(var13);
               this.dumpCert(var11, var3);
            } else if (this.debug) {
               var3.println(var11.toString());
            } else {
               var3.println("trustedCertEntry, ");
               var3.println(rb.getString("Certificate.fingerprint.SHA1.") + this.getCertFingerPrint("SHA1", var11));
            }

            this.checkWeak(var1, var11);
         } else {
            var3.println(rb.getString("Unknown.Entry.Type"));
         }

      }
   }

   boolean inplaceImportCheck() throws Exception {
      if (!"PKCS11".equalsIgnoreCase(this.srcstoretype) && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
         if (this.srcksfname != null) {
            File var1 = new File(this.srcksfname);
            if (var1.exists() && var1.length() == 0L) {
               throw new Exception(rb.getString("Source.keystore.file.exists.but.is.empty.") + this.srcksfname);
            } else if (var1.getCanonicalFile().equals((new File(this.ksfname)).getCanonicalFile())) {
               return true;
            } else {
               System.err.println(String.format(rb.getString("importing.keystore.status"), this.srcksfname, this.ksfname));
               return false;
            }
         } else {
            throw new Exception(rb.getString("Please.specify.srckeystore"));
         }
      } else {
         return false;
      }
   }

   KeyStore loadSourceKeyStore() throws Exception {
      FileInputStream var1 = null;
      File var2 = null;
      if (!"PKCS11".equalsIgnoreCase(this.srcstoretype) && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
         var2 = new File(this.srcksfname);
         var1 = new FileInputStream(var2);
      } else if (!"NONE".equals(this.srcksfname)) {
         System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), this.srcstoretype));
         System.err.println();
         this.tinyHelp();
      }

      KeyStore var3;
      try {
         if (this.srcstoretype == null) {
            this.srcstoretype = KeyStore.getDefaultType();
         }

         if (this.srcProviderName == null) {
            var3 = KeyStore.getInstance(this.srcstoretype);
         } else {
            var3 = KeyStore.getInstance(this.srcstoretype, this.srcProviderName);
         }

         if (this.srcstorePass == null && !this.srcprotectedPath && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
            System.err.print(rb.getString("Enter.source.keystore.password."));
            System.err.flush();
            this.srcstorePass = Password.readPassword(System.in);
            this.passwords.add(this.srcstorePass);
         }

         if ("PKCS12".equalsIgnoreCase(this.srcstoretype) && this.srckeyPass != null && this.srcstorePass != null && !Arrays.equals(this.srcstorePass, this.srckeyPass)) {
            MessageFormat var4 = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
            Object[] var5 = new Object[]{"-srckeypass"};
            System.err.println(var4.format(var5));
            this.srckeyPass = this.srcstorePass;
         }

         var3.load(var1, this.srcstorePass);
      } finally {
         if (var1 != null) {
            var1.close();
         }

      }

      if (this.srcstorePass == null && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
         System.err.println();
         System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
         System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.the.srckeystore."));
         System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
         System.err.println();
      }

      return var3;
   }

   private void doImportKeyStore(KeyStore var1) throws Exception {
      if (this.alias != null) {
         this.doImportKeyStoreSingle(var1, this.alias);
      } else {
         if (this.dest != null || this.srckeyPass != null) {
            throw new Exception(rb.getString("if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified"));
         }

         this.doImportKeyStoreAll(var1);
      }

      if (this.inplaceImport) {
         int var2 = 1;

         while(true) {
            this.inplaceBackupName = this.srcksfname + ".old" + (var2 == 1 ? "" : var2);
            File var3 = new File(this.inplaceBackupName);
            if (!var3.exists()) {
               Files.copy(Paths.get(this.srcksfname), var3.toPath());
               break;
            }

            ++var2;
         }
      }

   }

   private int doImportKeyStoreSingle(KeyStore var1, String var2) throws Exception {
      String var3 = this.dest == null ? var2 : this.dest;
      if (this.keyStore.containsAlias(var3)) {
         Object[] var4 = new Object[]{var2};
         if (this.noprompt) {
            System.err.println((new MessageFormat(rb.getString("Warning.Overwriting.existing.alias.alias.in.destination.keystore"))).format(var4));
         } else {
            String var5 = this.getYesNoReply((new MessageFormat(rb.getString("Existing.entry.alias.alias.exists.overwrite.no."))).format(var4));
            if ("NO".equals(var5)) {
               var3 = this.inputStringFromStdin(rb.getString("Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry."));
               if ("".equals(var3)) {
                  System.err.println((new MessageFormat(rb.getString("Entry.for.alias.alias.not.imported."))).format(var4));
                  return 0;
               }
            }
         }
      }

      Pair var12 = this.recoverEntry(var1, var2, this.srcstorePass, this.srckeyPass);
      KeyStore.Entry var13 = (KeyStore.Entry)var12.fst;
      KeyStore.PasswordProtection var6 = null;
      char[] var7 = null;
      if (this.destKeyPass != null) {
         var7 = this.destKeyPass;
         var6 = new KeyStore.PasswordProtection(this.destKeyPass);
      } else if (var12.snd != null) {
         var7 = (char[])var12.snd;
         var6 = new KeyStore.PasswordProtection((char[])var12.snd);
      }

      try {
         Certificate var8 = var1.getCertificate(var2);
         if (var8 != null) {
            this.checkWeak("<" + var3 + ">", var8);
         }

         this.keyStore.setEntry(var3, var13, var6);
         if ("PKCS12".equalsIgnoreCase(this.storetype) && var7 != null && !Arrays.equals(var7, this.storePass)) {
            throw new Exception(rb.getString("The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified."));
         } else {
            return 1;
         }
      } catch (KeyStoreException var11) {
         Object[] var9 = new Object[]{var2, var11.toString()};
         MessageFormat var10 = new MessageFormat(rb.getString("Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported."));
         System.err.println(var10.format(var9));
         return 2;
      }
   }

   private void doImportKeyStoreAll(KeyStore var1) throws Exception {
      int var2 = 0;
      int var3 = var1.size();
      Enumeration var4 = var1.aliases();

      while(var4.hasMoreElements()) {
         String var5 = (String)var4.nextElement();
         int var6 = this.doImportKeyStoreSingle(var1, var5);
         if (var6 == 1) {
            ++var2;
            Object[] var11 = new Object[]{var5};
            MessageFormat var8 = new MessageFormat(rb.getString("Entry.for.alias.alias.successfully.imported."));
            System.err.println(var8.format(var11));
         } else if (var6 == 2 && !this.noprompt) {
            String var7 = this.getYesNoReply("Do you want to quit the import process? [no]:  ");
            if ("YES".equals(var7)) {
               break;
            }
         }
      }

      Object[] var9 = new Object[]{var2, var3 - var2};
      MessageFormat var10 = new MessageFormat(rb.getString("Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled"));
      System.err.println(var10.format(var9));
   }

   private void doPrintEntries(PrintStream var1) throws Exception {
      var1.println(rb.getString("Keystore.type.") + this.keyStore.getType());
      var1.println(rb.getString("Keystore.provider.") + this.keyStore.getProvider().getName());
      var1.println();
      MessageFormat var2 = this.keyStore.size() == 1 ? new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entry")) : new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entries"));
      Object[] var3 = new Object[]{new Integer(this.keyStore.size())};
      var1.println(var2.format(var3));
      var1.println();
      Enumeration var4 = this.keyStore.aliases();

      while(true) {
         do {
            if (!var4.hasMoreElements()) {
               return;
            }

            String var5 = (String)var4.nextElement();
            this.doPrintEntry("<" + var5 + ">", var5, var1);
         } while(!this.verbose && !this.rfc);

         var1.println(rb.getString("NEWLINE"));
         var1.println(rb.getString("STAR"));
         var1.println(rb.getString("STARNN"));
      }
   }

   private static <T> Iterable<T> e2i(final Enumeration<T> var0) {
      return new Iterable<T>() {
         public Iterator<T> iterator() {
            return new Iterator<T>() {
               public boolean hasNext() {
                  return var0.hasMoreElements();
               }

               public T next() {
                  return var0.nextElement();
               }

               public void remove() {
                  throw new UnsupportedOperationException("Not supported yet.");
               }
            };
         }
      };
   }

   public static Collection<? extends CRL> loadCRLs(String var0) throws Exception {
      Object var1 = null;
      URI var2 = null;
      if (var0 == null) {
         var1 = System.in;
      } else {
         try {
            var2 = new URI(var0);
            if (!var2.getScheme().equals("ldap")) {
               var1 = var2.toURL().openStream();
            }
         } catch (Exception var11) {
            try {
               var1 = new FileInputStream(var0);
            } catch (Exception var10) {
               if (var2 != null && var2.getScheme() != null) {
                  throw var11;
               }

               throw var10;
            }
         }
      }

      if (var1 != null) {
         try {
            ByteArrayOutputStream var13 = new ByteArrayOutputStream();
            byte[] var14 = new byte[4096];

            while(true) {
               int var15 = ((InputStream)var1).read(var14);
               if (var15 < 0) {
                  Collection var16 = CertificateFactory.getInstance("X509").generateCRLs(new ByteArrayInputStream(var13.toByteArray()));
                  return var16;
               }

               var13.write(var14, 0, var15);
            }
         } finally {
            if (var1 != System.in) {
               ((InputStream)var1).close();
            }

         }
      } else {
         CertStoreHelper var3 = CertStoreHelper.getInstance("LDAP");
         String var4 = var2.getPath();
         if (var4.charAt(0) == '/') {
            var4 = var4.substring(1);
         }

         CertStore var5 = var3.getCertStore(var2);
         X509CRLSelector var6 = var3.wrap((X509CRLSelector)(new X509CRLSelector()), (Collection)null, var4);
         return var5.getCRLs(var6);
      }
   }

   public static List<CRL> readCRLsFromCert(X509Certificate var0) throws Exception {
      ArrayList var1 = new ArrayList();
      CRLDistributionPointsExtension var2 = X509CertImpl.toImpl(var0).getCRLDistributionPointsExtension();
      if (var2 == null) {
         return var1;
      } else {
         List var3 = var2.get("points");
         Iterator var4 = var3.iterator();

         while(true) {
            while(true) {
               GeneralNames var6;
               do {
                  if (!var4.hasNext()) {
                     return var1;
                  }

                  DistributionPoint var5 = (DistributionPoint)var4.next();
                  var6 = var5.getFullName();
               } while(var6 == null);

               Iterator var7 = var6.names().iterator();

               while(var7.hasNext()) {
                  GeneralName var8 = (GeneralName)var7.next();
                  if (var8.getType() == 6) {
                     URIName var9 = (URIName)var8.getName();
                     Iterator var10 = loadCRLs(var9.getName()).iterator();

                     while(var10.hasNext()) {
                        CRL var11 = (CRL)var10.next();
                        if (var11 instanceof X509CRL) {
                           var1.add((X509CRL)var11);
                        }
                     }
                     break;
                  }
               }
            }
         }
      }
   }

   private static String verifyCRL(KeyStore var0, CRL var1) throws Exception {
      X509CRLImpl var2 = (X509CRLImpl)var1;
      X500Principal var3 = var2.getIssuerX500Principal();
      Iterator var4 = e2i(var0.aliases()).iterator();

      while(true) {
         String var5;
         Certificate var6;
         X509Certificate var7;
         do {
            do {
               if (!var4.hasNext()) {
                  return null;
               }

               var5 = (String)var4.next();
               var6 = var0.getCertificate(var5);
            } while(!(var6 instanceof X509Certificate));

            var7 = (X509Certificate)var6;
         } while(!var7.getSubjectX500Principal().equals(var3));

         try {
            ((X509CRLImpl)var1).verify(var6.getPublicKey());
            return var5;
         } catch (Exception var9) {
         }
      }
   }

   private void doPrintCRL(String var1, PrintStream var2) throws Exception {
      CRL var4;
      Certificate var6;
      for(Iterator var3 = loadCRLs(var1).iterator(); var3.hasNext(); this.checkWeak(rb.getString("the.crl"), (CRL)var4, var6 == null ? null : var6.getPublicKey())) {
         var4 = (CRL)var3.next();
         this.printCRL(var4, var2);
         String var5 = null;
         var6 = null;
         if (this.caks != null) {
            var5 = verifyCRL(this.caks, var4);
            if (var5 != null) {
               var6 = this.caks.getCertificate(var5);
               var2.printf(rb.getString("verified.by.s.in.s.weak"), var5, "cacerts", this.withWeak(var6.getPublicKey()));
               var2.println();
            }
         }

         if (var5 == null && this.keyStore != null) {
            var5 = verifyCRL(this.keyStore, var4);
            if (var5 != null) {
               var6 = this.keyStore.getCertificate(var5);
               var2.printf(rb.getString("verified.by.s.in.s.weak"), var5, "keystore", this.withWeak(var6.getPublicKey()));
               var2.println();
            }
         }

         if (var5 == null) {
            var2.println(rb.getString("STAR"));
            var2.println(rb.getString("warning.not.verified.make.sure.keystore.is.correct"));
            var2.println(rb.getString("STARNN"));
         }
      }

   }

   private void printCRL(CRL var1, PrintStream var2) throws Exception {
      X509CRL var3 = (X509CRL)var1;
      if (this.rfc) {
         var2.println("-----BEGIN X509 CRL-----");
         var2.println(Base64.getMimeEncoder(64, CRLF).encodeToString(var3.getEncoded()));
         var2.println("-----END X509 CRL-----");
      } else {
         String var4;
         if (var1 instanceof X509CRLImpl) {
            X509CRLImpl var5 = (X509CRLImpl)var1;
            var4 = var5.toStringWithAlgName(this.withWeak("" + var5.getSigAlgId()));
         } else {
            var4 = var1.toString();
         }

         var2.println(var4);
      }

   }

   private void doPrintCertReq(InputStream var1, PrintStream var2) throws Exception {
      BufferedReader var3 = new BufferedReader(new InputStreamReader(var1));
      StringBuffer var4 = new StringBuffer();
      boolean var5 = false;

      while(true) {
         String var6 = var3.readLine();
         if (var6 == null) {
            break;
         }

         if (!var5) {
            if (var6.startsWith("-----")) {
               var5 = true;
            }
         } else {
            if (var6.startsWith("-----")) {
               break;
            }

            var4.append(var6);
         }
      }

      PKCS10 var13 = new PKCS10(Pem.decode(new String(var4)));
      PublicKey var7 = var13.getSubjectPublicKeyInfo();
      var2.printf(rb.getString("PKCS.10.with.weak"), var13.getSubjectName(), var7.getFormat(), this.withWeak(var7), this.withWeak(var13.getSigAlg()));
      Iterator var8 = var13.getAttributes().getAttributes().iterator();

      while(var8.hasNext()) {
         PKCS10Attribute var9 = (PKCS10Attribute)var8.next();
         ObjectIdentifier var10 = var9.getAttributeId();
         if (var10.equals((Object)PKCS9Attribute.EXTENSION_REQUEST_OID)) {
            CertificateExtensions var11 = (CertificateExtensions)var9.getAttributeValue();
            if (var11 != null) {
               printExtensions(rb.getString("Extension.Request."), var11, var2);
            }
         } else {
            var2.println("Attribute: " + var9.getAttributeId());
            PKCS9Attribute var14 = new PKCS9Attribute(var9.getAttributeId(), var9.getAttributeValue());
            var2.print(var14.getName() + ": ");
            Object var12 = var9.getAttributeValue();
            var2.println(var12 instanceof String[] ? Arrays.toString((Object[])((String[])((String[])var12))) : var12);
         }
      }

      if (this.debug) {
         var2.println((Object)var13);
      }

      this.checkWeak(rb.getString("the.certificate.request"), var13);
   }

   private void printCertFromStream(InputStream var1, PrintStream var2) throws Exception {
      Collection var3 = null;

      try {
         var3 = this.cf.generateCertificates(var1);
      } catch (CertificateException var10) {
         throw new Exception(rb.getString("Failed.to.parse.input"), var10);
      }

      if (var3.isEmpty()) {
         throw new Exception(rb.getString("Empty.input"));
      } else {
         Certificate[] var4 = (Certificate[])var3.toArray(new Certificate[var3.size()]);

         for(int var5 = 0; var5 < var4.length; ++var5) {
            X509Certificate var6 = null;

            try {
               var6 = (X509Certificate)var4[var5];
            } catch (ClassCastException var9) {
               throw new Exception(rb.getString("Not.X.509.certificate"));
            }

            if (var4.length > 1) {
               MessageFormat var7 = new MessageFormat(rb.getString("Certificate.i.1."));
               Object[] var8 = new Object[]{new Integer(var5 + 1)};
               var2.println(var7.format(var8));
            }

            if (this.rfc) {
               this.dumpCert(var6, var2);
            } else {
               this.printX509Cert(var6, var2);
            }

            if (var5 < var4.length - 1) {
               var2.println();
            }

            this.checkWeak(oneInMany(rb.getString("the.certificate"), var5, var4.length), (Certificate)var6);
         }

      }
   }

   private static String oneInMany(String var0, int var1, int var2) {
      return var2 == 1 ? var0 : String.format(rb.getString("one.in.many"), var0, var1 + 1, var2);
   }

   private void doPrintCert(PrintStream var1) throws Exception {
      if (this.jarfile != null) {
         JarFile var49 = new JarFile(this.jarfile, true);
         Enumeration var51 = var49.entries();
         HashSet var52 = new HashSet();
         byte[] var53 = new byte[8192];
         int var54 = 0;

         while(true) {
            CodeSigner[] var56;
            do {
               if (!var51.hasMoreElements()) {
                  var49.close();
                  if (var52.isEmpty()) {
                     var1.println(rb.getString("Not.a.signed.jar.file"));
                  }

                  return;
               }

               JarEntry var55 = (JarEntry)var51.nextElement();
               InputStream var8 = var49.getInputStream(var55);
               Throwable var9 = null;

               try {
                  while(var8.read(var53) != -1) {
                  }
               } catch (Throwable var44) {
                  var9 = var44;
                  throw var44;
               } finally {
                  if (var8 != null) {
                     if (var9 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var41) {
                           var9.addSuppressed(var41);
                        }
                     } else {
                        var8.close();
                     }
                  }

               }

               var56 = var55.getCodeSigners();
            } while(var56 == null);

            CodeSigner[] var57 = var56;
            int var10 = var56.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               CodeSigner var12 = var57[var11];
               if (!var52.contains(var12)) {
                  var52.add(var12);
                  String var10001 = rb.getString("Signer.d.");
                  Object[] var61 = new Object[1];
                  ++var54;
                  var61[0] = var54;
                  var1.printf(var10001, var61);
                  var1.println();
                  var1.println();
                  var1.println(rb.getString("Signature."));
                  var1.println();
                  List var13 = var12.getSignerCertPath().getCertificates();
                  int var14 = 0;
                  Iterator var15 = var13.iterator();

                  while(var15.hasNext()) {
                     Certificate var16 = (Certificate)var15.next();
                     X509Certificate var17 = (X509Certificate)var16;
                     if (this.rfc) {
                        var1.println(rb.getString("Certificate.owner.") + var17.getSubjectDN() + "\n");
                        this.dumpCert(var17, var1);
                     } else {
                        this.printX509Cert(var17, var1);
                     }

                     var1.println();
                     this.checkWeak(oneInMany(rb.getString("the.certificate"), var14++, var13.size()), (Certificate)var17);
                  }

                  Timestamp var58 = var12.getTimestamp();
                  if (var58 != null) {
                     var1.println(rb.getString("Timestamp."));
                     var1.println();
                     var13 = var58.getSignerCertPath().getCertificates();
                     var14 = 0;
                     Iterator var59 = var13.iterator();

                     while(var59.hasNext()) {
                        Certificate var60 = (Certificate)var59.next();
                        X509Certificate var18 = (X509Certificate)var60;
                        if (this.rfc) {
                           var1.println(rb.getString("Certificate.owner.") + var18.getSubjectDN() + "\n");
                           this.dumpCert(var18, var1);
                        } else {
                           this.printX509Cert(var18, var1);
                        }

                        var1.println();
                        this.checkWeak(oneInMany(rb.getString("the.tsa.certificate"), var14++, var13.size()), (Certificate)var18);
                     }
                  }
               }
            }
         }
      } else if (this.sslserver != null) {
         CertStoreHelper var48 = CertStoreHelper.getInstance("SSLServer");
         CertStore var50 = var48.getCertStore(new URI("https://" + this.sslserver));

         Collection var4;
         try {
            var4 = var50.getCertificates((CertSelector)null);
            if (var4.isEmpty()) {
               throw new Exception(rb.getString("No.certificate.from.the.SSL.server"));
            }
         } catch (CertStoreException var47) {
            if (var47.getCause() instanceof IOException) {
               throw new Exception(rb.getString("No.certificate.from.the.SSL.server"), var47.getCause());
            }

            throw var47;
         }

         int var5 = 0;
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            Certificate var7 = (Certificate)var6.next();

            try {
               if (this.rfc) {
                  this.dumpCert(var7, var1);
               } else {
                  var1.println("Certificate #" + var5);
                  var1.println("====================================");
                  this.printX509Cert((X509Certificate)var7, var1);
                  var1.println();
               }

               this.checkWeak(oneInMany(rb.getString("the.certificate"), var5++, var4.size()), var7);
            } catch (Exception var46) {
               if (this.debug) {
                  var46.printStackTrace();
               }
            }
         }
      } else if (this.filename != null) {
         FileInputStream var2 = new FileInputStream(this.filename);
         Throwable var3 = null;

         try {
            this.printCertFromStream(var2, var1);
         } catch (Throwable var42) {
            var3 = var42;
            throw var42;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var40) {
                     var3.addSuppressed(var40);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } else {
         this.printCertFromStream(System.in, var1);
      }

   }

   private void doSelfCert(String var1, String var2, String var3) throws Exception {
      if (var1 == null) {
         var1 = "mykey";
      }

      Pair var4 = this.recoverKey(var1, this.storePass, this.keyPass);
      PrivateKey var5 = (PrivateKey)var4.fst;
      if (this.keyPass == null) {
         this.keyPass = (char[])var4.snd;
      }

      if (var3 == null) {
         var3 = getCompatibleSigAlgName(var5.getAlgorithm());
      }

      Certificate var6 = this.keyStore.getCertificate(var1);
      MessageFormat var17;
      Object[] var18;
      if (var6 == null) {
         var17 = new MessageFormat(rb.getString("alias.has.no.public.key"));
         var18 = new Object[]{var1};
         throw new Exception(var17.format(var18));
      } else if (!(var6 instanceof X509Certificate)) {
         var17 = new MessageFormat(rb.getString("alias.has.no.X.509.certificate"));
         var18 = new Object[]{var1};
         throw new Exception(var17.format(var18));
      } else {
         byte[] var7 = var6.getEncoded();
         X509CertImpl var8 = new X509CertImpl(var7);
         X509CertInfo var9 = (X509CertInfo)var8.get("x509.info");
         Date var10 = getStartDate(this.startDate);
         Date var11 = new Date();
         var11.setTime(var10.getTime() + this.validity * 1000L * 24L * 60L * 60L);
         CertificateValidity var12 = new CertificateValidity(var10, var11);
         var9.set("validity", var12);
         var9.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & Integer.MAX_VALUE));
         X500Name var13;
         if (var2 == null) {
            var13 = (X500Name)var9.get("subject.dname");
         } else {
            var13 = new X500Name(var2);
            var9.set("subject.dname", var13);
         }

         var9.set("issuer.dname", var13);
         X509CertImpl var14 = new X509CertImpl(var9);
         var14.sign(var5, var3);
         AlgorithmId var15 = (AlgorithmId)var14.get("x509.algorithm");
         var9.set("algorithmID.algorithm", var15);
         var9.set("version", new CertificateVersion(2));
         CertificateExtensions var16 = this.createV3Extensions((CertificateExtensions)null, (CertificateExtensions)var9.get("extensions"), this.v3ext, var6.getPublicKey(), (PublicKey)null);
         var9.set("extensions", var16);
         var14 = new X509CertImpl(var9);
         var14.sign(var5, var3);
         this.keyStore.setKeyEntry(var1, var5, this.keyPass != null ? this.keyPass : this.storePass, new Certificate[]{var14});
         if (this.verbose) {
            System.err.println(rb.getString("New.certificate.self.signed."));
            System.err.print(var14.toString());
            System.err.println();
         }

      }
   }

   private boolean installReply(String var1, InputStream var2) throws Exception {
      if (var1 == null) {
         var1 = "mykey";
      }

      Pair var3 = this.recoverKey(var1, this.storePass, this.keyPass);
      PrivateKey var4 = (PrivateKey)var3.fst;
      if (this.keyPass == null) {
         this.keyPass = (char[])var3.snd;
      }

      Certificate var5 = this.keyStore.getCertificate(var1);
      if (var5 == null) {
         MessageFormat var9 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
         Object[] var10 = new Object[]{var1};
         throw new Exception(var9.format(var10));
      } else {
         Collection var6 = this.cf.generateCertificates(var2);
         if (var6.isEmpty()) {
            throw new Exception(rb.getString("Reply.has.no.certificates"));
         } else {
            Certificate[] var7 = (Certificate[])var6.toArray(new Certificate[var6.size()]);
            Certificate[] var8;
            if (var7.length == 1) {
               var8 = this.establishCertChain(var5, var7[0]);
            } else {
               var8 = this.validateReply(var1, var5, var7);
            }

            if (var8 != null) {
               this.keyStore.setKeyEntry(var1, var4, this.keyPass != null ? this.keyPass : this.storePass, var8);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private boolean addTrustedCert(String var1, InputStream var2) throws Exception {
      if (var1 == null) {
         throw new Exception(rb.getString("Must.specify.alias"));
      } else if (this.keyStore.containsAlias(var1)) {
         MessageFormat var11 = new MessageFormat(rb.getString("Certificate.not.imported.alias.alias.already.exists"));
         Object[] var12 = new Object[]{var1};
         throw new Exception(var11.format(var12));
      } else {
         X509Certificate var3 = null;

         try {
            var3 = (X509Certificate)this.cf.generateCertificate(var2);
         } catch (CertificateException | ClassCastException var9) {
            throw new Exception(rb.getString("Input.not.an.X.509.certificate"));
         }

         if (this.noprompt) {
            this.checkWeak(rb.getString("the.input"), (Certificate)var3);
            this.keyStore.setCertificateEntry(var1, var3);
            return true;
         } else {
            boolean var4 = false;
            if (this.isSelfSigned(var3)) {
               var3.verify(var3.getPublicKey());
               var4 = true;
            }

            String var5 = null;
            String var6 = this.keyStore.getCertificateAlias(var3);
            MessageFormat var7;
            Object[] var8;
            if (var6 != null) {
               var7 = new MessageFormat(rb.getString("Certificate.already.exists.in.keystore.under.alias.trustalias."));
               var8 = new Object[]{var6};
               System.err.println(var7.format(var8));
               this.checkWeak(rb.getString("the.input"), (Certificate)var3);
               this.printWeakWarnings(true);
               var5 = this.getYesNoReply(rb.getString("Do.you.still.want.to.add.it.no."));
            } else if (var4) {
               if (this.trustcacerts && this.caks != null && (var6 = this.caks.getCertificateAlias(var3)) != null) {
                  var7 = new MessageFormat(rb.getString("Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias."));
                  var8 = new Object[]{var6};
                  System.err.println(var7.format(var8));
                  this.checkWeak(rb.getString("the.input"), (Certificate)var3);
                  this.printWeakWarnings(true);
                  var5 = this.getYesNoReply(rb.getString("Do.you.still.want.to.add.it.to.your.own.keystore.no."));
               }

               if (var6 == null) {
                  this.printX509Cert(var3, System.out);
                  this.checkWeak(rb.getString("the.input"), (Certificate)var3);
                  this.printWeakWarnings(true);
                  var5 = this.getYesNoReply(rb.getString("Trust.this.certificate.no."));
               }
            }

            if (var5 != null) {
               if ("YES".equals(var5)) {
                  this.keyStore.setCertificateEntry(var1, var3);
                  return true;
               } else {
                  return false;
               }
            } else {
               try {
                  Certificate[] var13 = this.establishCertChain((Certificate)null, var3);
                  if (var13 != null) {
                     this.keyStore.setCertificateEntry(var1, var3);
                     return true;
                  } else {
                     return false;
                  }
               } catch (Exception var10) {
                  this.printX509Cert(var3, System.out);
                  this.checkWeak(rb.getString("the.input"), (Certificate)var3);
                  this.printWeakWarnings(true);
                  var5 = this.getYesNoReply(rb.getString("Trust.this.certificate.no."));
                  if ("YES".equals(var5)) {
                     this.keyStore.setCertificateEntry(var1, var3);
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   private char[] getNewPasswd(String var1, char[] var2) throws Exception {
      Object var3 = null;
      char[] var4 = null;

      for(int var5 = 0; var5 < 3; ++var5) {
         MessageFormat var6 = new MessageFormat(rb.getString("New.prompt."));
         Object[] var7 = new Object[]{var1};
         System.err.print(var6.format(var7));
         char[] var9 = Password.readPassword(System.in);
         this.passwords.add(var9);
         if (var9 != null && var9.length >= 6) {
            if (Arrays.equals(var9, var2)) {
               System.err.println(rb.getString("Passwords.must.differ"));
            } else {
               var6 = new MessageFormat(rb.getString("Re.enter.new.prompt."));
               Object[] var8 = new Object[]{var1};
               System.err.print(var6.format(var8));
               var4 = Password.readPassword(System.in);
               this.passwords.add(var4);
               if (Arrays.equals(var9, var4)) {
                  Arrays.fill(var4, ' ');
                  return var9;
               }

               System.err.println(rb.getString("They.don.t.match.Try.again"));
            }
         } else {
            System.err.println(rb.getString("Password.is.too.short.must.be.at.least.6.characters"));
         }

         if (var9 != null) {
            Arrays.fill(var9, ' ');
            var3 = null;
         }

         if (var4 != null) {
            Arrays.fill(var4, ' ');
            var4 = null;
         }
      }

      throw new Exception(rb.getString("Too.many.failures.try.later"));
   }

   private String getAlias(String var1) throws Exception {
      if (var1 != null) {
         MessageFormat var2 = new MessageFormat(rb.getString("Enter.prompt.alias.name."));
         Object[] var3 = new Object[]{var1};
         System.err.print(var2.format(var3));
      } else {
         System.err.print(rb.getString("Enter.alias.name."));
      }

      return (new BufferedReader(new InputStreamReader(System.in))).readLine();
   }

   private String inputStringFromStdin(String var1) throws Exception {
      System.err.print(var1);
      return (new BufferedReader(new InputStreamReader(System.in))).readLine();
   }

   private char[] getKeyPasswd(String var1, String var2, char[] var3) throws Exception {
      int var4 = 0;
      Object var5 = null;

      char[] var9;
      do {
         MessageFormat var6;
         Object[] var7;
         if (var3 != null) {
            var6 = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
            var7 = new Object[]{var1};
            System.err.println(var6.format(var7));
            var6 = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
            Object[] var8 = new Object[]{var2};
            System.err.print(var6.format(var8));
         } else {
            var6 = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
            var7 = new Object[]{var1};
            System.err.print(var6.format(var7));
         }

         System.err.flush();
         var9 = Password.readPassword(System.in);
         this.passwords.add(var9);
         if (var9 == null) {
            var9 = var3;
         }

         ++var4;
      } while(var9 == null && var4 < 3);

      if (var9 == null) {
         throw new Exception(rb.getString("Too.many.failures.try.later"));
      } else {
         return var9;
      }
   }

   private String withWeak(String var1) {
      return DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, var1, (AlgorithmParameters)null) ? var1 : String.format(rb.getString("with.weak"), var1);
   }

   private String withWeak(PublicKey var1) {
      return DISABLED_CHECK.permits((Set)SIG_PRIMITIVE_SET, (Key)var1) ? String.format(rb.getString("key.bit"), KeyUtil.getKeySize((Key)var1), var1.getAlgorithm()) : String.format(rb.getString("key.bit.weak"), KeyUtil.getKeySize((Key)var1), var1.getAlgorithm());
   }

   private void printX509Cert(X509Certificate var1, PrintStream var2) throws Exception {
      MessageFormat var3 = new MessageFormat(rb.getString(".PATTERN.printX509Cert.with.weak"));
      PublicKey var4 = var1.getPublicKey();
      String var5 = var1.getSigAlgName();
      if (!this.isTrustedCert(var1)) {
         var5 = this.withWeak(var5);
      }

      Object[] var6 = new Object[]{var1.getSubjectDN().toString(), var1.getIssuerDN().toString(), var1.getSerialNumber().toString(16), var1.getNotBefore().toString(), var1.getNotAfter().toString(), this.getCertFingerPrint("MD5", var1), this.getCertFingerPrint("SHA1", var1), this.getCertFingerPrint("SHA-256", var1), var5, this.withWeak(var4), var1.getVersion()};
      var2.println(var3.format(var6));
      if (var1 instanceof X509CertImpl) {
         X509CertImpl var7 = (X509CertImpl)var1;
         X509CertInfo var8 = (X509CertInfo)var7.get("x509.info");
         CertificateExtensions var9 = (CertificateExtensions)var8.get("extensions");
         if (var9 != null) {
            printExtensions(rb.getString("Extensions."), var9, var2);
         }
      }

   }

   private static void printExtensions(String var0, CertificateExtensions var1, PrintStream var2) throws Exception {
      int var3 = 0;
      Iterator var4 = var1.getAllExtensions().iterator();

      for(Iterator var5 = var1.getUnparseableExtensions().values().iterator(); var4.hasNext() || var5.hasNext(); var2.println()) {
         Extension var6 = var4.hasNext() ? (Extension)var4.next() : (Extension)var5.next();
         if (var3 == 0) {
            var2.println();
            var2.println(var0);
            var2.println();
         }

         StringBuilder var10001 = (new StringBuilder()).append("#");
         ++var3;
         var2.print(var10001.append(var3).append(": ").append((Object)var6).toString());
         if (var6.getClass() == Extension.class) {
            byte[] var7 = var6.getExtensionValue();
            if (var7.length == 0) {
               var2.println(rb.getString(".Empty.value."));
            } else {
               (new HexDumpEncoder()).encodeBuffer(var6.getExtensionValue(), var2);
               var2.println();
            }
         }
      }

   }

   private boolean isSelfSigned(X509Certificate var1) {
      return this.signedBy(var1, var1);
   }

   private boolean signedBy(X509Certificate var1, X509Certificate var2) {
      if (!var2.getSubjectDN().equals(var1.getIssuerDN())) {
         return false;
      } else {
         try {
            var1.verify(var2.getPublicKey());
            return true;
         } catch (Exception var4) {
            return false;
         }
      }
   }

   private static Pair<String, Certificate> getSigner(Certificate var0, KeyStore var1) throws Exception {
      if (var1.getCertificateAlias(var0) != null) {
         return new Pair("", var0);
      } else {
         Enumeration var2 = var1.aliases();

         while(true) {
            String var3;
            Certificate var4;
            do {
               if (!var2.hasMoreElements()) {
                  return null;
               }

               var3 = (String)var2.nextElement();
               var4 = var1.getCertificate(var3);
            } while(var4 == null);

            try {
               var0.verify(var4.getPublicKey());
               return new Pair(var3, var4);
            } catch (Exception var6) {
            }
         }
      }
   }

   private X500Name getX500Name() throws IOException {
      BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));
      String var2 = "Unknown";
      String var3 = "Unknown";
      String var4 = "Unknown";
      String var5 = "Unknown";
      String var6 = "Unknown";
      String var7 = "Unknown";
      String var9 = null;
      int var10 = 20;

      X500Name var8;
      do {
         if (var10-- < 0) {
            throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
         }

         var2 = this.inputString(var1, rb.getString("What.is.your.first.and.last.name."), var2);
         var3 = this.inputString(var1, rb.getString("What.is.the.name.of.your.organizational.unit."), var3);
         var4 = this.inputString(var1, rb.getString("What.is.the.name.of.your.organization."), var4);
         var5 = this.inputString(var1, rb.getString("What.is.the.name.of.your.City.or.Locality."), var5);
         var6 = this.inputString(var1, rb.getString("What.is.the.name.of.your.State.or.Province."), var6);
         var7 = this.inputString(var1, rb.getString("What.is.the.two.letter.country.code.for.this.unit."), var7);
         var8 = new X500Name(var2, var3, var4, var5, var6, var7);
         MessageFormat var11 = new MessageFormat(rb.getString("Is.name.correct."));
         Object[] var12 = new Object[]{var8};
         var9 = this.inputString(var1, var11.format(var12), rb.getString("no"));
      } while(collator.compare(var9, rb.getString("yes")) != 0 && collator.compare(var9, rb.getString("y")) != 0);

      System.err.println();
      return var8;
   }

   private String inputString(BufferedReader var1, String var2, String var3) throws IOException {
      System.err.println(var2);
      MessageFormat var4 = new MessageFormat(rb.getString(".defaultValue."));
      Object[] var5 = new Object[]{var3};
      System.err.print(var4.format(var5));
      System.err.flush();
      String var6 = var1.readLine();
      if (var6 == null || collator.compare(var6, "") == 0) {
         var6 = var3;
      }

      return var6;
   }

   private void dumpCert(Certificate var1, PrintStream var2) throws IOException, CertificateException {
      if (this.rfc) {
         var2.println("-----BEGIN CERTIFICATE-----");
         var2.println(Base64.getMimeEncoder(64, CRLF).encodeToString(var1.getEncoded()));
         var2.println("-----END CERTIFICATE-----");
      } else {
         var2.write((byte[])var1.getEncoded());
      }

   }

   private void byte2hex(byte var1, StringBuffer var2) {
      char[] var3 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      int var4 = (var1 & 240) >> 4;
      int var5 = var1 & 15;
      var2.append(var3[var4]);
      var2.append(var3[var5]);
   }

   private String toHexString(byte[] var1) {
      StringBuffer var2 = new StringBuffer();
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         this.byte2hex(var1[var4], var2);
         if (var4 < var3 - 1) {
            var2.append(":");
         }
      }

      return var2.toString();
   }

   private Pair<Key, char[]> recoverKey(String var1, char[] var2, char[] var3) throws Exception {
      Key var4 = null;
      MessageFormat var5;
      Object[] var6;
      if (!this.keyStore.containsAlias(var1)) {
         var5 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
         var6 = new Object[]{var1};
         throw new Exception(var5.format(var6));
      } else if (!this.keyStore.entryInstanceOf(var1, KeyStore.PrivateKeyEntry.class) && !this.keyStore.entryInstanceOf(var1, KeyStore.SecretKeyEntry.class)) {
         var5 = new MessageFormat(rb.getString("Alias.alias.has.no.key"));
         var6 = new Object[]{var1};
         throw new Exception(var5.format(var6));
      } else {
         if (var3 == null) {
            try {
               var4 = this.keyStore.getKey(var1, var2);
               var3 = var2;
               this.passwords.add(var2);
            } catch (UnrecoverableKeyException var7) {
               if (this.token) {
                  throw var7;
               }

               var3 = this.getKeyPasswd(var1, (String)null, (char[])null);
               var4 = this.keyStore.getKey(var1, var3);
            }
         } else {
            var4 = this.keyStore.getKey(var1, var3);
         }

         return Pair.of(var4, var3);
      }
   }

   private Pair<KeyStore.Entry, char[]> recoverEntry(KeyStore var1, String var2, char[] var3, char[] var4) throws Exception {
      if (!var1.containsAlias(var2)) {
         MessageFormat var11 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
         Object[] var12 = new Object[]{var2};
         throw new Exception(var11.format(var12));
      } else {
         KeyStore.PasswordProtection var5 = null;

         KeyStore.Entry var6;
         try {
            var6 = var1.getEntry(var2, var5);
            var4 = null;
         } catch (UnrecoverableEntryException var10) {
            if ("PKCS11".equalsIgnoreCase(var1.getType()) || KeyStoreUtil.isWindowsKeyStore(var1.getType())) {
               throw var10;
            }

            if (var4 != null) {
               var5 = new KeyStore.PasswordProtection(var4);
               var6 = var1.getEntry(var2, var5);
            } else {
               try {
                  var5 = new KeyStore.PasswordProtection(var3);
                  var6 = var1.getEntry(var2, var5);
                  var4 = var3;
               } catch (UnrecoverableEntryException var9) {
                  if ("PKCS12".equalsIgnoreCase(var1.getType())) {
                     throw var9;
                  }

                  var4 = this.getKeyPasswd(var2, (String)null, (char[])null);
                  var5 = new KeyStore.PasswordProtection(var4);
                  var6 = var1.getEntry(var2, var5);
               }
            }
         }

         return Pair.of(var6, var4);
      }
   }

   private String getCertFingerPrint(String var1, Certificate var2) throws Exception {
      byte[] var3 = var2.getEncoded();
      MessageDigest var4 = MessageDigest.getInstance(var1);
      byte[] var5 = var4.digest(var3);
      return this.toHexString(var5);
   }

   private void printNoIntegrityWarning() {
      System.err.println();
      System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
      System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.your.keystore."));
      System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
      System.err.println();
   }

   private Certificate[] validateReply(String var1, Certificate var2, Certificate[] var3) throws Exception {
      this.checkWeak(rb.getString("reply"), var3);
      PublicKey var5 = var2.getPublicKey();

      int var4;
      for(var4 = 0; var4 < var3.length && !var5.equals(var3[var4].getPublicKey()); ++var4) {
      }

      if (var4 == var3.length) {
         MessageFormat var12 = new MessageFormat(rb.getString("Certificate.reply.does.not.contain.public.key.for.alias."));
         Object[] var13 = new Object[]{var1};
         throw new Exception(var12.format(var13));
      } else {
         Certificate var6 = var3[0];
         var3[0] = var3[var4];
         var3[var4] = var6;
         X509Certificate var7 = (X509Certificate)var3[0];

         for(var4 = 1; var4 < var3.length - 1; ++var4) {
            int var8;
            for(var8 = var4; var8 < var3.length; ++var8) {
               if (this.signedBy(var7, (X509Certificate)var3[var8])) {
                  var6 = var3[var4];
                  var3[var4] = var3[var8];
                  var3[var8] = var6;
                  var7 = (X509Certificate)var3[var4];
                  break;
               }
            }

            if (var8 == var3.length) {
               throw new Exception(rb.getString("Incomplete.certificate.chain.in.reply"));
            }
         }

         if (this.noprompt) {
            return var3;
         } else {
            Certificate var14 = var3[var3.length - 1];
            boolean var9 = true;
            Pair var10 = getSigner(var14, this.keyStore);
            if (var10 == null && this.trustcacerts && this.caks != null) {
               var10 = getSigner(var14, this.caks);
               var9 = false;
            }

            if (var10 == null) {
               System.err.println();
               System.err.println(rb.getString("Top.level.certificate.in.reply."));
               this.printX509Cert((X509Certificate)var14, System.out);
               System.err.println();
               System.err.print(rb.getString(".is.not.trusted."));
               this.printWeakWarnings(true);
               String var11 = this.getYesNoReply(rb.getString("Install.reply.anyway.no."));
               if ("NO".equals(var11)) {
                  return null;
               }
            } else if (var10.snd != var14) {
               Certificate[] var15 = new Certificate[var3.length + 1];
               System.arraycopy(var3, 0, var15, 0, var3.length);
               var15[var15.length - 1] = (Certificate)var10.snd;
               var3 = var15;
               this.checkWeak(String.format(rb.getString(var9 ? "alias.in.keystore" : "alias.in.cacerts"), var10.fst), (Certificate)var10.snd);
            }

            return var3;
         }
      }
   }

   private Certificate[] establishCertChain(Certificate var1, Certificate var2) throws Exception {
      if (var1 != null) {
         PublicKey var3 = var1.getPublicKey();
         PublicKey var4 = var2.getPublicKey();
         if (!var3.equals(var4)) {
            throw new Exception(rb.getString("Public.keys.in.reply.and.keystore.don.t.match"));
         }

         if (var2.equals(var1)) {
            throw new Exception(rb.getString("Certificate.reply.and.certificate.in.keystore.are.identical"));
         }
      }

      Hashtable var8 = null;
      if (this.keyStore.size() > 0) {
         var8 = new Hashtable(11);
         this.keystorecerts2Hashtable(this.keyStore, var8);
      }

      if (this.trustcacerts && this.caks != null && this.caks.size() > 0) {
         if (var8 == null) {
            var8 = new Hashtable(11);
         }

         this.keystorecerts2Hashtable(this.caks, var8);
      }

      Vector var9 = new Vector(2);
      if (!this.buildChain(new Pair(rb.getString("the.input"), (X509Certificate)var2), var9, var8)) {
         throw new Exception(rb.getString("Failed.to.establish.chain.from.reply"));
      } else {
         Iterator var5 = var9.iterator();

         while(var5.hasNext()) {
            Pair var6 = (Pair)var5.next();
            this.checkWeak((String)var6.fst, (Certificate)var6.snd);
         }

         Certificate[] var10 = new Certificate[var9.size()];
         int var11 = 0;

         for(int var7 = var9.size() - 1; var7 >= 0; --var7) {
            var10[var11] = (Certificate)((Pair)var9.elementAt(var7)).snd;
            ++var11;
         }

         return var10;
      }
   }

   private boolean buildChain(Pair<String, X509Certificate> var1, Vector<Pair<String, X509Certificate>> var2, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> var3) {
      if (this.isSelfSigned((X509Certificate)var1.snd)) {
         var2.addElement(var1);
         return true;
      } else {
         Principal var4 = ((X509Certificate)var1.snd).getIssuerDN();
         Vector var5 = (Vector)var3.get(var4);
         if (var5 == null) {
            return false;
         } else {
            Enumeration var6 = var5.elements();

            while(var6.hasMoreElements()) {
               Pair var7 = (Pair)var6.nextElement();
               PublicKey var8 = ((X509Certificate)var7.snd).getPublicKey();

               try {
                  ((X509Certificate)var1.snd).verify(var8);
               } catch (Exception var10) {
                  continue;
               }

               if (this.buildChain(var7, var2, var3)) {
                  var2.addElement(var1);
                  return true;
               }
            }

            return false;
         }
      }
   }

   private String getYesNoReply(String var1) throws IOException {
      String var2 = null;
      int var3 = 20;

      do {
         if (var3-- < 0) {
            throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
         }

         System.err.print(var1);
         System.err.flush();
         var2 = (new BufferedReader(new InputStreamReader(System.in))).readLine();
         if (collator.compare(var2, "") != 0 && collator.compare(var2, rb.getString("n")) != 0 && collator.compare(var2, rb.getString("no")) != 0) {
            if (collator.compare(var2, rb.getString("y")) != 0 && collator.compare(var2, rb.getString("yes")) != 0) {
               System.err.println(rb.getString("Wrong.answer.try.again"));
               var2 = null;
            } else {
               var2 = "YES";
            }
         } else {
            var2 = "NO";
         }
      } while(var2 == null);

      return var2;
   }

   private void keystorecerts2Hashtable(KeyStore var1, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> var2) throws Exception {
      Enumeration var3 = var1.aliases();

      while(var3.hasMoreElements()) {
         String var4 = (String)var3.nextElement();
         Certificate var5 = var1.getCertificate(var4);
         if (var5 != null) {
            Principal var6 = ((X509Certificate)var5).getSubjectDN();
            Pair var7 = new Pair(String.format(rb.getString(var1 == this.caks ? "alias.in.cacerts" : "alias.in.keystore"), var4), (X509Certificate)var5);
            Vector var8 = (Vector)var2.get(var6);
            if (var8 == null) {
               var8 = new Vector();
               var8.addElement(var7);
            } else if (!var8.contains(var7)) {
               var8.addElement(var7);
            }

            var2.put(var6, var8);
         }
      }

   }

   private static Date getStartDate(String var0) throws IOException {
      GregorianCalendar var1 = new GregorianCalendar();
      if (var0 != null) {
         IOException var2 = new IOException(rb.getString("Illegal.startdate.value"));
         int var3 = var0.length();
         if (var3 == 0) {
            throw var2;
         }

         int var6;
         if (var0.charAt(0) != '-' && var0.charAt(0) != '+') {
            String var9 = null;
            String var11 = null;
            if (var3 == 19) {
               var9 = var0.substring(0, 10);
               var11 = var0.substring(11);
               if (var0.charAt(10) != ' ') {
                  throw var2;
               }
            } else if (var3 == 10) {
               var9 = var0;
            } else {
               if (var3 != 8) {
                  throw var2;
               }

               var11 = var0;
            }

            if (var9 != null) {
               if (!var9.matches("\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d")) {
                  throw var2;
               }

               var1.set(Integer.valueOf(var9.substring(0, 4)), Integer.valueOf(var9.substring(5, 7)) - 1, Integer.valueOf(var9.substring(8, 10)));
            }

            if (var11 != null) {
               if (!var11.matches("\\d\\d:\\d\\d:\\d\\d")) {
                  throw var2;
               }

               var1.set(11, Integer.valueOf(var11.substring(0, 2)));
               var1.set(12, Integer.valueOf(var11.substring(0, 2)));
               var1.set(13, Integer.valueOf(var11.substring(0, 2)));
               var1.set(14, 0);
            }
         } else {
            for(int var4 = 0; var4 < var3; var4 = var6 + 1) {
               boolean var5 = false;
               byte var10;
               switch(var0.charAt(var4)) {
               case '+':
                  var10 = 1;
                  break;
               case '-':
                  var10 = -1;
                  break;
               default:
                  throw var2;
               }

               for(var6 = var4 + 1; var6 < var3; ++var6) {
                  char var7 = var0.charAt(var6);
                  if (var7 < '0' || var7 > '9') {
                     break;
                  }
               }

               if (var6 == var4 + 1) {
                  throw var2;
               }

               int var12 = Integer.parseInt(var0.substring(var4 + 1, var6));
               if (var6 >= var3) {
                  throw var2;
               }

               boolean var8 = false;
               byte var13;
               switch(var0.charAt(var6)) {
               case 'H':
                  var13 = 10;
                  break;
               case 'M':
                  var13 = 12;
                  break;
               case 'S':
                  var13 = 13;
                  break;
               case 'd':
                  var13 = 5;
                  break;
               case 'm':
                  var13 = 2;
                  break;
               case 'y':
                  var13 = 1;
                  break;
               default:
                  throw var2;
               }

               var1.add(var13, var10 * var12);
            }
         }
      }

      return var1.getTime();
   }

   private static int oneOf(String var0, String... var1) throws Exception {
      int[] var2 = new int[var1.length];
      int var3 = 0;
      int var4 = Integer.MAX_VALUE;

      for(int var5 = 0; var5 < var1.length; ++var5) {
         String var6 = var1[var5];
         if (var6 == null) {
            var4 = var5;
         } else if (var6.toLowerCase(Locale.ENGLISH).startsWith(var0.toLowerCase(Locale.ENGLISH))) {
            var2[var3++] = var5;
         } else {
            StringBuffer var7 = new StringBuffer();
            boolean var8 = true;
            char[] var9 = var6.toCharArray();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               char var12 = var9[var11];
               if (var8) {
                  var7.append(var12);
                  var8 = false;
               } else if (!Character.isLowerCase(var12)) {
                  var7.append(var12);
               }
            }

            if (var7.toString().equalsIgnoreCase(var0)) {
               var2[var3++] = var5;
            }
         }
      }

      if (var3 == 0) {
         return -1;
      } else if (var3 == 1) {
         return var2[0];
      } else if (var2[1] > var4) {
         return var2[0];
      } else {
         StringBuffer var13 = new StringBuffer();
         MessageFormat var14 = new MessageFormat(rb.getString("command.{0}.is.ambiguous."));
         Object[] var15 = new Object[]{var0};
         var13.append(var14.format(var15));
         var13.append("\n    ");

         for(int var16 = 0; var16 < var3 && var2[var16] < var4; ++var16) {
            var13.append(' ');
            var13.append(var1[var2[var16]]);
         }

         throw new Exception(var13.toString());
      }
   }

   private GeneralName createGeneralName(String var1, String var2) throws Exception {
      int var4 = oneOf(var1, "EMAIL", "URI", "DNS", "IP", "OID");
      if (var4 < 0) {
         throw new Exception(rb.getString("Unrecognized.GeneralName.type.") + var1);
      } else {
         Object var3;
         switch(var4) {
         case 0:
            var3 = new RFC822Name(var2);
            break;
         case 1:
            var3 = new URIName(var2);
            break;
         case 2:
            var3 = new DNSName(var2);
            break;
         case 3:
            var3 = new IPAddressName(var2);
            break;
         default:
            var3 = new OIDName(var2);
         }

         return new GeneralName((GeneralNameInterface)var3);
      }
   }

   private ObjectIdentifier findOidForExtName(String var1) throws Exception {
      switch(oneOf(var1, extSupported)) {
      case 0:
         return PKIXExtensions.BasicConstraints_Id;
      case 1:
         return PKIXExtensions.KeyUsage_Id;
      case 2:
         return PKIXExtensions.ExtendedKeyUsage_Id;
      case 3:
         return PKIXExtensions.SubjectAlternativeName_Id;
      case 4:
         return PKIXExtensions.IssuerAlternativeName_Id;
      case 5:
         return PKIXExtensions.SubjectInfoAccess_Id;
      case 6:
         return PKIXExtensions.AuthInfoAccess_Id;
      case 7:
      default:
         return new ObjectIdentifier(var1);
      case 8:
         return PKIXExtensions.CRLDistributionPoints_Id;
      }
   }

   private CertificateExtensions createV3Extensions(CertificateExtensions var1, CertificateExtensions var2, List<String> var3, PublicKey var4, PublicKey var5) throws Exception {
      if (var2 != null && var1 != null) {
         throw new Exception("One of request and original should be null.");
      } else {
         if (var2 == null) {
            var2 = new CertificateExtensions();
         }

         try {
            Iterator var6;
            String var7;
            int var12;
            int var14;
            if (var1 != null) {
               var6 = var3.iterator();

               label324:
               while(var6.hasNext()) {
                  var7 = (String)var6.next();
                  if (var7.toLowerCase(Locale.ENGLISH).startsWith("honored=")) {
                     List var8 = Arrays.asList(var7.toLowerCase(Locale.ENGLISH).substring(8).split(","));
                     if (var8.contains("all")) {
                        var2 = var1;
                     }

                     Iterator var9 = var8.iterator();

                     while(true) {
                        Extension var15;
                        String var38;
                        do {
                           while(true) {
                              String var10;
                              do {
                                 if (!var9.hasNext()) {
                                    break label324;
                                 }

                                 var10 = (String)var9.next();
                              } while(var10.equals("all"));

                              boolean var11 = true;
                              var12 = -1;
                              String var13 = null;
                              if (var10.startsWith("-")) {
                                 var11 = false;
                                 var13 = var10.substring(1);
                              } else {
                                 var14 = var10.indexOf(58);
                                 if (var14 >= 0) {
                                    var13 = var10.substring(0, var14);
                                    var12 = oneOf(var10.substring(var14 + 1), "critical", "non-critical");
                                    if (var12 == -1) {
                                       throw new Exception(rb.getString("Illegal.value.") + var10);
                                    }
                                 }
                              }

                              var38 = var1.getNameByOid(this.findOidForExtName(var13));
                              if (var11) {
                                 var15 = var1.get(var38);
                                 break;
                              }

                              var2.delete(var38);
                           }
                        } while((var15.isCritical() || var12 != 0) && (!var15.isCritical() || var12 != 1));

                        var15 = Extension.newExtension(var15.getExtensionId(), !var15.isCritical(), var15.getExtensionValue());
                        var2.set(var38, var15);
                     }
                  }
               }
            }

            var6 = var3.iterator();

            while(true) {
               String var33;
               String var34;
               boolean var35;
               do {
                  if (!var6.hasNext()) {
                     var2.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension((new KeyIdentifier(var4)).getIdentifier()));
                     if (var5 != null && !var4.equals(var5)) {
                        var2.set("AuthorityKeyIdentifier", new AuthorityKeyIdentifierExtension(new KeyIdentifier(var5), (GeneralNames)null, (SerialNumber)null));
                     }

                     return var2;
                  }

                  var7 = (String)var6.next();
                  var35 = false;
                  int var36 = var7.indexOf(61);
                  if (var36 >= 0) {
                     var33 = var7.substring(0, var36);
                     var34 = var7.substring(var36 + 1);
                  } else {
                     var33 = var7;
                     var34 = null;
                  }

                  var12 = var33.indexOf(58);
                  if (var12 >= 0) {
                     if (oneOf(var33.substring(var12 + 1), "critical") == 0) {
                        var35 = true;
                     }

                     var33 = var33.substring(0, var12);
                  }
               } while(var33.equalsIgnoreCase("honored"));

               int var37 = oneOf(var33, extSupported);
               String[] var16;
               GeneralNames var17;
               String[] var18;
               int var19;
               int var20;
               String var21;
               String var22;
               String var23;
               String[] var43;
               int var46;
               String var49;
               int var50;
               switch(var37) {
               case -1:
                  ObjectIdentifier var44 = new ObjectIdentifier(var33);
                  var17 = null;
                  byte[] var47;
                  if (var34 != null) {
                     var47 = new byte[var34.length() / 2 + 1];
                     var46 = 0;
                     char[] var48 = var34.toCharArray();
                     var20 = var48.length;

                     for(var50 = 0; var50 < var20; ++var50) {
                        char var52 = var48[var50];
                        int var54;
                        if (var52 >= '0' && var52 <= '9') {
                           var54 = var52 - 48;
                        } else if (var52 >= 'A' && var52 <= 'F') {
                           var54 = var52 - 65 + 10;
                        } else {
                           if (var52 < 'a' || var52 > 'f') {
                              continue;
                           }

                           var54 = var52 - 97 + 10;
                        }

                        if (var46 % 2 == 0) {
                           var47[var46 / 2] = (byte)(var54 << 4);
                        } else {
                           var47[var46 / 2] = (byte)(var47[var46 / 2] + var54);
                        }

                        ++var46;
                     }

                     if (var46 % 2 != 0) {
                        throw new Exception(rb.getString("Odd.number.of.hex.digits.found.") + var7);
                     }

                     var47 = Arrays.copyOf(var47, var46 / 2);
                  } else {
                     var47 = new byte[0];
                  }

                  var2.set(var44.toString(), new Extension(var44, var35, (new DerValue((byte)4, var47)).toByteArray()));
                  break;
               case 0:
                  var14 = -1;
                  boolean var39 = false;
                  if (var34 == null) {
                     var39 = true;
                  } else {
                     try {
                        var14 = Integer.parseInt(var34);
                        var39 = true;
                     } catch (NumberFormatException var31) {
                        var43 = var34.split(",");
                        var46 = var43.length;

                        for(var19 = 0; var19 < var46; ++var19) {
                           var49 = var43[var19];
                           String[] var53 = var49.split(":");
                           if (var53.length != 2) {
                              throw new Exception(rb.getString("Illegal.value.") + var7);
                           }

                           if (var53[0].equalsIgnoreCase("ca")) {
                              var39 = Boolean.parseBoolean(var53[1]);
                           } else {
                              if (!var53[0].equalsIgnoreCase("pathlen")) {
                                 throw new Exception(rb.getString("Illegal.value.") + var7);
                              }

                              var14 = Integer.parseInt(var53[1]);
                           }
                        }
                     }
                  }

                  var2.set("BasicConstraints", new BasicConstraintsExtension(var35, var39, var14));
                  break;
               case 1:
                  if (var34 == null) {
                     throw new Exception(rb.getString("Illegal.value.") + var7);
                  }

                  boolean[] var42 = new boolean[9];
                  var43 = var34.split(",");
                  var46 = var43.length;

                  for(var19 = 0; var19 < var46; ++var19) {
                     var49 = var43[var19];
                     var50 = oneOf(var49, "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly", "contentCommitment");
                     if (var50 < 0) {
                        throw new Exception(rb.getString("Unknown.keyUsage.type.") + var49);
                     }

                     if (var50 == 9) {
                        var50 = 1;
                     }

                     var42[var50] = true;
                  }

                  KeyUsageExtension var45 = new KeyUsageExtension(var42);
                  var2.set("KeyUsage", Extension.newExtension(var45.getExtensionId(), var35, var45.getExtensionValue()));
                  break;
               case 2:
                  if (var34 == null) {
                     throw new Exception(rb.getString("Illegal.value.") + var7);
                  }

                  Vector var41 = new Vector();
                  var43 = var34.split(",");
                  var46 = var43.length;

                  for(var19 = 0; var19 < var46; ++var19) {
                     var49 = var43[var19];
                     var50 = oneOf(var49, "anyExtendedKeyUsage", "serverAuth", "clientAuth", "codeSigning", "emailProtection", "", "", "", "timeStamping", "OCSPSigning");
                     if (var50 < 0) {
                        try {
                           var41.add(new ObjectIdentifier(var49));
                        } catch (Exception var30) {
                           throw new Exception(rb.getString("Unknown.extendedkeyUsage.type.") + var49);
                        }
                     } else if (var50 == 0) {
                        var41.add(new ObjectIdentifier("2.5.29.37.0"));
                     } else {
                        var41.add(new ObjectIdentifier("1.3.6.1.5.5.7.3." + var50));
                     }
                  }

                  var2.set("ExtendedKeyUsage", new ExtendedKeyUsageExtension(var35, var41));
                  break;
               case 3:
               case 4:
                  if (var34 == null) {
                     throw new Exception(rb.getString("Illegal.value.") + var7);
                  }

                  var16 = var34.split(",");
                  var17 = new GeneralNames();
                  var18 = var16;
                  var19 = var16.length;

                  for(var20 = 0; var20 < var19; ++var20) {
                     var21 = var18[var20];
                     var12 = var21.indexOf(58);
                     if (var12 < 0) {
                        throw new Exception("Illegal item " + var21 + " in " + var7);
                     }

                     var22 = var21.substring(0, var12);
                     var23 = var21.substring(var12 + 1);
                     var17.add(this.createGeneralName(var22, var23));
                  }

                  if (var37 == 3) {
                     var2.set("SubjectAlternativeName", new SubjectAlternativeNameExtension(var35, var17));
                  } else {
                     var2.set("IssuerAlternativeName", new IssuerAlternativeNameExtension(var35, var17));
                  }
                  break;
               case 5:
               case 6:
                  if (var35) {
                     throw new Exception(rb.getString("This.extension.cannot.be.marked.as.critical.") + var7);
                  }

                  if (var34 == null) {
                     throw new Exception(rb.getString("Illegal.value.") + var7);
                  }

                  ArrayList var40 = new ArrayList();
                  var43 = var34.split(",");
                  var18 = var43;
                  var19 = var43.length;

                  for(var20 = 0; var20 < var19; ++var20) {
                     var21 = var18[var20];
                     var12 = var21.indexOf(58);
                     int var51 = var21.indexOf(58, var12 + 1);
                     if (var12 < 0 || var51 < 0) {
                        throw new Exception(rb.getString("Illegal.value.") + var7);
                     }

                     var23 = var21.substring(0, var12);
                     String var24 = var21.substring(var12 + 1, var51);
                     String var25 = var21.substring(var51 + 1);
                     int var26 = oneOf(var23, "", "ocsp", "caIssuers", "timeStamping", "", "caRepository");
                     ObjectIdentifier var27;
                     if (var26 < 0) {
                        try {
                           var27 = new ObjectIdentifier(var23);
                        } catch (Exception var29) {
                           throw new Exception(rb.getString("Unknown.AccessDescription.type.") + var23);
                        }
                     } else {
                        var27 = new ObjectIdentifier("1.3.6.1.5.5.7.48." + var26);
                     }

                     var40.add(new AccessDescription(var27, this.createGeneralName(var24, var25)));
                  }

                  if (var37 == 5) {
                     var2.set("SubjectInfoAccess", new SubjectInfoAccessExtension(var40));
                  } else {
                     var2.set("AuthorityInfoAccess", new AuthorityInfoAccessExtension(var40));
                  }
                  break;
               case 7:
               default:
                  throw new Exception(rb.getString("Unknown.extension.type.") + var7);
               case 8:
                  if (var34 == null) {
                     throw new Exception(rb.getString("Illegal.value.") + var7);
                  }

                  var16 = var34.split(",");
                  var17 = new GeneralNames();
                  var18 = var16;
                  var19 = var16.length;

                  for(var20 = 0; var20 < var19; ++var20) {
                     var21 = var18[var20];
                     var12 = var21.indexOf(58);
                     if (var12 < 0) {
                        throw new Exception("Illegal item " + var21 + " in " + var7);
                     }

                     var22 = var21.substring(0, var12);
                     var23 = var21.substring(var12 + 1);
                     var17.add(this.createGeneralName(var22, var23));
                  }

                  var2.set("CRLDistributionPoints", new CRLDistributionPointsExtension(var35, Collections.singletonList(new DistributionPoint(var17, (boolean[])null, (GeneralNames)null))));
               }
            }
         } catch (IOException var32) {
            throw new RuntimeException(var32);
         }
      }
   }

   private boolean isTrustedCert(Certificate var1) throws KeyStoreException {
      if (this.caks != null && this.caks.getCertificateAlias(var1) != null) {
         return true;
      } else {
         String var2 = this.keyStore.getCertificateAlias(var1);
         return var2 != null && this.keyStore.isCertificateEntry(var2);
      }
   }

   private void checkWeak(String var1, String var2, Key var3) {
      if (var2 != null && !DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, var2, (AlgorithmParameters)null)) {
         this.weakWarnings.add(String.format(rb.getString("whose.sigalg.risk"), var1, var2));
      }

      if (var3 != null && !DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, var3)) {
         this.weakWarnings.add(String.format(rb.getString("whose.key.risk"), var1, String.format(rb.getString("key.bit"), KeyUtil.getKeySize(var3), var3.getAlgorithm())));
      }

   }

   private void checkWeak(String var1, Certificate[] var2) throws KeyStoreException {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         Certificate var4 = var2[var3];
         if (var4 instanceof X509Certificate) {
            X509Certificate var5 = (X509Certificate)var4;
            String var6 = var1;
            if (var2.length > 1) {
               var6 = oneInMany(var1, var3, var2.length);
            }

            this.checkWeak(var6, (Certificate)var5);
         }
      }

   }

   private void checkWeak(String var1, Certificate var2) throws KeyStoreException {
      if (var2 instanceof X509Certificate) {
         X509Certificate var3 = (X509Certificate)var2;
         String var4 = this.isTrustedCert(var2) ? null : var3.getSigAlgName();
         this.checkWeak(var1, (String)var4, var3.getPublicKey());
      }

   }

   private void checkWeak(String var1, PKCS10 var2) {
      this.checkWeak(var1, (String)var2.getSigAlg(), var2.getSubjectPublicKeyInfo());
   }

   private void checkWeak(String var1, CRL var2, Key var3) {
      if (var2 instanceof X509CRLImpl) {
         X509CRLImpl var4 = (X509CRLImpl)var2;
         this.checkWeak(var1, var4.getSigAlgName(), var3);
      }

   }

   private void printWeakWarnings(boolean var1) {
      if (!this.weakWarnings.isEmpty() && !this.nowarn) {
         System.err.println("\nWarning:");
         Iterator var2 = this.weakWarnings.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            System.err.println(var3);
         }

         if (var1) {
            System.err.println();
         }
      }

      this.weakWarnings.clear();
   }

   private void usage() {
      if (this.command != null) {
         System.err.println("keytool " + this.command + rb.getString(".OPTION."));
         System.err.println();
         System.err.println(rb.getString(this.command.description));
         System.err.println();
         System.err.println(rb.getString("Options."));
         System.err.println();
         String[] var1 = new String[this.command.options.length];
         String[] var2 = new String[this.command.options.length];
         boolean var3 = false;
         int var4 = 0;

         int var5;
         for(var5 = 0; var5 < var1.length; ++var5) {
            Main.Option var6 = this.command.options[var5];
            var1[var5] = var6.toString();
            if (var6.arg != null) {
               var1[var5] = var1[var5] + " " + var6.arg;
            }

            if (var1[var5].length() > var4) {
               var4 = var1[var5].length();
            }

            var2[var5] = rb.getString(var6.description);
         }

         for(var5 = 0; var5 < var1.length; ++var5) {
            System.err.printf(" %-" + var4 + "s  %s\n", var1[var5], var2[var5]);
         }

         System.err.println();
         System.err.println(rb.getString("Use.keytool.help.for.all.available.commands"));
      } else {
         System.err.println(rb.getString("Key.and.Certificate.Management.Tool"));
         System.err.println();
         System.err.println(rb.getString("Commands."));
         System.err.println();
         Main.Command[] var7 = Main.Command.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Main.Command var10 = var7[var9];
            if (var10 == Main.Command.KEYCLONE) {
               break;
            }

            System.err.printf(" %-20s%s\n", var10, rb.getString(var10.description));
         }

         System.err.println();
         System.err.println(rb.getString("Use.keytool.command.name.help.for.usage.of.command.name"));
      }

   }

   private void tinyHelp() {
      this.usage();
      if (this.debug) {
         throw new RuntimeException("NO BIG ERROR, SORRY");
      } else {
         System.exit(1);
      }
   }

   private void errorNeedArgument(String var1) {
      Object[] var2 = new Object[]{var1};
      System.err.println((new MessageFormat(rb.getString("Command.option.flag.needs.an.argument."))).format(var2));
      this.tinyHelp();
   }

   private char[] getPass(String var1, String var2) {
      char[] var3 = KeyStoreUtil.getPassWithModifier(var1, var2, rb);
      if (var3 != null) {
         return var3;
      } else {
         this.tinyHelp();
         return null;
      }
   }

   static {
      SIG_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
      PARAM_STRING = new Class[]{String.class};
      rb = ResourceBundle.getBundle("sun.security.tools.keytool.Resources");
      collator = Collator.getInstance();
      collator.setStrength(0);
      extSupported = new String[]{"BasicConstraints", "KeyUsage", "ExtendedKeyUsage", "SubjectAlternativeName", "IssuerAlternativeName", "SubjectInfoAccess", "AuthorityInfoAccess", null, "CRLDistributionPoints"};
   }

   static enum Option {
      ALIAS("alias", "<alias>", "alias.name.of.the.entry.to.process"),
      DESTALIAS("destalias", "<destalias>", "destination.alias"),
      DESTKEYPASS("destkeypass", "<arg>", "destination.key.password"),
      DESTKEYSTORE("destkeystore", "<destkeystore>", "destination.keystore.name"),
      DESTPROTECTED("destprotected", (String)null, "destination.keystore.password.protected"),
      DESTPROVIDERNAME("destprovidername", "<destprovidername>", "destination.keystore.provider.name"),
      DESTSTOREPASS("deststorepass", "<arg>", "destination.keystore.password"),
      DESTSTORETYPE("deststoretype", "<deststoretype>", "destination.keystore.type"),
      DNAME("dname", "<dname>", "distinguished.name"),
      EXT("ext", "<value>", "X.509.extension"),
      FILEOUT("file", "<filename>", "output.file.name"),
      FILEIN("file", "<filename>", "input.file.name"),
      ID("id", "<id:reason>", "Serial.ID.of.cert.to.revoke"),
      INFILE("infile", "<filename>", "input.file.name"),
      KEYALG("keyalg", "<keyalg>", "key.algorithm.name"),
      KEYPASS("keypass", "<arg>", "key.password"),
      KEYSIZE("keysize", "<keysize>", "key.bit.size"),
      KEYSTORE("keystore", "<keystore>", "keystore.name"),
      NEW("new", "<arg>", "new.password"),
      NOPROMPT("noprompt", (String)null, "do.not.prompt"),
      OUTFILE("outfile", "<filename>", "output.file.name"),
      PROTECTED("protected", (String)null, "password.through.protected.mechanism"),
      PROVIDERARG("providerarg", "<arg>", "provider.argument"),
      PROVIDERCLASS("providerclass", "<providerclass>", "provider.class.name"),
      PROVIDERNAME("providername", "<providername>", "provider.name"),
      PROVIDERPATH("providerpath", "<pathlist>", "provider.classpath"),
      RFC("rfc", (String)null, "output.in.RFC.style"),
      SIGALG("sigalg", "<sigalg>", "signature.algorithm.name"),
      SRCALIAS("srcalias", "<srcalias>", "source.alias"),
      SRCKEYPASS("srckeypass", "<arg>", "source.key.password"),
      SRCKEYSTORE("srckeystore", "<srckeystore>", "source.keystore.name"),
      SRCPROTECTED("srcprotected", (String)null, "source.keystore.password.protected"),
      SRCPROVIDERNAME("srcprovidername", "<srcprovidername>", "source.keystore.provider.name"),
      SRCSTOREPASS("srcstorepass", "<arg>", "source.keystore.password"),
      SRCSTORETYPE("srcstoretype", "<srcstoretype>", "source.keystore.type"),
      SSLSERVER("sslserver", "<server[:port]>", "SSL.server.host.and.port"),
      JARFILE("jarfile", "<filename>", "signed.jar.file"),
      STARTDATE("startdate", "<startdate>", "certificate.validity.start.date.time"),
      STOREPASS("storepass", "<arg>", "keystore.password"),
      STORETYPE("storetype", "<storetype>", "keystore.type"),
      TRUSTCACERTS("trustcacerts", (String)null, "trust.certificates.from.cacerts"),
      V("v", (String)null, "verbose.output"),
      VALIDITY("validity", "<valDays>", "validity.number.of.days");

      final String name;
      final String arg;
      final String description;

      private Option(String var3, String var4, String var5) {
         this.name = var3;
         this.arg = var4;
         this.description = var5;
      }

      public String toString() {
         return "-" + this.name;
      }
   }

   static enum Command {
      CERTREQ("Generates.a.certificate.request", new Main.Option[]{Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.FILEOUT, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.DNAME, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      CHANGEALIAS("Changes.an.entry.s.alias", new Main.Option[]{Main.Option.ALIAS, Main.Option.DESTALIAS, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      DELETE("Deletes.an.entry", new Main.Option[]{Main.Option.ALIAS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      EXPORTCERT("Exports.certificate", new Main.Option[]{Main.Option.RFC, Main.Option.ALIAS, Main.Option.FILEOUT, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      GENKEYPAIR("Generates.a.key.pair", new Main.Option[]{Main.Option.ALIAS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.SIGALG, Main.Option.DESTALIAS, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.EXT, Main.Option.VALIDITY, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      GENSECKEY("Generates.a.secret.key", new Main.Option[]{Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      GENCERT("Generates.certificate.from.a.certificate.request", new Main.Option[]{Main.Option.RFC, Main.Option.INFILE, Main.Option.OUTFILE, Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.EXT, Main.Option.VALIDITY, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      IMPORTCERT("Imports.a.certificate.or.a.certificate.chain", new Main.Option[]{Main.Option.NOPROMPT, Main.Option.TRUSTCACERTS, Main.Option.PROTECTED, Main.Option.ALIAS, Main.Option.FILEIN, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V}),
      IMPORTPASS("Imports.a.password", new Main.Option[]{Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      IMPORTKEYSTORE("Imports.one.or.all.entries.from.another.keystore", new Main.Option[]{Main.Option.SRCKEYSTORE, Main.Option.DESTKEYSTORE, Main.Option.SRCSTORETYPE, Main.Option.DESTSTORETYPE, Main.Option.SRCSTOREPASS, Main.Option.DESTSTOREPASS, Main.Option.SRCPROTECTED, Main.Option.SRCPROVIDERNAME, Main.Option.DESTPROVIDERNAME, Main.Option.SRCALIAS, Main.Option.DESTALIAS, Main.Option.SRCKEYPASS, Main.Option.DESTKEYPASS, Main.Option.NOPROMPT, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V}),
      KEYPASSWD("Changes.the.key.password.of.an.entry", new Main.Option[]{Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.NEW, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V}),
      LIST("Lists.entries.in.a.keystore", new Main.Option[]{Main.Option.RFC, Main.Option.ALIAS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      PRINTCERT("Prints.the.content.of.a.certificate", new Main.Option[]{Main.Option.RFC, Main.Option.FILEIN, Main.Option.SSLSERVER, Main.Option.JARFILE, Main.Option.V}),
      PRINTCERTREQ("Prints.the.content.of.a.certificate.request", new Main.Option[]{Main.Option.FILEIN, Main.Option.V}),
      PRINTCRL("Prints.the.content.of.a.CRL.file", new Main.Option[]{Main.Option.FILEIN, Main.Option.V}),
      STOREPASSWD("Changes.the.store.password.of.a.keystore", new Main.Option[]{Main.Option.NEW, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V}),
      KEYCLONE("Clones.a.key.entry", new Main.Option[]{Main.Option.ALIAS, Main.Option.DESTALIAS, Main.Option.KEYPASS, Main.Option.NEW, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V}),
      SELFCERT("Generates.a.self.signed.certificate", new Main.Option[]{Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.VALIDITY, Main.Option.KEYPASS, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V}),
      GENCRL("Generates.CRL", new Main.Option[]{Main.Option.RFC, Main.Option.FILEOUT, Main.Option.ID, Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.EXT, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED}),
      IDENTITYDB("Imports.entries.from.a.JDK.1.1.x.style.identity.database", new Main.Option[]{Main.Option.FILEIN, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V});

      final String description;
      final Main.Option[] options;

      private Command(String var3, Main.Option... var4) {
         this.description = var3;
         this.options = var4;
      }

      public String toString() {
         return "-" + this.name().toLowerCase(Locale.ENGLISH);
      }
   }
}
