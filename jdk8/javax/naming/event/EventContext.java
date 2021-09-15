package javax.naming.event;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public interface EventContext extends Context {
   int OBJECT_SCOPE = 0;
   int ONELEVEL_SCOPE = 1;
   int SUBTREE_SCOPE = 2;

   void addNamingListener(Name var1, int var2, NamingListener var3) throws NamingException;

   void addNamingListener(String var1, int var2, NamingListener var3) throws NamingException;

   void removeNamingListener(NamingListener var1) throws NamingException;

   boolean targetMustExist() throws NamingException;
}
