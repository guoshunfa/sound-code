package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

class ContinuationDirContext extends ContinuationContext implements DirContext {
   ContinuationDirContext(CannotProceedException var1, Hashtable<?, ?> var2) {
      super(var1, var2);
   }

   protected DirContextNamePair getTargetContext(Name var1) throws NamingException {
      if (this.cpe.getResolvedObj() == null) {
         throw (NamingException)this.cpe.fillInStackTrace();
      } else {
         Context var2 = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
         if (var2 == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
         } else if (var2 instanceof DirContext) {
            return new DirContextNamePair((DirContext)var2, var1);
         } else if (var2 instanceof Resolver) {
            Resolver var6 = (Resolver)var2;
            ResolveResult var4 = var6.resolveToClass(var1, DirContext.class);
            DirContext var5 = (DirContext)var4.getResolvedObj();
            return new DirContextNamePair(var5, var4.getRemainingName());
         } else {
            Object var3 = var2.lookup(var1);
            if (var3 instanceof DirContext) {
               return new DirContextNamePair((DirContext)var3, new CompositeName());
            } else {
               throw (NamingException)this.cpe.fillInStackTrace();
            }
         }
      }
   }

   protected DirContextStringPair getTargetContext(String var1) throws NamingException {
      if (this.cpe.getResolvedObj() == null) {
         throw (NamingException)this.cpe.fillInStackTrace();
      } else {
         Context var2 = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
         if (var2 instanceof DirContext) {
            return new DirContextStringPair((DirContext)var2, var1);
         } else if (var2 instanceof Resolver) {
            Resolver var8 = (Resolver)var2;
            ResolveResult var4 = var8.resolveToClass(var1, DirContext.class);
            DirContext var5 = (DirContext)var4.getResolvedObj();
            Name var6 = var4.getRemainingName();
            String var7 = var6 != null ? var6.toString() : "";
            return new DirContextStringPair(var5, var7);
         } else {
            Object var3 = var2.lookup(var1);
            if (var3 instanceof DirContext) {
               return new DirContextStringPair((DirContext)var3, "");
            } else {
               throw (NamingException)this.cpe.fillInStackTrace();
            }
         }
      }
   }

   public Attributes getAttributes(String var1) throws NamingException {
      DirContextStringPair var2 = this.getTargetContext(var1);
      return var2.getDirContext().getAttributes(var2.getString());
   }

   public Attributes getAttributes(String var1, String[] var2) throws NamingException {
      DirContextStringPair var3 = this.getTargetContext(var1);
      return var3.getDirContext().getAttributes(var3.getString(), var2);
   }

   public Attributes getAttributes(Name var1) throws NamingException {
      DirContextNamePair var2 = this.getTargetContext(var1);
      return var2.getDirContext().getAttributes(var2.getName());
   }

   public Attributes getAttributes(Name var1, String[] var2) throws NamingException {
      DirContextNamePair var3 = this.getTargetContext(var1);
      return var3.getDirContext().getAttributes(var3.getName(), var2);
   }

   public void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException {
      DirContextNamePair var4 = this.getTargetContext(var1);
      var4.getDirContext().modifyAttributes(var4.getName(), var2, var3);
   }

   public void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException {
      DirContextStringPair var4 = this.getTargetContext(var1);
      var4.getDirContext().modifyAttributes(var4.getString(), var2, var3);
   }

   public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException {
      DirContextNamePair var3 = this.getTargetContext(var1);
      var3.getDirContext().modifyAttributes(var3.getName(), var2);
   }

   public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException {
      DirContextStringPair var3 = this.getTargetContext(var1);
      var3.getDirContext().modifyAttributes(var3.getString(), var2);
   }

   public void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      DirContextNamePair var4 = this.getTargetContext(var1);
      var4.getDirContext().bind(var4.getName(), var2, var3);
   }

   public void bind(String var1, Object var2, Attributes var3) throws NamingException {
      DirContextStringPair var4 = this.getTargetContext(var1);
      var4.getDirContext().bind(var4.getString(), var2, var3);
   }

   public void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      DirContextNamePair var4 = this.getTargetContext(var1);
      var4.getDirContext().rebind(var4.getName(), var2, var3);
   }

   public void rebind(String var1, Object var2, Attributes var3) throws NamingException {
      DirContextStringPair var4 = this.getTargetContext(var1);
      var4.getDirContext().rebind(var4.getString(), var2, var3);
   }

   public DirContext createSubcontext(Name var1, Attributes var2) throws NamingException {
      DirContextNamePair var3 = this.getTargetContext(var1);
      return var3.getDirContext().createSubcontext(var3.getName(), var2);
   }

   public DirContext createSubcontext(String var1, Attributes var2) throws NamingException {
      DirContextStringPair var3 = this.getTargetContext(var1);
      return var3.getDirContext().createSubcontext(var3.getString(), var2);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException {
      DirContextNamePair var4 = this.getTargetContext(var1);
      return var4.getDirContext().search(var4.getName(), var2, var3);
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException {
      DirContextStringPair var4 = this.getTargetContext(var1);
      return var4.getDirContext().search(var4.getString(), var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException {
      DirContextNamePair var3 = this.getTargetContext(var1);
      return var3.getDirContext().search(var3.getName(), var2);
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException {
      DirContextStringPair var3 = this.getTargetContext(var1);
      return var3.getDirContext().search(var3.getString(), var2);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException {
      DirContextNamePair var4 = this.getTargetContext(var1);
      return var4.getDirContext().search(var4.getName(), var2, var3);
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException {
      DirContextStringPair var4 = this.getTargetContext(var1);
      return var4.getDirContext().search(var4.getString(), var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      DirContextNamePair var5 = this.getTargetContext(var1);
      return var5.getDirContext().search(var5.getName(), var2, var3, var4);
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      DirContextStringPair var5 = this.getTargetContext(var1);
      return var5.getDirContext().search(var5.getString(), var2, var3, var4);
   }

   public DirContext getSchema(String var1) throws NamingException {
      DirContextStringPair var2 = this.getTargetContext(var1);
      return var2.getDirContext().getSchema(var2.getString());
   }

   public DirContext getSchema(Name var1) throws NamingException {
      DirContextNamePair var2 = this.getTargetContext(var1);
      return var2.getDirContext().getSchema(var2.getName());
   }

   public DirContext getSchemaClassDefinition(String var1) throws NamingException {
      DirContextStringPair var2 = this.getTargetContext(var1);
      return var2.getDirContext().getSchemaClassDefinition(var2.getString());
   }

   public DirContext getSchemaClassDefinition(Name var1) throws NamingException {
      DirContextNamePair var2 = this.getTargetContext(var1);
      return var2.getDirContext().getSchemaClassDefinition(var2.getName());
   }
}
