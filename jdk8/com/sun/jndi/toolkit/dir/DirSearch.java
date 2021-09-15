package com.sun.jndi.toolkit.dir;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class DirSearch {
   public static NamingEnumeration<SearchResult> search(DirContext var0, Attributes var1, String[] var2) throws NamingException {
      SearchControls var3 = new SearchControls(1, 0L, 0, var2, false, false);
      return new LazySearchEnumerationImpl(new ContextEnumerator(var0, 1), new ContainmentFilter(var1), var3);
   }

   public static NamingEnumeration<SearchResult> search(DirContext var0, String var1, SearchControls var2) throws NamingException {
      if (var2 == null) {
         var2 = new SearchControls();
      }

      return new LazySearchEnumerationImpl(new ContextEnumerator(var0, var2.getSearchScope()), new SearchFilter(var1), var2);
   }

   public static NamingEnumeration<SearchResult> search(DirContext var0, String var1, Object[] var2, SearchControls var3) throws NamingException {
      String var4 = SearchFilter.format(var1, var2);
      return search(var0, var4, var3);
   }
}
