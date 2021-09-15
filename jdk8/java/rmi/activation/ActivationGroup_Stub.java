package java.rmi.activation;

import java.lang.reflect.Method;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class ActivationGroup_Stub extends RemoteStub implements ActivationInstantiator, Remote {
   private static final long serialVersionUID = 2L;
   private static Method $method_newInstance_0;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationInstantiator;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationID;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationDesc;

   static {
      try {
         $method_newInstance_0 = (class$java$rmi$activation$ActivationInstantiator != null ? class$java$rmi$activation$ActivationInstantiator : (class$java$rmi$activation$ActivationInstantiator = class$("java.rmi.activation.ActivationInstantiator"))).getMethod("newInstance", class$java$rmi$activation$ActivationID != null ? class$java$rmi$activation$ActivationID : (class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")), class$java$rmi$activation$ActivationDesc != null ? class$java$rmi$activation$ActivationDesc : (class$java$rmi$activation$ActivationDesc = class$("java.rmi.activation.ActivationDesc")));
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public ActivationGroup_Stub(RemoteRef var1) {
      super(var1);
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public MarshalledObject newInstance(ActivationID var1, ActivationDesc var2) throws RemoteException, ActivationException {
      try {
         Object var3 = super.ref.invoke(this, $method_newInstance_0, new Object[]{var1, var2}, -5274445189091581345L);
         return (MarshalledObject)var3;
      } catch (RuntimeException var4) {
         throw var4;
      } catch (RemoteException var5) {
         throw var5;
      } catch (ActivationException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new UnexpectedException("undeclared checked exception", var7);
      }
   }
}
