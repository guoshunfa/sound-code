package javax.rmi.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.SendingContext.RunTime;

public interface ValueHandler {
   void writeValue(OutputStream var1, Serializable var2);

   Serializable readValue(InputStream var1, int var2, Class var3, String var4, RunTime var5);

   String getRMIRepositoryID(Class var1);

   boolean isCustomMarshaled(Class var1);

   RunTime getRunTimeCodeBase();

   Serializable writeReplace(Serializable var1);
}
