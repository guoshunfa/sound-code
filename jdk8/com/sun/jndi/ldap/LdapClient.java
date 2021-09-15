package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.PoolCallback;
import com.sun.jndi.ldap.pool.PooledConnection;
import com.sun.jndi.ldap.sasl.LdapSasl;
import com.sun.jndi.ldap.sasl.SaslInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.ldap.Control;

public final class LdapClient implements PooledConnection {
   private static final int debug = 0;
   static final boolean caseIgnore = true;
   private static final Hashtable<String, Boolean> defaultBinaryAttrs = new Hashtable(23, 0.75F);
   private static final String DISCONNECT_OID = "1.3.6.1.4.1.1466.20036";
   boolean isLdapv3;
   int referenceCount = 1;
   Connection conn;
   private final PoolCallback pcb;
   private final boolean pooled;
   private boolean authenticateCalled = false;
   static final int SCOPE_BASE_OBJECT = 0;
   static final int SCOPE_ONE_LEVEL = 1;
   static final int SCOPE_SUBTREE = 2;
   static final int ADD = 0;
   static final int DELETE = 1;
   static final int REPLACE = 2;
   static final int LDAP_VERSION3_VERSION2 = 32;
   static final int LDAP_VERSION2 = 2;
   static final int LDAP_VERSION3 = 3;
   static final int LDAP_VERSION = 3;
   static final int LDAP_REF_FOLLOW = 1;
   static final int LDAP_REF_THROW = 2;
   static final int LDAP_REF_IGNORE = 3;
   static final int LDAP_REF_FOLLOW_SCHEME = 4;
   static final String LDAP_URL = "ldap://";
   static final String LDAPS_URL = "ldaps://";
   static final int LBER_BOOLEAN = 1;
   static final int LBER_INTEGER = 2;
   static final int LBER_BITSTRING = 3;
   static final int LBER_OCTETSTRING = 4;
   static final int LBER_NULL = 5;
   static final int LBER_ENUMERATED = 10;
   static final int LBER_SEQUENCE = 48;
   static final int LBER_SET = 49;
   static final int LDAP_SUPERIOR_DN = 128;
   static final int LDAP_REQ_BIND = 96;
   static final int LDAP_REQ_UNBIND = 66;
   static final int LDAP_REQ_SEARCH = 99;
   static final int LDAP_REQ_MODIFY = 102;
   static final int LDAP_REQ_ADD = 104;
   static final int LDAP_REQ_DELETE = 74;
   static final int LDAP_REQ_MODRDN = 108;
   static final int LDAP_REQ_COMPARE = 110;
   static final int LDAP_REQ_ABANDON = 80;
   static final int LDAP_REQ_EXTENSION = 119;
   static final int LDAP_REP_BIND = 97;
   static final int LDAP_REP_SEARCH = 100;
   static final int LDAP_REP_SEARCH_REF = 115;
   static final int LDAP_REP_RESULT = 101;
   static final int LDAP_REP_MODIFY = 103;
   static final int LDAP_REP_ADD = 105;
   static final int LDAP_REP_DELETE = 107;
   static final int LDAP_REP_MODRDN = 109;
   static final int LDAP_REP_COMPARE = 111;
   static final int LDAP_REP_EXTENSION = 120;
   static final int LDAP_REP_REFERRAL = 163;
   static final int LDAP_REP_EXT_OID = 138;
   static final int LDAP_REP_EXT_VAL = 139;
   static final int LDAP_CONTROLS = 160;
   static final String LDAP_CONTROL_MANAGE_DSA_IT = "2.16.840.1.113730.3.4.2";
   static final String LDAP_CONTROL_PREFERRED_LANG = "1.3.6.1.4.1.1466.20035";
   static final String LDAP_CONTROL_PAGED_RESULTS = "1.2.840.113556.1.4.319";
   static final String LDAP_CONTROL_SERVER_SORT_REQ = "1.2.840.113556.1.4.473";
   static final String LDAP_CONTROL_SERVER_SORT_RES = "1.2.840.113556.1.4.474";
   static final int LDAP_SUCCESS = 0;
   static final int LDAP_OPERATIONS_ERROR = 1;
   static final int LDAP_PROTOCOL_ERROR = 2;
   static final int LDAP_TIME_LIMIT_EXCEEDED = 3;
   static final int LDAP_SIZE_LIMIT_EXCEEDED = 4;
   static final int LDAP_COMPARE_FALSE = 5;
   static final int LDAP_COMPARE_TRUE = 6;
   static final int LDAP_AUTH_METHOD_NOT_SUPPORTED = 7;
   static final int LDAP_STRONG_AUTH_REQUIRED = 8;
   static final int LDAP_PARTIAL_RESULTS = 9;
   static final int LDAP_REFERRAL = 10;
   static final int LDAP_ADMIN_LIMIT_EXCEEDED = 11;
   static final int LDAP_UNAVAILABLE_CRITICAL_EXTENSION = 12;
   static final int LDAP_CONFIDENTIALITY_REQUIRED = 13;
   static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
   static final int LDAP_NO_SUCH_ATTRIBUTE = 16;
   static final int LDAP_UNDEFINED_ATTRIBUTE_TYPE = 17;
   static final int LDAP_INAPPROPRIATE_MATCHING = 18;
   static final int LDAP_CONSTRAINT_VIOLATION = 19;
   static final int LDAP_ATTRIBUTE_OR_VALUE_EXISTS = 20;
   static final int LDAP_INVALID_ATTRIBUTE_SYNTAX = 21;
   static final int LDAP_NO_SUCH_OBJECT = 32;
   static final int LDAP_ALIAS_PROBLEM = 33;
   static final int LDAP_INVALID_DN_SYNTAX = 34;
   static final int LDAP_IS_LEAF = 35;
   static final int LDAP_ALIAS_DEREFERENCING_PROBLEM = 36;
   static final int LDAP_INAPPROPRIATE_AUTHENTICATION = 48;
   static final int LDAP_INVALID_CREDENTIALS = 49;
   static final int LDAP_INSUFFICIENT_ACCESS_RIGHTS = 50;
   static final int LDAP_BUSY = 51;
   static final int LDAP_UNAVAILABLE = 52;
   static final int LDAP_UNWILLING_TO_PERFORM = 53;
   static final int LDAP_LOOP_DETECT = 54;
   static final int LDAP_NAMING_VIOLATION = 64;
   static final int LDAP_OBJECT_CLASS_VIOLATION = 65;
   static final int LDAP_NOT_ALLOWED_ON_NON_LEAF = 66;
   static final int LDAP_NOT_ALLOWED_ON_RDN = 67;
   static final int LDAP_ENTRY_ALREADY_EXISTS = 68;
   static final int LDAP_OBJECT_CLASS_MODS_PROHIBITED = 69;
   static final int LDAP_AFFECTS_MULTIPLE_DSAS = 71;
   static final int LDAP_OTHER = 80;
   static final String[] ldap_error_message;
   private Vector<LdapCtx> unsolicited = new Vector(3);

