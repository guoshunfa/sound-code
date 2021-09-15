package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.dir.HierMemDirCtx;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;

final class LdapSchemaCtx extends HierMemDirCtx {
   private static final boolean debug = false;
   private static final int LEAF = 0;
   private static final int SCHEMA_ROOT = 1;
   static final int OBJECTCLASS_ROOT = 2;
   static final int ATTRIBUTE_ROOT = 3;
   static final int SYNTAX_ROOT = 4;
   static final int MATCHRULE_ROOT = 5;
   static final int OBJECTCLASS = 6;
   static final int ATTRIBUTE = 7;
   static final int SYNTAX = 8;
   static final int MATCHRULE = 9;
   private LdapSchemaCtx.SchemaInfo info = null;
   private boolean setupMode = true;
   private int objectType;

   static DirContext createSchemaTree(Hashtable<String, Object> var0, String var1, LdapCtx var2, Attributes var3, boolean var4) throws NamingException {
      try {
         LdapSchemaParser var5 = new LdapSchemaParser(var4);
         LdapSchemaCtx.SchemaInfo var6 = new LdapSchemaCtx.SchemaInfo(var1, var2, var5);
         LdapSchemaCtx var7 = new LdapSchemaCtx(1, var0, var6);
         LdapSchemaParser.LDAP2JNDISchema(var3, var7);
         return var7;
      } catch (NamingException var8) {
         var2.close();
         throw var8;
      }
   }

   private LdapSchemaCtx(int var1, Hashtable<String, Object> var2, LdapSchemaCtx.SchemaInfo var3) {
      super(var2, true);
      this.objectType = var1;
      this.info = var3;
   }

   public void close() throws NamingException {
      this.info.close();
   }

   public final void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      if (!this.setupMode) {
         if (var2 != null) {
            throw new IllegalArgumentException("obj must be null");
         }

         this.addServerSchema(var3);
      }

