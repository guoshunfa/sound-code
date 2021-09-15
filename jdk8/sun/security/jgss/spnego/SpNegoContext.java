package sun.security.jgss.spnego;

import com.sun.security.jgss.ExtendedGSSContext;
import com.sun.security.jgss.InquireType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSCredentialImpl;
import sun.security.jgss.GSSNameImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;

public class SpNegoContext implements GSSContextSpi {
   private static final int STATE_NEW = 1;
   private static final int STATE_IN_PROCESS = 2;
   private static final int STATE_DONE = 3;
   private static final int STATE_DELETED = 4;
   private int state = 1;
   private boolean credDelegState = false;
   private boolean mutualAuthState = true;
   private boolean replayDetState = true;
   private boolean sequenceDetState = true;
   private boolean confState = true;
   private boolean integState = true;
   private boolean delegPolicyState = false;
   private GSSNameSpi peerName = null;
   private GSSNameSpi myName = null;
   private SpNegoCredElement myCred = null;
   private GSSContext mechContext = null;
   private byte[] DER_mechTypes = null;
   private int lifetime;
   private ChannelBinding channelBinding;
   private boolean initiator;
   private Oid internal_mech = null;
   private final SpNegoMechFactory factory;
   static final boolean DEBUG = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.security.spnego.debug")));

   public SpNegoContext(SpNegoMechFactory var1, GSSNameSpi var2, GSSCredentialSpi var3, int var4) throws GSSException {
      if (var2 == null) {
         throw new IllegalArgumentException("Cannot have null peer name");
      } else if (var3 != null && !(var3 instanceof SpNegoCredElement)) {
         throw new IllegalArgumentException("Wrong cred element type");
      } else {
         this.peerName = var2;
         this.myCred = (SpNegoCredElement)var3;
         this.lifetime = var4;
         this.initiator = true;
         this.factory = var1;
      }
   }

   public SpNegoContext(SpNegoMechFactory var1, GSSCredentialSpi var2) throws GSSException {
      if (var2 != null && !(var2 instanceof SpNegoCredElement)) {
         throw new IllegalArgumentException("Wrong cred element type");
      } else {
         this.myCred = (SpNegoCredElement)var2;
         this.initiator = false;
         this.factory = var1;
      }
   }

   public SpNegoContext(SpNegoMechFactory var1, byte[] var2) throws GSSException {
      throw new GSSException(16, -1, "GSS Import Context not available");
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

   public final void requestDelegPolicy(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.delegPolicyState = var1;
      }

   }

   public final boolean getIntegState() {
      return this.integState;
   }

   public final boolean getDelegPolicyState() {
      return !this.isInitiator() || this.mechContext == null || !(this.mechContext instanceof ExtendedGSSContext) || this.state != 2 && this.state != 3 ? this.delegPolicyState : ((ExtendedGSSContext)this.mechContext).getDelegPolicyState();
   }

