package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

final class LdapAttribute extends BasicAttribute {
   static final long serialVersionUID = -4288716561020779584L;
   private transient DirContext baseCtx = null;
   private Name rdn = new CompositeName();
   private String baseCtxURL;
   private Hashtable<String, ? super String> baseCtxEnv;

   public Object clone() {
      LdapAttribute var1 = new LdapAttribute(this.attrID, this.baseCtx, this.rdn);
      var1.values = (Vector)this.values.clone();
      return var1;
   }

   public boolean add(Object var1) {
      this.values.addElement(var1);
      return true;
   }

   LdapAttribute(String var1) {
      super(var1);
   }

   private LdapAttribute(String var1, DirContext var2, Name var3) {
      super(var1);
      this.baseCtx = var2;
      this.rdn = var3;
   }

   void setParent(DirContext var1, Name var2) {
      this.baseCtx = var1;
      this.rdn = var2;
   }

   private DirContext getBaseCtx() throws NamingException {
      if (this.baseCtx == null) {
         if (this.baseCtxEnv == null) {
            this.baseCtxEnv = new Hashtable(3);
         }

         this.baseCtxEnv.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
         this.baseCtxEnv.put("java.naming.provider.url", this.baseCtxURL);
         this.baseCtx = new InitialDirContext(this.baseCtxEnv);
      }

      return this.baseCtx;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.setBaseCtxInfo();
      var1.defaultWriteObject();
   }

   private void setBaseCtxInfo() {
      Hashtable var1 = null;
      Hashtable var2 = null;
      if (this.baseCtx != null) {
         var1 = ((LdapCtx)this.baseCtx).envprops;
         this.baseCtxURL = ((LdapCtx)this.baseCtx).getURL();
      }

      if (var1 != null && var1.size() > 0) {
         Iterator var3 = var1.keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (var4.indexOf("security") != -1) {
               if (var2 == null) {
                  var2 = (Hashtable)var1.clone();
               }

               var2.remove(var4);
            }
         }
      }

      this.baseCtxEnv = var2 == null ? var1 : var2;
   }

   public DirContext getAttributeSyntaxDefinition() throws NamingException {
      DirContext var1 = this.getBaseCtx().getSchema(this.rdn);
      DirContext var2 = (DirContext)var1.lookup("AttributeDefinition/" + this.getID());
      Attribute var3 = var2.getAttributes("").get("SYNTAX");
      if (var3 != null && var3.size() != 0) {
         String var4 = (String)var3.get();
         return (DirContext)var1.lookup("SyntaxDefinition/" + var4);
      } else {
         throw new NameNotFoundException(this.getID() + "does not have a syntax associated with it");
      }
   }

   public DirContext getAttributeDefinition() throws NamingException {
      DirContext var1 = this.getBaseCtx().getSchema(this.rdn);
      return (DirContext)var1.lookup("AttributeDefinition/" + this.getID());
   }
}
