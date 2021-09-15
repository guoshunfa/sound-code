package com.sun.jndi.ldap;

import com.sun.jndi.ldap.ext.StartTlsResponseImpl;
import com.sun.jndi.toolkit.ctx.ComponentDirContext;
import com.sun.jndi.toolkit.ctx.Continuation;
import com.sun.jndi.toolkit.dir.HierMemDirCtx;
import com.sun.jndi.toolkit.dir.SearchFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.LimitExceededException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;
import javax.naming.PartialResultException;
import javax.naming.ServiceUnavailableException;
import javax.naming.SizeLimitExceededException;
import javax.naming.TimeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.event.EventDirContext;
import javax.naming.event.NamingListener;
import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.UnsolicitedNotificationListener;
import javax.naming.spi.DirectoryManager;

public final class LdapCtx extends ComponentDirContext implements EventDirContext, LdapContext {
   private static final boolean debug = false;
   private static final boolean HARD_CLOSE = true;
   private static final boolean SOFT_CLOSE = false;
   public static final int DEFAULT_PORT = 389;
   public static final int DEFAULT_SSL_PORT = 636;
   public static final String DEFAULT_HOST = "localhost";
   private static final boolean DEFAULT_DELETE_RDN = true;
   private static final boolean DEFAULT_TYPES_ONLY = false;
   private static final int DEFAULT_DEREF_ALIASES = 3;
   private static final int DEFAULT_LDAP_VERSION = 32;
   private static final int DEFAULT_BATCH_SIZE = 1;
   private static final int DEFAULT_REFERRAL_MODE = 3;
   private static final char DEFAULT_REF_SEPARATOR = '#';
   static final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
   private static final int DEFAULT_REFERRAL_LIMIT = 10;
   private static final String STARTTLS_REQ_OID = "1.3.6.1.4.1.1466.20037";
   private static final String[] SCHEMA_ATTRIBUTES = new String[]{"objectClasses", "attributeTypes", "matchingRules", "ldapSyntaxes"};
   private static final String VERSION = "java.naming.ldap.version";
   private static final String BINARY_ATTRIBUTES = "java.naming.ldap.attributes.binary";
   private static final String DELETE_RDN = "java.naming.ldap.deleteRDN";
   private static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
   private static final String TYPES_ONLY = "java.naming.ldap.typesOnly";
   private static final String REF_SEPARATOR = "java.naming.ldap.ref.separator";
   private static final String SOCKET_FACTORY = "java.naming.ldap.factory.socket";
   static final String BIND_CONTROLS = "java.naming.ldap.control.connect";
   private static final String REFERRAL_LIMIT = "java.naming.ldap.referral.limit";
   private static final String TRACE_BER = "com.sun.jndi.ldap.trace.ber";
   private static final String NETSCAPE_SCHEMA_BUG = "com.sun.jndi.ldap.netscape.schemaBugs";
   private static final String OLD_NETSCAPE_SCHEMA_BUG = "com.sun.naming.netscape.schemaBugs";
   private static final String CONNECT_TIMEOUT = "com.sun.jndi.ldap.connect.timeout";
   private static final String READ_TIMEOUT = "com.sun.jndi.ldap.read.timeout";
   private static final String ENABLE_POOL = "com.sun.jndi.ldap.connect.pool";
   private static final String DOMAIN_NAME = "com.sun.jndi.ldap.domainname";
   private static final String WAIT_FOR_REPLY = "com.sun.jndi.ldap.search.waitForReply";
   private static final String REPLY_QUEUE_SIZE = "com.sun.jndi.ldap.search.replyQueueSize";
   private static final NameParser parser = new LdapNameParser();
   private static final ControlFactory myResponseControlFactory = new DefaultResponseControlFactory();
   private static final Control manageReferralControl = new ManageReferralControl(false);
   private static final HierMemDirCtx EMPTY_SCHEMA = new HierMemDirCtx();
   int port_number;
   String hostname = null;
   LdapClient clnt = null;
   Hashtable<String, Object> envprops = null;
   int handleReferrals = 3;
   boolean hasLdapsScheme = false;
   String currentDN;
   Name currentParsedDN;
   Vector<Control> respCtls = null;
   Control[] reqCtls = null;
   private OutputStream trace = null;
   private boolean netscapeSchemaBug = false;
   private Control[] bindCtls = null;
   private int referralHopLimit = 10;
   private Hashtable<String, DirContext> schemaTrees = null;
   private int batchSize = 1;
   private boolean deleteRDN = true;
   private boolean typesOnly = false;
   private int derefAliases = 3;
   private char addrEncodingSeparator = '#';
   private Hashtable<String, Boolean> binaryAttrs = null;
   private int connectTimeout = -1;
   private int readTimeout = -1;
   private boolean waitForReply = true;
   private int replyQueueSize = -1;
   private boolean useSsl = false;
   private boolean useDefaultPortNumber = false;
   private boolean parentIsLdapCtx = false;
   private int hopCount = 1;
   private String url = null;
   private EventSupport eventSupport;
   private boolean unsolicited = false;
   private boolean sharable = true;
   private int enumCount = 0;
   private boolean closeRequested = false;

   public LdapCtx(String var1, String var2, int var3, Hashtable<?, ?> var4, boolean var5) throws NamingException {
      this.useSsl = this.hasLdapsScheme = var5;
      if (var4 != null) {
         this.envprops = (Hashtable)var4.clone();
         if ("ssl".equals(this.envprops.get("java.naming.security.protocol"))) {
            this.useSsl = true;
         }

         this.trace = (OutputStream)this.envprops.get("com.sun.jndi.ldap.trace.ber");
         if (var4.get("com.sun.jndi.ldap.netscape.schemaBugs") != null || var4.get("com.sun.naming.netscape.schemaBugs") != null) {
            this.netscapeSchemaBug = true;
         }
      }

      this.currentDN = var1 != null ? var1 : "";
      this.currentParsedDN = parser.parse(this.currentDN);
      this.hostname = var2 != null && var2.length() > 0 ? var2 : "localhost";
      if (this.hostname.charAt(0) == '[') {
         this.hostname = this.hostname.substring(1, this.hostname.length() - 1);
      }

      if (var3 > 0) {
         this.port_number = var3;
      } else {
         this.port_number = this.useSsl ? 636 : 389;
         this.useDefaultPortNumber = true;
      }

      this.schemaTrees = new Hashtable(11, 0.75F);
      this.initEnv();

      try {
         this.connect(false);
      } catch (NamingException var9) {
         try {
            this.close();
         } catch (Exception var8) {
         }

         throw var9;
      }
   }

   LdapCtx(LdapCtx var1, String var2) throws NamingException {
      this.useSsl = var1.useSsl;
      this.hasLdapsScheme = var1.hasLdapsScheme;
      this.useDefaultPortNumber = var1.useDefaultPortNumber;
      this.hostname = var1.hostname;
      this.port_number = var1.port_number;
      this.currentDN = var2;
      if (var1.currentDN == this.currentDN) {
         this.currentParsedDN = var1.currentParsedDN;
      } else {
         this.currentParsedDN = parser.parse(this.currentDN);
      }

      this.envprops = var1.envprops;
      this.schemaTrees = var1.schemaTrees;
      this.clnt = var1.clnt;
      this.clnt.incRefCount();
      this.parentIsLdapCtx = var2 != null && !var2.equals(var1.currentDN) ? true : var1.parentIsLdapCtx;
      this.trace = var1.trace;
      this.netscapeSchemaBug = var1.netscapeSchemaBug;
      this.initEnv();
   }

   public LdapContext newInstance(Control[] var1) throws NamingException {
      LdapCtx var2 = new LdapCtx(this, this.currentDN);
      var2.setRequestControls(var1);
      return var2;
   }

   protected void c_bind(Name var1, Object var2, Continuation var3) throws NamingException {
      this.c_bind(var1, var2, (Attributes)null, var3);
   }

   protected void c_bind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      var4.setError(this, (Name)var1);
      Attributes var5 = var3;

