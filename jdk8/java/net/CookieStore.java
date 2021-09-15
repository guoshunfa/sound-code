package java.net;

import java.util.List;

public interface CookieStore {
   void add(URI var1, HttpCookie var2);

   List<HttpCookie> get(URI var1);

   List<HttpCookie> getCookies();

   List<URI> getURIs();

   boolean remove(URI var1, HttpCookie var2);

   boolean removeAll();
}
