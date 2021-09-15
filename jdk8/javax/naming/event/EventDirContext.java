package javax.naming.event;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

public interface EventDirContext extends EventContext, DirContext {
   void addNamingListener(Name var1, String var2, SearchControls var3, NamingListener var4) throws NamingException;

   void addNamingListener(String var1, String var2, SearchControls var3, NamingListener var4) throws NamingException;

   void addNamingListener(Name var1, String var2, Object[] var3, SearchControls var4, NamingListener var5) throws NamingException;

   void addNamingListener(String var1, String var2, Object[] var3, SearchControls var4, NamingListener var5) throws NamingException;
}