   LdapClient(String var1, int var2, String var3, int var4, int var5, OutputStream var6, PoolCallback var7) throws NamingException {
      this.conn = new Connection(this, var1, var2, var3, var4, var5, var6);
      this.pcb = var7;
      this.pooled = var7 != null;
   }

   synchronized boolean authenticateCalled() {
      return this.authenticateCalled;
   }

   synchronized LdapResult authenticate(boolean var1, String var2, Object var3, int var4, String var5, Control[] var6, Hashtable<?, ?> var7) throws NamingException {
      int var8 = this.conn.readTimeout;
      this.conn.readTimeout = this.conn.connectTimeout;
      LdapResult var9 = null;

      LdapResult var52;
      try {
         this.authenticateCalled = true;

         CommunicationException var11;
         try {
            this.ensureOpen();
         } catch (IOException var48) {
            var11 = new CommunicationException();
            var11.setRootCause(var48);
            throw var11;
         }

         switch(var4) {
         case 2:
            this.isLdapv3 = false;
            break;
         case 3:
         case 32:
            this.isLdapv3 = true;
            break;
         default:
            throw new CommunicationException("Protocol version " + var4 + " not supported");
         }

         byte[] var10;
         CommunicationException var12;
         int var53;
         if (!var5.equalsIgnoreCase("none") && !var5.equalsIgnoreCase("anonymous")) {
            if (var5.equalsIgnoreCase("simple")) {
               var10 = null;
               boolean var43 = false;

               try {
                  var43 = true;
                  var10 = encodePassword(var3, this.isLdapv3);
                  var9 = this.ldapBind(var2, var10, var6, (String)null, false);
                  if (var9.status == 0) {
                     this.conn.setBound();
                     var43 = false;
                  } else {
                     var43 = false;
                  }
               } catch (IOException var46) {
                  var12 = new CommunicationException("simple bind failed: " + this.conn.host + ":" + this.conn.port);
                  var12.setRootCause(var46);
                  throw var12;
               } finally {
                  if (var43) {
                     if (var10 != var3 && var10 != null) {
                        for(int var14 = 0; var14 < var10.length; ++var14) {
                           var10[var14] = 0;
                        }
                     }

                  }
               }

               if (var10 != var3 && var10 != null) {
                  for(var53 = 0; var53 < var10.length; ++var53) {
                     var10[var53] = 0;
                  }
               }
            } else {
               if (!this.isLdapv3) {
                  throw new AuthenticationNotSupportedException(var5);
               }

               try {
                  var9 = LdapSasl.saslBind(this, this.conn, this.conn.host, var2, var3, var5, var7, var6);
                  if (var9.status == 0) {
                     this.conn.setBound();
                  }
               } catch (IOException var45) {
                  var11 = new CommunicationException("SASL bind failed: " + this.conn.host + ":" + this.conn.port);
                  var11.setRootCause(var45);
                  throw var11;
               }
            }
         } else if (var1 && var4 != 2 && var4 != 32 && (var6 == null || var6.length <= 0)) {
            var9 = new LdapResult();
            var9.status = 0;
         } else {
            try {
               var2 = null;
               var3 = null;
               var9 = this.ldapBind((String)null, (byte[])((byte[])null), var6, (String)null, false);
               if (var9.status == 0) {
                  this.conn.setBound();
               }
            } catch (IOException var47) {
               var11 = new CommunicationException("anonymous bind failed: " + this.conn.host + ":" + this.conn.port);
               var11.setRootCause(var47);
               throw var11;
            }
         }

         if (var1 && var9.status == 2 && var4 == 32 && (var5.equalsIgnoreCase("none") || var5.equalsIgnoreCase("anonymous") || var5.equalsIgnoreCase("simple"))) {
            var10 = null;
            boolean var34 = false;

            try {
               var34 = true;
               this.isLdapv3 = false;
               var10 = encodePassword(var3, false);
               var9 = this.ldapBind(var2, var10, var6, (String)null, false);
               if (var9.status == 0) {
                  this.conn.setBound();
                  var34 = false;
               } else {
                  var34 = false;
               }
            } catch (IOException var44) {
               var12 = new CommunicationException(var5 + ":" + this.conn.host + ":" + this.conn.port);
               var12.setRootCause(var44);
               throw var12;
            } finally {
               if (var34) {
                  if (var10 != var3 && var10 != null) {
                     for(int var16 = 0; var16 < var10.length; ++var16) {
                        var10[var16] = 0;
                     }
                  }

               }
            }

            if (var10 != var3 && var10 != null) {
               for(var53 = 0; var53 < var10.length; ++var53) {
                  var10[var53] = 0;
               }
            }
         }

         if (var9.status == 32) {
            throw new AuthenticationException(getErrorMessage(var9.status, var9.errorMessage));
         }

         this.conn.setV3(this.isLdapv3);
         var52 = var9;
      } finally {
         this.conn.readTimeout = var8;
      }

      return var52;
   }

