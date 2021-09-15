package javax.naming.spi;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public interface Resolver {
   ResolveResult resolveToClass(Name var1, Class<? extends Context> var2) throws NamingException;

   ResolveResult resolveToClass(String var1, Class<? extends Context> var2) throws NamingException;
}
