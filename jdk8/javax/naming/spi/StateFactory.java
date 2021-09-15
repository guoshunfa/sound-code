package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public interface StateFactory {
   Object getStateToBind(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws NamingException;
}
