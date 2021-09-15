package javax.net.ssl;

import java.util.Enumeration;

public interface SSLSessionContext {
   SSLSession getSession(byte[] var1);

   Enumeration<byte[]> getIds();

   void setSessionTimeout(int var1) throws IllegalArgumentException;

   int getSessionTimeout();

   void setSessionCacheSize(int var1) throws IllegalArgumentException;

   int getSessionCacheSize();
}
