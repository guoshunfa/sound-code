package javax.naming.directory;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public interface DirContext extends Context {
   int ADD_ATTRIBUTE = 1;
   int REPLACE_ATTRIBUTE = 2;
   int REMOVE_ATTRIBUTE = 3;

   Attributes getAttributes(Name var1) throws NamingException;

   Attributes getAttributes(String var1) throws NamingException;

   Attributes getAttributes(Name var1, String[] var2) throws NamingException;

   Attributes getAttributes(String var1, String[] var2) throws NamingException;

   void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException;

   void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException;

   void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException;

   void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException;

   void bind(Name var1, Object var2, Attributes var3) throws NamingException;

   void bind(String var1, Object var2, Attributes var3) throws NamingException;

   void rebind(Name var1, Object var2, Attributes var3) throws NamingException;

   void rebind(String var1, Object var2, Attributes var3) throws NamingException;

   DirContext createSubcontext(Name var1, Attributes var2) throws NamingException;

   DirContext createSubcontext(String var1, Attributes var2) throws NamingException;

   DirContext getSchema(Name var1) throws NamingException;

   DirContext getSchema(String var1) throws NamingException;

   DirContext getSchemaClassDefinition(Name var1) throws NamingException;

   DirContext getSchemaClassDefinition(String var1) throws NamingException;

   NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException;

   NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException;

   NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException;

   NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException;

   NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException;

   NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException;

   NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException;

   NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException;
}
