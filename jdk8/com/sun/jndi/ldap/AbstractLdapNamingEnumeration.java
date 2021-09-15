package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.naming.LimitExceededException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

abstract class AbstractLdapNamingEnumeration<T extends NameClassPair> implements NamingEnumeration<T>, ReferralEnumeration<T> {
   protected Name listArg;
   private boolean cleaned = false;
   private LdapResult res;
   private LdapClient enumClnt;
   private Continuation cont;
   private Vector<LdapEntry> entries = null;
   private int limit = 0;
   private int posn = 0;
   protected LdapCtx homeCtx;
   private LdapReferralException refEx = null;
   private NamingException errEx = null;
   private boolean more = true;
   private boolean hasMoreCalled = false;

   AbstractLdapNamingEnumeration(LdapCtx var1, LdapResult var2, Name var3, Continuation var4) throws NamingException {
      if (var2.status != 0 && var2.status != 4 && var2.status != 3 && var2.status != 11 && var2.status != 10 && var2.status != 9) {
         NamingException var5 = new NamingException(LdapClient.getErrorMessage(var2.status, var2.errorMessage));
         throw var4.fillInException(var5);
      } else {
         this.res = var2;
         this.entries = var2.entries;
         this.limit = this.entries == null ? 0 : this.entries.size();
         this.listArg = var3;
         this.cont = var4;
         if (var2.refEx != null) {
            this.refEx = var2.refEx;
         }

         this.homeCtx = var1;
         var1.incEnumCount();
         this.enumClnt = var1.clnt;
      }
   }

   public final T nextElement() {
      try {
         return this.next();
      } catch (NamingException var2) {
         this.cleanup();
         return null;
      }
   }

   public final boolean hasMoreElements() {
      try {
         return this.hasMore();
      } catch (NamingException var2) {
         this.cleanup();
         return false;
      }
   }

   private void getNextBatch() throws NamingException {
      this.res = this.homeCtx.getSearchReply(this.enumClnt, this.res);
      if (this.res == null) {
         this.limit = this.posn = 0;
      } else {
         this.entries = this.res.entries;
         this.limit = this.entries == null ? 0 : this.entries.size();
         this.posn = 0;
         if (this.res.status != 0 || this.res.status == 0 && this.res.referrals != null) {
            try {
               this.homeCtx.processReturnCode(this.res, this.listArg);
            } catch (PartialResultException | LimitExceededException var2) {
               this.setNamingException(var2);
            }
         }

         if (this.res.refEx != null) {
            if (this.refEx == null) {
               this.refEx = this.res.refEx;
            } else {
               this.refEx = this.refEx.appendUnprocessedReferrals(this.res.refEx);
            }

            this.res.refEx = null;
         }

         if (this.res.resControls != null) {
            this.homeCtx.respCtls = this.res.resControls;
         }

      }
   }

   public final boolean hasMore() throws NamingException {
      if (this.hasMoreCalled) {
         return this.more;
      } else {
         this.hasMoreCalled = true;
         return !this.more ? false : (this.more = this.hasMoreImpl());
      }
   }

   public final T next() throws NamingException {
      if (!this.hasMoreCalled) {
         this.hasMore();
      }

      this.hasMoreCalled = false;
      return this.nextImpl();
   }

   private boolean hasMoreImpl() throws NamingException {
      if (this.posn == this.limit) {
         this.getNextBatch();
      }

      if (this.posn < this.limit) {
         return true;
      } else {
         try {
            return this.hasMoreReferrals();
         } catch (LimitExceededException | PartialResultException | LdapReferralException var3) {
            this.cleanup();
            throw var3;
         } catch (NamingException var4) {
            this.cleanup();
            PartialResultException var2 = new PartialResultException();
            var2.setRootCause(var4);
            throw var2;
         }
      }
   }

   private T nextImpl() throws NamingException {
      try {
         return this.nextAux();
      } catch (NamingException var2) {
         this.cleanup();
         throw this.cont.fillInException(var2);
      }
   }

   private T nextAux() throws NamingException {
      if (this.posn == this.limit) {
         this.getNextBatch();
      }

      if (this.posn >= this.limit) {
         this.cleanup();
         throw new NoSuchElementException("invalid enumeration handle");
      } else {
         LdapEntry var1 = (LdapEntry)this.entries.elementAt(this.posn++);
         return this.createItem(var1.DN, var1.attributes, var1.respCtls);
      }
   }

   protected final String getAtom(String var1) {
      try {
         LdapName var2 = new LdapName(var1);
         return var2.get(var2.size() - 1);
      } catch (NamingException var3) {
         return var1;
      }
   }

   protected abstract T createItem(String var1, Attributes var2, Vector<Control> var3) throws NamingException;

   public void appendUnprocessedReferrals(LdapReferralException var1) {
      if (this.refEx != null) {
         this.refEx = this.refEx.appendUnprocessedReferrals(var1);
      } else {
         this.refEx = var1.appendUnprocessedReferrals(this.refEx);
      }

   }

   final void setNamingException(NamingException var1) {
      this.errEx = var1;
   }

   protected abstract AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext var1) throws NamingException;

   protected final boolean hasMoreReferrals() throws NamingException {
      if (this.refEx == null || !this.refEx.hasMoreReferrals() && !this.refEx.hasMoreReferralExceptions()) {
         this.cleanup();
         if (this.errEx != null) {
            throw this.errEx;
         } else {
            return false;
         }
      } else if (this.homeCtx.handleReferrals == 2) {
         throw (NamingException)((NamingException)this.refEx.fillInStackTrace());
      } else {
         while(true) {
            LdapReferralContext var1 = (LdapReferralContext)this.refEx.getReferralContext(this.homeCtx.envprops, this.homeCtx.reqCtls);

            try {
               this.update(this.getReferredResults(var1));
               return this.hasMoreImpl();
            } catch (LdapReferralException var6) {
               if (this.errEx == null) {
                  this.errEx = var6.getNamingException();
               }

               this.refEx = var6;
            } finally {
               var1.close();
            }
         }
      }
   }

   protected void update(AbstractLdapNamingEnumeration<? extends NameClassPair> var1) {
      this.homeCtx.decEnumCount();
      this.homeCtx = var1.homeCtx;
      this.enumClnt = var1.enumClnt;
      var1.homeCtx = null;
      this.posn = var1.posn;
      this.limit = var1.limit;
      this.res = var1.res;
      this.entries = var1.entries;
      this.refEx = var1.refEx;
      this.listArg = var1.listArg;
   }

   protected final void finalize() {
      this.cleanup();
   }

   protected final void cleanup() {
      if (!this.cleaned) {
         if (this.enumClnt != null) {
            this.enumClnt.clearSearchReply(this.res, this.homeCtx.reqCtls);
         }

         this.enumClnt = null;
         this.cleaned = true;
         if (this.homeCtx != null) {
            this.homeCtx.decEnumCount();
            this.homeCtx = null;
         }

      }
   }

   public final void close() {
      this.cleanup();
   }
}
