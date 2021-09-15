package javax.naming.spi;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

public class DirectoryManager extends NamingManager {
   DirectoryManager() {
   }

   public static DirContext getContinuationDirContext(CannotProceedException var0) throws NamingException {
      Hashtable var1 = var0.getEnvironment();
      if (var1 == null) {
         var1 = new Hashtable(7);
      } else {
         var1 = (Hashtable)var1.clone();
      }

      var1.put("java.naming.spi.CannotProceedException", var0);
      return new ContinuationDirContext(var0, var1);
   }

   public static Object getObjectInstance(Object var0, Name var1, Context var2, Hashtable<?, ?> var3, Attributes var4) throws Exception {
      ObjectFactoryBuilder var6 = getObjectFactoryBuilder();
      ObjectFactory var5;
      if (var6 != null) {
         var5 = var6.createObjectFactory(var0, var3);
         return var5 instanceof DirObjectFactory ? ((DirObjectFactory)var5).getObjectInstance(var0, var1, var2, var3, var4) : var5.getObjectInstance(var0, var1, var2, var3);
      } else {
         Reference var7 = null;
         if (var0 instanceof Reference) {
            var7 = (Reference)var0;
         } else if (var0 instanceof Referenceable) {
            var7 = ((Referenceable)((Referenceable)var0)).getReference();
         }

         Object var8;
         if (var7 != null) {
            String var9 = var7.getFactoryClassName();
            if (var9 != null) {
               var5 = getObjectFactoryFromReference(var7, var9);
               if (var5 instanceof DirObjectFactory) {
                  return ((DirObjectFactory)var5).getObjectInstance(var7, var1, var2, var3, var4);
               }

               if (var5 != null) {
                  return var5.getObjectInstance(var7, var1, var2, var3);
               }

               return var0;
            }

            var8 = processURLAddrs(var7, var1, var2, var3);
            if (var8 != null) {
               return var8;
            }
         }

         var8 = createObjectFromFactories(var0, var1, var2, var3, var4);
         return var8 != null ? var8 : var0;
      }
   }

   private static Object createObjectFromFactories(Object var0, Name var1, Context var2, Hashtable<?, ?> var3, Attributes var4) throws Exception {
      FactoryEnumeration var5 = ResourceManager.getFactories("java.naming.factory.object", var3, var2);
      if (var5 == null) {
         return null;
      } else {
         Object var7 = null;

         while(var7 == null && var5.hasMore()) {
            ObjectFactory var6 = (ObjectFactory)var5.next();
            if (var6 instanceof DirObjectFactory) {
               var7 = ((DirObjectFactory)var6).getObjectInstance(var0, var1, var2, var3, var4);
            } else {
               var7 = var6.getObjectInstance(var0, var1, var2, var3);
            }
         }

         return var7;
      }
   }

   public static DirStateFactory.Result getStateToBind(Object var0, Name var1, Context var2, Hashtable<?, ?> var3, Attributes var4) throws NamingException {
      FactoryEnumeration var5 = ResourceManager.getFactories("java.naming.factory.state", var3, var2);
      if (var5 == null) {
         return new DirStateFactory.Result(var0, var4);
      } else {
         DirStateFactory.Result var8 = null;

         while(var8 == null && var5.hasMore()) {
            StateFactory var6 = (StateFactory)var5.next();
            if (var6 instanceof DirStateFactory) {
               var8 = ((DirStateFactory)var6).getStateToBind(var0, var1, var2, var3, var4);
            } else {
               Object var7 = var6.getStateToBind(var0, var1, var2, var3);
               if (var7 != null) {
                  var8 = new DirStateFactory.Result(var7, var4);
               }
            }
         }

         return var8 != null ? var8 : new DirStateFactory.Result(var0, var4);
      }
   }
}
