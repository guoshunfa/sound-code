package javax.sql.rowset.spi;

import com.sun.rowset.internal.SyncResolverImpl;
import java.sql.SQLException;

public class SyncProviderException extends SQLException {
   private SyncResolver syncResolver = null;
   static final long serialVersionUID = -939908523620640692L;

   public SyncProviderException() {
   }

   public SyncProviderException(String var1) {
      super(var1);
   }

   public SyncProviderException(SyncResolver var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot instantiate a SyncProviderException with a null SyncResolver object");
      } else {
         this.syncResolver = var1;
      }
   }

   public SyncResolver getSyncResolver() {
      if (this.syncResolver != null) {
         return this.syncResolver;
      } else {
         try {
            this.syncResolver = new SyncResolverImpl();
         } catch (SQLException var2) {
         }

         return this.syncResolver;
      }
   }

   public void setSyncResolver(SyncResolver var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot set a null SyncResolver object");
      } else {
         this.syncResolver = var1;
      }
   }
}
