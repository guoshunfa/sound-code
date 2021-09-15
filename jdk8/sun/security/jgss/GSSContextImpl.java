package sun.security.jgss;

import com.sun.security.jgss.ExtendedGSSContext;
import com.sun.security.jgss.InquireSecContextPermission;
import com.sun.security.jgss.InquireType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.ObjectIdentifier;

class GSSContextImpl implements ExtendedGSSContext {
   private final GSSManagerImpl gssManager;
   private final boolean initiator;
   private static final int PRE_INIT = 1;
   private static final int IN_PROGRESS = 2;
   private static final int READY = 3;
   private static final int DELETED = 4;
   private int currentState = 1;
   private GSSContextSpi mechCtxt = null;
   private Oid mechOid = null;
   private ObjectIdentifier objId = null;
   private GSSCredentialImpl myCred = null;
   private GSSNameImpl srcName = null;
   private GSSNameImpl targName = null;
   private int reqLifetime = Integer.MAX_VALUE;
   private ChannelBinding channelBindings = null;
   private boolean reqConfState = true;
   private boolean reqIntegState = true;
   private boolean reqMutualAuthState = true;
   private boolean reqReplayDetState = true;
   private boolean reqSequenceDetState = true;
   private boolean reqCredDelegState = false;
   private boolean reqAnonState = false;
   private boolean reqDelegPolicyState = false;

   public GSSContextImpl(GSSManagerImpl var1, GSSName var2, Oid var3, GSSCredential var4, int var5) throws GSSException {
      if (var2 != null && var2 instanceof GSSNameImpl) {
         if (var3 == null) {
            var3 = ProviderList.DEFAULT_MECH_OID;
         }

         this.gssManager = var1;
         this.myCred = (GSSCredentialImpl)var4;
         this.reqLifetime = var5;
         this.targName = (GSSNameImpl)var2;
         this.mechOid = var3;
         this.initiator = true;
      } else {
         throw new GSSException(3);
      }
   }

   public GSSContextImpl(GSSManagerImpl var1, GSSCredential var2) throws GSSException {
      this.gssManager = var1;
      this.myCred = (GSSCredentialImpl)var2;
      this.initiator = false;
   }

   public GSSContextImpl(GSSManagerImpl var1, byte[] var2) throws GSSException {
      this.gssManager = var1;
      this.mechCtxt = var1.getMechanismContext(var2);
      this.initiator = this.mechCtxt.isInitiator();
      this.mechOid = this.mechCtxt.getMech();
   }

