package javax.rmi.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;

public interface ValueHandlerMultiFormat extends ValueHandler {
   byte getMaximumStreamFormatVersion();

   void writeValue(OutputStream var1, Serializable var2, byte var3);
}
