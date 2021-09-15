package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;

public interface ObjectFactory {
   Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws Exception;
}
