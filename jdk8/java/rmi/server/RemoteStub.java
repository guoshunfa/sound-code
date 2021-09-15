package java.rmi.server;

/** @deprecated */
@Deprecated
public abstract class RemoteStub extends RemoteObject {
   private static final long serialVersionUID = -1585587260594494182L;

   protected RemoteStub() {
   }

   protected RemoteStub(RemoteRef var1) {
      super(var1);
   }

   /** @deprecated */
   @Deprecated
   protected static void setRef(RemoteStub var0, RemoteRef var1) {
      throw new UnsupportedOperationException();
   }
}
