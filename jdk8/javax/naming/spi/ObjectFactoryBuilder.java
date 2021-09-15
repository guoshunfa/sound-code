package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.NamingException;

public interface ObjectFactoryBuilder {
   ObjectFactory createObjectFactory(Object var1, Hashtable<?, ?> var2) throws NamingException;
}
