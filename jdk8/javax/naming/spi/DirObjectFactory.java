package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;

public interface DirObjectFactory extends ObjectFactory {
   Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4, Attributes var5) throws Exception;
}
