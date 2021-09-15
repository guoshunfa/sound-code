package sun.security.jgss.spi;

import com.sun.security.jgss.InquireType;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

public interface GSSContextSpi {
   Provider getProvider();

   void requestLifetime(int var1) throws GSSException;

   void requestMutualAuth(boolean var1) throws GSSException;

   void requestReplayDet(boolean var1) throws GSSException;

   void requestSequenceDet(boolean var1) throws GSSException;

   void requestCredDeleg(boolean var1) throws GSSException;

   void requestAnonymity(boolean var1) throws GSSException;

   void requestConf(boolean var1) throws GSSException;

   void requestInteg(boolean var1) throws GSSException;

   void requestDelegPolicy(boolean var1) throws GSSException;

   void setChannelBinding(ChannelBinding var1) throws GSSException;

   boolean getCredDelegState();

   boolean getMutualAuthState();

   boolean getReplayDetState();

   boolean getSequenceDetState();

   boolean getAnonymityState();

   boolean getDelegPolicyState();

   boolean isTransferable() throws GSSException;

   boolean isProtReady();

   boolean isInitiator();

   boolean getConfState();

   boolean getIntegState();

   int getLifetime();

   boolean isEstablished();

   GSSNameSpi getSrcName() throws GSSException;

   GSSNameSpi getTargName() throws GSSException;

   Oid getMech() throws GSSException;

   GSSCredentialSpi getDelegCred() throws GSSException;

   byte[] initSecContext(InputStream var1, int var2) throws GSSException;

   byte[] acceptSecContext(InputStream var1, int var2) throws GSSException;

   int getWrapSizeLimit(int var1, boolean var2, int var3) throws GSSException;

   void wrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException;

   byte[] wrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException;

   void unwrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException;

   byte[] unwrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException;

   void getMIC(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException;

   byte[] getMIC(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException;

   void verifyMIC(InputStream var1, InputStream var2, MessageProp var3) throws GSSException;

   void verifyMIC(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, MessageProp var7) throws GSSException;

   byte[] export() throws GSSException;

   void dispose() throws GSSException;

   Object inquireSecContext(InquireType var1) throws GSSException;
}
