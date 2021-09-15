package org.ietf.jgss;

import java.io.InputStream;
import java.io.OutputStream;

public interface GSSContext {
   int DEFAULT_LIFETIME = 0;
   int INDEFINITE_LIFETIME = Integer.MAX_VALUE;

   byte[] initSecContext(byte[] var1, int var2, int var3) throws GSSException;

   int initSecContext(InputStream var1, OutputStream var2) throws GSSException;

   byte[] acceptSecContext(byte[] var1, int var2, int var3) throws GSSException;

   void acceptSecContext(InputStream var1, OutputStream var2) throws GSSException;

   boolean isEstablished();

   void dispose() throws GSSException;

   int getWrapSizeLimit(int var1, boolean var2, int var3) throws GSSException;

   byte[] wrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException;

   void wrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException;

   byte[] unwrap(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException;

   void unwrap(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException;

   byte[] getMIC(byte[] var1, int var2, int var3, MessageProp var4) throws GSSException;

   void getMIC(InputStream var1, OutputStream var2, MessageProp var3) throws GSSException;

   void verifyMIC(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, MessageProp var7) throws GSSException;

   void verifyMIC(InputStream var1, InputStream var2, MessageProp var3) throws GSSException;

   byte[] export() throws GSSException;

   void requestMutualAuth(boolean var1) throws GSSException;

   void requestReplayDet(boolean var1) throws GSSException;

   void requestSequenceDet(boolean var1) throws GSSException;

   void requestCredDeleg(boolean var1) throws GSSException;

   void requestAnonymity(boolean var1) throws GSSException;

   void requestConf(boolean var1) throws GSSException;

   void requestInteg(boolean var1) throws GSSException;

   void requestLifetime(int var1) throws GSSException;

   void setChannelBinding(ChannelBinding var1) throws GSSException;

   boolean getCredDelegState();

   boolean getMutualAuthState();

   boolean getReplayDetState();

   boolean getSequenceDetState();

   boolean getAnonymityState();

   boolean isTransferable() throws GSSException;

   boolean isProtReady();

   boolean getConfState();

   boolean getIntegState();

   int getLifetime();

   GSSName getSrcName() throws GSSException;

   GSSName getTargName() throws GSSException;

   Oid getMech() throws GSSException;

   GSSCredential getDelegCred() throws GSSException;

   boolean isInitiator() throws GSSException;
}
