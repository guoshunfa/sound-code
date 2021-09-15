package com.sun.jndi.toolkit.dir;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public final class LazySearchEnumerationImpl implements NamingEnumeration<SearchResult> {
   private NamingEnumeration<Binding> candidates;
   private SearchResult nextMatch;
   private SearchControls cons;
   private AttrFilter filter;
   private Context context;
   private Hashtable<String, Object> env;
   private boolean useFactory;

   public LazySearchEnumerationImpl(NamingEnumeration<Binding> var1, AttrFilter var2, SearchControls var3) throws NamingException {
      this.nextMatch = null;
      this.useFactory = true;
      this.candidates = var1;
      this.filter = var2;
      if (var3 == null) {
         this.cons = new SearchControls();
      } else {
         this.cons = var3;
      }

   }

   public LazySearchEnumerationImpl(NamingEnumeration<Binding> var1, AttrFilter var2, SearchControls var3, Context var4, Hashtable<String, Object> var5, boolean var6) throws NamingException {
      this.nextMatch = null;
      this.useFactory = true;
      this.candidates = var1;
      this.filter = var2;
      this.env = (Hashtable)((Hashtable)(var5 == null ? null : var5.clone()));
      this.context = var4;
      this.useFactory = var6;
      if (var3 == null) {
         this.cons = new SearchControls();
      } else {
         this.cons = var3;
      }

   }

   public LazySearchEnumerationImpl(NamingEnumeration<Binding> var1, AttrFilter var2, SearchControls var3, Context var4, Hashtable<String, Object> var5) throws NamingException {
      this(var1, var2, var3, var4, var5, true);
   }

   public boolean hasMore() throws NamingException {
      return this.findNextMatch(false) != null;
   }

   public boolean hasMoreElements() {
      try {
         return this.hasMore();
      } catch (NamingException var2) {
         return false;
      }
   }

   public SearchResult nextElement() {
      try {
         return this.findNextMatch(true);
      } catch (NamingException var2) {
         throw new NoSuchElementException(var2.toString());
      }
   }

   public SearchResult next() throws NamingException {
      return this.findNextMatch(true);
   }

   public void close() throws NamingException {
      if (this.candidates != null) {
         this.candidates.close();
      }

   }

   private SearchResult findNextMatch(boolean var1) throws NamingException {
      SearchResult var2;
      if (this.nextMatch != null) {
         var2 = this.nextMatch;
         if (var1) {
            this.nextMatch = null;
         }

         return var2;
      } else {
         while(this.candidates.hasMore()) {
            Binding var3 = (Binding)this.candidates.next();
            Object var4 = var3.getObject();
            if (var4 instanceof DirContext) {
               Attributes var5 = ((DirContext)((DirContext)var4)).getAttributes("");
               if (this.filter.check(var5)) {
                  if (!this.cons.getReturningObjFlag()) {
                     var4 = null;
                  } else if (this.useFactory) {
                     try {
                        CompositeName var6 = this.context != null ? new CompositeName(var3.getName()) : null;
                        var4 = DirectoryManager.getObjectInstance(var4, var6, this.context, this.env, var5);
                     } catch (NamingException var8) {
                        throw var8;
                     } catch (Exception var9) {
                        NamingException var7 = new NamingException("problem generating object using object factory");
                        var7.setRootCause(var9);
                        throw var7;
                     }
                  }

                  var2 = new SearchResult(var3.getName(), var3.getClassName(), var4, SearchFilter.selectAttributes(var5, this.cons.getReturningAttributes()), true);
                  if (!var1) {
                     this.nextMatch = var2;
                  }

                  return var2;
               }
            }
         }

         return null;
      }
   }
}