   public synchronized LdapResult ldapBind(String var1, byte[] var2, Control[] var3, String var4, boolean var5) throws IOException, NamingException {
      this.ensureOpen();
      this.conn.abandonOutstandingReqs((Control[])null);
      BerEncoder var6 = new BerEncoder();
      int var7 = this.conn.getMsgId();
      LdapResult var8 = new LdapResult();
      var8.status = 1;
      var6.beginSeq(48);
      var6.encodeInt(var7);
      var6.beginSeq(96);
      var6.encodeInt(this.isLdapv3 ? 3 : 2);
      var6.encodeString(var1, this.isLdapv3);
      if (var4 != null) {
         var6.beginSeq(163);
         var6.encodeString(var4, this.isLdapv3);
         if (var2 != null) {
            var6.encodeOctetString(var2, 4);
         }

         var6.endSeq();
      } else if (var2 != null) {
         var6.encodeOctetString(var2, 128);
      } else {
         var6.encodeOctetString((byte[])null, 128, 0, 0);
      }

      var6.endSeq();
      if (this.isLdapv3) {
         encodeControls(var6, var3);
      }

      var6.endSeq();
      LdapRequest var9 = this.conn.writeRequest(var6, var7, var5);
      if (var2 != null) {
         var6.reset();
      }

      BerDecoder var10 = this.conn.readReply(var9);
      var10.parseSeq((int[])null);
      var10.parseInt();
      if (var10.parseByte() != 97) {
         return var8;
      } else {
         var10.parseLength();
         parseResult(var10, var8, this.isLdapv3);
         if (this.isLdapv3 && var10.bytesLeft() > 0 && var10.peekByte() == 135) {
            var8.serverCreds = var10.parseOctetString(135, (int[])null);
         }

         var8.resControls = this.isLdapv3 ? parseControls(var10) : null;
         this.conn.removeRequest(var9);
         return var8;
      }
   }

   boolean usingSaslStreams() {
      return this.conn.inStream instanceof SaslInputStream;
   }

   synchronized void incRefCount() {
      ++this.referenceCount;
   }

   private static byte[] encodePassword(Object var0, boolean var1) throws IOException {
      if (var0 instanceof char[]) {
         var0 = new String((char[])((char[])var0));
      }

      if (var0 instanceof String) {
         return var1 ? ((String)var0).getBytes("UTF8") : ((String)var0).getBytes("8859_1");
      } else {
         return (byte[])((byte[])var0);
      }
   }

