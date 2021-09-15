package javax.print.attribute;

import java.io.Serializable;
import java.net.URI;

public abstract class URISyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = -7842661210486401678L;
   private URI uri;

   protected URISyntax(URI var1) {
      this.uri = verify(var1);
   }

   private static URI verify(URI var0) {
      if (var0 == null) {
         throw new NullPointerException(" uri is null");
      } else {
         return var0;
      }
   }

   public URI getURI() {
      return this.uri;
   }

   public int hashCode() {
      return this.uri.hashCode();
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof URISyntax && this.uri.equals(((URISyntax)var1).uri);
   }

   public String toString() {
      return this.uri.toString();
   }
}
