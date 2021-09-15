package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.io.IOException;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.InterruptedNamingException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.event.EventContext;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

final class NamingEventNotifier implements Runnable {
   private static final boolean debug = false;
   private Vector<NamingListener> namingListeners;
   private Thread worker;
   private LdapCtx context;
   private EventContext eventSrc;
   private EventSupport support;
   private NamingEnumeration<SearchResult> results;
   NotifierArgs info;

   NamingEventNotifier(EventSupport var1, LdapCtx var2, NotifierArgs var3, NamingListener var4) throws NamingException {
      this.info = var3;
      this.support = var1;

      PersistentSearchControl var5;
      try {
         var5 = new PersistentSearchControl(var3.mask, true, true, true);
      } catch (IOException var8) {
         NamingException var7 = new NamingException("Problem creating persistent search control");
         var7.setRootCause(var8);
         throw var7;
      }

      this.context = (LdapCtx)var2.newInstance(new Control[]{var5});
      this.eventSrc = var2;
      this.namingListeners = new Vector();
      this.namingListeners.addElement(var4);
      this.worker = Obj.helper.createThread(this);
      this.worker.setDaemon(true);
      this.worker.start();
   }

   void addNamingListener(NamingListener var1) {
      this.namingListeners.addElement(var1);
   }

   void removeNamingListener(NamingListener var1) {
      this.namingListeners.removeElement(var1);
   }

   boolean hasNamingListeners() {
      return this.namingListeners.size() > 0;
   }

   public void run() {
      try {
         Continuation var1 = new Continuation();
         var1.setError(this, (String)this.info.name);
         Object var2 = this.info.name != null && !this.info.name.equals("") ? (new CompositeName()).add(this.info.name) : new CompositeName();
         this.results = this.context.searchAux((Name)var2, this.info.filter, this.info.controls, true, false, var1);
         ((LdapSearchEnumeration)this.results).setStartName(this.context.currentParsedDN);

         while(this.results.hasMore()) {
            SearchResult var3 = (SearchResult)this.results.next();
            Control[] var4 = var3 instanceof HasControls ? ((HasControls)var3).getControls() : null;
            if (var4 != null) {
               byte var8 = 0;
               if (var8 < var4.length && var4[var8] instanceof EntryChangeResponseControl) {
                  EntryChangeResponseControl var5 = (EntryChangeResponseControl)var4[var8];
                  long var6 = var5.getChangeNumber();
                  switch(var5.getChangeType()) {
                  case 1:
                     this.fireObjectAdded(var3, var6);
                     break;
                  case 2:
                     this.fireObjectRemoved(var3, var6);
                  case 3:
                  case 5:
                  case 6:
                  case 7:
                  default:
                     break;
                  case 4:
                     this.fireObjectChanged(var3, var6);
                     break;
                  case 8:
                     this.fireObjectRenamed(var3, var5.getPreviousDN(), var6);
                  }
               }
            }
         }
      } catch (InterruptedNamingException var13) {
      } catch (NamingException var14) {
         this.fireNamingException(var14);
         this.support.removeDeadNotifier(this.info);
      } finally {
         this.cleanup();
      }

   }

   private void cleanup() {
      try {
         if (this.results != null) {
            this.results.close();
            this.results = null;
         }

         if (this.context != null) {
            this.context.close();
            this.context = null;
         }
      } catch (NamingException var2) {
      }

   }

   void stop() {
      if (this.worker != null) {
         this.worker.interrupt();
         this.worker = null;
      }

   }

   private void fireObjectAdded(Binding var1, long var2) {
      if (this.namingListeners != null && this.namingListeners.size() != 0) {
         NamingEvent var4 = new NamingEvent(this.eventSrc, 0, var1, (Binding)null, new Long(var2));
         this.support.queueEvent(var4, this.namingListeners);
      }
   }

   private void fireObjectRemoved(Binding var1, long var2) {
      if (this.namingListeners != null && this.namingListeners.size() != 0) {
         NamingEvent var4 = new NamingEvent(this.eventSrc, 1, (Binding)null, var1, new Long(var2));
         this.support.queueEvent(var4, this.namingListeners);
      }
   }

   private void fireObjectChanged(Binding var1, long var2) {
      if (this.namingListeners != null && this.namingListeners.size() != 0) {
         Binding var4 = new Binding(var1.getName(), (Object)null, var1.isRelative());
         NamingEvent var5 = new NamingEvent(this.eventSrc, 3, var1, var4, new Long(var2));
         this.support.queueEvent(var5, this.namingListeners);
      }
   }

   private void fireObjectRenamed(Binding var1, String var2, long var3) {
      if (this.namingListeners != null && this.namingListeners.size() != 0) {
         Binding var5 = null;

         try {
            javax.naming.ldap.LdapName var6 = new javax.naming.ldap.LdapName(var2);
            if (var6.startsWith(this.context.currentParsedDN)) {
               String var7 = var6.getSuffix(this.context.currentParsedDN.size()).toString();
               var5 = new Binding(var7, (Object)null);
            }
         } catch (NamingException var8) {
         }

         if (var5 == null) {
            var5 = new Binding(var2, (Object)null, false);
         }

         NamingEvent var9 = new NamingEvent(this.eventSrc, 2, var1, var5, new Long(var3));
         this.support.queueEvent(var9, this.namingListeners);
      }
   }

   private void fireNamingException(NamingException var1) {
      if (this.namingListeners != null && this.namingListeners.size() != 0) {
         NamingExceptionEvent var2 = new NamingExceptionEvent(this.eventSrc, var1);
         this.support.queueEvent(var2, this.namingListeners);
      }
   }
}