   synchronized void close(Control[] var1, boolean var2) {
      --this.referenceCount;
      if (this.referenceCount <= 0 && this.conn != null) {
         if (!this.pooled) {
            this.conn.cleanup(var1, false);
            this.conn = null;
         } else if (var2) {
            this.conn.cleanup(var1, false);
            this.conn = null;
            this.pcb.removePooledConnection(this);
         } else {
            this.pcb.releasePooledConnection(this);
         }
      }

   }

   private void forceClose(boolean var1) {
      this.referenceCount = 0;
      if (this.conn != null) {
         this.conn.cleanup((Control[])null, false);
         this.conn = null;
         if (var1) {
            this.pcb.removePooledConnection(this);
         }
      }

   }

   protected void finalize() {
      this.forceClose(this.pooled);
   }

   public synchronized void closeConnection() {
      this.forceClose(false);
   }

   void processConnectionClosure() {
      if (this.unsolicited.size() > 0) {
         String var1;
         if (this.conn != null) {
            var1 = this.conn.host + ":" + this.conn.port + " connection closed";
         } else {
            var1 = "Connection closed";
         }

         this.notifyUnsolicited(new CommunicationException(var1));
      }

      if (this.pooled) {
         this.pcb.removePooledConnection(this);
      }

   }

   LdapResult search(String var1, int var2, int var3, int var4, int var5, boolean var6, String[] var7, String var8, int var9, Control[] var10, Hashtable<String, Boolean> var11, boolean var12, int var13) throws IOException, NamingException {
      this.ensureOpen();
      LdapResult var14 = new LdapResult();
      BerEncoder var15 = new BerEncoder();
      int var16 = this.conn.getMsgId();
      var15.beginSeq(48);
      var15.encodeInt(var16);
      var15.beginSeq(99);
      var15.encodeString(var1 == null ? "" : var1, this.isLdapv3);
      var15.encodeInt(var2, 10);
      var15.encodeInt(var3, 10);
      var15.encodeInt(var4);
      var15.encodeInt(var5);
      var15.encodeBoolean(var6);
      Filter.encodeFilterString(var15, var8, this.isLdapv3);
      var15.beginSeq(48);
      var15.encodeStringArray(var7, this.isLdapv3);
      var15.endSeq();
      var15.endSeq();
      if (this.isLdapv3) {
         encodeControls(var15, var10);
      }

      var15.endSeq();
      LdapRequest var17 = this.conn.writeRequest(var15, var16, false, var13);
      var14.msgId = var16;
      var14.status = 0;
      if (var12) {
         var14 = this.getSearchReply(var17, var9, var14, var11);
      }

      return var14;
   }

   void clearSearchReply(LdapResult var1, Control[] var2) {
      if (var1 != null && this.conn != null) {
         LdapRequest var3 = this.conn.findRequest(var1.msgId);
         if (var3 == null) {
            return;
         }

         if (var3.hasSearchCompleted()) {
            this.conn.removeRequest(var3);
         } else {
            this.conn.abandonRequest(var3, var2);
         }
      }

   }

   LdapResult getSearchReply(int var1, LdapResult var2, Hashtable<String, Boolean> var3) throws IOException, NamingException {
      this.ensureOpen();
      LdapRequest var4;
      return (var4 = this.conn.findRequest(var2.msgId)) == null ? null : this.getSearchReply(var4, var1, var2, var3);
   }

   private LdapResult getSearchReply(LdapRequest var1, int var2, LdapResult var3, Hashtable<String, Boolean> var4) throws IOException, NamingException {
      if (var2 == 0) {
         var2 = Integer.MAX_VALUE;
      }

      if (var3.entries != null) {
         var3.entries.setSize(0);
      } else {
         var3.entries = new Vector(var2 == Integer.MAX_VALUE ? 32 : var2);
      }

      if (var3.referrals != null) {
         var3.referrals.setSize(0);
      }

      int var13 = 0;

      while(true) {
         while(var13 < var2) {
            BerDecoder var5 = this.conn.readReply(var1);
            var5.parseSeq((int[])null);
            var5.parseInt();
            int var6 = var5.parseSeq((int[])null);
            if (var6 == 100) {
               BasicAttributes var7 = new BasicAttributes(true);
               String var9 = var5.parseString(this.isLdapv3);
               LdapEntry var10 = new LdapEntry(var9, var7);
               int[] var11 = new int[1];
               var5.parseSeq(var11);
               int var12 = var5.getParsePosition() + var11[0];

               while(var5.getParsePosition() < var12 && var5.bytesLeft() > 0) {
                  Attribute var8 = this.parseAttribute(var5, var4);
                  var7.put(var8);
               }

               var10.respCtls = this.isLdapv3 ? parseControls(var5) : null;
               var3.entries.addElement(var10);
               ++var13;
            } else if (var6 == 115 && this.isLdapv3) {
               Vector var14 = new Vector(4);
               if (var5.peekByte() == 48) {
                  var5.parseSeq((int[])null);
               }

               while(var5.bytesLeft() > 0 && var5.peekByte() == 4) {
                  var14.addElement(var5.parseString(this.isLdapv3));
               }

               if (var3.referrals == null) {
                  var3.referrals = new Vector(4);
               }

               var3.referrals.addElement(var14);
               var3.resControls = this.isLdapv3 ? parseControls(var5) : null;
            } else if (var6 == 120) {
               this.parseExtResponse(var5, var3);
            } else if (var6 == 101) {
               parseResult(var5, var3, this.isLdapv3);
               var3.resControls = this.isLdapv3 ? parseControls(var5) : null;
               this.conn.removeRequest(var1);
               return var3;
            }
         }

         return var3;
      }
   }