      LdapSchemaCtx var4 = (LdapSchemaCtx)super.doCreateSubcontext(var1, var3);
   }

   protected final void doBind(Name var1, Object var2, Attributes var3, boolean var4) throws NamingException {
      if (!this.setupMode) {
         throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()");
      } else {
         super.doBind(var1, var2, var3, false);
      }
   }

   public final void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      try {
         this.doLookup(var1, false);
         throw new SchemaViolationException("Cannot replace existing schema object");
      } catch (NameNotFoundException var5) {
         this.bind(var1, var2, var3);
      }
   }

   protected final void doRebind(Name var1, Object var2, Attributes var3, boolean var4) throws NamingException {
      if (!this.setupMode) {
         throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()");
      } else {
         super.doRebind(var1, var2, var3, false);
      }
   }

   protected final void doUnbind(Name var1) throws NamingException {
      if (!this.setupMode) {
         try {
            LdapSchemaCtx var2 = (LdapSchemaCtx)this.doLookup(var1, false);
            this.deleteServerSchema(var2.attrs);
         } catch (NameNotFoundException var3) {
            return;
         }
      }

      super.doUnbind(var1);
   }

   protected final void doRename(Name var1, Name var2) throws NamingException {
      if (!this.setupMode) {
         throw new SchemaViolationException("Cannot rename a schema object");
      } else {
         super.doRename(var1, var2);
      }
   }

   protected final void doDestroySubcontext(Name var1) throws NamingException {
      if (!this.setupMode) {
         try {
            LdapSchemaCtx var2 = (LdapSchemaCtx)this.doLookup(var1, false);
            this.deleteServerSchema(var2.attrs);
         } catch (NameNotFoundException var3) {
            return;
         }
      }

      super.doDestroySubcontext(var1);
   }

   final LdapSchemaCtx setup(int var1, String var2, Attributes var3) throws NamingException {
      LdapSchemaCtx var5;
      try {
         this.setupMode = true;
         LdapSchemaCtx var4 = (LdapSchemaCtx)super.doCreateSubcontext(new CompositeName(var2), var3);
         var4.objectType = var1;
         var4.setupMode = false;
         var5 = var4;
      } finally {
         this.setupMode = false;
      }

      return var5;
   }

   protected final DirContext doCreateSubcontext(Name var1, Attributes var2) throws NamingException {
      if (var2 != null && var2.size() != 0) {
         if (!this.setupMode) {
            this.addServerSchema(var2);
         }

         LdapSchemaCtx var3 = (LdapSchemaCtx)super.doCreateSubcontext(var1, var2);
         return var3;
      } else {
         throw new SchemaViolationException("Must supply attributes describing schema");
      }
   }

   private static final Attributes deepClone(Attributes var0) throws NamingException {
      BasicAttributes var1 = new BasicAttributes(true);
      NamingEnumeration var2 = var0.getAll();

      while(var2.hasMore()) {
         var1.put((Attribute)((Attribute)var2.next()).clone());
      }

      return var1;
   }

   protected final void doModifyAttributes(ModificationItem[] var1) throws NamingException {
      if (this.setupMode) {
         super.doModifyAttributes(var1);
      } else {
         Attributes var2 = deepClone(this.attrs);
         applyMods(var1, var2);
         this.modifyServerSchema(this.attrs, var2);
         this.attrs = var2;
      }

   }

   protected final HierMemDirCtx createNewCtx() {
      LdapSchemaCtx var1 = new LdapSchemaCtx(0, this.myEnv, this.info);
      return var1;
   }

   private final void addServerSchema(Attributes var1) throws NamingException {
      Attribute var2;
      switch(this.objectType) {
      case 1:
         throw new SchemaViolationException("Cannot create new entry under schema root");
      case 2:
         var2 = this.info.parser.stringifyObjDesc(var1);
         break;
      case 3:
         var2 = this.info.parser.stringifyAttrDesc(var1);
         break;
      case 4:
         var2 = this.info.parser.stringifySyntaxDesc(var1);
         break;
      case 5:
         var2 = this.info.parser.stringifyMatchRuleDesc(var1);
         break;
      default:
         throw new SchemaViolationException("Cannot create child of schema object");
      }

      BasicAttributes var3 = new BasicAttributes(true);
      var3.put(var2);
      this.info.modifyAttributes(this.myEnv, 1, var3);
   }

   private final void deleteServerSchema(Attributes var1) throws NamingException {
      Attribute var2;
      switch(this.objectType) {
      case 1:
         throw new SchemaViolationException("Cannot delete schema root");
      case 2:
         var2 = this.info.parser.stringifyObjDesc(var1);
         break;
      case 3:
         var2 = this.info.parser.stringifyAttrDesc(var1);
         break;
      case 4:
         var2 = this.info.parser.stringifySyntaxDesc(var1);
         break;
      case 5:
         var2 = this.info.parser.stringifyMatchRuleDesc(var1);
         break;
      default:
         throw new SchemaViolationException("Cannot delete child of schema object");
      }

      ModificationItem[] var3 = new ModificationItem[]{new ModificationItem(3, var2)};
      this.info.modifyAttributes(this.myEnv, var3);
   }

   private final void modifyServerSchema(Attributes var1, Attributes var2) throws NamingException {
      Attribute var3;
      Attribute var4;
      switch(this.objectType) {
      case 6:
         var4 = this.info.parser.stringifyObjDesc(var1);
         var3 = this.info.parser.stringifyObjDesc(var2);
         break;
      case 7:
         var4 = this.info.parser.stringifyAttrDesc(var1);
         var3 = this.info.parser.stringifyAttrDesc(var2);
         break;
      case 8:
         var4 = this.info.parser.stringifySyntaxDesc(var1);
         var3 = this.info.parser.stringifySyntaxDesc(var2);
         break;
      case 9:
         var4 = this.info.parser.stringifyMatchRuleDesc(var1);
         var3 = this.info.parser.stringifyMatchRuleDesc(var2);
         break;
      default:
         throw new SchemaViolationException("Cannot modify schema root");
      }

      ModificationItem[] var5 = new ModificationItem[]{new ModificationItem(3, var4), new ModificationItem(1, var3)};
      this.info.modifyAttributes(this.myEnv, var5);
   }

   private static final class SchemaInfo {
      private LdapCtx schemaEntry;
      private String schemaEntryName;
      LdapSchemaParser parser;
      private String host;
      private int port;
      private boolean hasLdapsScheme;

      SchemaInfo(String var1, LdapCtx var2, LdapSchemaParser var3) {
         this.schemaEntryName = var1;
         this.schemaEntry = var2;
         this.parser = var3;
         this.port = var2.port_number;
         this.host = var2.hostname;
         this.hasLdapsScheme = var2.hasLdapsScheme;
      }

      synchronized void close() throws NamingException {
         if (this.schemaEntry != null) {
            this.schemaEntry.close();
            this.schemaEntry = null;
         }

      }

      private LdapCtx reopenEntry(Hashtable<?, ?> var1) throws NamingException {
         return new LdapCtx(this.schemaEntryName, this.host, this.port, var1, this.hasLdapsScheme);
      }

      synchronized void modifyAttributes(Hashtable<?, ?> var1, ModificationItem[] var2) throws NamingException {
         if (this.schemaEntry == null) {
            this.schemaEntry = this.reopenEntry(var1);
         }

         this.schemaEntry.modifyAttributes("", var2);
      }

      synchronized void modifyAttributes(Hashtable<?, ?> var1, int var2, Attributes var3) throws NamingException {
         if (this.schemaEntry == null) {
            this.schemaEntry = this.reopenEntry(var1);
         }

         this.schemaEntry.modifyAttributes("", var2, var3);
      }
   }
}
