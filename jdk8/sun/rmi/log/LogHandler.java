package sun.rmi.log;

import java.io.InputStream;
import java.io.OutputStream;
import sun.rmi.server.MarshalInputStream;
import sun.rmi.server.MarshalOutputStream;

public abstract class LogHandler {
   public abstract Object initialSnapshot() throws Exception;

   public void snapshot(OutputStream var1, Object var2) throws Exception {
      MarshalOutputStream var3 = new MarshalOutputStream(var1);
      var3.writeObject(var2);
      var3.flush();
   }

   public Object recover(InputStream var1) throws Exception {
      MarshalInputStream var2 = new MarshalInputStream(var1);
      return var2.readObject();
   }

   public void writeUpdate(LogOutputStream var1, Object var2) throws Exception {
      MarshalOutputStream var3 = new MarshalOutputStream(var1);
      var3.writeObject(var2);
      var3.flush();
   }

   public Object readUpdate(LogInputStream var1, Object var2) throws Exception {
      MarshalInputStream var3 = new MarshalInputStream(var1);
      return this.applyUpdate(var3.readObject(), var2);
   }

   public abstract Object applyUpdate(Object var1, Object var2) throws Exception;
}
