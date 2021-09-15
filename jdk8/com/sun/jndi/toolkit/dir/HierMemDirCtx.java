package com.sun.jndi.toolkit.dir;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeModificationException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirStateFactory;
import javax.naming.spi.DirectoryManager;

public class HierMemDirCtx implements DirContext {
   private static final boolean debug = false;
   private static final NameParser defaultParser = new HierarchicalNameParser();
   protected Hashtable<String, Object> myEnv;
   protected Hashtable<Name, Object> bindings;
   protected Attributes attrs;
   protected boolean ignoreCase;
   protected NamingException readOnlyEx;
   protected NameParser myParser;
   private boolean alwaysUseFactory;

   public void close() throws NamingException {
      this.myEnv = null;
      this.bindings = null;
      this.attrs = null;
   }

   public String getNameInNamespace() throws NamingException {
      throw new OperationNotSupportedException("Cannot determine full name");
   }

   public HierMemDirCtx() {
      this((Hashtable)null, false, false);
   }

   public HierMemDirCtx(boolean var1) {
      this((Hashtable)null, var1, false);
   }

   public HierMemDirCtx(Hashtable<String, Object> var1, boolean var2) {
      this(var1, var2, false);
   }

   protected HierMemDirCtx(Hashtable<String, Object> var1, boolean var2, boolean var3) {
      this.ignoreCase = false;
      this.readOnlyEx = null;
      this.myParser = defaultParser;
      this.myEnv = var1;
      this.ignoreCase = var2;
      this.init();
      this.alwaysUseFactory = var3;
   }

   private void init() {
      this.attrs = new BasicAttributes(this.ignoreCase);
      this.bindings = new Hashtable(11, 0.75F);
   }

   public Object lookup(String var1) throws NamingException {
      return this.lookup(this.myParser.parse(var1));
   }

   public Object lookup(Name var1) throws NamingException {
      return this.doLookup(var1, this.alwaysUseFactory);
   }

   public Object doLookup(Name var1, boolean var2) throws NamingException {
      Object var3 = null;
      var1 = this.canonizeName(var1);
      switch(var1.size()) {
      case 0:
         var3 = this;
         break;
      case 1:
         var3 = this.bindings.get(var1);
         break;
      default:
         HierMemDirCtx var4 = (HierMemDirCtx)this.bindings.get(var1.getPrefix(1));
         if (var4 == null) {
            var3 = null;
         } else {
            var3 = var4.doLookup(var1.getSuffix(1), false);
         }
      }

      if (var3 == null) {
         throw new NameNotFoundException(var1.toString());
      } else if (var2) {
         try {
            return DirectoryManager.getObjectInstance(var3, var1, this, this.myEnv, var3 instanceof HierMemDirCtx ? ((HierMemDirCtx)var3).attrs : null);
         } catch (NamingException var6) {
            throw var6;
         } catch (Exception var7) {
            NamingException var5 = new NamingException("Problem calling getObjectInstance");
            var5.setRootCause(var7);
            throw var5;
         }
      } else {
         return var3;
      }
   }

   public void bind(String var1, Object var2) throws NamingException {
      this.bind(this.myParser.parse(var1), var2);
   }

   public void bind(Name var1, Object var2) throws NamingException {
      this.doBind(var1, var2, (Attributes)null, this.alwaysUseFactory);
   }

   public void bind(String var1, Object var2, Attributes var3) throws NamingException {
      this.bind(this.myParser.parse(var1), var2, var3);
   }