   public final void requestCredDeleg(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.credDelegState = var1;
      }

   }

   public final boolean getCredDelegState() {
      return !this.isInitiator() || this.mechContext == null || this.state != 2 && this.state != 3 ? this.credDelegState : this.mechContext.getCredDelegState();
   }

   public final void requestMutualAuth(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.mutualAuthState = var1;
      }

   }

   public final boolean getMutualAuthState() {
      return this.mutualAuthState;
   }

   public final Oid getMech() {
      return this.isEstablished() ? this.getNegotiatedMech() : SpNegoMechFactory.GSS_SPNEGO_MECH_OID;
   }

   public final Oid getNegotiatedMech() {
      return this.internal_mech;
   }

   public final Provider getProvider() {
      return SpNegoMechFactory.PROVIDER;
   }

   public final void dispose() throws GSSException {
      this.mechContext = null;
      this.state = 4;
   }

   public final boolean isInitiator() {
      return this.initiator;
   }

   public final boolean isProtReady() {
      return this.state == 3;
   }

   public final byte[] initSecContext(InputStream var1, int var2) throws GSSException {
      byte[] var3 = null;
      NegTokenInit var4 = null;
      byte[] var5 = null;
      byte var6 = 11;
      if (DEBUG) {
         System.out.println("Entered SpNego.initSecContext with state=" + printState(this.state));
      }

      if (!this.isInitiator()) {
         throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
      } else {
         GSSException var8;
         try {
            if (this.state == 1) {
               this.state = 2;
               boolean var15 = true;
               Oid[] var7 = this.getAvailableMechs();
               this.DER_mechTypes = this.getEncodedMechs(var7);
               this.internal_mech = var7[0];
               var5 = this.GSS_initSecContext((byte[])null);
               var15 = true;
               var4 = new NegTokenInit(this.DER_mechTypes, this.getContextFlags(), var5, (byte[])null);
               if (DEBUG) {
                  System.out.println("SpNegoContext.initSecContext: sending token of type = " + SpNegoToken.getTokenName(var4.getType()));
               }

               var3 = var4.getEncoded();
            } else if (this.state == 2) {
               var6 = 11;
               if (var1 == null) {
                  throw new GSSException(var6, -1, "No token received from peer!");
               }

               var6 = 10;
               byte[] var16 = new byte[var1.available()];
               SpNegoToken.readFully(var1, var16);
               if (DEBUG) {
                  System.out.println("SpNegoContext.initSecContext: process received token = " + SpNegoToken.getHexBytes(var16));
               }

               NegTokenTarg var17 = new NegTokenTarg(var16);
               if (DEBUG) {
                  System.out.println("SpNegoContext.initSecContext: received token of type = " + SpNegoToken.getTokenName(var17.getType()));
               }

               this.internal_mech = var17.getSupportedMech();
               if (this.internal_mech == null) {
                  throw new GSSException(var6, -1, "supported mechanism from server is null");
               }

               SpNegoToken.NegoResult var9 = null;
               int var10 = var17.getNegotiatedResult();
               switch(var10) {
               case 0:
                  var9 = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
                  this.state = 3;
                  break;
               case 1:
                  var9 = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
                  this.state = 2;
                  break;
               case 2:
                  var9 = SpNegoToken.NegoResult.REJECT;
                  this.state = 4;
                  break;
               default:
                  this.state = 3;
               }

               var6 = 2;
               if (var9 == SpNegoToken.NegoResult.REJECT) {
                  throw new GSSException(var6, -1, this.internal_mech.toString());
               }

               var6 = 10;
               if (var9 == SpNegoToken.NegoResult.ACCEPT_COMPLETE || var9 == SpNegoToken.NegoResult.ACCEPT_INCOMPLETE) {
                  byte[] var11 = var17.getResponseToken();
                  if (var11 == null) {
                     if (!this.isMechContextEstablished()) {
                        throw new GSSException(var6, -1, "mechanism token from server is null");
                     }
                  } else {
                     var5 = this.GSS_initSecContext(var11);
                  }

                  if (!GSSUtil.useMSInterop()) {
                     byte[] var12 = var17.getMechListMIC();
                     if (!this.verifyMechListMIC(this.DER_mechTypes, var12)) {
                        throw new GSSException(var6, -1, "verification of MIC on MechList Failed!");
                     }
                  }

                  if (this.isMechContextEstablished()) {
                     this.state = 3;
                     var3 = var5;
                     if (DEBUG) {
                        System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech));
                     }
                  } else {
                     var4 = new NegTokenInit((byte[])null, (BitArray)null, var5, (byte[])null);
                     if (DEBUG) {
                        System.out.println("SpNegoContext.initSecContext: continue sending token of type = " + SpNegoToken.getTokenName(var4.getType()));
                     }

                     var3 = var4.getEncoded();
                  }
               }
            } else if (DEBUG) {
               System.out.println(this.state);
            }

            if (DEBUG && var3 != null) {
               System.out.println("SNegoContext.initSecContext: sending token = " + SpNegoToken.getHexBytes(var3));
            }

            return var3;
         } catch (GSSException var13) {
            var8 = new GSSException(var6, -1, var13.getMessage());
            var8.initCause(var13);
            throw var8;
         } catch (IOException var14) {
            var8 = new GSSException(11, -1, var14.getMessage());
            var8.initCause(var14);
            throw var8;
         }
      }
   }

   public final byte[] acceptSecContext(InputStream var1, int var2) throws GSSException {
      byte[] var3 = null;
      boolean var5 = true;
      if (DEBUG) {
         System.out.println("Entered SpNegoContext.acceptSecContext with state=" + printState(this.state));
      }

      if (this.isInitiator()) {
         throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
      } else {
         try {
            SpNegoToken.NegoResult var4;
            byte[] var6;
            if (this.state != 1) {
               if (this.state == 2) {
                  var6 = new byte[var1.available()];
                  SpNegoToken.readFully(var1, var6);
                  if (DEBUG) {
                     System.out.println("SpNegoContext.acceptSecContext: receiving token = " + SpNegoToken.getHexBytes(var6));
                  }

                  NegTokenTarg var15 = new NegTokenTarg(var6);
                  if (DEBUG) {
                     System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(var15.getType()));
                  }

                  byte[] var16 = var15.getResponseToken();
                  byte[] var17 = this.GSS_acceptSecContext(var16);
                  if (var17 == null) {
                     var5 = false;
                  }

                  if (var5) {
                     if (this.isMechContextEstablished()) {
                        var4 = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
                        this.state = 3;
                     } else {
                        var4 = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
                        this.state = 2;
                     }
                  } else {
                     var4 = SpNegoToken.NegoResult.REJECT;
                     this.state = 3;
                  }

                  NegTokenTarg var18 = new NegTokenTarg(var4.ordinal(), (Oid)null, var17, (byte[])null);
                  if (DEBUG) {
                     System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(var18.getType()));
                  }

                  var3 = var18.getEncoded();
               } else if (DEBUG) {
                  System.out.println("AcceptSecContext: state = " + this.state);
               }
            } else {
               this.state = 2;
               var6 = new byte[var1.available()];
               SpNegoToken.readFully(var1, var6);
               if (DEBUG) {
                  System.out.println("SpNegoContext.acceptSecContext: receiving token = " + SpNegoToken.getHexBytes(var6));
               }

               NegTokenInit var14 = new NegTokenInit(var6);
               if (DEBUG) {
                  System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(var14.getType()));
               }

               Oid[] var8 = var14.getMechTypeList();
               this.DER_mechTypes = var14.getMechTypes();
               if (this.DER_mechTypes == null) {
                  var5 = false;
               }

               Oid[] var9 = this.getAvailableMechs();
               Oid var10 = negotiate_mech_type(var9, var8);
               if (var10 == null) {
                  var5 = false;
               }

               this.internal_mech = var10;
               byte[] var11;
               if (!var8[0].equals(var10) && (!GSSUtil.isKerberosMech(var8[0]) || !GSSUtil.isKerberosMech(var10))) {
                  var11 = null;
               } else {
                  if (DEBUG && !var10.equals(var8[0])) {
                     System.out.println("SpNegoContext.acceptSecContext: negotiated mech adjusted to " + var8[0]);
                  }

                  byte[] var12 = var14.getMechToken();
                  if (var12 == null) {
                     throw new GSSException(11, -1, "mechToken is missing");
                  }

                  var11 = this.GSS_acceptSecContext(var12);
                  var10 = var8[0];
               }

               if (!GSSUtil.useMSInterop() && var5) {
                  var5 = this.verifyMechListMIC(this.DER_mechTypes, var14.getMechListMIC());
               }

               if (var5) {
                  if (this.isMechContextEstablished()) {
                     var4 = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
                     this.state = 3;
                     this.setContextFlags();
                     if (DEBUG) {
                        System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech));
                     }
                  } else {
                     var4 = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
                     this.state = 2;
                  }
               } else {
                  var4 = SpNegoToken.NegoResult.REJECT;
                  this.state = 3;
               }

               if (DEBUG) {
                  System.out.println("SpNegoContext.acceptSecContext: mechanism wanted = " + var10);
                  System.out.println("SpNegoContext.acceptSecContext: negotiated result = " + var4);
               }

               NegTokenTarg var19 = new NegTokenTarg(var4.ordinal(), var10, var11, (byte[])null);
               if (DEBUG) {
                  System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(var19.getType()));
               }

               var3 = var19.getEncoded();
            }

            if (DEBUG) {
               System.out.println("SpNegoContext.acceptSecContext: sending token = " + SpNegoToken.getHexBytes(var3));
            }
         } catch (IOException var13) {
            GSSException var7 = new GSSException(11, -1, var13.getMessage());
            var7.initCause(var13);
            throw var7;
         }

         if (this.state == 3) {
            this.setContextFlags();
         }

         return var3;
      }
   }

   private Oid[] getAvailableMechs() {
      if (this.myCred != null) {
         Oid[] var1 = new Oid[]{this.myCred.getInternalMech()};
         return var1;
      } else {
         return this.factory.availableMechs;
      }
   }

   private byte[] getEncodedMechs(Oid[] var1) throws IOException, GSSException {
      DerOutputStream var2 = new DerOutputStream();

      byte[] var4;
      for(int var3 = 0; var3 < var1.length; ++var3) {
         var4 = var1[var3].getDER();
         var2.write(var4);
      }

      DerOutputStream var5 = new DerOutputStream();
      var5.write((byte)48, (DerOutputStream)var2);
      var4 = var5.toByteArray();
      return var4;
   }

   private BitArray getContextFlags() {
      BitArray var1 = new BitArray(7);
      if (this.getCredDelegState()) {
         var1.set(0, true);
      }

      if (this.getMutualAuthState()) {
         var1.set(1, true);
      }

      if (this.getReplayDetState()) {
         var1.set(2, true);
      }

      if (this.getSequenceDetState()) {
         var1.set(3, true);
      }

      if (this.getConfState()) {
         var1.set(5, true);
      }

      if (this.getIntegState()) {
         var1.set(6, true);
      }

      return var1;
   }

   private void setContextFlags() {
      if (this.mechContext != null) {
         if (this.mechContext.getCredDelegState()) {
            this.credDelegState = true;
         }

         if (!this.mechContext.getMutualAuthState()) {
            this.mutualAuthState = false;
         }

         if (!this.mechContext.getReplayDetState()) {
            this.replayDetState = false;
         }

         if (!this.mechContext.getSequenceDetState()) {
            this.sequenceDetState = false;
         }

         if (!this.mechContext.getIntegState()) {
            this.integState = false;
         }

         if (!this.mechContext.getConfState()) {
            this.confState = false;
         }
      }

   }

   private boolean verifyMechListMIC(byte[] var1, byte[] var2) throws GSSException {
      if (var2 == null) {
         if (DEBUG) {
            System.out.println("SpNegoContext: no MIC token validation");
         }

         return true;
      } else if (!this.mechContext.getIntegState()) {
         if (DEBUG) {
            System.out.println("SpNegoContext: no MIC token validation - mechanism does not support integrity");
         }

         return true;
      } else {
         boolean var3 = false;

         try {
            MessageProp var4 = new MessageProp(0, true);
            this.verifyMIC(var2, 0, var2.length, var1, 0, var1.length, var4);
            var3 = true;
         } catch (GSSException var5) {
            var3 = false;
            if (DEBUG) {
               System.out.println("SpNegoContext: MIC validation failed! " + var5.getMessage());
            }
         }

         return var3;
      }
   }

   private byte[] GSS_initSecContext(byte[] var1) throws GSSException {
      Object var2 = null;
      if (this.mechContext == null) {
         GSSName var3 = this.factory.manager.createName(this.peerName.toString(), this.peerName.getStringNameType(), this.internal_mech);
         GSSCredentialImpl var4 = null;
         if (this.myCred != null) {
            var4 = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
         }

         this.mechContext = this.factory.manager.createContext(var3, this.internal_mech, var4, 0);
         this.mechContext.requestConf(this.confState);
         this.mechContext.requestInteg(this.integState);
         this.mechContext.requestCredDeleg(this.credDelegState);
         this.mechContext.requestMutualAuth(this.mutualAuthState);
         this.mechContext.requestReplayDet(this.replayDetState);
         this.mechContext.requestSequenceDet(this.sequenceDetState);
         if (this.mechContext instanceof ExtendedGSSContext) {
            ((ExtendedGSSContext)this.mechContext).requestDelegPolicy(this.delegPolicyState);
         }
      }

      byte[] var5;
      if (var1 != null) {
         var5 = var1;
      } else {
         var5 = new byte[0];
      }

      byte[] var6 = this.mechContext.initSecContext(var5, 0, var5.length);
      return var6;
   }

   private byte[] GSS_acceptSecContext(byte[] var1) throws GSSException {
      if (this.mechContext == null) {
         GSSCredentialImpl var2 = null;
         if (this.myCred != null) {
            var2 = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
         }

         this.mechContext = this.factory.manager.createContext((GSSCredential)var2);
      }

      byte[] var3 = this.mechContext.acceptSecContext(var1, 0, var1.length);
      return var3;
   }

   private static Oid negotiate_mech_type(Oid[] var0, Oid[] var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3].equals(var0[var2])) {
               if (DEBUG) {
                  System.out.println("SpNegoContext: negotiated mechanism = " + var1[var3]);
               }

               return var1[var3];
            }
         }
      }

      return null;
   }

   public final boolean isEstablished() {
      return this.state == 3;
   }

   public final boolean isMechContextEstablished() {
      if (this.mechContext != null) {
         return this.mechContext.isEstablished();
      } else {
         if (DEBUG) {
            System.out.println("The underlying mechanism context has not been initialized");
         }

         return false;
      }
   }

   public final byte[] export() throws GSSException {
      throw new GSSException(16, -1, "GSS Export Context not available");
   }

   public final void setChannelBinding(ChannelBinding var1) throws GSSException {
      this.channelBinding = var1;
   }

   final ChannelBinding getChannelBinding() {
      return this.channelBinding;
   }

   public final void requestAnonymity(boolean var1) throws GSSException {
   }

   public final boolean getAnonymityState() {
      return false;
   }

   public void requestLifetime(int var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.lifetime = var1;
      }

   }

   public final int getLifetime() {
      return this.mechContext != null ? this.mechContext.getLifetime() : Integer.MAX_VALUE;
   }

   public final boolean isTransferable() throws GSSException {
      return false;
   }

   public final void requestSequenceDet(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.sequenceDetState = var1;
      }

   }

   public final boolean getSequenceDetState() {
      return this.sequenceDetState || this.replayDetState;
   }

   public final void requestReplayDet(boolean var1) throws GSSException {
      if (this.state == 1 && this.isInitiator()) {
         this.replayDetState = var1;
      }

   }

   public final boolean getReplayDetState() {
      return this.replayDetState || this.sequenceDetState;
   }

   public final GSSNameSpi getTargName() throws GSSException {
      if (this.mechContext != null) {
         GSSNameImpl var1 = (GSSNameImpl)this.mechContext.getTargName();
         this.peerName = var1.getElement(this.internal_mech);
         return this.peerName;
      } else {
         if (DEBUG) {
            System.out.println("The underlying mechanism context has not been initialized");
         }

         return null;
      }
   }

   public final GSSNameSpi getSrcName() throws GSSException {
      if (this.mechContext != null) {
         GSSNameImpl var1 = (GSSNameImpl)this.mechContext.getSrcName();
         this.myName = var1.getElement(this.internal_mech);
         return this.myName;
      } else {
         if (DEBUG) {
            System.out.println("The underlying mechanism context has not been initialized");
         }

         return null;
      }
   }

   public final GSSCredentialSpi getDelegCred() throws GSSException {
      if (this.state != 2 && this.state != 3) {
         throw new GSSException(12);
      } else if (this.mechContext != null) {
         GSSCredentialImpl var1 = (GSSCredentialImpl)this.mechContext.getDelegCred();
         if (var1 == null) {
            return null;
         } else {
            boolean var2 = false;
            if (var1.getUsage() == 1) {
               var2 = true;
            }

            GSSCredentialSpi var3 = var1.getElement(this.internal_mech, var2);
            SpNegoCredElement var4 = new SpNegoCredElement(var3);
            return var4.getInternalCred();
         }
      } else {
         throw new GSSException(12, -1, "getDelegCred called in invalid state!");
      }
   }

   public final int getWrapSizeLimit(int var1, boolean var2, int var3) throws GSSException {
      if (this.mechContext != null) {
         return this.mechContext.getWrapSizeLimit(var1, var2, var3);
      } else {
         throw new GSSException(12, -1, "getWrapSizeLimit called in invalid state!");
      }
   }

   public final byte[] wrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (this.mechContext != null) {
         return this.mechContext.wrap(var1, var2, var3, var4);
      } else {
         throw new GSSException(12, -1, "Wrap called in invalid state!");
      }
   }

   public final void wrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      if (this.mechContext != null) {
         this.mechContext.wrap(var1, var2, var3);
      } else {
         throw new GSSException(12, -1, "Wrap called in invalid state!");
      }
   }

   public final byte[] unwrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (this.mechContext != null) {
         return this.mechContext.unwrap(var1, var2, var3, var4);
      } else {
         throw new GSSException(12, -1, "UnWrap called in invalid state!");
      }
   }

   public final void unwrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      if (this.mechContext != null) {
         this.mechContext.unwrap(var1, var2, var3);
      } else {
         throw new GSSException(12, -1, "UnWrap called in invalid state!");
      }
   }

   public final byte[] getMIC(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (this.mechContext != null) {
         return this.mechContext.getMIC(var1, var2, var3, var4);
      } else {
         throw new GSSException(12, -1, "getMIC called in invalid state!");
      }
   }

   public final void getMIC(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      if (this.mechContext != null) {
         this.mechContext.getMIC(var1, var2, var3);
      } else {
         throw new GSSException(12, -1, "getMIC called in invalid state!");
      }
   }

   public final void verifyMIC(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, MessageProp var7) throws GSSException {
      if (this.mechContext != null) {
         this.mechContext.verifyMIC(var1, var2, var3, var4, var5, var6, var7);
      } else {
         throw new GSSException(12, -1, "verifyMIC called in invalid state!");
      }
   }

   public final void verifyMIC(InputStream var1, InputStream var2, MessageProp var3) throws GSSException {
      if (this.mechContext != null) {
         this.mechContext.verifyMIC(var1, var2, var3);
      } else {
         throw new GSSException(12, -1, "verifyMIC called in invalid state!");
      }
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

   public Object inquireSecContext(InquireType var1) throws GSSException {
      if (this.mechContext == null) {
         throw new GSSException(12, -1, "Underlying mech not established.");
      } else if (this.mechContext instanceof ExtendedGSSContext) {
         return ((ExtendedGSSContext)this.mechContext).inquireSecContext(var1);
      } else {
         throw new GSSException(2, -1, "inquireSecContext not supported by underlying mech.");
      }
   }
}
