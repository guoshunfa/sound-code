package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.dir.SearchFilter;
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
import javax.naming.NotContextException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import javax.naming.spi.NamingManager;

final class LdapReferralContext implements DirContext, LdapContext {
   private DirContext refCtx = null;
   private Name urlName = null;
   private String urlAttrs = null;
   private String urlScope = null;
   private String urlFilter = null;
   private LdapReferralException refEx = null;
   private boolean skipThisReferral = false;
   private int hopCount = 1;
   private NamingException previousEx = null;

   LdapReferralContext(LdapReferralException var1, Hashtable<?, ?> var2, Control[] var3, Control[] var4, String var5, boolean var6, int var7) throws NamingException {
      this.refEx = var1;
      if (!(this.skipThisReferral = var6)) {
         if (var2 != null) {
            var2 = (Hashtable)var2.clone();
            if (var3 == null) {
               var2.remove("java.naming.ldap.control.connect");
            }
         } else if (var3 != null) {
            var2 = new Hashtable(5);
         }

         if (var3 != null) {
            Control[] var9 = new Control[var3.length];
            System.arraycopy(var3, 0, var9, 0, var3.length);
            var2.put("java.naming.ldap.control.connect", var9);
         }

         while(true) {
            String var8;
            try {
               var8 = this.refEx.getNextReferral();
               if (var8 == null) {
                  if (this.previousEx != null) {
                     throw (NamingException)((NamingException)this.previousEx.fillInStackTrace());
                  }

                  throw new NamingException("Illegal encoding: referral is empty");
               }
            } catch (LdapReferralException var15) {
               if (var7 == 2) {
                  throw var15;
               }

               this.refEx = var15;
               continue;
            }

            Reference var16 = new Reference("javax.naming.directory.DirContext", new StringRefAddr("URL", var8));

            Object var10;
            try {
               var10 = NamingManager.getObjectInstance(var16, (Name)null, (Context)null, var2);
            } catch (NamingException var13) {
               if (var7 == 2) {
                  throw var13;
               }

               this.previousEx = var13;
               continue;
            } catch (Exception var14) {
               NamingException var12 = new NamingException("problem generating object using object factory");
               var12.setRootCause(var14);
               throw var12;
            }

            if (var10 instanceof DirContext) {
               this.refCtx = (DirContext)var10;
               if (this.refCtx instanceof LdapContext && var4 != null) {
                  ((LdapContext)this.refCtx).setRequestControls(var4);
               }

               this.initDefaults(var8, var5);
               return;
            }

            NotContextException var11 = new NotContextException("Cannot create context for: " + var8);
            var11.setRemainingName((new CompositeName()).add(var5));
            throw var11;
         }
      }
   }

   private void initDefaults(String var1, String var2) throws NamingException {
      String var3;
      try {
         LdapURL var4 = new LdapURL(var1);
         var3 = var4.getDN();
         this.urlAttrs = var4.getAttributes();
         this.urlScope = var4.getScope();
         this.urlFilter = var4.getFilter();
      } catch (NamingException var5) {
         var3 = var1;
         this.urlAttrs = this.urlScope = this.urlFilter = null;
      }

      if (var3 == null) {
         var3 = var2;
      } else {
         var3 = "";
      }

      if (var3 == null) {
         this.urlName = null;
      } else {
         this.urlName = (Name)(var3.equals("") ? new CompositeName() : (new CompositeName()).add(var3));
      }

   }

   public void close() throws NamingException {
      if (this.refCtx != null) {
         this.refCtx.close();
         this.refCtx = null;
      }

      this.refEx = null;
   }

   void setHopCount(int var1) {
      this.hopCount = var1;
      if (this.refCtx != null && this.refCtx instanceof LdapCtx) {
         ((LdapCtx)this.refCtx).setHopCount(var1);
      }

   }

   public Object lookup(String var1) throws NamingException {
      return this.lookup(this.toName(var1));
   }

