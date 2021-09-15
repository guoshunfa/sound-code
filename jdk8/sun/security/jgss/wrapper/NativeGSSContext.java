package sun.security.jgss.wrapper;

import com.sun.security.jgss.InquireType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import javax.security.auth.kerberos.DelegationPermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.jgss.GSSHeader;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.NegTokenInit;
import sun.security.jgss.spnego.NegTokenTarg;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

class NativeGSSContext implements GSSContextSpi {
   private static final int GSS_C_DELEG_FLAG = 1;
   private static final int GSS_C_MUTUAL_FLAG = 2;
   private static final int GSS_C_REPLAY_FLAG = 4;
   private static final int GSS_C_SEQUENCE_FLAG = 8;
   private static final int GSS_C_CONF_FLAG = 16;
   private static final int GSS_C_INTEG_FLAG = 32;
   private static final int GSS_C_ANON_FLAG = 64;
   private static final int GSS_C_PROT_READY_FLAG = 128;
   private static final int GSS_C_TRANS_FLAG = 256;
   private static final int NUM_OF_INQUIRE_VALUES = 6;
   private long pContext = 0L;
   private GSSNameElement srcName;
   private GSSNameElement targetName;
   private GSSCredElement cred;
   private boolean isInitiator;
   private boolean isEstablished;
   private Oid actualMech;
   private ChannelBinding cb;
   private GSSCredElement delegatedCred;
   private int flags;
   private int lifetime = 0;
   private final GSSLibStub cStub;
   private boolean skipDelegPermCheck;
   private boolean skipServicePermCheck;

   private static Oid getMechFromSpNegoToken(byte[] var0, boolean var1) throws GSSException {
      Oid var2 = null;
      if (var1) {
         GSSHeader var3 = null;

         try {
            var3 = new GSSHeader(new ByteArrayInputStream(var0));
         } catch (IOException var8) {
            throw new GSSExceptionImpl(11, var8);
         }

         int var4 = var3.getMechTokenLength();
         byte[] var5 = new byte[var4];
         System.arraycopy(var0, var0.length - var4, var5, 0, var5.length);
         NegTokenInit var6 = new NegTokenInit(var5);
         if (var6.getMechToken() != null) {
            Oid[] var7 = var6.getMechTypeList();
            var2 = var7[0];
         }
      } else {
         NegTokenTarg var9 = new NegTokenTarg(var0);
         var2 = var9.getSupportedMech();
      }

      return var2;
   }

   private void doServicePermCheck() throws GSSException {
      if (System.getSecurityManager() != null) {
         String var1 = this.isInitiator ? "initiate" : "accept";
         String var3;
         if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && this.isInitiator && !this.isEstablished) {
            if (this.srcName == null) {
               GSSCredElement var2 = new GSSCredElement((GSSNameElement)null, this.lifetime, 1, GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID));
               var2.dispose();
            } else {
               var3 = Krb5Util.getTGSName(this.srcName);
               Krb5Util.checkServicePermission(var3, var1);
            }
         }