      try {
         this.ensureOpen();
         if (var2 == null) {
            if (var3 == null) {
               throw new IllegalArgumentException("cannot bind null object with no attributes");
            }
         } else {
            var3 = Obj.determineBindAttrs(this.addrEncodingSeparator, var2, var3, false, var1, this, this.envprops);
         }

         String var20 = this.fullyQualifiedName(var1);
         var3 = addRdnAttributes(var20, var3, var5 != var3);
         LdapEntry var22 = new LdapEntry(var20, var3);
         LdapResult var8 = this.clnt.add(var22, this.reqCtls);
         this.respCtls = var8.resControls;
         if (var8.status != 0) {
            this.processReturnCode(var8, var1);
         }

      } catch (LdapReferralException var17) {
         LdapReferralException var6 = var17;
         if (this.handleReferrals == 2) {
            throw var4.fillInException(var17);
         } else {
            while(true) {
               LdapReferralContext var21 = (LdapReferralContext)var6.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var21.bind(var1, var2, var5);
                  return;
               } catch (LdapReferralException var15) {
                  var6 = var15;
               } finally {
                  var21.close();
               }
            }
         }
      } catch (IOException var18) {
         CommunicationException var7 = new CommunicationException(var18.getMessage());
         var7.setRootCause(var18);
         throw var4.fillInException(var7);
      } catch (NamingException var19) {
         throw var4.fillInException(var19);
      }
   }

   protected void c_rebind(Name var1, Object var2, Continuation var3) throws NamingException {
      this.c_rebind(var1, var2, (Attributes)null, var3);
   }

   protected void c_rebind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      var4.setError(this, (Name)var1);
      Attributes var5 = var3;

      try {
         Attributes var27 = null;

         try {
            var27 = this.c_getAttributes(var1, (String[])null, var4);
         } catch (NameNotFoundException var21) {
         }

         if (var27 == null) {
            this.c_bind(var1, var2, var3, var4);
         } else {
            if (var3 == null && var2 instanceof DirContext) {
               var3 = ((DirContext)var2).getAttributes("");
            }

            Attributes var29 = (Attributes)var27.clone();
            if (var3 == null) {
               Attribute var8 = var27.get(Obj.JAVA_ATTRIBUTES[0]);
               int var9;
               if (var8 != null) {
                  var8 = (Attribute)var8.clone();

                  for(var9 = 0; var9 < Obj.JAVA_OBJECT_CLASSES.length; ++var9) {
                     var8.remove(Obj.JAVA_OBJECT_CLASSES_LOWER[var9]);
                     var8.remove(Obj.JAVA_OBJECT_CLASSES[var9]);
                  }

                  var27.put(var8);
               }

               for(var9 = 1; var9 < Obj.JAVA_ATTRIBUTES.length; ++var9) {
                  var27.remove(Obj.JAVA_ATTRIBUTES[var9]);
               }

               var3 = var27;
            }

            if (var2 != null) {
               var3 = Obj.determineBindAttrs(this.addrEncodingSeparator, var2, var3, var5 != var3, var1, this, this.envprops);
            }

            String var30 = this.fullyQualifiedName(var1);
            LdapResult var31 = this.clnt.delete(var30, this.reqCtls);
            this.respCtls = var31.resControls;
            if (var31.status != 0) {
               this.processReturnCode(var31, var1);
            } else {
               NamingException var10 = null;

               try {
                  var3 = addRdnAttributes(var30, var3, var5 != var3);
                  LdapEntry var11 = new LdapEntry(var30, var3);
                  var31 = this.clnt.add(var11, this.reqCtls);
                  if (var31.resControls != null) {
                     this.respCtls = appendVector(this.respCtls, var31.resControls);
                  }
               } catch (IOException | NamingException var20) {
                  var10 = var20;
               }

               if (var10 != null && !(var10 instanceof LdapReferralException) || var31.status != 0) {
                  LdapResult var32 = this.clnt.add(new LdapEntry(var30, var29), this.reqCtls);
                  if (var32.resControls != null) {
                     this.respCtls = appendVector(this.respCtls, var32.resControls);
                  }

                  if (var10 == null) {
                     this.processReturnCode(var31, var1);
                  }
               }

               if (var10 instanceof NamingException) {
                  throw (NamingException)var10;
               } else if (var10 instanceof IOException) {
                  throw (IOException)var10;
               }
            }
         }
      } catch (LdapReferralException var24) {
         LdapReferralException var6 = var24;
         if (this.handleReferrals == 2) {
            throw var4.fillInException(var24);
         } else {
            while(true) {
               LdapReferralContext var28 = (LdapReferralContext)var6.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var28.rebind(var1, var2, var5);
                  return;
               } catch (LdapReferralException var22) {
                  var6 = var22;
               } finally {
                  var28.close();
               }
            }
         }
      } catch (IOException var25) {
         CommunicationException var7 = new CommunicationException(var25.getMessage());
         var7.setRootCause(var25);
         throw var4.fillInException(var7);
      } catch (NamingException var26) {
         throw var4.fillInException(var26);
      }
   }

   protected void c_unbind(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);

      try {
         this.ensureOpen();
         String var17 = this.fullyQualifiedName(var1);
         LdapResult var19 = this.clnt.delete(var17, this.reqCtls);
         this.respCtls = var19.resControls;
         this.adjustDeleteStatus(var17, var19);
         if (var19.status != 0) {
            this.processReturnCode(var19, var1);
         }

      } catch (LdapReferralException var14) {
         LdapReferralException var3 = var14;
         if (this.handleReferrals == 2) {
            throw var2.fillInException(var14);
         } else {
            while(true) {
               LdapReferralContext var18 = (LdapReferralContext)var3.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var18.unbind(var1);
                  return;
               } catch (LdapReferralException var12) {
                  var3 = var12;
               } finally {
                  var18.close();
               }
            }
         }
      } catch (IOException var15) {
         CommunicationException var4 = new CommunicationException(var15.getMessage());
         var4.setRootCause(var15);
         throw var2.fillInException(var4);
      } catch (NamingException var16) {
         throw var2.fillInException(var16);
      }
   }

   protected void c_rename(Name var1, Name var2, Continuation var3) throws NamingException {
      String var8 = null;
      String var9 = null;
      var3.setError(this, (Name)var1);

      try {
         this.ensureOpen();
         Name var6;
         if (var1.isEmpty()) {
            var6 = parser.parse("");
         } else {
            Name var4 = parser.parse(var1.get(0));
            var6 = var4.getPrefix(var4.size() - 1);
         }

         Name var5;
         if (var2 instanceof CompositeName) {
            var5 = parser.parse(var2.get(0));
         } else {
            var5 = var2;
         }

         Name var7 = var5.getPrefix(var5.size() - 1);
         if (!var6.equals(var7)) {
            if (!this.clnt.isLdapv3) {
               throw new InvalidNameException("LDAPv2 doesn't support changing the parent as a result of a rename");
            }

            var9 = this.fullyQualifiedName(var7.toString());
         }

         var8 = var5.get(var5.size() - 1);
         LdapResult var24 = this.clnt.moddn(this.fullyQualifiedName(var1), var8, this.deleteRDN, var9, this.reqCtls);
         this.respCtls = var24.resControls;
         if (var24.status != 0) {
            this.processReturnCode(var24, var1);
         }

      } catch (LdapReferralException var21) {
         LdapReferralException var10 = var21;
         var21.setNewRdn(var8);
         if (var9 != null) {
            PartialResultException var26 = new PartialResultException("Cannot continue referral processing when newSuperior is nonempty: " + var9);
            var26.setRootCause(var3.fillInException(var21));
            throw var3.fillInException(var26);
         } else if (this.handleReferrals == 2) {
            throw var3.fillInException(var21);
         } else {
            while(true) {
               LdapReferralContext var25 = (LdapReferralContext)var10.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var25.rename(var1, var2);
                  return;
               } catch (LdapReferralException var19) {
                  var10 = var19;
               } finally {
                  var25.close();
               }
            }
         }
      } catch (IOException var22) {
         CommunicationException var11 = new CommunicationException(var22.getMessage());
         var11.setRootCause(var22);
         throw var3.fillInException(var11);
      } catch (NamingException var23) {
         throw var3.fillInException(var23);
      }
   }

   protected Context c_createSubcontext(Name var1, Continuation var2) throws NamingException {
      return this.c_createSubcontext(var1, (Attributes)null, var2);
   }

   protected DirContext c_createSubcontext(Name var1, Attributes var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);
      Object var4 = var2;

      try {
         this.ensureOpen();
         if (var2 == null) {
            BasicAttribute var20 = new BasicAttribute(Obj.JAVA_ATTRIBUTES[0], Obj.JAVA_OBJECT_CLASSES[0]);
            var20.add("top");
            var2 = new BasicAttributes(true);
            ((Attributes)var2).put(var20);
         }

         String var21 = this.fullyQualifiedName(var1);
         Attributes var19 = addRdnAttributes(var21, (Attributes)var2, var4 != var2);
         LdapEntry var23 = new LdapEntry(var21, var19);
         LdapResult var24 = this.clnt.add(var23, this.reqCtls);
         this.respCtls = var24.resControls;
         if (var24.status != 0) {
            this.processReturnCode(var24, var1);
            return null;
         } else {
            return new LdapCtx(this, var21);
         }
      } catch (LdapReferralException var16) {
         LdapReferralException var5 = var16;
         if (this.handleReferrals == 2) {
            throw var3.fillInException(var16);
         } else {
            while(true) {
               LdapReferralContext var22 = (LdapReferralContext)var5.getReferralContext(this.envprops, this.bindCtls);

               try {
                  DirContext var7 = var22.createSubcontext((Name)var1, (Attributes)var4);
                  return var7;
               } catch (LdapReferralException var14) {
                  var5 = var14;
               } finally {
                  var22.close();
               }
            }
         }
      } catch (IOException var17) {
         CommunicationException var6 = new CommunicationException(var17.getMessage());
         var6.setRootCause(var17);
         throw var3.fillInException(var6);
      } catch (NamingException var18) {
         throw var3.fillInException(var18);
      }
   }

   protected void c_destroySubcontext(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);

      try {
         this.ensureOpen();
         String var17 = this.fullyQualifiedName(var1);
         LdapResult var19 = this.clnt.delete(var17, this.reqCtls);
         this.respCtls = var19.resControls;
         this.adjustDeleteStatus(var17, var19);
         if (var19.status != 0) {
            this.processReturnCode(var19, var1);
         }

      } catch (LdapReferralException var14) {
         LdapReferralException var3 = var14;
         if (this.handleReferrals == 2) {
            throw var2.fillInException(var14);
         } else {
            while(true) {
               LdapReferralContext var18 = (LdapReferralContext)var3.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var18.destroySubcontext(var1);
                  return;
               } catch (LdapReferralException var12) {
                  var3 = var12;
               } finally {
                  var18.close();
               }
            }
         }
      } catch (IOException var15) {
         CommunicationException var4 = new CommunicationException(var15.getMessage());
         var4.setRootCause(var15);
         throw var2.fillInException(var4);
      } catch (NamingException var16) {
         throw var2.fillInException(var16);
      }
   }

   private static Attributes addRdnAttributes(String var0, Attributes var1, boolean var2) throws NamingException {
      if (var0.equals("")) {
         return var1;
      } else {
         List var3 = (new javax.naming.ldap.LdapName(var0)).getRdns();
         Rdn var4 = (Rdn)var3.get(var3.size() - 1);
         Attributes var5 = var4.toAttributes();
         NamingEnumeration var6 = var5.getAll();

         while(true) {
            Attribute var7;
            do {
               do {
                  if (!var6.hasMore()) {
                     return var1;
                  }

                  var7 = (Attribute)var6.next();
               } while(var1.get(var7.getID()) != null);
            } while(!var1.isCaseIgnored() && containsIgnoreCase(var1.getIDs(), var7.getID()));

            if (!var2) {
               var1 = (Attributes)var1.clone();
               var2 = true;
            }

            var1.put(var7);
         }
      }
   }

   private static boolean containsIgnoreCase(NamingEnumeration<String> var0, String var1) throws NamingException {
      while(true) {
         if (var0.hasMore()) {
            String var2 = (String)var0.next();
            if (!var2.equalsIgnoreCase(var1)) {
               continue;
            }

            return true;
         }

         return false;
      }
   }

   private void adjustDeleteStatus(String var1, LdapResult var2) {
      if (var2.status == 32 && var2.matchedDN != null) {
         try {
            Name var3 = parser.parse(var1);
            Name var4 = parser.parse(var2.matchedDN);
            if (var3.size() - var4.size() == 1) {
               var2.status = 0;
            }
         } catch (NamingException var5) {
         }
      }

   }

   private static <T> Vector<T> appendVector(Vector<T> var0, Vector<T> var1) {
      if (var0 == null) {
         var0 = var1;
      } else {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            var0.addElement(var1.elementAt(var2));
         }
      }

      return var0;
   }

   protected Object c_lookupLink(Name var1, Continuation var2) throws NamingException {
      return this.c_lookup(var1, var2);
   }

   protected Object c_lookup(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);
      Object var3 = null;

      Object var4;
      try {
         SearchControls var22 = new SearchControls();
         var22.setSearchScope(0);
         var22.setReturningAttributes((String[])null);
         var22.setReturningObjFlag(true);
         LdapResult var23 = this.doSearchOnce(var1, "(objectClass=*)", var22, true);
         this.respCtls = var23.resControls;
         if (var23.status != 0) {
            this.processReturnCode(var23, var1);
         }

         if (var23.entries != null && var23.entries.size() == 1) {
            LdapEntry var25 = (LdapEntry)var23.entries.elementAt(0);
            var4 = var25.attributes;
            Vector var8 = var25.respCtls;
            if (var8 != null) {
               appendVector(this.respCtls, var8);
            }
         } else {
            var4 = new BasicAttributes(true);
         }

         if (((Attributes)var4).get(Obj.JAVA_ATTRIBUTES[2]) != null) {
            var3 = Obj.decodeObject((Attributes)var4);
         }

         if (var3 == null) {
            var3 = new LdapCtx(this, this.fullyQualifiedName(var1));
         }
      } catch (LdapReferralException var20) {
         LdapReferralException var5 = var20;
         if (this.handleReferrals == 2) {
            throw var2.fillInException(var20);
         }

         while(true) {
            LdapReferralContext var6 = (LdapReferralContext)var5.getReferralContext(this.envprops, this.bindCtls);

            try {
               Object var7 = var6.lookup(var1);
               return var7;
            } catch (LdapReferralException var18) {
               var5 = var18;
            } finally {
               var6.close();
            }
         }
      } catch (NamingException var21) {
         throw var2.fillInException(var21);
      }

      try {
         return DirectoryManager.getObjectInstance(var3, var1, this, this.envprops, (Attributes)var4);
      } catch (NamingException var16) {
         throw var2.fillInException(var16);
      } catch (Exception var17) {
         NamingException var24 = new NamingException("problem generating object using object factory");
         var24.setRootCause(var17);
         throw var2.fillInException(var24);
      }
   }

   protected NamingEnumeration<NameClassPair> c_list(Name var1, Continuation var2) throws NamingException {
      SearchControls var3 = new SearchControls();
      String[] var4 = new String[]{Obj.JAVA_ATTRIBUTES[0], Obj.JAVA_ATTRIBUTES[2]};
      var3.setReturningAttributes(var4);
      var3.setReturningObjFlag(true);
      var2.setError(this, (Name)var1);
      LdapResult var5 = null;

      LdapNamingEnumeration var7;
      try {
         var5 = this.doSearch(var1, "(objectClass=*)", var3, true, true);
         if (var5.status != 0 || var5.referrals != null) {
            this.processReturnCode(var5, var1);
         }

         return new LdapNamingEnumeration(this, var5, var1, var2);
      } catch (LdapReferralException var18) {
         LdapReferralException var6 = var18;
         if (this.handleReferrals == 2) {
            throw var2.fillInException(var18);
         } else {
            while(true) {
               LdapReferralContext var22 = (LdapReferralContext)var6.getReferralContext(this.envprops, this.bindCtls);

               try {
                  NamingEnumeration var8 = var22.list(var1);
                  return var8;
               } catch (LdapReferralException var16) {
                  var6 = var16;
               } finally {
                  var22.close();
               }
            }
         }
      } catch (LimitExceededException var19) {
         var7 = new LdapNamingEnumeration(this, var5, var1, var2);
         var7.setNamingException((LimitExceededException)var2.fillInException(var19));
         return var7;
      } catch (PartialResultException var20) {
         var7 = new LdapNamingEnumeration(this, var5, var1, var2);
         var7.setNamingException((PartialResultException)var2.fillInException(var20));
         return var7;
      } catch (NamingException var21) {
         throw var2.fillInException(var21);
      }
   }

   protected NamingEnumeration<Binding> c_listBindings(Name var1, Continuation var2) throws NamingException {
      SearchControls var3 = new SearchControls();
      var3.setReturningAttributes((String[])null);
      var3.setReturningObjFlag(true);
      var2.setError(this, (Name)var1);
      LdapResult var4 = null;

      LdapBindingEnumeration var6;
      try {
         var4 = this.doSearch(var1, "(objectClass=*)", var3, true, true);
         if (var4.status != 0 || var4.referrals != null) {
            this.processReturnCode(var4, var1);
         }

         return new LdapBindingEnumeration(this, var4, var1, var2);
      } catch (LdapReferralException var17) {
         LdapReferralException var5 = var17;
         if (this.handleReferrals == 2) {
            throw var2.fillInException(var17);
         } else {
            while(true) {
               LdapReferralContext var21 = (LdapReferralContext)var5.getReferralContext(this.envprops, this.bindCtls);

               try {
                  NamingEnumeration var7 = var21.listBindings(var1);
                  return var7;
               } catch (LdapReferralException var15) {
                  var5 = var15;
               } finally {
                  var21.close();
               }
            }
         }
      } catch (LimitExceededException var18) {
         var6 = new LdapBindingEnumeration(this, var4, var1, var2);
         var6.setNamingException(var2.fillInException(var18));
         return var6;
      } catch (PartialResultException var19) {
         var6 = new LdapBindingEnumeration(this, var4, var1, var2);
         var6.setNamingException(var2.fillInException(var19));
         return var6;
      } catch (NamingException var20) {
         throw var2.fillInException(var20);
      }
   }

   protected NameParser c_getNameParser(Name var1, Continuation var2) throws NamingException {
      var2.setSuccess();
      return parser;
   }

   public String getNameInNamespace() {
      return this.currentDN;
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      Name var3;
      if (var1 instanceof javax.naming.ldap.LdapName && var2 instanceof javax.naming.ldap.LdapName) {
         var3 = (Name)((Name)var2.clone());
         var3.addAll(var1);
         return (new CompositeName()).add(var3.toString());
      } else {
         if (!(var1 instanceof CompositeName)) {
            var1 = (new CompositeName()).add(var1.toString());
         }

         if (!(var2 instanceof CompositeName)) {
            var2 = (new CompositeName()).add(var2.toString());
         }

         int var4 = var2.size() - 1;
         if (!var1.isEmpty() && !var2.isEmpty() && !var1.get(0).equals("") && !var2.get(var4).equals("")) {
            var3 = (Name)((Name)var2.clone());
            var3.addAll(var1);
            if (this.parentIsLdapCtx) {
               String var5 = concatNames(var3.get(var4 + 1), var3.get(var4));
               var3.remove(var4 + 1);
               var3.remove(var4);
               var3.add(var4, var5);
            }

            return var3;
         } else {
            return super.composeName(var1, var2);
         }
      }
   }

   private String fullyQualifiedName(Name var1) {
      return var1.isEmpty() ? this.currentDN : this.fullyQualifiedName(var1.get(0));
   }

   private String fullyQualifiedName(String var1) {
      return concatNames(var1, this.currentDN);
   }

   private static String concatNames(String var0, String var1) {
      if (var0 != null && !var0.equals("")) {
         return var1 != null && !var1.equals("") ? var0 + "," + var1 : var0;
      } else {
         return var1;
      }
   }

   protected Attributes c_getAttributes(Name var1, String[] var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);
      SearchControls var4 = new SearchControls();
      var4.setSearchScope(0);
      var4.setReturningAttributes(var2);

      try {
         LdapResult var17 = this.doSearchOnce(var1, "(objectClass=*)", var4, true);
         this.respCtls = var17.resControls;
         if (var17.status != 0) {
            this.processReturnCode(var17, var1);
         }

         if (var17.entries != null && var17.entries.size() == 1) {
            LdapEntry var18 = (LdapEntry)var17.entries.elementAt(0);
            Vector var19 = var18.respCtls;
            if (var19 != null) {
               appendVector(this.respCtls, var19);
            }

            this.setParents(var18.attributes, (Name)var1.clone());
            return var18.attributes;
         } else {
            return new BasicAttributes(true);
         }
      } catch (LdapReferralException var15) {
         LdapReferralException var5 = var15;
         if (this.handleReferrals == 2) {
            throw var3.fillInException(var15);
         } else {
            while(true) {
               LdapReferralContext var6 = (LdapReferralContext)var5.getReferralContext(this.envprops, this.bindCtls);

               try {
                  Attributes var7 = var6.getAttributes(var1, var2);
                  return var7;
               } catch (LdapReferralException var13) {
                  var5 = var13;
               } finally {
                  var6.close();
               }
            }
         }
      } catch (NamingException var16) {
         throw var3.fillInException(var16);
      }
   }

   protected void c_modifyAttributes(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException {
      var4.setError(this, (Name)var1);

      try {
         this.ensureOpen();
         if (var3 != null && var3.size() != 0) {
            String var22 = this.fullyQualifiedName(var1);
            int var24 = convertToLdapModCode(var2);
            int[] var7 = new int[var3.size()];
            Attribute[] var8 = new Attribute[var3.size()];
            NamingEnumeration var9 = var3.getAll();

            for(int var10 = 0; var10 < var7.length && var9.hasMore(); ++var10) {
               var7[var10] = var24;
               var8[var10] = (Attribute)var9.next();
            }

            LdapResult var25 = this.clnt.modify(var22, var7, var8, this.reqCtls);
            this.respCtls = var25.resControls;
            if (var25.status != 0) {
               this.processReturnCode(var25, var1);
            }
         }
      } catch (LdapReferralException var19) {
         LdapReferralException var5 = var19;
         if (this.handleReferrals == 2) {
            throw var4.fillInException(var19);
         } else {
            while(true) {
               LdapReferralContext var23 = (LdapReferralContext)var5.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var23.modifyAttributes(var1, var2, var3);
                  return;
               } catch (LdapReferralException var17) {
                  var5 = var17;
               } finally {
                  var23.close();
               }
            }
         }
      } catch (IOException var20) {
         CommunicationException var6 = new CommunicationException(var20.getMessage());
         var6.setRootCause(var20);
         throw var4.fillInException(var6);
      } catch (NamingException var21) {
         throw var4.fillInException(var21);
      }
   }

   protected void c_modifyAttributes(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);

      try {
         this.ensureOpen();
         if (var2 != null && var2.length != 0) {
            String var20 = this.fullyQualifiedName(var1);
            int[] var22 = new int[var2.length];
            Attribute[] var6 = new Attribute[var2.length];

            for(int var8 = 0; var8 < var22.length; ++var8) {
               ModificationItem var7 = var2[var8];
               var22[var8] = convertToLdapModCode(var7.getModificationOp());
               var6[var8] = var7.getAttribute();
            }

            LdapResult var23 = this.clnt.modify(var20, var22, var6, this.reqCtls);
            this.respCtls = var23.resControls;
            if (var23.status != 0) {
               this.processReturnCode(var23, var1);
            }

         }
      } catch (LdapReferralException var17) {
         LdapReferralException var4 = var17;
         if (this.handleReferrals == 2) {
            throw var3.fillInException(var17);
         } else {
            while(true) {
               LdapReferralContext var21 = (LdapReferralContext)var4.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var21.modifyAttributes(var1, var2);
                  return;
               } catch (LdapReferralException var15) {
                  var4 = var15;
               } finally {
                  var21.close();
               }
            }
         }
      } catch (IOException var18) {
         CommunicationException var5 = new CommunicationException(var18.getMessage());
         var5.setRootCause(var18);
         throw var3.fillInException(var5);
      } catch (NamingException var19) {
         throw var3.fillInException(var19);
      }
   }

   private static int convertToLdapModCode(int var0) {
      switch(var0) {
      case 1:
         return 0;
      case 2:
         return 2;
      case 3:
         return 1;
      default:
         throw new IllegalArgumentException("Invalid modification code");
      }
   }

   protected DirContext c_getSchema(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);

      try {
         return this.getSchemaTree(var1);
      } catch (NamingException var4) {
         throw var2.fillInException(var4);
      }
   }

   protected DirContext c_getSchemaClassDefinition(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);

      try {
         Attribute var3 = this.c_getAttributes(var1, new String[]{"objectclass"}, var2).get("objectclass");
         if (var3 != null && var3.size() != 0) {
            Context var4 = (Context)this.c_getSchema(var1, var2).lookup("ClassDefinition");
            HierMemDirCtx var5 = new HierMemDirCtx();
            NamingEnumeration var8 = var3.getAll();

            while(var8.hasMoreElements()) {
               String var7 = (String)var8.nextElement();
               DirContext var6 = (DirContext)var4.lookup(var7);
               var5.bind((String)var7, var6);
            }

            var5.setReadOnly(new SchemaViolationException("Cannot update schema object"));
            return var5;
         } else {
            return EMPTY_SCHEMA;
         }
      } catch (NamingException var9) {
         throw var2.fillInException(var9);
      }
   }

   private DirContext getSchemaTree(Name var1) throws NamingException {
      String var2 = this.getSchemaEntry(var1, true);
      DirContext var3 = (DirContext)this.schemaTrees.get(var2);
      if (var3 == null) {
         var3 = this.buildSchemaTree(var2);
         this.schemaTrees.put(var2, var3);
      }

      return var3;
   }

   private DirContext buildSchemaTree(String var1) throws NamingException {
      SearchControls var2 = new SearchControls(0, 0L, 0, SCHEMA_ATTRIBUTES, true, false);
      Name var3 = (new CompositeName()).add(var1);
      NamingEnumeration var4 = this.searchAux(var3, "(objectClass=subschema)", var2, false, true, new Continuation());
      if (!var4.hasMore()) {
         throw new OperationNotSupportedException("Cannot get read subschemasubentry: " + var1);
      } else {
         SearchResult var5 = (SearchResult)var4.next();
         var4.close();
         Object var6 = var5.getObject();
         if (!(var6 instanceof LdapCtx)) {
            throw new NamingException("Cannot get schema object as DirContext: " + var1);
         } else {
            return LdapSchemaCtx.createSchemaTree(this.envprops, var1, (LdapCtx)var6, var5.getAttributes(), this.netscapeSchemaBug);
         }
      }
   }

   private String getSchemaEntry(Name var1, boolean var2) throws NamingException {
      SearchControls var3 = new SearchControls(0, 0L, 0, new String[]{"subschemasubentry"}, false, false);

      NamingEnumeration var4;
      try {
         var4 = this.searchAux(var1, "objectclass=*", var3, var2, true, new Continuation());
      } catch (NamingException var7) {
         if (!this.clnt.isLdapv3 && this.currentDN.length() == 0 && var1.isEmpty()) {
            throw new OperationNotSupportedException("Cannot get schema information from server");
         }

         throw var7;
      }

      if (!var4.hasMoreElements()) {
         throw new ConfigurationException("Requesting schema of nonexistent entry: " + var1);
      } else {
         SearchResult var5 = (SearchResult)var4.next();
         var4.close();
         Attribute var6 = var5.getAttributes().get("subschemasubentry");
         if (var6 != null && var6.size() >= 0) {
            return (String)((String)var6.get());
         } else if (this.currentDN.length() == 0 && var1.isEmpty()) {
            throw new OperationNotSupportedException("Cannot read subschemasubentry of root DSE");
         } else {
            return this.getSchemaEntry(new CompositeName(), false);
         }
      }
   }

   void setParents(Attributes var1, Name var2) throws NamingException {
      NamingEnumeration var3 = var1.getAll();

      while(var3.hasMore()) {
         ((LdapAttribute)var3.next()).setParent(this, var2);
      }

   }

   String getURL() {
      if (this.url == null) {
         this.url = LdapURL.toUrlString(this.hostname, this.port_number, this.currentDN, this.hasLdapsScheme);
      }

      return this.url;
   }

   protected NamingEnumeration<SearchResult> c_search(Name var1, Attributes var2, Continuation var3) throws NamingException {
      return this.c_search(var1, (Attributes)var2, (String[])null, var3);
   }

   protected NamingEnumeration<SearchResult> c_search(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException {
      SearchControls var5 = new SearchControls();
      var5.setReturningAttributes(var3);

      String var6;
      try {
         var6 = SearchFilter.format(var2);
      } catch (NamingException var8) {
         var4.setError(this, (Name)var1);
         throw var4.fillInException(var8);
      }

      return this.c_search(var1, var6, var5, var4);
   }

   protected NamingEnumeration<SearchResult> c_search(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException {
      return this.searchAux(var1, var2, cloneSearchControls(var3), true, this.waitForReply, var4);
   }

   protected NamingEnumeration<SearchResult> c_search(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException {
      String var6;
      try {
         var6 = SearchFilter.format(var2, var3);
      } catch (NamingException var8) {
         var5.setError(this, (Name)var1);
         throw var5.fillInException(var8);
      }

      return this.c_search(var1, var6, var4, var5);
   }

   NamingEnumeration<SearchResult> searchAux(Name var1, String var2, SearchControls var3, boolean var4, boolean var5, Continuation var6) throws NamingException {
      LdapResult var7 = null;
      String[] var8 = new String[2];
      if (var3 == null) {
         var3 = new SearchControls();
      }

      String[] var9 = var3.getReturningAttributes();
      if (var3.getReturningObjFlag() && var9 != null) {
         boolean var10 = false;

         for(int var11 = var9.length - 1; var11 >= 0; --var11) {
            if (var9[var11].equals("*")) {
               var10 = true;
               break;
            }
         }

         if (!var10) {
            String[] var30 = new String[var9.length + Obj.JAVA_ATTRIBUTES.length];
            System.arraycopy(var9, 0, var30, 0, var9.length);
            System.arraycopy(Obj.JAVA_ATTRIBUTES, 0, var30, var9.length, Obj.JAVA_ATTRIBUTES.length);
            var3.setReturningAttributes(var30);
         }
      }

      LdapCtx.SearchArgs var29 = new LdapCtx.SearchArgs(var1, var2, var3, var9);
      var6.setError(this, (Name)var1);

      LdapSearchEnumeration var32;
      try {
         if (searchToCompare(var2, var3, var8)) {
            var7 = this.compare(var1, var8[0], var8[1]);
            if (!var7.compareToSearchResult(this.fullyQualifiedName(var1))) {
               this.processReturnCode(var7, var1);
            }
         } else {
            var7 = this.doSearch(var1, var2, var3, var4, var5);
            this.processReturnCode(var7, var1);
         }

         return new LdapSearchEnumeration(this, var7, this.fullyQualifiedName(var1), var29, var6);
      } catch (LdapReferralException var24) {
         LdapReferralException var31 = var24;
         if (this.handleReferrals == 2) {
            throw var6.fillInException(var24);
         } else {
            while(true) {
               LdapReferralContext var33 = (LdapReferralContext)var31.getReferralContext(this.envprops, this.bindCtls);

               try {
                  NamingEnumeration var13 = var33.search(var1, var2, var3);
                  return var13;
               } catch (LdapReferralException var22) {
                  var31 = var22;
               } finally {
                  var33.close();
               }
            }
         }
      } catch (LimitExceededException var25) {
         var32 = new LdapSearchEnumeration(this, var7, this.fullyQualifiedName(var1), var29, var6);
         var32.setNamingException(var25);
         return var32;
      } catch (PartialResultException var26) {
         var32 = new LdapSearchEnumeration(this, var7, this.fullyQualifiedName(var1), var29, var6);
         var32.setNamingException(var26);
         return var32;
      } catch (IOException var27) {
         CommunicationException var12 = new CommunicationException(var27.getMessage());
         var12.setRootCause(var27);
         throw var6.fillInException(var12);
      } catch (NamingException var28) {
         throw var6.fillInException(var28);
      }
   }

   LdapResult getSearchReply(LdapClient var1, LdapResult var2) throws NamingException {
      if (this.clnt != var1) {
         throw new CommunicationException("Context's connection changed; unable to continue enumeration");
      } else {
         try {
            return var1.getSearchReply(this.batchSize, var2, this.binaryAttrs);
         } catch (IOException var5) {
            CommunicationException var4 = new CommunicationException(var5.getMessage());
            var4.setRootCause(var5);
            throw var4;
         }
      }
   }

   private LdapResult doSearchOnce(Name var1, String var2, SearchControls var3, boolean var4) throws NamingException {
      int var5 = this.batchSize;
      this.batchSize = 2;
      LdapResult var6 = this.doSearch(var1, var2, var3, var4, true);
      this.batchSize = var5;
      return var6;
   }

   private LdapResult doSearch(Name var1, String var2, SearchControls var3, boolean var4, boolean var5) throws NamingException {
      this.ensureOpen();

      try {
         byte var6;
         switch(var3.getSearchScope()) {
         case 0:
            var6 = 0;
            break;
         case 1:
         default:
            var6 = 1;
            break;
         case 2:
            var6 = 2;
         }

         String[] var13 = var3.getReturningAttributes();
         if (var13 != null && var13.length == 0) {
            var13 = new String[]{"1.1"};
         }

         String var8 = var4 ? this.fullyQualifiedName(var1) : (var1.isEmpty() ? "" : var1.get(0));
         int var9 = var3.getTimeLimit();
         int var10 = 0;
         if (var9 > 0) {
            var10 = var9 / 1000 + 1;
         }

         LdapResult var11 = this.clnt.search(var8, var6, this.derefAliases, (int)var3.getCountLimit(), var10, var3.getReturningObjFlag() ? false : this.typesOnly, var13, var2, this.batchSize, this.reqCtls, this.binaryAttrs, var5, this.replyQueueSize);
         this.respCtls = var11.resControls;
         return var11;
      } catch (IOException var12) {
         CommunicationException var7 = new CommunicationException(var12.getMessage());
         var7.setRootCause(var12);
         throw var7;
      }
   }

   private static boolean searchToCompare(String var0, SearchControls var1, String[] var2) {
      if (var1.getSearchScope() != 0) {
         return false;
      } else {
         String[] var3 = var1.getReturningAttributes();
         if (var3 != null && var3.length == 0) {
            return filterToAssertion(var0, var2);
         } else {
            return false;
         }
      }
   }

   private static boolean filterToAssertion(String var0, String[] var1) {
      StringTokenizer var2 = new StringTokenizer(var0, "=");
      if (var2.countTokens() != 2) {
         return false;
      } else {
         var1[0] = var2.nextToken();
         var1[1] = var2.nextToken();
         if (var1[1].indexOf(42) != -1) {
            return false;
         } else {
            boolean var3 = false;
            int var4 = var1[1].length();
            if (var1[0].charAt(0) == '(' && var1[1].charAt(var4 - 1) == ')') {
               var3 = true;
            } else if (var1[0].charAt(0) == '(' || var1[1].charAt(var4 - 1) == ')') {
               return false;
            }

            StringTokenizer var5 = new StringTokenizer(var1[0], "()&|!=~><*", true);
            if (var5.countTokens() != (var3 ? 2 : 1)) {
               return false;
            } else {
               var5 = new StringTokenizer(var1[1], "()&|!=~><*", true);
               if (var5.countTokens() != (var3 ? 2 : 1)) {
                  return false;
               } else {
                  if (var3) {
                     var1[0] = var1[0].substring(1);
                     var1[1] = var1[1].substring(0, var4 - 1);
                  }

                  return true;
               }
            }
         }
      }
   }

   private LdapResult compare(Name var1, String var2, String var3) throws IOException, NamingException {
      this.ensureOpen();
      String var4 = this.fullyQualifiedName(var1);
      LdapResult var5 = this.clnt.compare(var4, var2, var3, this.reqCtls);
      this.respCtls = var5.resControls;
      return var5;
   }

   private static SearchControls cloneSearchControls(SearchControls var0) {
      if (var0 == null) {
         return null;
      } else {
         String[] var1 = var0.getReturningAttributes();
         if (var1 != null) {
            String[] var2 = new String[var1.length];
            System.arraycopy(var1, 0, var2, 0, var1.length);
            var1 = var2;
         }

         return new SearchControls(var0.getSearchScope(), var0.getCountLimit(), var0.getTimeLimit(), var1, var0.getReturningObjFlag(), var0.getDerefLinkFlag());
      }
   }

   protected Hashtable<String, Object> p_getEnvironment() {
      return this.envprops;
   }

   public Hashtable<String, Object> getEnvironment() throws NamingException {
      return this.envprops == null ? new Hashtable(5, 0.75F) : (Hashtable)this.envprops.clone();
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      if (this.envprops != null && this.envprops.get(var1) != null) {
         byte var3 = -1;
         switch(var1.hashCode()) {
         case -2125978773:
            if (var1.equals("java.naming.ldap.version")) {
               var3 = 13;
            }
            break;
         case -1907752796:
            if (var1.equals("java.naming.ldap.deleteRDN")) {
               var3 = 2;
            }
            break;
         case -1793828884:
            if (var1.equals("java.naming.security.protocol")) {
               var3 = 12;
            }
            break;
         case -1690536287:
            if (var1.equals("java.naming.batchsize")) {
               var3 = 4;
            }
            break;
         case -1273698008:
            if (var1.equals("java.naming.security.credentials")) {
               var3 = 17;
            }
            break;
         case -1264241948:
            if (var1.equals("java.naming.ldap.factory.socket")) {
               var3 = 14;
            }
            break;
         case -1012288104:
            if (var1.equals("java.naming.ldap.typesOnly")) {
               var3 = 1;
            }
            break;
         case -986853030:
            if (var1.equals("java.naming.security.principal")) {
               var3 = 16;
            }
            break;
         case -597571529:
            if (var1.equals("java.naming.ldap.referral.limit")) {
               var3 = 5;
            }
            break;
         case 51576954:
            if (var1.equals("com.sun.jndi.ldap.read.timeout")) {
               var3 = 9;
            }
            break;
         case 520248524:
            if (var1.equals("java.naming.security.authentication")) {
               var3 = 15;
            }
            break;
         case 523906937:
            if (var1.equals("java.naming.ldap.derefAliases")) {
               var3 = 3;
            }
            break;
         case 704861003:
            if (var1.equals("java.naming.ldap.attributes.binary")) {
               var3 = 7;
            }
            break;
         case 778463863:
            if (var1.equals("java.naming.referral")) {
               var3 = 6;
            }
            break;
         case 1186209693:
            if (var1.equals("java.naming.ldap.ref.separator")) {
               var3 = 0;
            }
            break;
         case 1514015564:
            if (var1.equals("com.sun.jndi.ldap.connect.timeout")) {
               var3 = 8;
            }
            break;
         case 1515176491:
            if (var1.equals("com.sun.jndi.ldap.search.waitForReply")) {
               var3 = 10;
            }
            break;
         case 1859284381:
            if (var1.equals("com.sun.jndi.ldap.search.replyQueueSize")) {
               var3 = 11;
            }
         }

         switch(var3) {
         case 0:
            this.addrEncodingSeparator = '#';
            break;
         case 1:
            this.typesOnly = false;
            break;
         case 2:
            this.deleteRDN = true;
            break;
         case 3:
            this.derefAliases = 3;
            break;
         case 4:
            this.batchSize = 1;
            break;
         case 5:
            this.referralHopLimit = 10;
            break;
         case 6:
            this.setReferralMode((String)null, true);
            break;
         case 7:
            this.setBinaryAttributes((String)null);
            break;
         case 8:
            this.connectTimeout = -1;
            break;
         case 9:
            this.readTimeout = -1;
            break;
         case 10:
            this.waitForReply = true;
            break;
         case 11:
            this.replyQueueSize = -1;
            break;
         case 12:
            this.closeConnection(false);
            if (this.useSsl && !this.hasLdapsScheme) {
               this.useSsl = false;
               this.url = null;
               if (this.useDefaultPortNumber) {
                  this.port_number = 389;
               }
            }
            break;
         case 13:
         case 14:
            this.closeConnection(false);
            break;
         case 15:
         case 16:
         case 17:
            this.sharable = false;
         }

         this.envprops = (Hashtable)this.envprops.clone();
         return this.envprops.remove(var1);
      } else {
         return null;
      }
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      if (var2 == null) {
         return this.removeFromEnvironment(var1);
      } else {
         byte var4 = -1;
         switch(var1.hashCode()) {
         case -2125978773:
            if (var1.equals("java.naming.ldap.version")) {
               var4 = 13;
            }
            break;
         case -1907752796:
            if (var1.equals("java.naming.ldap.deleteRDN")) {
               var4 = 2;
            }
            break;
         case -1793828884:
            if (var1.equals("java.naming.security.protocol")) {
               var4 = 12;
            }
            break;
         case -1690536287:
            if (var1.equals("java.naming.batchsize")) {
               var4 = 4;
            }
            break;
         case -1273698008:
            if (var1.equals("java.naming.security.credentials")) {
               var4 = 17;
            }
            break;
         case -1264241948:
            if (var1.equals("java.naming.ldap.factory.socket")) {
               var4 = 14;
            }
            break;
         case -1012288104:
            if (var1.equals("java.naming.ldap.typesOnly")) {
               var4 = 1;
            }
            break;
         case -986853030:
            if (var1.equals("java.naming.security.principal")) {
               var4 = 16;
            }
            break;
         case -597571529:
            if (var1.equals("java.naming.ldap.referral.limit")) {
               var4 = 5;
            }
            break;
         case 51576954:
            if (var1.equals("com.sun.jndi.ldap.read.timeout")) {
               var4 = 9;
            }
            break;
         case 520248524:
            if (var1.equals("java.naming.security.authentication")) {
               var4 = 15;
            }
            break;
         case 523906937:
            if (var1.equals("java.naming.ldap.derefAliases")) {
               var4 = 3;
            }
            break;
         case 704861003:
            if (var1.equals("java.naming.ldap.attributes.binary")) {
               var4 = 7;
            }
            break;
         case 778463863:
            if (var1.equals("java.naming.referral")) {
               var4 = 6;
            }
            break;
         case 1186209693:
            if (var1.equals("java.naming.ldap.ref.separator")) {
               var4 = 0;
            }
            break;
         case 1514015564:
            if (var1.equals("com.sun.jndi.ldap.connect.timeout")) {
               var4 = 8;
            }
            break;
         case 1515176491:
            if (var1.equals("com.sun.jndi.ldap.search.waitForReply")) {
               var4 = 10;
            }
            break;
         case 1859284381:
            if (var1.equals("com.sun.jndi.ldap.search.replyQueueSize")) {
               var4 = 11;
            }
         }

         switch(var4) {
         case 0:
            this.setRefSeparator((String)var2);
            break;
         case 1:
            this.setTypesOnly((String)var2);
            break;
         case 2:
            this.setDeleteRDN((String)var2);
            break;
         case 3:
            this.setDerefAliases((String)var2);
            break;
         case 4:
            this.setBatchSize((String)var2);
            break;
         case 5:
            this.setReferralLimit((String)var2);
            break;
         case 6:
            this.setReferralMode((String)var2, true);
            break;
         case 7:
            this.setBinaryAttributes((String)var2);
            break;
         case 8:
            this.setConnectTimeout((String)var2);
            break;
         case 9:
            this.setReadTimeout((String)var2);
            break;
         case 10:
            this.setWaitForReply((String)var2);
            break;
         case 11:
            this.setReplyQueueSize((String)var2);
            break;
         case 12:
            this.closeConnection(false);
            if ("ssl".equals(var2)) {
               this.useSsl = true;
               this.url = null;
               if (this.useDefaultPortNumber) {
                  this.port_number = 636;
               }
            }
            break;
         case 13:
         case 14:
            this.closeConnection(false);
            break;
         case 15:
         case 16:
         case 17:
            this.sharable = false;
         }

         this.envprops = this.envprops == null ? new Hashtable(5, 0.75F) : (Hashtable)this.envprops.clone();
         return this.envprops.put(var1, var2);
      }
   }

   void setProviderUrl(String var1) {
      if (this.envprops != null) {
         this.envprops.put("java.naming.provider.url", var1);
      }

   }

   void setDomainName(String var1) {
      if (this.envprops != null) {
         this.envprops.put("com.sun.jndi.ldap.domainname", var1);
      }

   }

   private void initEnv() throws NamingException {
      if (this.envprops == null) {
         this.setReferralMode((String)null, false);
      } else {
         this.setBatchSize((String)this.envprops.get("java.naming.batchsize"));
         this.setRefSeparator((String)this.envprops.get("java.naming.ldap.ref.separator"));
         this.setDeleteRDN((String)this.envprops.get("java.naming.ldap.deleteRDN"));
         this.setTypesOnly((String)this.envprops.get("java.naming.ldap.typesOnly"));
         this.setDerefAliases((String)this.envprops.get("java.naming.ldap.derefAliases"));
         this.setReferralLimit((String)this.envprops.get("java.naming.ldap.referral.limit"));
         this.setBinaryAttributes((String)this.envprops.get("java.naming.ldap.attributes.binary"));
         this.bindCtls = cloneControls((Control[])((Control[])this.envprops.get("java.naming.ldap.control.connect")));
         this.setReferralMode((String)this.envprops.get("java.naming.referral"), false);
         this.setConnectTimeout((String)this.envprops.get("com.sun.jndi.ldap.connect.timeout"));
         this.setReadTimeout((String)this.envprops.get("com.sun.jndi.ldap.read.timeout"));
         this.setWaitForReply((String)this.envprops.get("com.sun.jndi.ldap.search.waitForReply"));
         this.setReplyQueueSize((String)this.envprops.get("com.sun.jndi.ldap.search.replyQueueSize"));
      }
   }

   private void setDeleteRDN(String var1) {
      if (var1 != null && var1.equalsIgnoreCase("false")) {
         this.deleteRDN = false;
      } else {
         this.deleteRDN = true;
      }

   }

   private void setTypesOnly(String var1) {
      if (var1 != null && var1.equalsIgnoreCase("true")) {
         this.typesOnly = true;
      } else {
         this.typesOnly = false;
      }

   }

   private void setBatchSize(String var1) {
      if (var1 != null) {
         this.batchSize = Integer.parseInt(var1);
      } else {
         this.batchSize = 1;
      }

   }

   private void setReferralMode(String var1, boolean var2) {
      if (var1 != null) {
         byte var4 = -1;
         switch(var1.hashCode()) {
         case -1268958287:
            if (var1.equals("follow")) {
               var4 = 1;
            }
            break;
         case -1190396462:
            if (var1.equals("ignore")) {
               var4 = 3;
            }
            break;
         case -412873919:
            if (var1.equals("follow-scheme")) {
               var4 = 0;
            }
            break;
         case 110339814:
            if (var1.equals("throw")) {
               var4 = 2;
            }
         }

         switch(var4) {
         case 0:
            this.handleReferrals = 4;
            break;
         case 1:
            this.handleReferrals = 1;
            break;
         case 2:
            this.handleReferrals = 2;
            break;
         case 3:
            this.handleReferrals = 3;
            break;
         default:
            throw new IllegalArgumentException("Illegal value for java.naming.referral property.");
         }
      } else {
         this.handleReferrals = 3;
      }

      if (this.handleReferrals == 3) {
         this.reqCtls = addControl(this.reqCtls, manageReferralControl);
      } else if (var2) {
         this.reqCtls = removeControl(this.reqCtls, manageReferralControl);
      }

   }

   private void setDerefAliases(String var1) {
      if (var1 != null) {
         byte var3 = -1;
         switch(var1.hashCode()) {
         case -1414557169:
            if (var1.equals("always")) {
               var3 = 3;
            }
            break;
         case -853173367:
            if (var1.equals("finding")) {
               var3 = 2;
            }
            break;
         case 104712844:
            if (var1.equals("never")) {
               var3 = 0;
            }
            break;
         case 1778217274:
            if (var1.equals("searching")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            this.derefAliases = 0;
            break;
         case 1:
            this.derefAliases = 1;
            break;
         case 2:
            this.derefAliases = 2;
            break;
         case 3:
            this.derefAliases = 3;
            break;
         default:
            throw new IllegalArgumentException("Illegal value for java.naming.ldap.derefAliases property.");
         }
      } else {
         this.derefAliases = 3;
      }

   }

   private void setRefSeparator(String var1) throws NamingException {
      if (var1 != null && var1.length() > 0) {
         this.addrEncodingSeparator = var1.charAt(0);
      } else {
         this.addrEncodingSeparator = '#';
      }

   }

   private void setReferralLimit(String var1) {
      if (var1 != null) {
         this.referralHopLimit = Integer.parseInt(var1);
         if (this.referralHopLimit == 0) {
            this.referralHopLimit = Integer.MAX_VALUE;
         }
      } else {
         this.referralHopLimit = 10;
      }

   }

   void setHopCount(int var1) {
      this.hopCount = var1;
   }

   private void setConnectTimeout(String var1) {
      if (var1 != null) {
         this.connectTimeout = Integer.parseInt(var1);
      } else {
         this.connectTimeout = -1;
      }

   }

   private void setReplyQueueSize(String var1) {
      if (var1 != null) {
         this.replyQueueSize = Integer.parseInt(var1);
         if (this.replyQueueSize <= 0) {
            this.replyQueueSize = -1;
         }
      } else {
         this.replyQueueSize = -1;
      }

   }

   private void setWaitForReply(String var1) {
      if (var1 != null && var1.equalsIgnoreCase("false")) {
         this.waitForReply = false;
      } else {
         this.waitForReply = true;
      }

   }

   private void setReadTimeout(String var1) {
      if (var1 != null) {
         this.readTimeout = Integer.parseInt(var1);
      } else {
         this.readTimeout = -1;
      }

   }

   private static Vector<Vector<String>> extractURLs(String var0) {
      int var1 = 0;

      int var2;
      for(var2 = 0; (var1 = var0.indexOf(10, var1)) >= 0; ++var2) {
         ++var1;
      }

      Vector var3 = new Vector(var2);
      boolean var5 = false;
      var1 = var0.indexOf(10);

      int var4;
      Vector var6;
      for(var4 = var1 + 1; (var1 = var0.indexOf(10, var4)) >= 0; var4 = var1 + 1) {
         var6 = new Vector(1);
         var6.addElement(var0.substring(var4, var1));
         var3.addElement(var6);
      }

      var6 = new Vector(1);
      var6.addElement(var0.substring(var4));
      var3.addElement(var6);
      return var3;
   }

   private void setBinaryAttributes(String var1) {
      if (var1 == null) {
         this.binaryAttrs = null;
      } else {
         this.binaryAttrs = new Hashtable(11, 0.75F);
         StringTokenizer var2 = new StringTokenizer(var1.toLowerCase(Locale.ENGLISH), " ");

         while(var2.hasMoreTokens()) {
            this.binaryAttrs.put(var2.nextToken(), Boolean.TRUE);
         }
      }

   }

   protected void finalize() {
      try {
         this.close();
      } catch (NamingException var2) {
      }

   }

   public synchronized void close() throws NamingException {
      if (this.eventSupport != null) {
         this.eventSupport.cleanup();
         this.removeUnsolicited();
      }

      if (this.enumCount > 0) {
         this.closeRequested = true;
      } else {
         this.closeConnection(false);
      }
   }

   public void reconnect(Control[] var1) throws NamingException {
      this.envprops = this.envprops == null ? new Hashtable(5, 0.75F) : (Hashtable)this.envprops.clone();
      if (var1 == null) {
         this.envprops.remove("java.naming.ldap.control.connect");
         this.bindCtls = null;
      } else {
         this.envprops.put("java.naming.ldap.control.connect", this.bindCtls = cloneControls(var1));
      }

      this.sharable = false;
      this.ensureOpen();
   }

   private void ensureOpen() throws NamingException {
      this.ensureOpen(false);
   }

   private void ensureOpen(boolean var1) throws NamingException {
      try {
         if (this.clnt == null) {
            this.schemaTrees = new Hashtable(11, 0.75F);
            this.connect(var1);
         } else if (!this.sharable || var1) {
            synchronized(this.clnt) {
               if (!this.clnt.isLdapv3 || this.clnt.referenceCount > 1 || this.clnt.usingSaslStreams()) {
                  this.closeConnection(false);
               }
            }

            this.schemaTrees = new Hashtable(11, 0.75F);
            this.connect(var1);
         }
      } finally {
         this.sharable = true;
      }

   }

   private void connect(boolean var1) throws NamingException {
      String var2 = null;
      Object var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;
      String var7 = null;
      boolean var9 = false;
      if (this.envprops != null) {
         var2 = (String)this.envprops.get("java.naming.security.principal");
         var3 = this.envprops.get("java.naming.security.credentials");
         var7 = (String)this.envprops.get("java.naming.ldap.version");
         var4 = this.useSsl ? "ssl" : (String)this.envprops.get("java.naming.security.protocol");
         var5 = (String)this.envprops.get("java.naming.ldap.factory.socket");
         var6 = (String)this.envprops.get("java.naming.security.authentication");
         var9 = "true".equalsIgnoreCase((String)this.envprops.get("com.sun.jndi.ldap.connect.pool"));
      }

      if (var5 == null) {
         var5 = "ssl".equals(var4) ? "javax.net.ssl.SSLSocketFactory" : null;
      }

      if (var6 == null) {
         var6 = var2 == null ? "none" : "simple";
      }

      try {
         boolean var17 = this.clnt == null;
         int var8;
         if (var17) {
            var8 = var7 != null ? Integer.parseInt(var7) : 32;
            this.clnt = LdapClient.getInstance(var9, this.hostname, this.port_number, var5, this.connectTimeout, this.readTimeout, this.trace, var8, var6, this.bindCtls, var4, var2, var3, this.envprops);
            if (this.clnt.authenticateCalled()) {
               return;
            }
         } else {
            if (this.sharable && var1) {
               return;
            }

            var8 = 3;
         }

         LdapResult var18 = this.clnt.authenticate(var17, var2, var3, var8, var6, this.bindCtls, this.envprops);
         this.respCtls = var18.resControls;
         if (var18.status != 0) {
            if (var17) {
               this.closeConnection(true);
            }

            this.processReturnCode(var18);
         }
      } catch (LdapReferralException var16) {
         LdapReferralException var10 = var16;
         if (this.handleReferrals == 2) {
            throw var16;
         }

         NamingException var13 = null;

         while(true) {
            String var11;
            if ((var11 = var10.getNextReferral()) == null) {
               if (var13 != null) {
                  throw (NamingException)((NamingException)var13.fillInStackTrace());
               }

               throw new NamingException("Internal error processing referral during connection");
            }

            LdapURL var12 = new LdapURL(var11);
            this.hostname = var12.getHost();
            if (this.hostname != null && this.hostname.charAt(0) == '[') {
               this.hostname = this.hostname.substring(1, this.hostname.length() - 1);
            }

            this.port_number = var12.getPort();

            try {
               this.connect(var1);
               break;
            } catch (NamingException var15) {
               var13 = var15;
            }
         }
      }

   }

   private void closeConnection(boolean var1) {
      this.removeUnsolicited();
      if (this.clnt != null) {
         this.clnt.close(this.reqCtls, var1);
         this.clnt = null;
      }

   }

   synchronized void incEnumCount() {
      ++this.enumCount;
   }

   synchronized void decEnumCount() {
      --this.enumCount;
      if (this.enumCount == 0 && this.closeRequested) {
         try {
            this.close();
         } catch (NamingException var2) {
         }
      }

   }

   protected void processReturnCode(LdapResult var1) throws NamingException {
      this.processReturnCode(var1, (Name)null, this, (Name)null, this.envprops, (String)null);
   }

   void processReturnCode(LdapResult var1, Name var2) throws NamingException {
      this.processReturnCode(var1, (new CompositeName()).add(this.currentDN), this, var2, this.envprops, this.fullyQualifiedName(var2));
   }

   protected void processReturnCode(LdapResult var1, Name var2, Object var3, Name var4, Hashtable<?, ?> var5, String var6) throws NamingException {
      String var7 = LdapClient.getErrorMessage(var1.status, var1.errorMessage);
      LdapReferralException var9 = null;
      Object var8;
      LimitExceededException var14;
      switch(var1.status) {
      case 0:
         if (var1.referrals == null) {
            return;
         }

         var7 = "Unprocessed Continuation Reference(s)";
         if (this.handleReferrals != 3) {
            int var15 = var1.referrals.size();
            LdapReferralException var16 = null;
            LdapReferralException var17 = null;
            var7 = "Continuation Reference";

            for(int var13 = 0; var13 < var15; ++var13) {
               var9 = new LdapReferralException(var2, var3, var4, var7, var5, var6, this.handleReferrals, this.reqCtls);
               var9.setReferralInfo((Vector)var1.referrals.elementAt(var13), true);
               if (this.hopCount > 1) {
                  var9.setHopCount(this.hopCount);
               }

               if (var16 == null) {
                  var17 = var9;
                  var16 = var9;
               } else {
                  var17.nextReferralEx = var9;
                  var17 = var9;
               }
            }

            var1.referrals = null;
            if (var1.refEx == null) {
               var1.refEx = var16;
            } else {
               for(var17 = var1.refEx; var17.nextReferralEx != null; var17 = var17.nextReferralEx) {
               }

               var17.nextReferralEx = var16;
            }

            if (this.hopCount > this.referralHopLimit) {
               LimitExceededException var18 = new LimitExceededException("Referral limit exceeded");
               var18.setRootCause(var9);
               throw var18;
            }

            return;
         }

         var8 = new PartialResultException(var7);
         break;
      case 9:
         if (this.handleReferrals == 3) {
            var8 = new PartialResultException(var7);
         } else {
            if (var1.errorMessage != null && !var1.errorMessage.equals("")) {
               var1.referrals = extractURLs(var1.errorMessage);
               var9 = new LdapReferralException(var2, var3, var4, var7, var5, var6, this.handleReferrals, this.reqCtls);
               if (this.hopCount > 1) {
                  var9.setHopCount(this.hopCount);
               }

               if (var1.entries != null && !var1.entries.isEmpty() || var1.referrals == null || var1.referrals.size() != 1) {
                  var9.setReferralInfo(var1.referrals, true);
                  var1.refEx = var9;
                  return;
               }

               var9.setReferralInfo(var1.referrals, false);
               if (this.hopCount > this.referralHopLimit) {
                  var14 = new LimitExceededException("Referral limit exceeded");
                  var14.setRootCause(var9);
                  var8 = var14;
               } else {
                  var8 = var9;
               }
               break;
            }

            var8 = new PartialResultException(var7);
         }
         break;
      case 10:
         if (this.handleReferrals == 3) {
            var8 = new PartialResultException(var7);
         } else {
            var9 = new LdapReferralException(var2, var3, var4, var7, var5, var6, this.handleReferrals, this.reqCtls);
            Vector var10;
            if (var1.referrals == null) {
               var10 = null;
            } else if (this.handleReferrals != 4) {
               var10 = (Vector)var1.referrals.elementAt(0);
            } else {
               var10 = new Vector();
               Iterator var11 = ((Vector)var1.referrals.elementAt(0)).iterator();

               while(var11.hasNext()) {
                  String var12 = (String)var11.next();
                  if (var12.startsWith("ldap:")) {
                     var10.add(var12);
                  }
               }

               if (var10.isEmpty()) {
                  var10 = null;
               }
            }

            var9.setReferralInfo(var10, false);
            if (this.hopCount > 1) {
               var9.setHopCount(this.hopCount);
            }

            if (this.hopCount > this.referralHopLimit) {
               var14 = new LimitExceededException("Referral limit exceeded");
               var14.setRootCause(var9);
               var8 = var14;
            } else {
               var8 = var9;
            }
         }
         break;
      case 34:
      case 64:
         if (var4 != null) {
            var8 = new InvalidNameException(var4.toString() + ": " + var7);
         } else {
            var8 = new InvalidNameException(var7);
         }
         break;
      default:
         var8 = mapErrorCode(var1.status, var1.errorMessage);
      }

      ((NamingException)var8).setResolvedName(var2);
      ((NamingException)var8).setResolvedObj(var3);
      ((NamingException)var8).setRemainingName(var4);
      throw var8;
   }

   public static NamingException mapErrorCode(int var0, String var1) {
      if (var0 == 0) {
         return null;
      } else {
         Object var2 = null;
         String var3 = LdapClient.getErrorMessage(var0, var1);
         switch(var0) {
         case 1:
            var2 = new NamingException(var3);
            break;
         case 2:
            var2 = new CommunicationException(var3);
            break;
         case 3:
            var2 = new TimeLimitExceededException(var3);
            break;
         case 4:
            var2 = new SizeLimitExceededException(var3);
            break;
         case 5:
         case 6:
         case 35:
            var2 = new NamingException(var3);
            break;
         case 7:
         case 8:
         case 13:
         case 48:
            var2 = new AuthenticationNotSupportedException(var3);
            break;
         case 9:
            var2 = new NamingException(var3);
            break;
         case 10:
            var2 = new NamingException(var3);
            break;
         case 11:
            var2 = new LimitExceededException(var3);
            break;
         case 12:
            var2 = new OperationNotSupportedException(var3);
            break;
         case 14:
         case 49:
            var2 = new AuthenticationException(var3);
            break;
         case 15:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 55:
         case 56:
         case 57:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         default:
            var2 = new NamingException(var3);
            break;
         case 16:
            var2 = new NoSuchAttributeException(var3);
            break;
         case 17:
            var2 = new InvalidAttributeIdentifierException(var3);
            break;
         case 18:
            var2 = new InvalidSearchFilterException(var3);
            break;
         case 19:
         case 21:
            var2 = new InvalidAttributeValueException(var3);
            break;
         case 20:
            var2 = new AttributeInUseException(var3);
            break;
         case 32:
            var2 = new NameNotFoundException(var3);
            break;
         case 33:
            var2 = new NamingException(var3);
            break;
         case 34:
         case 64:
            var2 = new InvalidNameException(var3);
            break;
         case 36:
            var2 = new NamingException(var3);
            break;
         case 50:
            var2 = new NoPermissionException(var3);
            break;
         case 51:
         case 52:
            var2 = new ServiceUnavailableException(var3);
            break;
         case 53:
            var2 = new OperationNotSupportedException(var3);
            break;
         case 54:
            var2 = new NamingException(var3);
            break;
         case 65:
         case 67:
         case 69:
            var2 = new SchemaViolationException(var3);
            break;
         case 66:
            var2 = new ContextNotEmptyException(var3);
            break;
         case 68:
            var2 = new NameAlreadyBoundException(var3);
            break;
         case 80:
            var2 = new NamingException(var3);
         }

         return (NamingException)var2;
      }
   }

   public ExtendedResponse extendedOperation(ExtendedRequest var1) throws NamingException {
      boolean var2 = var1.getID().equals("1.3.6.1.4.1.1466.20037");
      this.ensureOpen(var2);

      ExtendedResponse var5;
      try {
         LdapResult var16 = this.clnt.extendedOp(var1.getID(), var1.getEncodedValue(), this.reqCtls, var2);
         this.respCtls = var16.resControls;
         if (var16.status != 0) {
            this.processReturnCode(var16, new CompositeName());
         }

         int var18 = var16.extensionValue == null ? 0 : var16.extensionValue.length;
         var5 = var1.createExtendedResponse(var16.extensionId, var16.extensionValue, 0, var18);
         if (var5 instanceof StartTlsResponseImpl) {
            String var6 = (String)((String)(this.envprops != null ? this.envprops.get("com.sun.jndi.ldap.domainname") : null));
            ((StartTlsResponseImpl)var5).setConnection(this.clnt.conn, var6);
         }

         return var5;
      } catch (LdapReferralException var14) {
         LdapReferralException var3 = var14;
         if (this.handleReferrals == 2) {
            throw var14;
         } else {
            while(true) {
               LdapReferralContext var17 = (LdapReferralContext)var3.getReferralContext(this.envprops, this.bindCtls);

               try {
                  var5 = var17.extendedOperation(var1);
                  return var5;
               } catch (LdapReferralException var12) {
                  var3 = var12;
               } finally {
                  var17.close();
               }
            }
         }
      } catch (IOException var15) {
         CommunicationException var4 = new CommunicationException(var15.getMessage());
         var4.setRootCause(var15);
         throw var4;
      }
   }

   public void setRequestControls(Control[] var1) throws NamingException {
      if (this.handleReferrals == 3) {
         this.reqCtls = addControl(var1, manageReferralControl);
      } else {
         this.reqCtls = cloneControls(var1);
      }

   }

   public Control[] getRequestControls() throws NamingException {
      return cloneControls(this.reqCtls);
   }

   public Control[] getConnectControls() throws NamingException {
      return cloneControls(this.bindCtls);
   }

   public Control[] getResponseControls() throws NamingException {
      return this.respCtls != null ? this.convertControls(this.respCtls) : null;
   }

   Control[] convertControls(Vector<Control> var1) throws NamingException {
      int var2 = var1.size();
      if (var2 == 0) {
         return null;
      } else {
         Control[] var3 = new Control[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = myResponseControlFactory.getControlInstance((Control)var1.elementAt(var4));
            if (var3[var4] == null) {
               var3[var4] = ControlFactory.getControlInstance((Control)var1.elementAt(var4), this, this.envprops);
            }
         }

         return var3;
      }
   }

   private static Control[] addControl(Control[] var0, Control var1) {
      if (var0 == null) {
         return new Control[]{var1};
      } else {
         int var2 = findControl(var0, var1);
         if (var2 != -1) {
            return var0;
         } else {
            Control[] var3 = new Control[var0.length + 1];
            System.arraycopy(var0, 0, var3, 0, var0.length);
            var3[var0.length] = var1;
            return var3;
         }
      }
   }

   private static int findControl(Control[] var0, Control var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2] == var1) {
            return var2;
         }
      }

      return -1;
   }

   private static Control[] removeControl(Control[] var0, Control var1) {
      if (var0 == null) {
         return null;
      } else {
         int var2 = findControl(var0, var1);
         if (var2 == -1) {
            return var0;
         } else {
            Control[] var3 = new Control[var0.length - 1];
            System.arraycopy(var0, 0, var3, 0, var2);
            System.arraycopy(var0, var2 + 1, var3, var2, var0.length - var2 - 1);
            return var3;
         }
      }
   }

   private static Control[] cloneControls(Control[] var0) {
      if (var0 == null) {
         return null;
      } else {
         Control[] var1 = new Control[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   public void addNamingListener(Name var1, int var2, NamingListener var3) throws NamingException {
      this.addNamingListener(getTargetName(var1), var2, var3);
   }

   public void addNamingListener(String var1, int var2, NamingListener var3) throws NamingException {
      if (this.eventSupport == null) {
         this.eventSupport = new EventSupport(this);
      }

      this.eventSupport.addNamingListener(getTargetName(new CompositeName(var1)), var2, var3);
      if (var3 instanceof UnsolicitedNotificationListener && !this.unsolicited) {
         this.addUnsolicited();
      }

   }

   public void removeNamingListener(NamingListener var1) throws NamingException {
      if (this.eventSupport != null) {
         this.eventSupport.removeNamingListener(var1);
         if (var1 instanceof UnsolicitedNotificationListener && !this.eventSupport.hasUnsolicited()) {
            this.removeUnsolicited();
         }

      }
   }

   public void addNamingListener(String var1, String var2, SearchControls var3, NamingListener var4) throws NamingException {
      if (this.eventSupport == null) {
         this.eventSupport = new EventSupport(this);
      }

      this.eventSupport.addNamingListener(getTargetName(new CompositeName(var1)), var2, cloneSearchControls(var3), var4);
      if (var4 instanceof UnsolicitedNotificationListener && !this.unsolicited) {
         this.addUnsolicited();
      }

   }

   public void addNamingListener(Name var1, String var2, SearchControls var3, NamingListener var4) throws NamingException {
      this.addNamingListener(getTargetName(var1), var2, var3, var4);
   }

   public void addNamingListener(Name var1, String var2, Object[] var3, SearchControls var4, NamingListener var5) throws NamingException {
      this.addNamingListener(getTargetName(var1), var2, var3, var4, var5);
   }

   public void addNamingListener(String var1, String var2, Object[] var3, SearchControls var4, NamingListener var5) throws NamingException {
      String var6 = SearchFilter.format(var2, var3);
      this.addNamingListener(getTargetName(new CompositeName(var1)), var6, var4, var5);
   }

   public boolean targetMustExist() {
      return true;
   }

   private static String getTargetName(Name var0) throws NamingException {
      if (var0 instanceof CompositeName) {
         if (var0.size() > 1) {
            throw new InvalidNameException("Target cannot span multiple namespaces: " + var0);
         } else {
            return var0.isEmpty() ? "" : var0.get(0);
         }
      } else {
         return var0.toString();
      }
   }

   private void addUnsolicited() throws NamingException {
      this.ensureOpen();
      synchronized(this.eventSupport) {
         this.clnt.addUnsolicited(this);
         this.unsolicited = true;
      }
   }

   private void removeUnsolicited() {
      if (this.eventSupport != null) {
         synchronized(this.eventSupport) {
            if (this.unsolicited && this.clnt != null) {
               this.clnt.removeUnsolicited(this);
            }

            this.unsolicited = false;
         }
      }
   }

   void fireUnsolicited(Object var1) {
      synchronized(this.eventSupport) {
         if (this.unsolicited) {
            this.eventSupport.fireUnsolicited(var1);
            if (var1 instanceof NamingException) {
               this.unsolicited = false;
            }
         }

      }
   }

   static {
      EMPTY_SCHEMA.setReadOnly(new SchemaViolationException("Cannot update schema object"));
   }

   static final class SearchArgs {
      Name name;
      String filter;
      SearchControls cons;
      String[] reqAttrs;

      SearchArgs(Name var1, String var2, SearchControls var3, String[] var4) {
         this.name = var1;
         this.filter = var2;
         this.cons = var3;
         this.reqAttrs = var4;
      }
   }
}