   public Object lookup(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.lookup(this.overrideName(var1));
      }
   }

   public void bind(String var1, Object var2) throws NamingException {
      this.bind(this.toName(var1), var2);
   }

   public void bind(Name var1, Object var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.bind(this.overrideName(var1), var2);
      }
   }

   public void rebind(String var1, Object var2) throws NamingException {
      this.rebind(this.toName(var1), var2);
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.rebind(this.overrideName(var1), var2);
      }
   }

   public void unbind(String var1) throws NamingException {
      this.unbind(this.toName(var1));
   }

   public void unbind(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.unbind(this.overrideName(var1));
      }
   }

   public void rename(String var1, String var2) throws NamingException {
      this.rename(this.toName(var1), this.toName(var2));
   }

   public void rename(Name var1, Name var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.rename(this.overrideName(var1), this.toName(this.refEx.getNewRdn()));
      }
   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      return this.list(this.toName(var1));
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         try {
            NamingEnumeration var2 = null;
            if (this.urlScope != null && this.urlScope.equals("base")) {
               SearchControls var3 = new SearchControls();
               var3.setReturningObjFlag(true);
               var3.setSearchScope(0);
               var2 = this.refCtx.search(this.overrideName(var1), "(objectclass=*)", var3);
            } else {
               var2 = this.refCtx.list(this.overrideName(var1));
            }

            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)var2).appendUnprocessedReferrals(this.refEx);
            return var2;
         } catch (LdapReferralException var4) {
            var4.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)((NamingException)var4.fillInStackTrace());
         } catch (NamingException var5) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
               this.refEx.setNamingException(var5);
            }

            if (this.refEx == null || !this.refEx.hasMoreReferrals() && !this.refEx.hasMoreReferralExceptions()) {
               throw var5;
            } else {
               throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
            }
         }
      }
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      return this.listBindings(this.toName(var1));
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         try {
            NamingEnumeration var2 = null;
            if (this.urlScope != null && this.urlScope.equals("base")) {
               SearchControls var3 = new SearchControls();
               var3.setReturningObjFlag(true);
               var3.setSearchScope(0);
               var2 = this.refCtx.search(this.overrideName(var1), "(objectclass=*)", var3);
            } else {
               var2 = this.refCtx.listBindings(this.overrideName(var1));
            }

            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)var2).appendUnprocessedReferrals(this.refEx);
            return var2;
         } catch (LdapReferralException var4) {
            var4.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)((NamingException)var4.fillInStackTrace());
         } catch (NamingException var5) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
               this.refEx.setNamingException(var5);
            }

            if (this.refEx == null || !this.refEx.hasMoreReferrals() && !this.refEx.hasMoreReferralExceptions()) {
               throw var5;
            } else {
               throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
            }
         }
      }
   }

   public void destroySubcontext(String var1) throws NamingException {
      this.destroySubcontext(this.toName(var1));
   }

   public void destroySubcontext(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.destroySubcontext(this.overrideName(var1));
      }
   }

   public Context createSubcontext(String var1) throws NamingException {
      return this.createSubcontext(this.toName(var1));
   }

   public Context createSubcontext(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.createSubcontext(this.overrideName(var1));
      }
   }

   public Object lookupLink(String var1) throws NamingException {
      return this.lookupLink(this.toName(var1));
   }

   public Object lookupLink(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.lookupLink(this.overrideName(var1));
      }
   }

   public NameParser getNameParser(String var1) throws NamingException {
      return this.getNameParser(this.toName(var1));
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.getNameParser(this.overrideName(var1));
      }
   }

   public String composeName(String var1, String var2) throws NamingException {
      return this.composeName(this.toName(var1), this.toName(var2)).toString();
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.composeName(var1, var2);
      }
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.addToEnvironment(var1, var2);
      }
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.removeFromEnvironment(var1);
      }
   }

   public Hashtable<?, ?> getEnvironment() throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.getEnvironment();
      }
   }

   public Attributes getAttributes(String var1) throws NamingException {
      return this.getAttributes(this.toName(var1));
   }

   public Attributes getAttributes(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.getAttributes(this.overrideName(var1));
      }
   }

   public Attributes getAttributes(String var1, String[] var2) throws NamingException {
      return this.getAttributes(this.toName(var1), var2);
   }

   public Attributes getAttributes(Name var1, String[] var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.getAttributes(this.overrideName(var1), var2);
      }
   }

   public void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException {
      this.modifyAttributes(this.toName(var1), var2, var3);
   }

   public void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.modifyAttributes(this.overrideName(var1), var2, var3);
      }
   }

   public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException {
      this.modifyAttributes(this.toName(var1), var2);
   }

   public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.modifyAttributes(this.overrideName(var1), var2);
      }
   }

   public void bind(String var1, Object var2, Attributes var3) throws NamingException {
      this.bind(this.toName(var1), var2, var3);
   }

   public void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.bind(this.overrideName(var1), var2, var3);
      }
   }

   public void rebind(String var1, Object var2, Attributes var3) throws NamingException {
      this.rebind(this.toName(var1), var2, var3);
   }

   public void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         this.refCtx.rebind(this.overrideName(var1), var2, var3);
      }
   }

   public DirContext createSubcontext(String var1, Attributes var2) throws NamingException {
      return this.createSubcontext(this.toName(var1), var2);
   }

   public DirContext createSubcontext(Name var1, Attributes var2) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.createSubcontext(this.overrideName(var1), var2);
      }
   }

   public DirContext getSchema(String var1) throws NamingException {
      return this.getSchema(this.toName(var1));
   }

   public DirContext getSchema(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.getSchema(this.overrideName(var1));
      }
   }

   public DirContext getSchemaClassDefinition(String var1) throws NamingException {
      return this.getSchemaClassDefinition(this.toName(var1));
   }

   public DirContext getSchemaClassDefinition(Name var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.refCtx.getSchemaClassDefinition(this.overrideName(var1));
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException {
      return this.search(this.toName(var1), SearchFilter.format(var2), new SearchControls());
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException {
      return this.search(var1, SearchFilter.format(var2), new SearchControls());
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException {
      SearchControls var4 = new SearchControls();
      var4.setReturningAttributes(var3);
      return this.search(this.toName(var1), SearchFilter.format(var2), var4);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException {
      SearchControls var4 = new SearchControls();
      var4.setReturningAttributes(var3);
      return this.search(var1, SearchFilter.format(var2), var4);
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException {
      return this.search(this.toName(var1), var2, var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         try {
            NamingEnumeration var4 = this.refCtx.search(this.overrideName(var1), this.overrideFilter(var2), this.overrideAttributesAndScope(var3));
            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)var4).appendUnprocessedReferrals(this.refEx);
            return var4;
         } catch (LdapReferralException var5) {
            var5.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)((NamingException)var5.fillInStackTrace());
         } catch (NamingException var6) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
               this.refEx.setNamingException(var6);
            }

            if (this.refEx == null || !this.refEx.hasMoreReferrals() && !this.refEx.hasMoreReferralExceptions()) {
               throw var6;
            } else {
               throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
            }
         }
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      return this.search(this.toName(var1), var2, var3, var4);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         try {
            NamingEnumeration var5;
            if (this.urlFilter != null) {
               var5 = this.refCtx.search(this.overrideName(var1), this.urlFilter, this.overrideAttributesAndScope(var4));
            } else {
               var5 = this.refCtx.search(this.overrideName(var1), var2, var3, this.overrideAttributesAndScope(var4));
            }

            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)var5).appendUnprocessedReferrals(this.refEx);
            return var5;
         } catch (LdapReferralException var6) {
            var6.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)((NamingException)var6.fillInStackTrace());
         } catch (NamingException var7) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
               this.refEx.setNamingException(var7);
            }

            if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions())) {
               throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
            } else {
               throw var7;
            }
         }
      }
   }

   public String getNameInNamespace() throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else {
         return this.urlName != null && !this.urlName.isEmpty() ? this.urlName.get(0) : "";
      }
   }

   public ExtendedResponse extendedOperation(ExtendedRequest var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else if (!(this.refCtx instanceof LdapContext)) {
         throw new NotContextException("Referral context not an instance of LdapContext");
      } else {
         return ((LdapContext)this.refCtx).extendedOperation(var1);
      }
   }

   public LdapContext newInstance(Control[] var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else if (!(this.refCtx instanceof LdapContext)) {
         throw new NotContextException("Referral context not an instance of LdapContext");
      } else {
         return ((LdapContext)this.refCtx).newInstance(var1);
      }
   }

   public void reconnect(Control[] var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else if (!(this.refCtx instanceof LdapContext)) {
         throw new NotContextException("Referral context not an instance of LdapContext");
      } else {
         ((LdapContext)this.refCtx).reconnect(var1);
      }
   }

   public Control[] getConnectControls() throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else if (!(this.refCtx instanceof LdapContext)) {
         throw new NotContextException("Referral context not an instance of LdapContext");
      } else {
         return ((LdapContext)this.refCtx).getConnectControls();
      }
   }

   public void setRequestControls(Control[] var1) throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else if (!(this.refCtx instanceof LdapContext)) {
         throw new NotContextException("Referral context not an instance of LdapContext");
      } else {
         ((LdapContext)this.refCtx).setRequestControls(var1);
      }
   }

   public Control[] getRequestControls() throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else if (!(this.refCtx instanceof LdapContext)) {
         throw new NotContextException("Referral context not an instance of LdapContext");
      } else {
         return ((LdapContext)this.refCtx).getRequestControls();
      }
   }

   public Control[] getResponseControls() throws NamingException {
      if (this.skipThisReferral) {
         throw (NamingException)((NamingException)this.refEx.appendUnprocessedReferrals((LdapReferralException)null).fillInStackTrace());
      } else if (!(this.refCtx instanceof LdapContext)) {
         throw new NotContextException("Referral context not an instance of LdapContext");
      } else {
         return ((LdapContext)this.refCtx).getResponseControls();
      }
   }

   private Name toName(String var1) throws InvalidNameException {
      return (Name)(var1.equals("") ? new CompositeName() : (new CompositeName()).add(var1));
   }

   private Name overrideName(Name var1) throws InvalidNameException {
      return this.urlName == null ? var1 : this.urlName;
   }

   private SearchControls overrideAttributesAndScope(SearchControls var1) {
      if (this.urlScope == null && this.urlAttrs == null) {
         return var1;
      } else {
         SearchControls var2 = new SearchControls(var1.getSearchScope(), var1.getCountLimit(), var1.getTimeLimit(), var1.getReturningAttributes(), var1.getReturningObjFlag(), var1.getDerefLinkFlag());
         if (this.urlScope != null) {
            if (this.urlScope.equals("base")) {
               var2.setSearchScope(0);
            } else if (this.urlScope.equals("one")) {
               var2.setSearchScope(1);
            } else if (this.urlScope.equals("sub")) {
               var2.setSearchScope(2);
            }
         }

         if (this.urlAttrs != null) {
            StringTokenizer var3 = new StringTokenizer(this.urlAttrs, ",");
            int var4 = var3.countTokens();
            String[] var5 = new String[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = var3.nextToken();
            }

            var2.setReturningAttributes(var5);
         }

         return var2;
      }
   }

   private String overrideFilter(String var1) {
      return this.urlFilter == null ? var1 : this.urlFilter;
   }
}