         var3 = this.targetName.getKrbName();
         Krb5Util.checkServicePermission(var3, var1);
         this.skipServicePermCheck = true;
      }

   }

   private void doDelegPermCheck() throws GSSException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         String var2 = this.targetName.getKrbName();
         String var3 = Krb5Util.getTGSName(this.targetName);
         StringBuffer var4 = new StringBuffer("\"");
         var4.append(var2).append("\" \"");
         var4.append(var3).append('"');
         String var5 = var4.toString();
         SunNativeProvider.debug("Checking DelegationPermission (" + var5 + ")");
         DelegationPermission var6 = new DelegationPermission(var5);
         var1.checkPermission(var6);
         this.skipDelegPermCheck = true;
      }

   }

   private byte[] retrieveToken(InputStream var1, int var2) throws GSSException {
      try {
         Object var3 = null;
         byte[] var9;
         if (var2 != -1) {
            SunNativeProvider.debug("Precomputed mechToken length: " + var2);
            GSSHeader var4 = new GSSHeader(new ObjectIdentifier(this.cStub.getMech().toString()), var2);
            ByteArrayOutputStream var5 = new ByteArrayOutputStream(600);
            byte[] var6 = new byte[var2];
            int var7 = var1.read(var6);

            assert var2 == var7;

            var4.encode(var5);
            var5.write(var6);
            var9 = var5.toByteArray();
         } else {
            assert var2 == -1;

            DerValue var10 = new DerValue(var1);
            var9 = var10.toByteArray();
         }

         SunNativeProvider.debug("Complete Token length: " + var9.length);
         return var9;
      } catch (IOException var8) {
         throw new GSSExceptionImpl(11, var8);
      }
   }

   NativeGSSContext(GSSNameElement var1, GSSCredElement var2, int var3, GSSLibStub var4) throws GSSException {
      if (var1 == null) {
         throw new GSSException(11, 1, "null peer");
      } else {
         this.cStub = var4;
         this.cred = var2;
         this.targetName = var1;
         this.isInitiator = true;
         this.lifetime = var3;
         if (GSSUtil.isKerberosMech(this.cStub.getMech())) {
            this.doServicePermCheck();
            if (this.cred == null) {
               this.cred = new GSSCredElement((GSSNameElement)null, this.lifetime, 1, this.cStub);
            }

            this.srcName = this.cred.getName();
         }

      }
   }

   NativeGSSContext(GSSCredElement var1, GSSLibStub var2) throws GSSException {
      this.cStub = var2;
      this.cred = var1;
      if (this.cred != null) {
         this.targetName = this.cred.getName();
      }

      this.isInitiator = false;
      if (GSSUtil.isKerberosMech(this.cStub.getMech()) && this.targetName != null) {
         this.doServicePermCheck();
      }

   }

   NativeGSSContext(long var1, GSSLibStub var3) throws GSSException {
      assert this.pContext != 0L;

      this.pContext = var1;
      this.cStub = var3;
      long[] var4 = this.cStub.inquireContext(this.pContext);
      if (var4.length != 6) {
         throw new RuntimeException("Bug w/ GSSLibStub.inquireContext()");
      } else {
         this.srcName = new GSSNameElement(var4[0], this.cStub);
         this.targetName = new GSSNameElement(var4[1], this.cStub);
         this.isInitiator = var4[2] != 0L;
         this.isEstablished = var4[3] != 0L;
         this.flags = (int)var4[4];
         this.lifetime = (int)var4[5];
         Oid var5 = this.cStub.getMech();
         if (GSSUtil.isSpNegoMech(var5) || GSSUtil.isKerberosMech(var5)) {
            this.doServicePermCheck();
         }

      }
   }

   public Provider getProvider() {
      return SunNativeProvider.INSTANCE;
   }

   public byte[] initSecContext(InputStream var1, int var2) throws GSSException {
      byte[] var3 = null;
      if (!this.isEstablished && this.isInitiator) {
         byte[] var4 = null;
         if (this.pContext != 0L) {
            var4 = this.retrieveToken(var1, var2);
            SunNativeProvider.debug("initSecContext=> inToken len=" + var4.length);
         }

         if (!this.getCredDelegState()) {
            this.skipDelegPermCheck = true;
         }

         if (GSSUtil.isKerberosMech(this.cStub.getMech()) && !this.skipDelegPermCheck) {
            this.doDelegPermCheck();
         }

         long var5 = this.cred == null ? 0L : this.cred.pCred;
         var3 = this.cStub.initContext(var5, this.targetName.pName, this.cb, var4, this);
         SunNativeProvider.debug("initSecContext=> outToken len=" + (var3 == null ? 0 : var3.length));
         if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && var3 != null) {
            this.actualMech = getMechFromSpNegoToken(var3, true);
            if (GSSUtil.isKerberosMech(this.actualMech)) {
               if (!this.skipServicePermCheck) {
                  this.doServicePermCheck();
               }

               if (!this.skipDelegPermCheck) {
                  this.doDelegPermCheck();
               }
            }
         }

         if (this.isEstablished) {
            if (this.srcName == null) {
               this.srcName = new GSSNameElement(this.cStub.getContextName(this.pContext, true), this.cStub);
            }

            if (this.cred == null) {
               this.cred = new GSSCredElement(this.srcName, this.lifetime, 1, this.cStub);
            }
         }
      }

      return var3;
   }

   public byte[] acceptSecContext(InputStream var1, int var2) throws GSSException {
      byte[] var3 = null;
      if (!this.isEstablished && !this.isInitiator) {
         byte[] var4 = this.retrieveToken(var1, var2);
         SunNativeProvider.debug("acceptSecContext=> inToken len=" + var4.length);
         long var5 = this.cred == null ? 0L : this.cred.pCred;
         var3 = this.cStub.acceptContext(var5, this.cb, var4, this);
         SunNativeProvider.debug("acceptSecContext=> outToken len=" + (var3 == null ? 0 : var3.length));
         if (this.targetName == null) {
            this.targetName = new GSSNameElement(this.cStub.getContextName(this.pContext, false), this.cStub);
            if (this.cred != null) {
               this.cred.dispose();
            }

            this.cred = new GSSCredElement(this.targetName, this.lifetime, 2, this.cStub);
         }

         if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && var3 != null && !this.skipServicePermCheck && GSSUtil.isKerberosMech(getMechFromSpNegoToken(var3, false))) {
            this.doServicePermCheck();
         }
      }

      return var3;
   }

   public boolean isEstablished() {
      return this.isEstablished;
   }

   public void dispose() throws GSSException {
      this.srcName = null;
      this.targetName = null;
      this.cred = null;
      this.delegatedCred = null;
      if (this.pContext != 0L) {
         this.pContext = this.cStub.deleteContext(this.pContext);
         this.pContext = 0L;
      }

   }

   public int getWrapSizeLimit(int var1, boolean var2, int var3) throws GSSException {
      return this.cStub.wrapSizeLimit(this.pContext, var2 ? 1 : 0, var1, var3);
   }

   public byte[] wrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      byte[] var5 = var1;
      if (var2 != 0 || var3 != var1.length) {
         var5 = new byte[var3];
         System.arraycopy(var1, var2, var5, 0, var3);
      }

      return this.cStub.wrap(this.pContext, var5, var4);
   }

   public void wrap(byte[] var1, int var2, int var3, OutputStream var4, MessageProp var5) throws GSSException {
      try {
         byte[] var6 = this.wrap(var1, var2, var3, var5);
         var4.write(var6);
      } catch (IOException var7) {
         throw new GSSExceptionImpl(11, var7);
      }
   }

   public int wrap(byte[] var1, int var2, int var3, byte[] var4, int var5, MessageProp var6) throws GSSException {
      byte[] var7 = this.wrap(var1, var2, var3, var6);
      System.arraycopy(var7, 0, var4, var5, var7.length);
      return var7.length;
   }

   public void wrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      try {
         byte[] var4 = new byte[var1.available()];
         int var5 = var1.read(var4);
         byte[] var6 = this.wrap(var4, 0, var5, var3);
         var2.write(var6);
      } catch (IOException var7) {
         throw new GSSExceptionImpl(11, var7);
      }
   }

   public byte[] unwrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      if (var2 == 0 && var3 == var1.length) {
         return this.cStub.unwrap(this.pContext, var1, var4);
      } else {
         byte[] var5 = new byte[var3];
         System.arraycopy(var1, var2, var5, 0, var3);
         return this.cStub.unwrap(this.pContext, var5, var4);
      }
   }

   public int unwrap(byte[] var1, int var2, int var3, byte[] var4, int var5, MessageProp var6) throws GSSException {
      Object var7 = null;
      byte[] var9;
      if (var2 == 0 && var3 == var1.length) {
         var9 = this.cStub.unwrap(this.pContext, var1, var6);
      } else {
         byte[] var8 = new byte[var3];
         System.arraycopy(var1, var2, var8, 0, var3);
         var9 = this.cStub.unwrap(this.pContext, var8, var6);
      }

      System.arraycopy(var9, 0, var4, var5, var9.length);
      return var9.length;
   }

   public void unwrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      try {
         byte[] var4 = new byte[var1.available()];
         int var5 = var1.read(var4);
         byte[] var6 = this.unwrap(var4, 0, var5, var3);
         var2.write(var6);
         var2.flush();
      } catch (IOException var7) {
         throw new GSSExceptionImpl(11, var7);
      }
   }

   public int unwrap(InputStream var1, byte[] var2, int var3, MessageProp var4) throws GSSException {
      Object var5 = null;
      boolean var6 = false;

      byte[] var9;
      int var10;
      try {
         var9 = new byte[var1.available()];
         var10 = var1.read(var9);
         this.unwrap(var9, 0, var10, var4);
      } catch (IOException var8) {
         throw new GSSExceptionImpl(11, var8);
      }

      byte[] var7 = this.unwrap(var9, 0, var10, var4);
      System.arraycopy(var7, 0, var2, var3, var7.length);
      return var7.length;
   }

   public byte[] getMIC(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException {
      int var5 = var4 == null ? 0 : var4.getQOP();
      byte[] var6 = var1;
      if (var2 != 0 || var3 != var1.length) {
         var6 = new byte[var3];
         System.arraycopy(var1, var2, var6, 0, var3);
      }

      return this.cStub.getMic(this.pContext, var5, var6);
   }

   public void getMIC(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException {
      try {
         boolean var4 = false;
         byte[] var5 = new byte[var1.available()];
         int var8 = var1.read(var5);
         byte[] var6 = this.getMIC(var5, 0, var8, var3);
         if (var6 != null && var6.length != 0) {
            var2.write(var6);
         }

      } catch (IOException var7) {
         throw new GSSExceptionImpl(11, var7);
      }
   }

   public void verifyMIC(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, MessageProp var7) throws GSSException {
      byte[] var8 = var1;
      byte[] var9 = var4;
      if (var2 != 0 || var3 != var1.length) {
         var8 = new byte[var3];
         System.arraycopy(var1, var2, var8, 0, var3);
      }

      if (var5 != 0 || var6 != var4.length) {
         var9 = new byte[var6];
         System.arraycopy(var4, var5, var9, 0, var6);
      }

      this.cStub.verifyMic(this.pContext, var8, var9, var7);
   }

   public void verifyMIC(InputStream var1, InputStream var2, MessageProp var3) throws GSSException {
      try {
         byte[] var4 = new byte[var2.available()];
         int var5 = var2.read(var4);
         byte[] var6 = new byte[var1.available()];
         int var7 = var1.read(var6);
         this.verifyMIC(var6, 0, var7, var4, 0, var5, var3);
      } catch (IOException var8) {
         throw new GSSExceptionImpl(11, var8);
      }
   }

   public byte[] export() throws GSSException {
      byte[] var1 = this.cStub.exportContext(this.pContext);
      this.pContext = 0L;
      return var1;
   }

   private void changeFlags(int var1, boolean var2) {
      if (this.isInitiator && this.pContext == 0L) {
         if (var2) {
            this.flags |= var1;
         } else {
            this.flags &= ~var1;
         }
      }

   }

   public void requestMutualAuth(boolean var1) throws GSSException {
      this.changeFlags(2, var1);
   }

   public void requestReplayDet(boolean var1) throws GSSException {
      this.changeFlags(4, var1);
   }

   public void requestSequenceDet(boolean var1) throws GSSException {
      this.changeFlags(8, var1);
   }

   public void requestCredDeleg(boolean var1) throws GSSException {
      this.changeFlags(1, var1);
   }

   public void requestAnonymity(boolean var1) throws GSSException {
      this.changeFlags(64, var1);
   }

   public void requestConf(boolean var1) throws GSSException {
      this.changeFlags(16, var1);
   }

   public void requestInteg(boolean var1) throws GSSException {
      this.changeFlags(32, var1);
   }

   public void requestDelegPolicy(boolean var1) throws GSSException {
   }

   public void requestLifetime(int var1) throws GSSException {
      if (this.isInitiator && this.pContext == 0L) {
         this.lifetime = var1;
      }

   }

   public void setChannelBinding(ChannelBinding var1) throws GSSException {
      if (this.pContext == 0L) {
         this.cb = var1;
      }

   }

   private boolean checkFlags(int var1) {
      return (this.flags & var1) != 0;
   }

   public boolean getCredDelegState() {
      return this.checkFlags(1);
   }

   public boolean getMutualAuthState() {
      return this.checkFlags(2);
   }

   public boolean getReplayDetState() {
      return this.checkFlags(4);
   }

   public boolean getSequenceDetState() {
      return this.checkFlags(8);
   }

   public boolean getAnonymityState() {
      return this.checkFlags(64);
   }

   public boolean isTransferable() throws GSSException {
      return this.checkFlags(256);
   }

   public boolean isProtReady() {
      return this.checkFlags(128);
   }

   public boolean getConfState() {
      return this.checkFlags(16);
   }

   public boolean getIntegState() {
      return this.checkFlags(32);
   }

   public boolean getDelegPolicyState() {
      return false;
   }

   public int getLifetime() {
      return this.cStub.getContextTime(this.pContext);
   }

   public GSSNameSpi getSrcName() throws GSSException {
      return this.srcName;
   }

   public GSSNameSpi getTargName() throws GSSException {
      return this.targetName;
   }

   public Oid getMech() throws GSSException {
      return this.isEstablished && this.actualMech != null ? this.actualMech : this.cStub.getMech();
   }

   public GSSCredentialSpi getDelegCred() throws GSSException {
      return this.delegatedCred;
   }

   public boolean isInitiator() {
      return this.isInitiator;
   }

   protected void finalize() throws Throwable {
      this.dispose();
   }

   public Object inquireSecContext(InquireType var1) throws GSSException {
      throw new GSSException(16, -1, "Inquire type not supported.");
   }
}
