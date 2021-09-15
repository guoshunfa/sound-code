package java.security;

import java.net.URI;
import javax.security.auth.login.Configuration;

public class URIParameter implements Policy.Parameters, Configuration.Parameters {
   private URI uri;

   public URIParameter(URI var1) {
      if (var1 == null) {
         throw new NullPointerException("invalid null URI");
      } else {
         this.uri = var1;
      }
   }

   public URI getURI() {
      return this.uri;
   }
}
