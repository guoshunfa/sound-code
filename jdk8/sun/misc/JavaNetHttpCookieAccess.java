package sun.misc;

import java.net.HttpCookie;
import java.util.List;

public interface JavaNetHttpCookieAccess {
   List<HttpCookie> parse(String var1);

   String header(HttpCookie var1);
}
