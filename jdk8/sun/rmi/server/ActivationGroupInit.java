package sun.rmi.server;

import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;

public abstract class ActivationGroupInit {
   public static void main(String[] var0) {
      try {
         if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
         }

         MarshalInputStream var1 = new MarshalInputStream(System.in);
         ActivationGroupID var2 = (ActivationGroupID)var1.readObject();
         ActivationGroupDesc var3 = (ActivationGroupDesc)var1.readObject();
         long var4 = var1.readLong();
         ActivationGroup.createGroup(var2, var3, var4);
      } catch (Exception var14) {
         System.err.println("Exception in starting ActivationGroupInit:");
         var14.printStackTrace();
      } finally {
         try {
            System.in.close();
         } catch (Exception var13) {
         }

      }

   }
}