   public byte[] initSecContext(byte[] var1, int var2, int var3) throws GSSException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(600);
      ByteArrayInputStream var5 = new ByteArrayInputStream(var1, var2, var3);
      int var6 = this.initSecContext(var5, var4);
      return var6 == 0 ? null : var4.toByteArray();
   }

   public int initSecContext(InputStream var1, OutputStream var2) throws GSSException {
      if (this.mechCtxt != null && this.currentState != 2) {
         throw new GSSExceptionImpl(11, "Illegal call to initSecContext");
      } else {
         GSSHeader var3 = null;
         int var4 = -1;
         GSSCredentialSpi var5 = null;
         boolean var6 = false;

         try {
            if (this.mechCtxt != null) {
               if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && !GSSUtil.isSpNegoMech(this.mechOid)) {
                  var3 = new GSSHeader(var1);
                  if (!var3.getOid().equals((Object)this.objId)) {
                     throw new GSSExceptionImpl(10, "Mechanism not equal to " + this.mechOid.toString() + " in initSecContext token");
                  }

                  var4 = var3.getMechTokenLength();
               }
            } else {
               if (this.myCred != null) {
                  try {
                     var5 = this.myCred.getElement(this.mechOid, true);
                  } catch (GSSException var9) {
                     if (!GSSUtil.isSpNegoMech(this.mechOid) || var9.getMajor() != 13) {
                        throw var9;
                     }

                     var5 = this.myCred.getElement(this.myCred.getMechs()[0], true);
                  }
               }

               GSSNameSpi var7 = this.targName.getElement(this.mechOid);
               this.mechCtxt = this.gssManager.getMechanismContext(var7, var5, this.reqLifetime, this.mechOid);
               this.mechCtxt.requestConf(this.reqConfState);
               this.mechCtxt.requestInteg(this.reqIntegState);
               this.mechCtxt.requestCredDeleg(this.reqCredDelegState);
               this.mechCtxt.requestMutualAuth(this.reqMutualAuthState);
               this.mechCtxt.requestReplayDet(this.reqReplayDetState);
               this.mechCtxt.requestSequenceDet(this.reqSequenceDetState);
               this.mechCtxt.requestAnonymity(this.reqAnonState);
               this.mechCtxt.setChannelBinding(this.channelBindings);
               this.mechCtxt.requestDelegPolicy(this.reqDelegPolicyState);
               this.objId = new ObjectIdentifier(this.mechOid.toString());
               this.currentState = 2;
               var6 = true;
            }

            byte[] var11 = this.mechCtxt.initSecContext(var1, var4);
            int var8 = 0;
            if (var11 != null) {
               var8 = var11.length;
               if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && (var6 || !GSSUtil.isSpNegoMech(this.mechOid))) {
                  var3 = new GSSHeader(this.objId, var11.length);
                  var8 += var3.encode(var2);
               }

               var2.write(var11);
            }

            if (this.mechCtxt.isEstablished()) {
               this.currentState = 3;
            }

            return var8;
         } catch (IOException var10) {
            throw new GSSExceptionImpl(10, var10.getMessage());
         }
      }
   }

   public byte[] acceptSecContext(byte[] var1, int var2, int var3) throws GSSException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(100);
      this.acceptSecContext(new ByteArrayInputStream(var1, var2, var3), var4);
      byte[] var5 = var4.toByteArray();
      return var5.length == 0 ? null : var5;
   }

   public void acceptSecContext(InputStream var1, OutputStream var2) throws GSSException {
      if (this.mechCtxt != null && this.currentState != 2) {
         throw new GSSExceptionImpl(11, "Illegal call to acceptSecContext");
      } else {
         GSSHeader var3 = null;
         int var4 = -1;
         GSSCredentialSpi var5 = null;

         try {
            if (this.mechCtxt == null) {
               var3 = new GSSHeader(var1);
               var4 = var3.getMechTokenLength();
               this.objId = var3.getOid();
               this.mechOid = new Oid(this.objId.toString());
               if (this.myCred != null) {
                  var5 = this.myCred.getElement(this.mechOid, false);
               }

               this.mechCtxt = this.gssManager.getMechanismContext(var5, this.mechOid);
               this.mechCtxt.setChannelBinding(this.channelBindings);
               this.currentState = 2;
            } else if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && !GSSUtil.isSpNegoMech(this.mechOid)) {
               var3 = new GSSHeader(var1);
               if (!var3.getOid().equals((Object)this.objId)) {
                  throw new GSSExceptionImpl(10, "Mechanism not equal to " + this.mechOid.toString() + " in acceptSecContext token");
               }

               var4 = var3.getMechTokenLength();
            }

            byte[] var6 = this.mechCtxt.acceptSecContext(var1, var4);
            if (var6 != null) {
               int var7 = var6.length;
               if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && !GSSUtil.isSpNegoMech(this.mechOid)) {
                  var3 = new GSSHeader(this.objId, var6.length);
                  int var10000 = var7 + var3.encode(var2);
               }

               var2.write(var6);
            }

            if (this.mechCtxt.isEstablished()) {
               this.currentState = 3;
            }

         } catch (IOException var8) {
            throw new GSSExceptionImpl(10, var8.getMessage());
         }
      }
   }

   public boolean isEstablished() {
      if (this.mechCtxt == null) {
         return false;
      } else {
         return this.currentState == 3;
      }
   }

   public int getWrapSizeLimit(int var1, boolean var2, int var3) throws GSSException {
      if (this.mechCtxt != null) {
         return this.mechCtxt.getWrapSizeLimit(var1, var2, var3);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public byte[] wrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (this.mechCtxt != null) {
         return this.mechCtxt.wrap(var1, var2, var3, var4);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public void wrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      if (this.mechCtxt != null) {
         this.mechCtxt.wrap(var1, var2, var3);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public byte[] unwrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (this.mechCtxt != null) {
         return this.mechCtxt.unwrap(var1, var2, var3, var4);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public void unwrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      if (this.mechCtxt != null) {
         this.mechCtxt.unwrap(var1, var2, var3);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public byte[] getMIC(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (this.mechCtxt != null) {
         return this.mechCtxt.getMIC(var1, var2, var3, var4);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public void getMIC(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      if (this.mechCtxt != null) {
         this.mechCtxt.getMIC(var1, var2, var3);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public void verifyMIC(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, MessageProp var7) throws GSSException {
      if (this.mechCtxt != null) {
         this.mechCtxt.verifyMIC(var1, var2, var3, var4, var5, var6, var7);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public void verifyMIC(InputStream var1, InputStream var2, MessageProp var3) throws GSSException {
      if (this.mechCtxt != null) {
         this.mechCtxt.verifyMIC(var1, var2, var3);
      } else {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      }
   }

   public byte[] export() throws GSSException {
      byte[] var1 = null;
      if (this.mechCtxt.isTransferable() && this.mechCtxt.getProvider().getName().equals("SunNativeGSS")) {
         var1 = this.mechCtxt.export();
      }

      return var1;
   }

   public void requestMutualAuth(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqMutualAuthState = var1;
      }

   }

   public void requestReplayDet(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqReplayDetState = var1;
      }

   }

   public void requestSequenceDet(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqSequenceDetState = var1;
      }

   }

   public void requestCredDeleg(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqCredDelegState = var1;
      }

   }

   public void requestAnonymity(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqAnonState = var1;
      }

   }

   public void requestConf(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqConfState = var1;
      }

   }

   public void requestInteg(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqIntegState = var1;
      }

   }

   public void requestLifetime(int var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqLifetime = var1;
      }

   }

   public void setChannelBinding(ChannelBinding var1) throws GSSException {
      if (this.mechCtxt == null) {
         this.channelBindings = var1;
      }

   }

   public boolean getCredDelegState() {
      return this.mechCtxt != null ? this.mechCtxt.getCredDelegState() : this.reqCredDelegState;
   }

   public boolean getMutualAuthState() {
      return this.mechCtxt != null ? this.mechCtxt.getMutualAuthState() : this.reqMutualAuthState;
   }

   public boolean getReplayDetState() {
      return this.mechCtxt != null ? this.mechCtxt.getReplayDetState() : this.reqReplayDetState;
   }

   public boolean getSequenceDetState() {
      return this.mechCtxt != null ? this.mechCtxt.getSequenceDetState() : this.reqSequenceDetState;
   }

   public boolean getAnonymityState() {
      return this.mechCtxt != null ? this.mechCtxt.getAnonymityState() : this.reqAnonState;
   }

   public boolean isTransferable() throws GSSException {
      return this.mechCtxt != null ? this.mechCtxt.isTransferable() : false;
   }

   public boolean isProtReady() {
      return this.mechCtxt != null ? this.mechCtxt.isProtReady() : false;
   }

   public boolean getConfState() {
      return this.mechCtxt != null ? this.mechCtxt.getConfState() : this.reqConfState;
   }

   public boolean getIntegState() {
      return this.mechCtxt != null ? this.mechCtxt.getIntegState() : this.reqIntegState;
   }

   public int getLifetime() {
      return this.mechCtxt != null ? this.mechCtxt.getLifetime() : this.reqLifetime;
   }

   public GSSName getSrcName() throws GSSException {
      if (this.srcName == null) {
         this.srcName = GSSNameImpl.wrapElement(this.gssManager, this.mechCtxt.getSrcName());
      }

      return this.srcName;
   }

   public GSSName getTargName() throws GSSException {
      if (this.targName == null) {
         this.targName = GSSNameImpl.wrapElement(this.gssManager, this.mechCtxt.getTargName());
      }

      return this.targName;
   }

   public Oid getMech() throws GSSException {
      return this.mechCtxt != null ? this.mechCtxt.getMech() : this.mechOid;
   }

   public GSSCredential getDelegCred() throws GSSException {
      if (this.mechCtxt == null) {
         throw new GSSExceptionImpl(12, "No mechanism context yet!");
      } else {
         GSSCredentialSpi var1 = this.mechCtxt.getDelegCred();
         return var1 == null ? null : new GSSCredentialImpl(this.gssManager, var1);
      }
   }

   public boolean isInitiator() throws GSSException {
      return this.initiator;
   }

   public void dispose() throws GSSException {
      this.currentState = 4;
      if (this.mechCtxt != null) {
         this.mechCtxt.dispose();
         this.mechCtxt = null;
      }

      this.myCred = null;
      this.srcName = null;
      this.targName = null;
   }

   public Object inquireSecContext(InquireType var1) throws GSSException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new InquireSecContextPermission(var1.toString()));
      }

      if (this.mechCtxt == null) {
         throw new GSSException(12);
      } else {
         return this.mechCtxt.inquireSecContext(var1);
      }
   }

   public void requestDelegPolicy(boolean var1) throws GSSException {
      if (this.mechCtxt == null && this.initiator) {
         this.reqDelegPolicyState = var1;
      }

   }

   public boolean getDelegPolicyState() {
      return this.mechCtxt != null ? this.mechCtxt.getDelegPolicyState() : this.reqDelegPolicyState;
   }
}
