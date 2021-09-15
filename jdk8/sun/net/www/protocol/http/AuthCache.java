package sun.net.www.protocol.http;

public interface AuthCache {
   void put(String var1, AuthCacheValue var2);

   AuthCacheValue get(String var1, String var2);

   void remove(String var1, AuthCacheValue var2);
}
