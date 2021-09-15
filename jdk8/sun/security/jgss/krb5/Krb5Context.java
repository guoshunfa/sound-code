package sun.security.jgss.krb5;

import com.sun.security.jgss.AuthorizationDataEntry;
import com.sun.security.jgss.InquireType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Key;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.misc.HexDumpEncoder;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.TokenTracker;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Ticket;

class Krb5Context implements GSSContextSpi {
   private static final int STATE_NEW = 1;
   private static final int STATE_IN_PROCESS = 2;
   private static final int STATE_DONE = 3;
   private static final int STATE_DELETED = 4;
   private int state = 1;
   public static final int SESSION_KEY = 0;
   public static final int INITIATOR_SUBKEY = 1;
   public static final int ACCEPTOR_SUBKEY = 2;
   private boolean credDelegState = false;
   private boolean mutualAuthState = true;
   private boolean replayDetState = true;
   private boolean sequenceDetState = true;
   private boolean confState = true;
   private boolean integState = true;
   private boolean delegPolicyState = false;
   private boolean isConstrainedDelegationTried = false;
   private int mySeqNumber;
   private int peerSeqNumber;
   private int keySrc;
   private TokenTracker peerTokenTracker;
   private CipherHelper cipherHelper = null;
   private Object mySeqNumberLock = new Object();
   private Object peerSeqNumberLock = new Object();
   private EncryptionKey key;
   private Krb5NameElement myName;
   private Krb5NameElement peerName;
   private int lifetime;
   private boolean initiator;
   private ChannelBinding channelBinding;
   private Krb5CredElement myCred;
   private Krb5CredElement delegatedCred;
   private Credentials serviceCreds;
   private KrbApReq apReq;
   Ticket serviceTicket;
   private final GSSCaller caller;
   private static final boolean DEBUG;
   private boolean[] tktFlags;
   private String authTime;
   private AuthorizationDataEntry[] authzData;

   Krb5Context(GSSCaller var1, Krb5NameElement var2, Krb5CredElement var3, int var4) throws GSSException {
      if (var2 == null) {
         throw new IllegalArgumentException("Cannot have null peer name");
      } else {
         this.caller = var1;
         this.peerName = var2;
         this.myCred = var3;
         this.lifetime = var4;
         this.initiator = true;
      }
   }

   Krb5Context(GSSCaller var1, Krb5CredElement var2) throws GSSException {
      this.caller = var1;
      this.myCred = var2;
      this.initiator = false;
   }

   public Krb5Context(GSSCaller var1, byte[] var2) throws GSSException {
      throw new GSSException(16, -1, "GSS Import Context not available");
   }

   public final boolean isTransferable() throws GSSException {
      return false;
   }

   public final int getLifetime() {
      return Integer.MAX_VALUE;
   }