   private Attribute parseAttribute(BerDecoder var1, Hashtable<String, Boolean> var2) throws IOException {
      int[] var3 = new int[1];
      int var4 = var1.parseSeq((int[])null);
      String var5 = var1.parseString(this.isLdapv3);
      boolean var6 = this.isBinaryValued(var5, var2);
      LdapAttribute var7 = new LdapAttribute(var5);
      if (var1.parseSeq(var3) == 49) {
         int var8 = var3[0];

         while(var1.bytesLeft() > 0 && var8 > 0) {
            try {
               var8 -= this.parseAttributeValue(var1, var7, var6);
            } catch (IOException var10) {
               var1.seek(var8);
               break;
            }
         }
      } else {
         var1.seek(var3[0]);
      }

      return var7;
   }

   private int parseAttributeValue(BerDecoder var1, Attribute var2, boolean var3) throws IOException {
      int[] var4 = new int[1];
      if (var3) {
         var2.add(var1.parseOctetString(var1.peekByte(), var4));
      } else {
         var2.add(var1.parseStringWithTag(4, this.isLdapv3, var4));
      }

      return var4[0];
   }

   private boolean isBinaryValued(String var1, Hashtable<String, Boolean> var2) {
      String var3 = var1.toLowerCase(Locale.ENGLISH);
      return var3.indexOf(";binary") != -1 || defaultBinaryAttrs.containsKey(var3) || var2 != null && var2.containsKey(var3);
   }

   static void parseResult(BerDecoder var0, LdapResult var1, boolean var2) throws IOException {
      var1.status = var0.parseEnumeration();
      var1.matchedDN = var0.parseString(var2);
      var1.errorMessage = var0.parseString(var2);
      if (var2 && var0.bytesLeft() > 0 && var0.peekByte() == 163) {
         Vector var3 = new Vector(4);
         int[] var4 = new int[1];
         var0.parseSeq(var4);
         int var5 = var0.getParsePosition() + var4[0];

         while(var0.getParsePosition() < var5 && var0.bytesLeft() > 0) {
            var3.addElement(var0.parseString(var2));
         }

         if (var1.referrals == null) {
            var1.referrals = new Vector(4);
         }

         var1.referrals.addElement(var3);
      }

   }

