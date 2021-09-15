package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public interface DirStateFactory extends StateFactory {
   DirStateFactory.Result getStateToBind(Object var1, Name var2, Context var3, Hashtable<?, ?> var4, Attributes var5) throws NamingException;

   public static class Result {
      private Object obj;
      private Attributes attrs;

      public Result(Object var1, Attributes var2) {
         this.obj = var1;
         this.attrs = var2;
      }

      public Object getObject() {
         return this.obj;
      }

      public Attributes getAttributes() {
         return this.attrs;
      }
   }
}
