package javax.naming.directory;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;

public class InitialDirContext extends InitialContext implements DirContext {
   protected InitialDirContext(boolean var1) throws NamingException {
      super(var1);
   }

   public InitialDirContext() throws NamingException {
   }

   public InitialDirContext(Hashtable<?, ?> var1) throws NamingException {
      super(var1);
   }

   private DirContext getURLOrDefaultInitDirCtx(String var1) throws NamingException {
      Context var2 = this.getURLOrDefaultInitCtx(var1);
      if (!(var2 instanceof DirContext)) {
         if (var2 == null) {
            throw new NoInitialContextException();
         } else {
            throw new NotContextException("Not an instance of DirContext");
         }
      } else {
         return (DirContext)var2;
      }
   }

   private DirContext getURLOrDefaultInitDirCtx(Name var1) throws NamingException {
      Context var2 = this.getURLOrDefaultInitCtx(var1);
      if (!(var2 instanceof DirContext)) {
         if (var2 == null) {
            throw new NoInitialContextException();
         } else {
            throw new NotContextException("Not an instance of DirContext");
         }
      } else {
         return (DirContext)var2;
      }
   }

   public Attributes getAttributes(String var1) throws NamingException {
      return this.getAttributes((String)var1, (String[])null);
   }

   public Attributes getAttributes(String var1, String[] var2) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).getAttributes(var1, var2);
   }

   public Attributes getAttributes(Name var1) throws NamingException {
      return this.getAttributes((Name)var1, (String[])null);
   }

   public Attributes getAttributes(Name var1, String[] var2) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).getAttributes(var1, var2);
   }

   public void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).modifyAttributes(var1, var2, var3);
   }

   public void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).modifyAttributes(var1, var2, var3);
   }

   public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).modifyAttributes(var1, var2);
   }

   public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).modifyAttributes(var1, var2);
   }

   public void bind(String var1, Object var2, Attributes var3) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).bind(var1, var2, var3);
   }

   public void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).bind(var1, var2, var3);
   }

   public void rebind(String var1, Object var2, Attributes var3) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).rebind(var1, var2, var3);
   }

   public void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      this.getURLOrDefaultInitDirCtx(var1).rebind(var1, var2, var3);
   }

   public DirContext createSubcontext(String var1, Attributes var2) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).createSubcontext(var1, var2);
   }

   public DirContext createSubcontext(Name var1, Attributes var2) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).createSubcontext(var1, var2);
   }

   public DirContext getSchema(String var1) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).getSchema(var1);
   }

   public DirContext getSchema(Name var1) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).getSchema(var1);
   }

   public DirContext getSchemaClassDefinition(String var1) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).getSchemaClassDefinition(var1);
   }

   public DirContext getSchemaClassDefinition(Name var1) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).getSchemaClassDefinition(var1);
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2);
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2, var3);
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2, var3);
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2, var3, var4);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      return this.getURLOrDefaultInitDirCtx(var1).search(var1, var2, var3, var4);
   }
}
