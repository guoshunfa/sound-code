package com.sun.jndi.url.ldap;

import com.sun.jndi.ldap.LdapURL;
import com.sun.jndi.toolkit.url.GenericURLDirContext;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.ResolveResult;

public final class ldapURLContext extends GenericURLDirContext {
   ldapURLContext(Hashtable<?, ?> var1) {
      super(var1);
   }

   protected ResolveResult getRootURLContext(String var1, Hashtable<?, ?> var2) throws NamingException {
      return ldapURLContextFactory.getUsingURLIgnoreRootDN(var1, var2);
   }

   protected Name getURLSuffix(String var1, String var2) throws NamingException {
      LdapURL var3 = new LdapURL(var2);
      String var4 = var3.getDN() != null ? var3.getDN() : "";
      CompositeName var5 = new CompositeName();
      if (!"".equals(var4)) {
         var5.add(var4);
      }

      return var5;
   }

   public Object lookup(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.lookup(var1);
      }
   }

   public Object lookup(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.lookup(var1);
      }
   }

   public void bind(String var1, Object var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.bind(var1, var2);
      }
   }

   public void bind(Name var1, Object var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.bind(var1, var2);
      }
   }

   public void rebind(String var1, Object var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.rebind(var1, var2);
      }
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.rebind(var1, var2);
      }
   }

   public void unbind(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.unbind(var1);
      }
   }

   public void unbind(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.unbind(var1);
      }
   }

   public void rename(String var1, String var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else if (LdapURL.hasQueryComponents(var2)) {
         throw new InvalidNameException(var2);
      } else {
         super.rename(var1, var2);
      }
   }

   public void rename(Name var1, Name var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else if (LdapURL.hasQueryComponents(var2.get(0))) {
         throw new InvalidNameException(var2.toString());
      } else {
         super.rename(var1, var2);
      }
   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.list(var1);
      }
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.list(var1);
      }
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.listBindings(var1);
      }
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.listBindings(var1);
      }
   }

   public void destroySubcontext(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.destroySubcontext(var1);
      }
   }

   public void destroySubcontext(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.destroySubcontext(var1);
      }
   }

   public Context createSubcontext(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.createSubcontext(var1);
      }
   }

   public Context createSubcontext(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.createSubcontext(var1);
      }
   }

   public Object lookupLink(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.lookupLink(var1);
      }
   }

   public Object lookupLink(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.lookupLink(var1);
      }
   }

   public NameParser getNameParser(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.getNameParser(var1);
      }
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.getNameParser(var1);
      }
   }

   public String composeName(String var1, String var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else if (LdapURL.hasQueryComponents(var2)) {
         throw new InvalidNameException(var2);
      } else {
         return super.composeName(var1, var2);
      }
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else if (LdapURL.hasQueryComponents(var2.get(0))) {
         throw new InvalidNameException(var2.toString());
      } else {
         return super.composeName(var1, var2);
      }
   }

   public Attributes getAttributes(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.getAttributes(var1);
      }
   }

   public Attributes getAttributes(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.getAttributes(var1);
      }
   }

   public Attributes getAttributes(String var1, String[] var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.getAttributes(var1, var2);
      }
   }

   public Attributes getAttributes(Name var1, String[] var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.getAttributes(var1, var2);
      }
   }

   public void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.modifyAttributes(var1, var2, var3);
      }
   }

   public void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.modifyAttributes(var1, var2, var3);
      }
   }

   public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.modifyAttributes(var1, var2);
      }
   }

   public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.modifyAttributes(var1, var2);
      }
   }

   public void bind(String var1, Object var2, Attributes var3) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.bind(var1, var2, var3);
      }
   }

   public void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.bind(var1, var2, var3);
      }
   }

   public void rebind(String var1, Object var2, Attributes var3) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         super.rebind(var1, var2, var3);
      }
   }

   public void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         super.rebind(var1, var2, var3);
      }
   }

   public DirContext createSubcontext(String var1, Attributes var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.createSubcontext(var1, var2);
      }
   }

   public DirContext createSubcontext(Name var1, Attributes var2) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.createSubcontext(var1, var2);
      }
   }

   public DirContext getSchema(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.getSchema(var1);
      }
   }

   public DirContext getSchema(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.getSchema(var1);
      }
   }

   public DirContext getSchemaClassDefinition(String var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1)) {
         throw new InvalidNameException(var1);
      } else {
         return super.getSchemaClassDefinition(var1);
      }
   }

   public DirContext getSchemaClassDefinition(Name var1) throws NamingException {
      if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.getSchemaClassDefinition(var1);
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException {
      return LdapURL.hasQueryComponents(var1) ? this.searchUsingURL(var1) : super.search(var1, var2);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2);
      } else if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.search(var1, var2);
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException {
      return LdapURL.hasQueryComponents(var1) ? this.searchUsingURL(var1) : super.search(var1, var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2, var3);
      } else if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.search(var1, var2, var3);
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException {
      return LdapURL.hasQueryComponents(var1) ? this.searchUsingURL(var1) : super.search(var1, var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2, var3);
      } else if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.search(var1, var2, var3);
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      return LdapURL.hasQueryComponents(var1) ? this.searchUsingURL(var1) : super.search(var1, var2, var3, var4);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2, var3, var4);
      } else if (LdapURL.hasQueryComponents(var1.get(0))) {
         throw new InvalidNameException(var1.toString());
      } else {
         return super.search(var1, var2, var3, var4);
      }
   }

   private NamingEnumeration<SearchResult> searchUsingURL(String var1) throws NamingException {
      LdapURL var2 = new LdapURL(var1);
      ResolveResult var3 = this.getRootURLContext(var1, this.myEnv);
      DirContext var4 = (DirContext)var3.getResolvedObj();

      NamingEnumeration var5;
      try {
         var5 = var4.search(var3.getRemainingName(), setFilterUsingURL(var2), setSearchControlsUsingURL(var2));
      } finally {
         var4.close();
      }

      return var5;
   }

   private static String setFilterUsingURL(LdapURL var0) {
      String var1 = var0.getFilter();
      if (var1 == null) {
         var1 = "(objectClass=*)";
      }

      return var1;
   }

   private static SearchControls setSearchControlsUsingURL(LdapURL var0) {
      SearchControls var1 = new SearchControls();
      String var2 = var0.getScope();
      String var3 = var0.getAttributes();
      if (var2 == null) {
         var1.setSearchScope(0);
      } else if (var2.equals("sub")) {
         var1.setSearchScope(2);
      } else if (var2.equals("one")) {
         var1.setSearchScope(1);
      } else if (var2.equals("base")) {
         var1.setSearchScope(0);
      }

      if (var3 == null) {
         var1.setReturningAttributes((String[])null);
      } else {
         StringTokenizer var4 = new StringTokenizer(var3, ",");
         int var5 = var4.countTokens();
         String[] var6 = new String[var5];

         for(int var7 = 0; var7 < var5; ++var7) {
            var6[var7] = var4.nextToken();
         }

         var1.setReturningAttributes(var6);
      }

      return var1;
   }
}