   static Vector<Control> parseControls(BerDecoder var0) throws IOException {
      if (var0.bytesLeft() > 0 && var0.peekByte() == 160) {
         Vector var1 = new Vector(4);
         boolean var3 = false;
         byte[] var4 = null;
         int[] var5 = new int[1];
         var0.parseSeq(var5);
         int var6 = var0.getParsePosition() + var5[0];

         while(var0.getParsePosition() < var6 && var0.bytesLeft() > 0) {
            var0.parseSeq((int[])null);
            String var2 = var0.parseString(true);
            if (var0.bytesLeft() > 0 && var0.peekByte() == 1) {
               var3 = var0.parseBoolean();
            }

            if (var0.bytesLeft() > 0 && var0.peekByte() == 4) {
               var4 = var0.parseOctetString(4, (int[])null);
            }

            if (var2 != null) {
               var1.addElement(new BasicControl(var2, var3, var4));
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   private void parseExtResponse(BerDecoder var1, LdapResult var2) throws IOException {
      parseResult(var1, var2, this.isLdapv3);
      if (var1.bytesLeft() > 0 && var1.peekByte() == 138) {
         var2.extensionId = var1.parseStringWithTag(138, this.isLdapv3, (int[])null);
      }

      if (var1.bytesLeft() > 0 && var1.peekByte() == 139) {
         var2.extensionValue = var1.parseOctetString(139, (int[])null);
      }

      var2.resControls = parseControls(var1);
   }

   static void encodeControls(BerEncoder var0, Control[] var1) throws IOException {
      if (var1 != null && var1.length != 0) {
         var0.beginSeq(160);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var0.beginSeq(48);
            var0.encodeString(var1[var3].getID(), true);
            if (var1[var3].isCritical()) {
               var0.encodeBoolean(true);
            }

            byte[] var2;
            if ((var2 = var1[var3].getEncodedValue()) != null) {
               var0.encodeOctetString(var2, 4);
            }

            var0.endSeq();
         }

         var0.endSeq();
      }
   }

   private LdapResult processReply(LdapRequest var1, LdapResult var2, int var3) throws IOException, NamingException {
      BerDecoder var4 = this.conn.readReply(var1);
      var4.parseSeq((int[])null);
      var4.parseInt();
      if (var4.parseByte() != var3) {
         return var2;
      } else {
         var4.parseLength();
         parseResult(var4, var2, this.isLdapv3);
         var2.resControls = this.isLdapv3 ? parseControls(var4) : null;
         this.conn.removeRequest(var1);
         return var2;
      }
   }

   LdapResult modify(String var1, int[] var2, Attribute[] var3, Control[] var4) throws IOException, NamingException {
      this.ensureOpen();
      LdapResult var5 = new LdapResult();
      var5.status = 1;
      if (var1 != null && var2.length == var3.length) {
         BerEncoder var6 = new BerEncoder();
         int var7 = this.conn.getMsgId();
         var6.beginSeq(48);
         var6.encodeInt(var7);
         var6.beginSeq(102);
         var6.encodeString(var1, this.isLdapv3);
         var6.beginSeq(48);

         for(int var8 = 0; var8 < var2.length; ++var8) {
            var6.beginSeq(48);
            var6.encodeInt(var2[var8], 10);
            if (var2[var8] == 0 && hasNoValue(var3[var8])) {
               throw new InvalidAttributeValueException("'" + var3[var8].getID() + "' has no values.");
            }

            this.encodeAttribute(var6, var3[var8]);
            var6.endSeq();
         }

         var6.endSeq();
         var6.endSeq();
         if (this.isLdapv3) {
            encodeControls(var6, var4);
         }

         var6.endSeq();
         LdapRequest var9 = this.conn.writeRequest(var6, var7);
         return this.processReply(var9, var5, 103);
      } else {
         return var5;
      }
   }

   private void encodeAttribute(BerEncoder var1, Attribute var2) throws IOException, NamingException {
      var1.beginSeq(48);
      var1.encodeString(var2.getID(), this.isLdapv3);
      var1.beginSeq(49);
      NamingEnumeration var3 = var2.getAll();

      while(var3.hasMore()) {
         Object var4 = var3.next();
         if (var4 instanceof String) {
            var1.encodeString((String)var4, this.isLdapv3);
         } else if (var4 instanceof byte[]) {
            var1.encodeOctetString((byte[])((byte[])var4), 4);
         } else if (var4 != null) {
            throw new InvalidAttributeValueException("Malformed '" + var2.getID() + "' attribute value");
         }
      }

      var1.endSeq();
      var1.endSeq();
   }

   private static boolean hasNoValue(Attribute var0) throws NamingException {
      return var0.size() == 0 || var0.size() == 1 && var0.get() == null;
   }

   LdapResult add(LdapEntry var1, Control[] var2) throws IOException, NamingException {
      this.ensureOpen();
      LdapResult var3 = new LdapResult();
      var3.status = 1;
      if (var1 != null && var1.DN != null) {
         BerEncoder var4 = new BerEncoder();
         int var5 = this.conn.getMsgId();
         var4.beginSeq(48);
         var4.encodeInt(var5);
         var4.beginSeq(104);
         var4.encodeString(var1.DN, this.isLdapv3);
         var4.beginSeq(48);
         NamingEnumeration var7 = var1.attributes.getAll();

         while(var7.hasMore()) {
            Attribute var6 = (Attribute)var7.next();
            if (hasNoValue(var6)) {
               throw new InvalidAttributeValueException("'" + var6.getID() + "' has no values.");
            }

            this.encodeAttribute(var4, var6);
         }

         var4.endSeq();
         var4.endSeq();
         if (this.isLdapv3) {
            encodeControls(var4, var2);
         }

         var4.endSeq();
         LdapRequest var8 = this.conn.writeRequest(var4, var5);
         return this.processReply(var8, var3, 105);
      } else {
         return var3;
      }
   }

   LdapResult delete(String var1, Control[] var2) throws IOException, NamingException {
      this.ensureOpen();
      LdapResult var3 = new LdapResult();
      var3.status = 1;
      if (var1 == null) {
         return var3;
      } else {
         BerEncoder var4 = new BerEncoder();
         int var5 = this.conn.getMsgId();
         var4.beginSeq(48);
         var4.encodeInt(var5);
         var4.encodeString(var1, 74, this.isLdapv3);
         if (this.isLdapv3) {
            encodeControls(var4, var2);
         }

         var4.endSeq();
         LdapRequest var6 = this.conn.writeRequest(var4, var5);
         return this.processReply(var6, var3, 107);
      }
   }

   LdapResult moddn(String var1, String var2, boolean var3, String var4, Control[] var5) throws IOException, NamingException {
      this.ensureOpen();
      boolean var6 = var4 != null && var4.length() > 0;
      LdapResult var7 = new LdapResult();
      var7.status = 1;
      if (var1 != null && var2 != null) {
         BerEncoder var8 = new BerEncoder();
         int var9 = this.conn.getMsgId();
         var8.beginSeq(48);
         var8.encodeInt(var9);
         var8.beginSeq(108);
         var8.encodeString(var1, this.isLdapv3);
         var8.encodeString(var2, this.isLdapv3);
         var8.encodeBoolean(var3);
         if (this.isLdapv3 && var6) {
            var8.encodeString(var4, 128, this.isLdapv3);
         }

         var8.endSeq();
         if (this.isLdapv3) {
            encodeControls(var8, var5);
         }

         var8.endSeq();
         LdapRequest var10 = this.conn.writeRequest(var8, var9);
         return this.processReply(var10, var7, 109);
      } else {
         return var7;
      }
   }

   LdapResult compare(String var1, String var2, String var3, Control[] var4) throws IOException, NamingException {
      this.ensureOpen();
      LdapResult var5 = new LdapResult();
      var5.status = 1;
      if (var1 != null && var2 != null && var3 != null) {
         BerEncoder var6 = new BerEncoder();
         int var7 = this.conn.getMsgId();
         var6.beginSeq(48);
         var6.encodeInt(var7);
         var6.beginSeq(110);
         var6.encodeString(var1, this.isLdapv3);
         var6.beginSeq(48);
         var6.encodeString(var2, this.isLdapv3);
         byte[] var8 = this.isLdapv3 ? var3.getBytes("UTF8") : var3.getBytes("8859_1");
         var6.encodeOctetString(Filter.unescapeFilterValue(var8, 0, var8.length), 4);
         var6.endSeq();
         var6.endSeq();
         if (this.isLdapv3) {
            encodeControls(var6, var4);
         }

         var6.endSeq();
         LdapRequest var9 = this.conn.writeRequest(var6, var7);
         return this.processReply(var9, var5, 111);
      } else {
         return var5;
      }
   }

   LdapResult extendedOp(String var1, byte[] var2, Control[] var3, boolean var4) throws IOException, NamingException {
      this.ensureOpen();
      LdapResult var5 = new LdapResult();
      var5.status = 1;
      if (var1 == null) {
         return var5;
      } else {
         BerEncoder var6 = new BerEncoder();
         int var7 = this.conn.getMsgId();
         var6.beginSeq(48);
         var6.encodeInt(var7);
         var6.beginSeq(119);
         var6.encodeString(var1, 128, this.isLdapv3);
         if (var2 != null) {
            var6.encodeOctetString(var2, 129);
         }

         var6.endSeq();
         encodeControls(var6, var3);
         var6.endSeq();
         LdapRequest var8 = this.conn.writeRequest(var6, var7, var4);
         BerDecoder var9 = this.conn.readReply(var8);
         var9.parseSeq((int[])null);
         var9.parseInt();
         if (var9.parseByte() != 120) {
            return var5;
         } else {
            var9.parseLength();
            this.parseExtResponse(var9, var5);
            this.conn.removeRequest(var8);
            return var5;
         }
      }
   }

   static String getErrorMessage(int var0, String var1) {
      String var2 = "[LDAP: error code " + var0;
      if (var1 != null && var1.length() != 0) {
         var2 = var2 + " - " + var1 + "]";
      } else {
         try {
            if (ldap_error_message[var0] != null) {
               var2 = var2 + " - " + ldap_error_message[var0] + "]";
            }
         } catch (ArrayIndexOutOfBoundsException var4) {
            var2 = var2 + "]";
         }
      }

      return var2;
   }

   void addUnsolicited(LdapCtx var1) {
      this.unsolicited.addElement(var1);
   }

   void removeUnsolicited(LdapCtx var1) {
      this.unsolicited.removeElement(var1);
   }

   void processUnsolicited(BerDecoder var1) {
      CommunicationException var3;
      try {
         LdapResult var2 = new LdapResult();
         var1.parseSeq((int[])null);
         var1.parseInt();
         if (var1.parseByte() != 120) {
            throw new IOException("Unsolicited Notification must be an Extended Response");
         }

         var1.parseLength();
         this.parseExtResponse(var1, var2);
         if ("1.3.6.1.4.1.1466.20036".equals(var2.extensionId)) {
            this.forceClose(this.pooled);
         }

         var3 = null;
         UnsolicitedResponseImpl var4 = null;
         synchronized(this.unsolicited) {
            if (this.unsolicited.size() > 0) {
               LdapCtx var10 = (LdapCtx)this.unsolicited.elementAt(0);
               var4 = new UnsolicitedResponseImpl(var2.extensionId, var2.extensionValue, var2.referrals, var2.status, var2.errorMessage, var2.matchedDN, var2.resControls != null ? var10.convertControls(var2.resControls) : null);
            }
         }

         if (var4 != null) {
            this.notifyUnsolicited(var4);
            if ("1.3.6.1.4.1.1466.20036".equals(var2.extensionId)) {
               this.notifyUnsolicited(new CommunicationException("Connection closed"));
            }
         }
      } catch (IOException var8) {
         var3 = new CommunicationException("Problem parsing unsolicited notification");
         var3.setRootCause(var8);
         this.notifyUnsolicited(var3);
      } catch (NamingException var9) {
         this.notifyUnsolicited(var9);
      }

   }

   private void notifyUnsolicited(Object var1) {
      Vector var2;
      synchronized(this.unsolicited) {
         var2 = new Vector(this.unsolicited);
         if (var1 instanceof NamingException) {
            this.unsolicited.setSize(0);
         }
      }

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ((LdapCtx)var2.elementAt(var3)).fireUnsolicited(var1);
      }

   }

   private void ensureOpen() throws IOException {
      if (this.conn == null || !this.conn.useable) {
         if (this.conn != null && this.conn.closureReason != null) {
            throw this.conn.closureReason;
         } else {
            throw new IOException("connection closed");
         }
      }
   }

   static LdapClient getInstance(boolean var0, String var1, int var2, String var3, int var4, int var5, OutputStream var6, int var7, String var8, Control[] var9, String var10, String var11, Object var12, Hashtable<?, ?> var13) throws NamingException {
      if (var0 && LdapPoolManager.isPoolingAllowed(var3, var6, var8, var10, var13)) {
         LdapClient var14 = LdapPoolManager.getLdapClient(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         var14.referenceCount = 1;
         return var14;
      } else {
         return new LdapClient(var1, var2, var3, var4, var5, var6, (PoolCallback)null);
      }
   }

   static {
      defaultBinaryAttrs.put("userpassword", Boolean.TRUE);
      defaultBinaryAttrs.put("javaserializeddata", Boolean.TRUE);
      defaultBinaryAttrs.put("javaserializedobject", Boolean.TRUE);
      defaultBinaryAttrs.put("jpegphoto", Boolean.TRUE);
      defaultBinaryAttrs.put("audio", Boolean.TRUE);
      defaultBinaryAttrs.put("thumbnailphoto", Boolean.TRUE);
      defaultBinaryAttrs.put("thumbnaillogo", Boolean.TRUE);
      defaultBinaryAttrs.put("usercertificate", Boolean.TRUE);
      defaultBinaryAttrs.put("cacertificate", Boolean.TRUE);
      defaultBinaryAttrs.put("certificaterevocationlist", Boolean.TRUE);
      defaultBinaryAttrs.put("authorityrevocationlist", Boolean.TRUE);
      defaultBinaryAttrs.put("crosscertificatepair", Boolean.TRUE);
      defaultBinaryAttrs.put("photo", Boolean.TRUE);
      defaultBinaryAttrs.put("personalsignature", Boolean.TRUE);
      defaultBinaryAttrs.put("x500uniqueidentifier", Boolean.TRUE);
      ldap_error_message = new String[]{"Success", "Operations Error", "Protocol Error", "Timelimit Exceeded", "Sizelimit Exceeded", "Compare False", "Compare True", "Authentication Method Not Supported", "Strong Authentication Required", null, "Referral", "Administrative Limit Exceeded", "Unavailable Critical Extension", "Confidentiality Required", "SASL Bind In Progress", null, "No Such Attribute", "Undefined Attribute Type", "Inappropriate Matching", "Constraint Violation", "Attribute Or Value Exists", "Invalid Attribute Syntax", null, null, null, null, null, null, null, null, null, null, "No Such Object", "Alias Problem", "Invalid DN Syntax", null, "Alias Dereferencing Problem", null, null, null, null, null, null, null, null, null, null, null, "Inappropriate Authentication", "Invalid Credentials", "Insufficient Access Rights", "Busy", "Unavailable", "Unwilling To Perform", "Loop Detect", null, null, null, null, null, null, null, null, null, "Naming Violation", "Object Class Violation", "Not Allowed On Non-leaf", "Not Allowed On RDN", "Entry Already Exists", "Object Class Modifications Prohibited", null, "Affects Multiple DSAs", null, null, null, null, null, null, null, null, "Other", null, null, null, null, null, null, null, null, null, null};
   }
}
