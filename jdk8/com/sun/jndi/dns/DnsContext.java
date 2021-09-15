package com.sun.jndi.dns;

import com.sun.jndi.toolkit.ctx.ComponentDirContext;
import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public class DnsContext extends ComponentDirContext {
   DnsName domain;
   Hashtable<Object, Object> environment;
   private boolean envShared;
   private boolean parentIsDns;
   private String[] servers;
   private Resolver resolver;
   private boolean authoritative;
   private boolean recursion;
   private int timeout;
   private int retries;
   static final NameParser nameParser = new DnsNameParser();
   private static final int DEFAULT_INIT_TIMEOUT = 1000;
   private static final int DEFAULT_RETRIES = 4;
   private static final String INIT_TIMEOUT = "com.sun.jndi.dns.timeout.initial";
   private static final String RETRIES = "com.sun.jndi.dns.timeout.retries";
   private CT lookupCT;
   private static final String LOOKUP_ATTR = "com.sun.jndi.dns.lookup.attr";
   private static final String RECURSION = "com.sun.jndi.dns.recursion";
   private static final int ANY = 255;
   private static final ZoneNode zoneTree = new ZoneNode((String)null);
   private static final boolean debug = false;

   public DnsContext(String var1, String[] var2, Hashtable<?, ?> var3) throws NamingException {
      this.domain = new DnsName(var1.endsWith(".") ? var1 : var1 + ".");
      this.servers = var2 == null ? null : (String[])var2.clone();
      this.environment = (Hashtable)var3.clone();
      this.envShared = false;
      this.parentIsDns = false;
      this.resolver = null;
      this.initFromEnvironment();
   }

   DnsContext(DnsContext var1, DnsName var2) {
      this(var1);
      this.domain = var2;
      this.parentIsDns = true;
   }

   private DnsContext(DnsContext var1) {
      this.environment = var1.environment;
      this.envShared = var1.envShared = true;
      this.parentIsDns = var1.parentIsDns;
      this.domain = var1.domain;
      this.servers = var1.servers;
      this.resolver = var1.resolver;
      this.authoritative = var1.authoritative;
      this.recursion = var1.recursion;
      this.timeout = var1.timeout;
      this.retries = var1.retries;
      this.lookupCT = var1.lookupCT;
   }

   public void close() {
      if (this.resolver != null) {
         this.resolver.close();
         this.resolver = null;
      }

   }

   protected Hashtable<?, ?> p_getEnvironment() {
      return this.environment;
   }

   public Hashtable<?, ?> getEnvironment() throws NamingException {
      return (Hashtable)this.environment.clone();
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      if (var1.equals("com.sun.jndi.dns.lookup.attr")) {
         this.lookupCT = this.getLookupCT((String)var2);
      } else if (var1.equals("java.naming.authoritative")) {
         this.authoritative = "true".equalsIgnoreCase((String)var2);
      } else if (var1.equals("com.sun.jndi.dns.recursion")) {
         this.recursion = "true".equalsIgnoreCase((String)var2);
      } else {
         int var3;
         if (var1.equals("com.sun.jndi.dns.timeout.initial")) {
            var3 = Integer.parseInt((String)var2);
            if (this.timeout != var3) {
               this.timeout = var3;
               this.resolver = null;
            }
         } else if (var1.equals("com.sun.jndi.dns.timeout.retries")) {
            var3 = Integer.parseInt((String)var2);
            if (this.retries != var3) {
               this.retries = var3;
               this.resolver = null;
            }
         }
      }

      if (!this.envShared) {
         return this.environment.put(var1, var2);
      } else if (this.environment.get(var1) != var2) {
         this.environment = (Hashtable)this.environment.clone();
         this.envShared = false;
         return this.environment.put(var1, var2);
      } else {
         return var2;
      }
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      if (var1.equals("com.sun.jndi.dns.lookup.attr")) {
         this.lookupCT = this.getLookupCT((String)null);
      } else if (var1.equals("java.naming.authoritative")) {
         this.authoritative = false;
      } else if (var1.equals("com.sun.jndi.dns.recursion")) {
         this.recursion = true;
      } else if (var1.equals("com.sun.jndi.dns.timeout.initial")) {
         if (this.timeout != 1000) {
            this.timeout = 1000;
            this.resolver = null;
         }
      } else if (var1.equals("com.sun.jndi.dns.timeout.retries") && this.retries != 4) {
         this.retries = 4;
         this.resolver = null;
      }

      if (!this.envShared) {
         return this.environment.remove(var1);
      } else if (this.environment.get(var1) != null) {
         this.environment = (Hashtable)this.environment.clone();
         this.envShared = false;
         return this.environment.remove(var1);
      } else {
         return null;
      }
   }

   void setProviderUrl(String var1) {
      this.environment.put("java.naming.provider.url", var1);
   }

   private void initFromEnvironment() throws InvalidAttributeIdentifierException {
      this.lookupCT = this.getLookupCT((String)this.environment.get("com.sun.jndi.dns.lookup.attr"));
      this.authoritative = "true".equalsIgnoreCase((String)this.environment.get("java.naming.authoritative"));
      String var1 = (String)this.environment.get("com.sun.jndi.dns.recursion");
      this.recursion = var1 == null || "true".equalsIgnoreCase(var1);
      var1 = (String)this.environment.get("com.sun.jndi.dns.timeout.initial");
      this.timeout = var1 == null ? 1000 : Integer.parseInt(var1);
      var1 = (String)this.environment.get("com.sun.jndi.dns.timeout.retries");
      this.retries = var1 == null ? 4 : Integer.parseInt(var1);
   }

   private CT getLookupCT(String var1) throws InvalidAttributeIdentifierException {
      return var1 == null ? new CT(1, 16) : fromAttrId(var1);
   }

   public Object c_lookup(Name var1, Continuation var2) throws NamingException {
      var2.setSuccess();
      if (var1.isEmpty()) {
         DnsContext var9 = new DnsContext(this);
         var9.resolver = new Resolver(this.servers, this.timeout, this.retries);
         return var9;
      } else {
         try {
            DnsName var3 = this.fullyQualify(var1);
            ResourceRecords var10 = this.getResolver().query(var3, this.lookupCT.rrclass, this.lookupCT.rrtype, this.recursion, this.authoritative);
            Attributes var5 = rrsToAttrs(var10, (CT[])null);
            DnsContext var6 = new DnsContext(this, var3);
            return DirectoryManager.getObjectInstance(var6, var1, this, this.environment, var5);
         } catch (NamingException var7) {
            var2.setError(this, (Name)var1);
            throw var2.fillInException(var7);
         } catch (Exception var8) {
            var2.setError(this, (Name)var1);
            NamingException var4 = new NamingException("Problem generating object using object factory");
            var4.setRootCause(var8);
            throw var2.fillInException(var4);
         }
      }
   }

   public Object c_lookupLink(Name var1, Continuation var2) throws NamingException {
      return this.c_lookup(var1, var2);
   }

   public NamingEnumeration<NameClassPair> c_list(Name var1, Continuation var2) throws NamingException {
      var2.setSuccess();

      try {
         DnsName var3 = this.fullyQualify(var1);
         NameNode var4 = this.getNameNode(var3);
         DnsContext var5 = new DnsContext(this, var3);
         return new NameClassPairEnumeration(var5, var4.getChildren());
      } catch (NamingException var6) {
         var2.setError(this, (Name)var1);
         throw var2.fillInException(var6);
      }
   }

   public NamingEnumeration<Binding> c_listBindings(Name var1, Continuation var2) throws NamingException {
      var2.setSuccess();

      try {
         DnsName var3 = this.fullyQualify(var1);
         NameNode var4 = this.getNameNode(var3);
         DnsContext var5 = new DnsContext(this, var3);
         return new BindingEnumeration(var5, var4.getChildren());
      } catch (NamingException var6) {
         var2.setError(this, (Name)var1);
         throw var2.fillInException(var6);
      }
   }

   public void c_bind(Name var1, Object var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);
      throw var3.fillInException(new OperationNotSupportedException());
   }

   public void c_rebind(Name var1, Object var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);
      throw var3.fillInException(new OperationNotSupportedException());
   }

   public void c_unbind(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);
      throw var2.fillInException(new OperationNotSupportedException());
   }

   public void c_rename(Name var1, Name var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);
      throw var3.fillInException(new OperationNotSupportedException());
   }

   public Context c_createSubcontext(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);
      throw var2.fillInException(new OperationNotSupportedException());
   }

   public void c_destroySubcontext(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);
      throw var2.fillInException(new OperationNotSupportedException());
   }

   public NameParser c_getNameParser(Name var1, Continuation var2) throws NamingException {
      var2.setSuccess();
      return nameParser;
   }

   public void c_bind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      var4.setError(this, (Name)var1);
      throw var4.fillInException(new OperationNotSupportedException());
   }

   public void c_rebind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      var4.setError(this, (Name)var1);
      throw var4.fillInException(new OperationNotSupportedException());
   }

   public DirContext c_createSubcontext(Name var1, Attributes var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);
      throw var3.fillInException(new OperationNotSupportedException());
   }

   public Attributes c_getAttributes(Name var1, String[] var2, Continuation var3) throws NamingException {
      var3.setSuccess();

      try {
         DnsName var4 = this.fullyQualify(var1);
         CT[] var5 = attrIdsToClassesAndTypes(var2);
         CT var6 = getClassAndTypeToQuery(var5);
         ResourceRecords var7 = this.getResolver().query(var4, var6.rrclass, var6.rrtype, this.recursion, this.authoritative);
         return rrsToAttrs(var7, var5);
      } catch (NamingException var8) {
         var3.setError(this, (Name)var1);
         throw var3.fillInException(var8);
      }
   }

   public void c_modifyAttributes(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException {
      var4.setError(this, (Name)var1);
      throw var4.fillInException(new OperationNotSupportedException());
   }

   public void c_modifyAttributes(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException {
      var3.setError(this, (Name)var1);
      throw var3.fillInException(new OperationNotSupportedException());
   }

   public NamingEnumeration<SearchResult> c_search(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public NamingEnumeration<SearchResult> c_search(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public NamingEnumeration<SearchResult> c_search(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public DirContext c_getSchema(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);
      throw var2.fillInException(new OperationNotSupportedException());
   }

   public DirContext c_getSchemaClassDefinition(Name var1, Continuation var2) throws NamingException {
      var2.setError(this, (Name)var1);
      throw var2.fillInException(new OperationNotSupportedException());
   }

   public String getNameInNamespace() {
      return this.domain.toString();
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      if (!(var2 instanceof DnsName) && !(var2 instanceof CompositeName)) {
         var2 = (new DnsName()).addAll(var2);
      }

      if (!(var1 instanceof DnsName) && !(var1 instanceof CompositeName)) {
         var1 = (new DnsName()).addAll(var1);
      }

      if (var2 instanceof DnsName && var1 instanceof DnsName) {
         DnsName var8 = (DnsName)((DnsName)var2.clone());
         var8.addAll(var1);
         return (new CompositeName()).add(var8.toString());
      } else {
         Name var4 = var2 instanceof CompositeName ? var2 : (new CompositeName()).add(var2.toString());
         Name var5 = var1 instanceof CompositeName ? var1 : (new CompositeName()).add(var1.toString());
         int var6 = var4.size() - 1;
         if (!var5.isEmpty() && !var5.get(0).equals("") && !var4.isEmpty() && !var4.get(var6).equals("")) {
            Object var3 = var2 == var4 ? (CompositeName)var4.clone() : var4;
            ((Name)var3).addAll(var5);
            if (this.parentIsDns) {
               DnsName var7 = var2 instanceof DnsName ? (DnsName)var2.clone() : new DnsName(var4.get(var6));
               var7.addAll((Name)(var1 instanceof DnsName ? var1 : new DnsName(var5.get(0))));
               ((Name)var3).remove(var6 + 1);
               ((Name)var3).remove(var6);
               ((Name)var3).add(var6, var7.toString());
            }

            return (Name)var3;
         } else {
            return super.composeName(var5, var4);
         }
      }
   }

   private synchronized Resolver getResolver() throws NamingException {
      if (this.resolver == null) {
         this.resolver = new Resolver(this.servers, this.timeout, this.retries);
      }

      return this.resolver;
   }

   DnsName fullyQualify(Name var1) throws NamingException {
      if (var1.isEmpty()) {
         return this.domain;
      } else {
         DnsName var2 = var1 instanceof CompositeName ? new DnsName(var1.get(0)) : (DnsName)(new DnsName()).addAll(var1);
         if (var2.hasRootLabel()) {
            if (this.domain.size() == 1) {
               return var2;
            } else {
               throw new InvalidNameException("DNS name " + var2 + " not relative to " + this.domain);
            }
         } else {
            return (DnsName)var2.addAll(0, this.domain);
         }
      }
   }

   private static Attributes rrsToAttrs(ResourceRecords var0, CT[] var1) {
      BasicAttributes var2 = new BasicAttributes(true);

      for(int var3 = 0; var3 < var0.answer.size(); ++var3) {
         ResourceRecord var4 = (ResourceRecord)var0.answer.elementAt(var3);
         int var5 = var4.getType();
         int var6 = var4.getRrclass();
         if (classAndTypeMatch(var6, var5, var1)) {
            String var7 = toAttrId(var6, var5);
            Object var8 = var2.get(var7);
            if (var8 == null) {
               var8 = new BasicAttribute(var7);
               var2.put((Attribute)var8);
            }

            ((Attribute)var8).add(var4.getRdata());
         }
      }

      return var2;
   }

   private static boolean classAndTypeMatch(int var0, int var1, CT[] var2) {
      if (var2 == null) {
         return true;
      } else {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            CT var4 = var2[var3];
            boolean var5 = var4.rrclass == 255 || var4.rrclass == var0;
            boolean var6 = var4.rrtype == 255 || var4.rrtype == var1;
            if (var5 && var6) {
               return true;
            }
         }

         return false;
      }
   }

   private static String toAttrId(int var0, int var1) {
      String var2 = ResourceRecord.getTypeName(var1);
      if (var0 != 1) {
         var2 = ResourceRecord.getRrclassName(var0) + " " + var2;
      }

      return var2;
   }

   private static CT fromAttrId(String var0) throws InvalidAttributeIdentifierException {
      if (var0.equals("")) {
         throw new InvalidAttributeIdentifierException("Attribute ID cannot be empty");
      } else {
         int var3 = var0.indexOf(32);
         int var1;
         String var4;
         if (var3 < 0) {
            var1 = 1;
         } else {
            var4 = var0.substring(0, var3);
            var1 = ResourceRecord.getRrclass(var4);
            if (var1 < 0) {
               throw new InvalidAttributeIdentifierException("Unknown resource record class '" + var4 + '\'');
            }
         }

         var4 = var0.substring(var3 + 1);
         int var2 = ResourceRecord.getType(var4);
         if (var2 < 0) {
            throw new InvalidAttributeIdentifierException("Unknown resource record type '" + var4 + '\'');
         } else {
            return new CT(var1, var2);
         }
      }
   }

   private static CT[] attrIdsToClassesAndTypes(String[] var0) throws InvalidAttributeIdentifierException {
      if (var0 == null) {
         return null;
      } else {
         CT[] var1 = new CT[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = fromAttrId(var0[var2]);
         }

         return var1;
      }
   }

   private static CT getClassAndTypeToQuery(CT[] var0) {
      int var1;
      int var2;
      if (var0 == null) {
         var1 = 255;
         var2 = 255;
      } else if (var0.length == 0) {
         var1 = 1;
         var2 = 255;
      } else {
         var1 = var0[0].rrclass;
         var2 = var0[0].rrtype;

         for(int var3 = 1; var3 < var0.length; ++var3) {
            if (var1 != var0[var3].rrclass) {
               var1 = 255;
            }

            if (var2 != var0[var3].rrtype) {
               var2 = 255;
            }
         }
      }

      return new CT(var1, var2);
   }

   private NameNode getNameNode(DnsName var1) throws NamingException {
      dprint("getNameNode(" + var1 + ")");
      ZoneNode var2;
      synchronized(zoneTree) {
         var2 = zoneTree.getDeepestPopulated(var1);
      }

      dprint("Deepest related zone in zone tree: " + (var2 != null ? var2.getLabel() : "[none]"));
      DnsName var3;
      NameNode var4;
      NameNode var5;
      if (var2 != null) {
         synchronized(var2) {
            var4 = var2.getContents();
         }

         if (var4 != null) {
            var5 = var4.get(var1, var2.depth() + 1);
            if (var5 != null && !var5.isZoneCut()) {
               dprint("Found node " + var1 + " in zone tree");
               var3 = (DnsName)var1.getPrefix(var2.depth() + 1);
               boolean var6 = this.isZoneCurrent(var2, var3);
               boolean var7 = false;
               synchronized(var2) {
                  if (var4 != var2.getContents()) {
                     var7 = true;
                  } else {
                     if (var6) {
                        return var5;
                     }

                     var2.depopulate();
                  }
               }

               dprint("Zone not current; discarding node");
               if (var7) {
                  return this.getNameNode(var1);
               }
            }
         }
      }

      dprint("Adding node " + var1 + " to zone tree");
      var3 = this.getResolver().findZoneName(var1, 1, this.recursion);
      dprint("Node's zone is " + var3);
      synchronized(zoneTree) {
         var2 = (ZoneNode)zoneTree.add(var3, 1);
      }

      synchronized(var2) {
         var4 = var2.isPopulated() ? var2.getContents() : this.populateZone(var2, var3);
      }

      var5 = var4.get(var1, var3.size());
      if (var5 == null) {
         throw new ConfigurationException("DNS error: node not found in its own zone");
      } else {
         dprint("Found node in newly-populated zone");
         return var5;
      }
   }

   private NameNode populateZone(ZoneNode var1, DnsName var2) throws NamingException {
      dprint("Populating zone " + var2);
      ResourceRecords var3 = this.getResolver().queryZone(var2, 1, this.recursion);
      dprint("zone xfer complete: " + var3.answer.size() + " records");
      return var1.populate(var2, var3);
   }

   private boolean isZoneCurrent(ZoneNode var1, DnsName var2) throws NamingException {
      if (!var1.isPopulated()) {
         return false;
      } else {
         ResourceRecord var3 = this.getResolver().findSoa(var2, 1, this.recursion);
         synchronized(var1) {
            if (var3 == null) {
               var1.depopulate();
            }

            return var1.isPopulated() && var1.compareSerialNumberTo(var3) >= 0;
         }
      }
   }

   private static final void dprint(String var0) {
   }
}