   public void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      this.doBind(var1, var2, var3, this.alwaysUseFactory);
   }

   protected void doBind(Name var1, Object var2, Attributes var3, boolean var4) throws NamingException {
      if (var1.isEmpty()) {
         throw new InvalidNameException("Cannot bind empty name");
      } else {
         if (var4) {
            DirStateFactory.Result var5 = DirectoryManager.getStateToBind(var2, var1, this, this.myEnv, var3);
            var2 = var5.getObject();
            var3 = var5.getAttributes();
         }

         HierMemDirCtx var6 = (HierMemDirCtx)this.doLookup(this.getInternalName(var1), false);
         var6.doBindAux(this.getLeafName(var1), var2);
         if (var3 != null && var3.size() > 0) {
            this.modifyAttributes((Name)var1, 1, var3);
         }

      }
   }

   protected void doBindAux(Name var1, Object var2) throws NamingException {
      if (this.readOnlyEx != null) {
         throw (NamingException)this.readOnlyEx.fillInStackTrace();
      } else if (this.bindings.get(var1) != null) {
         throw new NameAlreadyBoundException(var1.toString());
      } else if (var2 instanceof HierMemDirCtx) {
         this.bindings.put(var1, var2);
      } else {
         throw new SchemaViolationException("This context only supports binding objects of it's own kind");
      }
   }

   public void rebind(String var1, Object var2) throws NamingException {
      this.rebind(this.myParser.parse(var1), var2);
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      this.doRebind(var1, var2, (Attributes)null, this.alwaysUseFactory);
   }

   public void rebind(String var1, Object var2, Attributes var3) throws NamingException {
      this.rebind(this.myParser.parse(var1), var2, var3);
   }

   public void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      this.doRebind(var1, var2, var3, this.alwaysUseFactory);
   }

   protected void doRebind(Name var1, Object var2, Attributes var3, boolean var4) throws NamingException {
      if (var1.isEmpty()) {
         throw new InvalidNameException("Cannot rebind empty name");
      } else {
         if (var4) {
            DirStateFactory.Result var5 = DirectoryManager.getStateToBind(var2, var1, this, this.myEnv, var3);
            var2 = var5.getObject();
            var3 = var5.getAttributes();
         }

         HierMemDirCtx var6 = (HierMemDirCtx)this.doLookup(this.getInternalName(var1), false);
         var6.doRebindAux(this.getLeafName(var1), var2);
         if (var3 != null && var3.size() > 0) {
            this.modifyAttributes((Name)var1, 1, var3);
         }

      }
   }

   protected void doRebindAux(Name var1, Object var2) throws NamingException {
      if (this.readOnlyEx != null) {
         throw (NamingException)this.readOnlyEx.fillInStackTrace();
      } else if (var2 instanceof HierMemDirCtx) {
         this.bindings.put(var1, var2);
      } else {
         throw new SchemaViolationException("This context only supports binding objects of it's own kind");
      }
   }

   public void unbind(String var1) throws NamingException {
      this.unbind(this.myParser.parse(var1));
   }

   public void unbind(Name var1) throws NamingException {
      if (var1.isEmpty()) {
         throw new InvalidNameException("Cannot unbind empty name");
      } else {
         HierMemDirCtx var2 = (HierMemDirCtx)this.doLookup(this.getInternalName(var1), false);
         var2.doUnbind(this.getLeafName(var1));
      }
   }

   protected void doUnbind(Name var1) throws NamingException {
      if (this.readOnlyEx != null) {
         throw (NamingException)this.readOnlyEx.fillInStackTrace();
      } else {
         this.bindings.remove(var1);
      }
   }

   public void rename(String var1, String var2) throws NamingException {
      this.rename(this.myParser.parse(var1), this.myParser.parse(var2));
   }

   public void rename(Name var1, Name var2) throws NamingException {
      if (!var2.isEmpty() && !var1.isEmpty()) {
         if (!this.getInternalName(var2).equals(this.getInternalName(var1))) {
            throw new InvalidNameException("Cannot rename across contexts");
         } else {
            HierMemDirCtx var3 = (HierMemDirCtx)this.doLookup(this.getInternalName(var2), false);
            var3.doRename(this.getLeafName(var1), this.getLeafName(var2));
         }
      } else {
         throw new InvalidNameException("Cannot rename empty name");
      }
   }

   protected void doRename(Name var1, Name var2) throws NamingException {
      if (this.readOnlyEx != null) {
         throw (NamingException)this.readOnlyEx.fillInStackTrace();
      } else {
         var1 = this.canonizeName(var1);
         var2 = this.canonizeName(var2);
         if (this.bindings.get(var2) != null) {
            throw new NameAlreadyBoundException(var2.toString());
         } else {
            Object var3 = this.bindings.remove(var1);
            if (var3 == null) {
               throw new NameNotFoundException(var1.toString());
            } else {
               this.bindings.put(var2, var3);
            }
         }
      }
   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      return this.list(this.myParser.parse(var1));
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      HierMemDirCtx var2 = (HierMemDirCtx)this.doLookup(var1, false);
      return var2.doList();
   }

   protected NamingEnumeration<NameClassPair> doList() throws NamingException {
      return new HierMemDirCtx.FlatNames(this.bindings.keys());
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      return this.listBindings(this.myParser.parse(var1));
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      HierMemDirCtx var2 = (HierMemDirCtx)this.doLookup(var1, false);
      return var2.doListBindings(this.alwaysUseFactory);
   }

   protected NamingEnumeration<Binding> doListBindings(boolean var1) throws NamingException {
      return new HierMemDirCtx.FlatBindings(this.bindings, this.myEnv, var1);
   }

   public void destroySubcontext(String var1) throws NamingException {
      this.destroySubcontext(this.myParser.parse(var1));
   }

   public void destroySubcontext(Name var1) throws NamingException {
      HierMemDirCtx var2 = (HierMemDirCtx)this.doLookup(this.getInternalName(var1), false);
      var2.doDestroySubcontext(this.getLeafName(var1));
   }

   protected void doDestroySubcontext(Name var1) throws NamingException {
      if (this.readOnlyEx != null) {
         throw (NamingException)this.readOnlyEx.fillInStackTrace();
      } else {
         var1 = this.canonizeName(var1);
         this.bindings.remove(var1);
      }
   }

   public Context createSubcontext(String var1) throws NamingException {
      return this.createSubcontext(this.myParser.parse(var1));
   }

   public Context createSubcontext(Name var1) throws NamingException {
      return this.createSubcontext((Name)var1, (Attributes)null);
   }

   public DirContext createSubcontext(String var1, Attributes var2) throws NamingException {
      return this.createSubcontext(this.myParser.parse(var1), var2);
   }

   public DirContext createSubcontext(Name var1, Attributes var2) throws NamingException {
      HierMemDirCtx var3 = (HierMemDirCtx)this.doLookup(this.getInternalName(var1), false);
      return var3.doCreateSubcontext(this.getLeafName(var1), var2);
   }

   protected DirContext doCreateSubcontext(Name var1, Attributes var2) throws NamingException {
      if (this.readOnlyEx != null) {
         throw (NamingException)this.readOnlyEx.fillInStackTrace();
      } else {
         var1 = this.canonizeName(var1);
         if (this.bindings.get(var1) != null) {
            throw new NameAlreadyBoundException(var1.toString());
         } else {
            HierMemDirCtx var3 = this.createNewCtx();
            this.bindings.put(var1, var3);
            if (var2 != null) {
               var3.modifyAttributes((String)"", 1, var2);
            }

            return var3;
         }
      }
   }

   public Object lookupLink(String var1) throws NamingException {
      return this.lookupLink(this.myParser.parse(var1));
   }

   public Object lookupLink(Name var1) throws NamingException {
      return this.lookup(var1);
   }

   public NameParser getNameParser(String var1) throws NamingException {
      return this.myParser;
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      return this.myParser;
   }

   public String composeName(String var1, String var2) throws NamingException {
      Name var3 = this.composeName((Name)(new CompositeName(var1)), (Name)(new CompositeName(var2)));
      return var3.toString();
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      var1 = this.canonizeName(var1);
      var2 = this.canonizeName(var2);
      Name var3 = (Name)((Name)var2.clone());
      var3.addAll(var1);
      return var3;
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      this.myEnv = this.myEnv == null ? new Hashtable(11, 0.75F) : (Hashtable)this.myEnv.clone();
      return this.myEnv.put(var1, var2);
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      if (this.myEnv == null) {
         return null;
      } else {
         this.myEnv = (Hashtable)this.myEnv.clone();
         return this.myEnv.remove(var1);
      }
   }

   public Hashtable<String, Object> getEnvironment() throws NamingException {
      return this.myEnv == null ? new Hashtable(5, 0.75F) : (Hashtable)this.myEnv.clone();
   }

   public Attributes getAttributes(String var1) throws NamingException {
      return this.getAttributes(this.myParser.parse(var1));
   }

   public Attributes getAttributes(Name var1) throws NamingException {
      HierMemDirCtx var2 = (HierMemDirCtx)this.doLookup(var1, false);
      return var2.doGetAttributes();
   }

   protected Attributes doGetAttributes() throws NamingException {
      return (Attributes)this.attrs.clone();
   }

   public Attributes getAttributes(String var1, String[] var2) throws NamingException {
      return this.getAttributes(this.myParser.parse(var1), var2);
   }

   public Attributes getAttributes(Name var1, String[] var2) throws NamingException {
      HierMemDirCtx var3 = (HierMemDirCtx)this.doLookup(var1, false);
      return var3.doGetAttributes(var2);
   }

   protected Attributes doGetAttributes(String[] var1) throws NamingException {
      if (var1 == null) {
         return this.doGetAttributes();
      } else {
         BasicAttributes var2 = new BasicAttributes(this.ignoreCase);
         Attribute var3 = null;

         for(int var4 = 0; var4 < var1.length; ++var4) {
            var3 = this.attrs.get(var1[var4]);
            if (var3 != null) {
               var2.put(var3);
            }
         }

         return var2;
      }
   }

   public void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException {
      this.modifyAttributes(this.myParser.parse(var1), var2, var3);
   }

   public void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException {
      if (var3 != null && var3.size() != 0) {
         NamingEnumeration var4 = var3.getAll();
         ModificationItem[] var5 = new ModificationItem[var3.size()];

         for(int var6 = 0; var6 < var5.length && var4.hasMoreElements(); ++var6) {
            var5[var6] = new ModificationItem(var2, (Attribute)var4.next());
         }

         this.modifyAttributes(var1, var5);
      } else {
         throw new IllegalArgumentException("Cannot modify without an attribute");
      }
   }

   public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException {
      this.modifyAttributes(this.myParser.parse(var1), var2);
   }

   public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException {
      HierMemDirCtx var3 = (HierMemDirCtx)this.doLookup(var1, false);
      var3.doModifyAttributes(var2);
   }

   protected void doModifyAttributes(ModificationItem[] var1) throws NamingException {
      if (this.readOnlyEx != null) {
         throw (NamingException)this.readOnlyEx.fillInStackTrace();
      } else {
         applyMods(var1, this.attrs);
      }
   }

   protected static Attributes applyMods(ModificationItem[] var0, Attributes var1) throws NamingException {
      label49:
      for(int var6 = 0; var6 < var0.length; ++var6) {
         ModificationItem var2 = var0[var6];
         Attribute var4 = var2.getAttribute();
         Attribute var3;
         NamingEnumeration var5;
         switch(var2.getModificationOp()) {
         case 1:
            var3 = var1.get(var4.getID());
            if (var3 == null) {
               var1.put((Attribute)var4.clone());
               break;
            } else {
               var5 = var4.getAll();

               while(true) {
                  if (!var5.hasMore()) {
                     continue label49;
                  }

                  var3.add(var5.next());
               }
            }
         case 2:
            if (var4.size() == 0) {
               var1.remove(var4.getID());
            } else {
               var1.put((Attribute)var4.clone());
            }
            break;
         case 3:
            var3 = var1.get(var4.getID());
            if (var3 != null) {
               if (var4.size() == 0) {
                  var1.remove(var4.getID());
               } else {
                  var5 = var4.getAll();

                  while(var5.hasMore()) {
                     var3.remove(var5.next());
                  }

                  if (var3.size() == 0) {
                     var1.remove(var4.getID());
                  }
               }
            }
            break;
         default:
            throw new AttributeModificationException("Unknown mod_op");
         }
      }

      return var1;
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException {
      return this.search((String)var1, (Attributes)var2, (String[])null);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException {
      return this.search((Name)var1, (Attributes)var2, (String[])null);
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException {
      return this.search(this.myParser.parse(var1), var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException {
      HierMemDirCtx var4 = (HierMemDirCtx)this.doLookup(var1, false);
      SearchControls var5 = new SearchControls();
      var5.setReturningAttributes(var3);
      return new LazySearchEnumerationImpl(var4.doListBindings(false), new ContainmentFilter(var2), var5, this, this.myEnv, false);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException {
      DirContext var4 = (DirContext)this.doLookup(var1, false);
      SearchFilter var5 = new SearchFilter(var2);
      return new LazySearchEnumerationImpl(new HierMemDirCtx.HierContextEnumerator(var4, var3 != null ? var3.getSearchScope() : 1), var5, var3, this, this.myEnv, this.alwaysUseFactory);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      String var5 = SearchFilter.format(var2, var3);
      return this.search(var1, var5, var4);
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException {
      return this.search(this.myParser.parse(var1), var2, var3);
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      return this.search(this.myParser.parse(var1), var2, var3, var4);
   }

   protected HierMemDirCtx createNewCtx() throws NamingException {
      return new HierMemDirCtx(this.myEnv, this.ignoreCase);
   }

   protected Name canonizeName(Name var1) throws NamingException {
      Object var2 = var1;
      if (!(var1 instanceof HierarchicalName)) {
         var2 = new HierarchicalName();
         int var3 = var1.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            ((Name)var2).add(var4, var1.get(var4));
         }
      }

      return (Name)var2;
   }

   protected Name getInternalName(Name var1) throws NamingException {
      return var1.getPrefix(var1.size() - 1);
   }

   protected Name getLeafName(Name var1) throws NamingException {
      return var1.getSuffix(var1.size() - 1);
   }

   public DirContext getSchema(String var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public DirContext getSchema(Name var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public DirContext getSchemaClassDefinition(String var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public DirContext getSchemaClassDefinition(Name var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public void setReadOnly(NamingException var1) {
      this.readOnlyEx = var1;
   }

   public void setIgnoreCase(boolean var1) {
      this.ignoreCase = var1;
   }

   public void setNameParser(NameParser var1) {
      this.myParser = var1;
   }

   public class HierContextEnumerator extends ContextEnumerator {
      public HierContextEnumerator(Context var2, int var3) throws NamingException {
         super(var2, var3);
      }

      protected HierContextEnumerator(Context var2, int var3, String var4, boolean var5) throws NamingException {
         super(var2, var3, var4, var5);
      }

      protected NamingEnumeration<Binding> getImmediateChildren(Context var1) throws NamingException {
         return ((HierMemDirCtx)var1).doListBindings(false);
      }

      protected ContextEnumerator newEnumerator(Context var1, int var2, String var3, boolean var4) throws NamingException {
         return HierMemDirCtx.this.new HierContextEnumerator(var1, var2, var3, var4);
      }
   }

   private final class FlatBindings extends HierMemDirCtx.BaseFlatNames<Binding> {
      private Hashtable<Name, Object> bds;
      private Hashtable<String, Object> env;
      private boolean useFactory;

      FlatBindings(Hashtable<Name, Object> var2, Hashtable<String, Object> var3, boolean var4) {
         super(var2.keys());
         this.env = var3;
         this.bds = var2;
         this.useFactory = var4;
      }

      public Binding next() throws NamingException {
         Name var1 = (Name)this.names.nextElement();
         HierMemDirCtx var2 = (HierMemDirCtx)this.bds.get(var1);
         Object var3 = var2;
         if (this.useFactory) {
            Attributes var4 = var2.getAttributes("");

            try {
               var3 = DirectoryManager.getObjectInstance(var2, var1, HierMemDirCtx.this, this.env, var4);
            } catch (NamingException var7) {
               throw var7;
            } catch (Exception var8) {
               NamingException var6 = new NamingException("Problem calling getObjectInstance");
               var6.setRootCause(var8);
               throw var6;
            }
         }

         return new Binding(var1.toString(), var3);
      }
   }

   private final class FlatNames extends HierMemDirCtx.BaseFlatNames<NameClassPair> {
      FlatNames(Enumeration<Name> var2) {
         super(var2);
      }

      public NameClassPair next() throws NamingException {
         Name var1 = (Name)this.names.nextElement();
         String var2 = HierMemDirCtx.this.bindings.get(var1).getClass().getName();
         return new NameClassPair(var1.toString(), var2);
      }
   }

   private abstract class BaseFlatNames<T> implements NamingEnumeration<T> {
      Enumeration<Name> names;

      BaseFlatNames(Enumeration<Name> var2) {
         this.names = var2;
      }

      public final boolean hasMoreElements() {
         try {
            return this.hasMore();
         } catch (NamingException var2) {
            return false;
         }
      }

      public final boolean hasMore() throws NamingException {
         return this.names.hasMoreElements();
      }

      public final T nextElement() {
         try {
            return this.next();
         } catch (NamingException var2) {
            throw new NoSuchElementException(var2.toString());
         }
      }

      public abstract T next() throws NamingException;

      public final void close() {
         this.names = null;
      }
   }
}
