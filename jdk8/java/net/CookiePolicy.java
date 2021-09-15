package java.net;

public interface CookiePolicy {
   CookiePolicy ACCEPT_ALL = new CookiePolicy() {
      public boolean shouldAccept(URI var1, HttpCookie var2) {
         return true;
      }
   };
   CookiePolicy ACCEPT_NONE = new CookiePolicy() {
      public boolean shouldAccept(URI var1, HttpCookie var2) {
         return false;
      }
   };
   CookiePolicy ACCEPT_ORIGINAL_SERVER = new CookiePolicy() {
      public boolean shouldAccept(URI var1, HttpCookie var2) {
         return var1 != null && var2 != null ? HttpCookie.domainMatches(var2.getDomain(), var1.getHost()) : false;
      }
   };

   boolean shouldAccept(URI var1, HttpCookie var2);
}