   public void requestLifetime(int var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.lifetime = var1;
      }

   }

   public final void requestConf(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.confState = var1;
      }

   }

   public final boolean getConfState() {
      return this.confState;
   }

   public final void requestInteg(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.integState = var1;
      }

   }

   public final boolean getIntegState() {
      return this.integState;
   }

   public final void requestCredDeleg(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator() && (this.myCred == null || !(this.myCred instanceof Krb5ProxyCredential))) {
         this.credDelegState = var1;
      }

   }

   public final boolean getCredDelegState() {
      if (this.isInitiator()) {
         return this.credDelegState;
      } else {
         this.tryConstrainedDelegation();
         return this.delegatedCred != null;
      }
   }

   public final void requestMutualAuth(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.mutualAuthState = var1;
      }

   }

   public final boolean getMutualAuthState() {
      return this.mutualAuthState;
   }

   public final void requestReplayDet(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.replayDetState = var1;
      }

   }

   public final boolean getReplayDetState() {
      return this.replayDetState || this.sequenceDetState;
   }

   public final void requestSequenceDet(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.sequenceDetState = var1;
      }

   }

   public final boolean getSequenceDetState() {
      return this.sequenceDetState || this.replayDetState;
   }

   public final void requestDelegPolicy(boolean var1) {
      if (this.state == 1 && this.isInitiator()) {
         this.delegPolicyState = var1;
      }

   }

   public final boolean getDelegPolicyState() {
      return this.delegPolicyState;
   }

   public final void requestAnonymity(boolean var1) throws GSSException {
   }

   public final boolean getAnonymityState() {
      return false;
   }

   final CipherHelper getCipherHelper(EncryptionKey var1) throws GSSException {
      EncryptionKey var2 = null;
      if (this.cipherHelper == null) {
         var2 = this.getKey() == null ? var1 : this.getKey();
         this.cipherHelper = new CipherHelper(var2);
      }

      return this.cipherHelper;
   }

   final int incrementMySequenceNumber() {
      synchronized(this.mySeqNumberLock) {
         int var1 = this.mySeqNumber++;
         return var1;
      }
   }

   final void resetMySequenceNumber(int var1) {
      if (DEBUG) {
         System.out.println("Krb5Context setting mySeqNumber to: " + var1);
      }

      synchronized(this.mySeqNumberLock) {
         this.mySeqNumber = var1;
      }
   }

   final void resetPeerSequenceNumber(int var1) {
      if (DEBUG) {
         System.out.println("Krb5Context setting peerSeqNumber to: " + var1);
      }

      synchronized(this.peerSeqNumberLock) {
         this.peerSeqNumber = var1;
         this.peerTokenTracker = new TokenTracker(this.peerSeqNumber);
      }
   }

   final void setKey(int var1, EncryptionKey var2) throws GSSException {
      this.key = var2;
      this.keySrc = var1;
      this.cipherHelper = new CipherHelper(var2);
   }

   public final int getKeySrc() {
      return this.keySrc;
   }

   private final EncryptionKey getKey() {
      return this.key;
   }

   final void setDelegCred(Krb5CredElement var1) {
      this.delegatedCred = var1;
   }

   final void setCredDelegState(boolean var1) {
      this.credDelegState = var1;
   }

   final void setMutualAuthState(boolean var1) {
      this.mutualAuthState = var1;
   }

   final void setReplayDetState(boolean var1) {
      this.replayDetState = var1;
   }

   final void setSequenceDetState(boolean var1) {
      this.sequenceDetState = var1;
   }

   final void setConfState(boolean var1) {
      this.confState = var1;
   }

   final void setIntegState(boolean var1) {
      this.integState = var1;
   }

   final void setDelegPolicyState(boolean var1) {
      this.delegPolicyState = var1;
   }

   public final void setChannelBinding(ChannelBinding var1) throws GSSException {
      this.channelBinding = var1;
   }

   final ChannelBinding getChannelBinding() {
      return this.channelBinding;
   }

   public final Oid getMech() {
      return Krb5MechFactory.GSS_KRB5_MECH_OID;
   }

   public final GSSNameSpi getSrcName() throws GSSException {
      return this.isInitiator() ? this.myName : this.peerName;
   }

   public final GSSNameSpi getTargName() throws GSSException {
      return !this.isInitiator() ? this.myName : this.peerName;
   }

   public final GSSCredentialSpi getDelegCred() throws GSSException {
      if (this.state != 2 && this.state != 3) {
         throw new GSSException(12);
      } else if (this.isInitiator()) {
         throw new GSSException(13);
      } else {
         this.tryConstrainedDelegation();
         if (this.delegatedCred == null) {
            throw new GSSException(13);
         } else {
            return this.delegatedCred;
         }
      }
   }

   private void tryConstrainedDelegation() {
      if (this.state == 2 || this.state == 3) {
         if (!this.isConstrainedDelegationTried) {
            if (this.delegatedCred == null) {
               if (DEBUG) {
                  System.out.println(">>> Constrained deleg from " + this.caller);
               }

               try {
                  this.delegatedCred = new Krb5ProxyCredential(Krb5InitCredential.getInstance(GSSCaller.CALLER_ACCEPT, this.myName, this.lifetime), this.peerName, this.serviceTicket);
               } catch (GSSException var2) {
               }
            }

            this.isConstrainedDelegationTried = true;
         }

      }
   }

   public final boolean isInitiator() {
      return this.initiator;
   }

   public final boolean isProtReady() {
      return this.state == 3;
   }

   public final byte[] initSecContext(InputStream var1, int var2) throws GSSException {
      byte[] var3 = null;
      InitSecContextToken var4 = null;
      byte var5 = 11;
      if (DEBUG) {
         System.out.println("Entered Krb5Context.initSecContext with state=" + printState(this.state));
      }

      if (!this.isInitiator()) {
         throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
      } else {
         GSSException var7;
         try {
            if (this.state == 1) {
               this.state = 2;
               var5 = 13;
               if (this.myCred == null) {
                  this.myCred = Krb5InitCredential.getInstance(this.caller, this.myName, 0);
               } else if (!this.myCred.isInitiatorCredential()) {
                  throw new GSSException(var5, -1, "No TGT available");
               }

               this.myName = (Krb5NameElement)this.myCred.getName();
               Credentials var6;
               final Krb5ProxyCredential var15;
               if (this.myCred instanceof Krb5InitCredential) {
                  var15 = null;
                  var6 = ((Krb5InitCredential)this.myCred).getKrb5Credentials();
               } else {
                  var15 = (Krb5ProxyCredential)this.myCred;
                  var6 = var15.self.getKrb5Credentials();
               }

               this.checkPermission(this.peerName.getKrb5PrincipalName().getName(), "initiate");
               final AccessControlContext var8 = AccessController.getContext();
               if (GSSUtil.useSubjectCredsOnly(this.caller)) {
                  KerberosTicket var9 = null;

                  try {
                     var9 = (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction<KerberosTicket>() {
                        public KerberosTicket run() throws Exception {
                           return Krb5Util.getTicket(GSSCaller.CALLER_UNKNOWN, var15 == null ? Krb5Context.this.myName.getKrb5PrincipalName().getName() : var15.getName().getKrb5PrincipalName().getName(), Krb5Context.this.peerName.getKrb5PrincipalName().getName(), var8);
                        }
                     });
                  } catch (PrivilegedActionException var11) {
                     if (DEBUG) {
                        System.out.println("Attempt to obtain service ticket from the subject failed!");
                     }
                  }

                  if (var9 != null) {
                     if (DEBUG) {
                        System.out.println("Found service ticket in the subject" + var9);
                     }

                     this.serviceCreds = Krb5Util.ticketToCreds(var9);
                  }
               }

               if (this.serviceCreds == null) {
                  if (DEBUG) {
                     System.out.println("Service ticket not found in the subject");
                  }

                  if (var15 == null) {
                     this.serviceCreds = Credentials.acquireServiceCreds(this.peerName.getKrb5PrincipalName().getName(), var6);
                  } else {
                     this.serviceCreds = Credentials.acquireS4U2proxyCreds(this.peerName.getKrb5PrincipalName().getName(), var15.tkt, var15.getName().getKrb5PrincipalName(), var6);
                  }

                  if (GSSUtil.useSubjectCredsOnly(this.caller)) {
                     final Subject var16 = (Subject)AccessController.doPrivileged(new PrivilegedAction<Subject>() {
                        public Subject run() {
                           return Subject.getSubject(var8);
                        }
                     });
                     if (var16 != null && !var16.isReadOnly()) {
                        final KerberosTicket var10 = Krb5Util.credsToTicket(this.serviceCreds);
                        AccessController.doPrivileged(new PrivilegedAction<Void>() {
                           public Void run() {
                              var16.getPrivateCredentials().add(var10);
                              return null;
                           }
                        });
                     } else if (DEBUG) {
                        System.out.println("Subject is readOnly;Kerberos Service ticket not stored");
                     }
                  }
               }

               boolean var14 = true;
               var4 = new InitSecContextToken(this, var6, this.serviceCreds);
               this.apReq = ((InitSecContextToken)var4).getKrbApReq();
               var3 = var4.encode();
               this.myCred = null;
               if (!this.getMutualAuthState()) {
                  this.state = 3;
               }

               if (DEBUG) {
                  System.out.println("Created InitSecContextToken:\n" + (new HexDumpEncoder()).encodeBuffer(var3));
               }
            } else if (this.state == 2) {
               new AcceptSecContextToken(this, this.serviceCreds, this.apReq, var1);
               this.serviceCreds = null;
               this.apReq = null;
               this.state = 3;
            } else if (DEBUG) {
               System.out.println(this.state);
            }

            return var3;
         } catch (KrbException var12) {
            if (DEBUG) {
               var12.printStackTrace();
            }

            var7 = new GSSException(var5, -1, var12.getMessage());
            var7.initCause(var12);
            throw var7;
         } catch (IOException var13) {
            var7 = new GSSException(var5, -1, var13.getMessage());
            var7.initCause(var13);
            throw var7;
         }
      }
   }

   public final boolean isEstablished() {
      return this.state == 3;
   }

   public final byte[] acceptSecContext(InputStream var1, int var2) throws GSSException {
      byte[] var3 = null;
      if (DEBUG) {
         System.out.println("Entered Krb5Context.acceptSecContext with state=" + printState(this.state));
      }

      if (this.isInitiator()) {
         throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
      } else {
         GSSException var5;
         try {
            if (this.state == 1) {
               this.state = 2;
               if (this.myCred == null) {
                  this.myCred = Krb5AcceptCredential.getInstance(this.caller, this.myName);
               } else if (!this.myCred.isAcceptorCredential()) {
                  throw new GSSException(13, -1, "No Secret Key available");
               }

               this.myName = (Krb5NameElement)this.myCred.getName();
               if (this.myName != null) {
                  Krb5MechFactory.checkAcceptCredPermission(this.myName, this.myName);
               }

               InitSecContextToken var4 = new InitSecContextToken(this, (Krb5AcceptCredential)this.myCred, var1);
               PrincipalName var8 = var4.getKrbApReq().getClient();
               this.peerName = Krb5NameElement.getInstance(var8);
               if (this.myName == null) {
                  this.myName = Krb5NameElement.getInstance(var4.getKrbApReq().getCreds().getServer());
                  Krb5MechFactory.checkAcceptCredPermission(this.myName, this.myName);
               }

               if (this.getMutualAuthState()) {
                  var3 = (new AcceptSecContextToken(this, var4.getKrbApReq())).encode();
               }

               this.serviceTicket = var4.getKrbApReq().getCreds().getTicket();
               this.myCred = null;
               this.state = 3;
            } else if (DEBUG) {
               System.out.println(this.state);
            }

            return var3;
         } catch (KrbException var6) {
            var5 = new GSSException(11, -1, var6.getMessage());
            var5.initCause(var6);
            throw var5;
         } catch (IOException var7) {
            if (DEBUG) {
               var7.printStackTrace();
            }

            var5 = new GSSException(11, -1, var7.getMessage());
            var5.initCause(var7);
            throw var5;
         }
      }
   }

   public final int getWrapSizeLimit(int var1, boolean var2, int var3) throws GSSException {
      int var4 = 0;
      if (this.cipherHelper.getProto() == 0) {
         var4 = WrapToken.getSizeLimit(var1, var2, var3, this.getCipherHelper((EncryptionKey)null));
      } else if (this.cipherHelper.getProto() == 1) {
         var4 = WrapToken_v2.getSizeLimit(var1, var2, var3, this.getCipherHelper((EncryptionKey)null));
      }

      return var4;
   }

   public final byte[] wrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (DEBUG) {
         System.out.println("Krb5Context.wrap: data=[" + getHexBytes(var1, var2, var3) + "]");
      }

      if (this.state != 3) {
         throw new GSSException(12, -1, "Wrap called in invalid state!");
      } else {
         byte[] var5 = null;

         try {
            if (this.cipherHelper.getProto() == 0) {
               WrapToken var6 = new WrapToken(this, var4, var1, var2, var3);
               var5 = var6.encode();
            } else if (this.cipherHelper.getProto() == 1) {
               WrapToken_v2 var10 = new WrapToken_v2(this, var4, var1, var2, var3);
               var5 = var10.encode();
            }

            if (DEBUG) {
               System.out.println("Krb5Context.wrap: token=[" + getHexBytes(var5, 0, var5.length) + "]");
            }

            return var5;
         } catch (IOException var8) {
            Object var9 = null;
            GSSException var7 = new GSSException(11, -1, var8.getMessage());
            var7.initCause(var8);
            throw var7;
         }
      }
   }

   public final int wrap(byte[] var1, int var2, int var3, byte[] var4, int var5, MessageProp var6) throws GSSException {
      if (this.state != 3) {
         throw new GSSException(12, -1, "Wrap called in invalid state!");
      } else {
         int var7 = 0;

         try {
            if (this.cipherHelper.getProto() == 0) {
               WrapToken var8 = new WrapToken(this, var6, var1, var2, var3);
               var7 = var8.encode(var4, var5);
            } else if (this.cipherHelper.getProto() == 1) {
               WrapToken_v2 var12 = new WrapToken_v2(this, var6, var1, var2, var3);
               var7 = var12.encode(var4, var5);
            }

            if (DEBUG) {
               System.out.println("Krb5Context.wrap: token=[" + getHexBytes(var4, var5, var7) + "]");
            }

            return var7;
         } catch (IOException var10) {
            boolean var11 = false;
            GSSException var9 = new GSSException(11, -1, var10.getMessage());
            var9.initCause(var10);
            throw var9;
         }
      }
   }

   public final void wrap(byte[] var1, int var2, int var3, OutputStream var4, MessageProp var5) throws GSSException {
      if (this.state != 3) {
         throw new GSSException(12, -1, "Wrap called in invalid state!");
      } else {
         byte[] var6 = null;

         try {
            if (this.cipherHelper.getProto() == 0) {
               WrapToken var7 = new WrapToken(this, var5, var1, var2, var3);
               var7.encode(var4);
               if (DEBUG) {
                  var6 = var7.encode();
               }
            } else if (this.cipherHelper.getProto() == 1) {
               WrapToken_v2 var10 = new WrapToken_v2(this, var5, var1, var2, var3);
               var10.encode(var4);
               if (DEBUG) {
                  var6 = var10.encode();
               }
            }
         } catch (IOException var9) {
            GSSException var8 = new GSSException(11, -1, var9.getMessage());
            var8.initCause(var9);
            throw var8;
         }

         if (DEBUG) {
            System.out.println("Krb5Context.wrap: token=[" + getHexBytes(var6, 0, var6.length) + "]");
         }

      }
   }

   public final void wrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      byte[] var4;
      try {
         var4 = new byte[var1.available()];
         var1.read(var4);
      } catch (IOException var7) {
         GSSException var6 = new GSSException(11, -1, var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }

      this.wrap(var4, 0, var4.length, var2, var3);
   }

   public final byte[] unwrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (DEBUG) {
         System.out.println("Krb5Context.unwrap: token=[" + getHexBytes(var1, var2, var3) + "]");
      }

      if (this.state != 3) {
         throw new GSSException(12, -1, " Unwrap called in invalid state!");
      } else {
         byte[] var5 = null;
         if (this.cipherHelper.getProto() == 0) {
            WrapToken var6 = new WrapToken(this, var1, var2, var3, var4);
            var5 = var6.getData();
            this.setSequencingAndReplayProps((MessageToken)var6, var4);
         } else if (this.cipherHelper.getProto() == 1) {
            WrapToken_v2 var7 = new WrapToken_v2(this, var1, var2, var3, var4);
            var5 = var7.getData();
            this.setSequencingAndReplayProps((MessageToken_v2)var7, var4);
         }

         if (DEBUG) {
            System.out.println("Krb5Context.unwrap: data=[" + getHexBytes(var5, 0, var5.length) + "]");
         }

         return var5;
      }
   }

   public final int unwrap(byte[] var1, int var2, int var3, byte[] var4, int var5, MessageProp var6) throws GSSException {
      if (this.state != 3) {
         throw new GSSException(12, -1, "Unwrap called in invalid state!");
      } else {
         if (this.cipherHelper.getProto() == 0) {
            WrapToken var7 = new WrapToken(this, var1, var2, var3, var6);
            var3 = var7.getData(var4, var5);
            this.setSequencingAndReplayProps((MessageToken)var7, var6);
         } else if (this.cipherHelper.getProto() == 1) {
            WrapToken_v2 var8 = new WrapToken_v2(this, var1, var2, var3, var6);
            var3 = var8.getData(var4, var5);
            this.setSequencingAndReplayProps((MessageToken_v2)var8, var6);
         }

         return var3;
      }
   }

   public final int unwrap(InputStream var1, byte[] var2, int var3, MessageProp var4) throws GSSException {
      if (this.state != 3) {
         throw new GSSException(12, -1, "Unwrap called in invalid state!");
      } else {
         int var5 = 0;
         if (this.cipherHelper.getProto() == 0) {
            WrapToken var6 = new WrapToken(this, var1, var4);
            var5 = var6.getData(var2, var3);
            this.setSequencingAndReplayProps((MessageToken)var6, var4);
         } else if (this.cipherHelper.getProto() == 1) {
            WrapToken_v2 var7 = new WrapToken_v2(this, var1, var4);
            var5 = var7.getData(var2, var3);
            this.setSequencingAndReplayProps((MessageToken_v2)var7, var4);
         }

         return var5;
      }
   }

   public final void unwrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      if (this.state != 3) {
         throw new GSSException(12, -1, "Unwrap called in invalid state!");
      } else {
         byte[] var4 = null;
         if (this.cipherHelper.getProto() == 0) {
            WrapToken var5 = new WrapToken(this, var1, var3);
            var4 = var5.getData();
            this.setSequencingAndReplayProps((MessageToken)var5, var3);
         } else if (this.cipherHelper.getProto() == 1) {
            WrapToken_v2 var8 = new WrapToken_v2(this, var1, var3);
            var4 = var8.getData();
            this.setSequencingAndReplayProps((MessageToken_v2)var8, var3);
         }

         try {
            var2.write(var4);
         } catch (IOException var7) {
            GSSException var6 = new GSSException(11, -1, var7.getMessage());
            var6.initCause(var7);
            throw var6;
         }
      }
   }

   public final byte[] getMIC(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      byte[] var5 = null;

      try {
         if (this.cipherHelper.getProto() == 0) {
            MicToken var6 = new MicToken(this, var4, var1, var2, var3);
            var5 = var6.encode();
         } else if (this.cipherHelper.getProto() == 1) {
            MicToken_v2 var10 = new MicToken_v2(this, var4, var1, var2, var3);
            var5 = var10.encode();
         }

         return var5;
      } catch (IOException var8) {
         Object var9 = null;
         GSSException var7 = new GSSException(11, -1, var8.getMessage());
         var7.initCause(var8);
         throw var7;
      }
   }

   private int getMIC(byte[] var1, int var2, int var3, byte[] var4, int var5, MessageProp var6) throws GSSException {
      int var7 = 0;

      try {
         if (this.cipherHelper.getProto() == 0) {
            MicToken var8 = new MicToken(this, var6, var1, var2, var3);
            var7 = var8.encode(var4, var5);
         } else if (this.cipherHelper.getProto() == 1) {
            MicToken_v2 var12 = new MicToken_v2(this, var6, var1, var2, var3);
            var7 = var12.encode(var4, var5);
         }

         return var7;
      } catch (IOException var10) {
         boolean var11 = false;
         GSSException var9 = new GSSException(11, -1, var10.getMessage());
         var9.initCause(var10);
         throw var9;
      }
   }

   private void getMIC(byte[] var1, int var2, int var3, OutputStream var4, MessageProp var5) throws GSSException {
      try {
         if (this.cipherHelper.getProto() == 0) {
            MicToken var6 = new MicToken(this, var5, var1, var2, var3);
            var6.encode(var4);
         } else if (this.cipherHelper.getProto() == 1) {
            MicToken_v2 var9 = new MicToken_v2(this, var5, var1, var2, var3);
            var9.encode(var4);
         }

      } catch (IOException var8) {
         GSSException var7 = new GSSException(11, -1, var8.getMessage());
         var7.initCause(var8);
         throw var7;
      }
   }

   public final void getMIC(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      byte[] var4;
      try {
         var4 = new byte[var1.available()];
         var1.read(var4);
      } catch (IOException var7) {
         GSSException var6 = new GSSException(11, -1, var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }

      this.getMIC(var4, 0, var4.length, var2, var3);
   }

   public final void verifyMIC(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, MessageProp var7) throws GSSException {
      if (this.cipherHelper.getProto() == 0) {
         MicToken var8 = new MicToken(this, var1, var2, var3, var7);
         var8.verify(var4, var5, var6);
         this.setSequencingAndReplayProps((MessageToken)var8, var7);
      } else if (this.cipherHelper.getProto() == 1) {
         MicToken_v2 var9 = new MicToken_v2(this, var1, var2, var3, var7);
         var9.verify(var4, var5, var6);
         this.setSequencingAndReplayProps((MessageToken_v2)var9, var7);
      }

   }

   private void verifyMIC(InputStream var1, byte[] var2, int var3, int var4, MessageProp var5) throws GSSException {
      if (this.cipherHelper.getProto() == 0) {
         MicToken var6 = new MicToken(this, var1, var5);
         var6.verify(var2, var3, var4);
         this.setSequencingAndReplayProps((MessageToken)var6, var5);
      } else if (this.cipherHelper.getProto() == 1) {
         MicToken_v2 var7 = new MicToken_v2(this, var1, var5);
         var7.verify(var2, var3, var4);
         this.setSequencingAndReplayProps((MessageToken_v2)var7, var5);
      }

   }

   public final void verifyMIC(InputStream var1, InputStream var2, MessageProp var3) throws GSSException {
      byte[] var4;
      try {
         var4 = new byte[var2.available()];
         var2.read(var4);
      } catch (IOException var7) {
         GSSException var6 = new GSSException(11, -1, var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }

      this.verifyMIC(var1, var4, 0, var4.length, var3);
   }

   public final byte[] export() throws GSSException {
      throw new GSSException(16, -1, "GSS Export Context not available");
   }

   public final void dispose() throws GSSException {
      this.state = 4;
      this.delegatedCred = null;
   }

   public final Provider getProvider() {
      return Krb5MechFactory.PROVIDER;
   }

   private void setSequencingAndReplayProps(MessageToken var1, MessageProp var2) {
      if (this.replayDetState || this.sequenceDetState) {
         int var3 = var1.getSequenceNumber();
         this.peerTokenTracker.getProps(var3, var2);
      }

   }

   private void setSequencingAndReplayProps(MessageToken_v2 var1, MessageProp var2) {
      if (this.replayDetState || this.sequenceDetState) {
         int var3 = var1.getSequenceNumber();
         this.peerTokenTracker.getProps(var3, var2);
      }

   }

   private void checkPermission(String var1, String var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         ServicePermission var4 = new ServicePermission(var1, var2);
         var3.checkPermission(var4);
      }

   }

   private static String getHexBytes(byte[] var0, int var1, int var2) {
      StringBuffer var3 = new StringBuffer();

      for(int var4 = 0; var4 < var2; ++var4) {
         int var5 = var0[var4] >> 4 & 15;
         int var6 = var0[var4] & 15;
         var3.append(Integer.toHexString(var5));
         var3.append(Integer.toHexString(var6));
         var3.append(' ');
      }

      return var3.toString();
   }

   private static String printState(int var0) {
      switch(var0) {
      case 1:
         return "STATE_NEW";
      case 2:
         return "STATE_IN_PROCESS";
      case 3:
         return "STATE_DONE";
      case 4:
         return "STATE_DELETED";
      default:
         return "Unknown state " + var0;
      }
   }

   GSSCaller getCaller() {
      return this.caller;
   }

   public Object inquireSecContext(InquireType var1) throws GSSException {
      if (!this.isEstablished()) {
         throw new GSSException(12, -1, "Security context not established.");
      } else {
         switch(var1) {
         case KRB5_GET_SESSION_KEY:
            return new Krb5Context.KerberosSessionKey(this.key);
         case KRB5_GET_TKT_FLAGS:
            return this.tktFlags.clone();
         case KRB5_GET_AUTHZ_DATA:
            if (this.isInitiator()) {
               throw new GSSException(16, -1, "AuthzData not available on initiator side.");
            }

            return this.authzData == null ? null : this.authzData.clone();
         case KRB5_GET_AUTHTIME:
            return this.authTime;
         default:
            throw new GSSException(16, -1, "Inquire type not supported.");
         }
      }
   }

   public void setTktFlags(boolean[] var1) {
      this.tktFlags = var1;
   }

   public void setAuthTime(String var1) {
      this.authTime = var1;
   }

   public void setAuthzData(AuthorizationDataEntry[] var1) {
      this.authzData = var1;
   }

   static {
      DEBUG = Krb5Util.DEBUG;
   }

   static class KerberosSessionKey implements Key {
      private static final long serialVersionUID = 699307378954123869L;
      private final EncryptionKey key;

      KerberosSessionKey(EncryptionKey var1) {
         this.key = var1;
      }

      public String getAlgorithm() {
         return Integer.toString(this.key.getEType());
      }

      public String getFormat() {
         return "RAW";
      }

      public byte[] getEncoded() {
         return (byte[])this.key.getBytes().clone();
      }

      public String toString() {
         return "Kerberos session key: etype: " + this.key.getEType() + "\n" + (new HexDumpEncoder()).encodeBuffer(this.key.getBytes());
      }
   }
}
