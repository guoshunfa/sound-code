package com.sun.jndi.ldap;

import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.event.ObjectChangeListener;
import javax.naming.ldap.UnsolicitedNotification;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotificationListener;

final class EventSupport {
   private static final boolean debug = false;
   private LdapCtx ctx;
   private Hashtable<NotifierArgs, NamingEventNotifier> notifiers = new Hashtable(11);
   private Vector<UnsolicitedNotificationListener> unsolicited = null;
   private EventQueue eventQueue;

   EventSupport(LdapCtx var1) {
      this.ctx = var1;
   }

   synchronized void addNamingListener(String var1, int var2, NamingListener var3) throws NamingException {
      if (var3 instanceof ObjectChangeListener || var3 instanceof NamespaceChangeListener) {
         NotifierArgs var4 = new NotifierArgs(var1, var2, var3);
         NamingEventNotifier var5 = (NamingEventNotifier)this.notifiers.get(var4);
         if (var5 == null) {
            var5 = new NamingEventNotifier(this, this.ctx, var4, var3);
            this.notifiers.put(var4, var5);
         } else {
            var5.addNamingListener(var3);
         }
      }

      if (var3 instanceof UnsolicitedNotificationListener) {
         if (this.unsolicited == null) {
            this.unsolicited = new Vector(3);
         }

         this.unsolicited.addElement((UnsolicitedNotificationListener)var3);
      }

   }

   synchronized void addNamingListener(String var1, String var2, SearchControls var3, NamingListener var4) throws NamingException {
      if (var4 instanceof ObjectChangeListener || var4 instanceof NamespaceChangeListener) {
         NotifierArgs var5 = new NotifierArgs(var1, var2, var3, var4);
         NamingEventNotifier var6 = (NamingEventNotifier)this.notifiers.get(var5);
         if (var6 == null) {
            var6 = new NamingEventNotifier(this, this.ctx, var5, var4);
            this.notifiers.put(var5, var6);
         } else {
            var6.addNamingListener(var4);
         }
      }

      if (var4 instanceof UnsolicitedNotificationListener) {
         if (this.unsolicited == null) {
            this.unsolicited = new Vector(3);
         }

         this.unsolicited.addElement((UnsolicitedNotificationListener)var4);
      }

   }

   synchronized void removeNamingListener(NamingListener var1) {
      Iterator var2 = this.notifiers.values().iterator();

      while(var2.hasNext()) {
         NamingEventNotifier var3 = (NamingEventNotifier)var2.next();
         if (var3 != null) {
            var3.removeNamingListener(var1);
            if (!var3.hasNamingListeners()) {
               var3.stop();
               this.notifiers.remove(var3.info);
            }
         }
      }

      if (this.unsolicited != null) {
         this.unsolicited.removeElement(var1);
      }

   }

   synchronized boolean hasUnsolicited() {
      return this.unsolicited != null && this.unsolicited.size() > 0;
   }

   synchronized void removeDeadNotifier(NotifierArgs var1) {
      this.notifiers.remove(var1);
   }

   synchronized void fireUnsolicited(Object var1) {
      if (this.unsolicited != null && this.unsolicited.size() != 0) {
         if (var1 instanceof UnsolicitedNotification) {
            UnsolicitedNotificationEvent var2 = new UnsolicitedNotificationEvent(this.ctx, (UnsolicitedNotification)var1);
            this.queueEvent(var2, this.unsolicited);
         } else if (var1 instanceof NamingException) {
            NamingExceptionEvent var3 = new NamingExceptionEvent(this.ctx, (NamingException)var1);
            this.queueEvent(var3, this.unsolicited);
            this.unsolicited = null;
         }

      }
   }

   synchronized void cleanup() {
      if (this.notifiers != null) {
         Iterator var1 = this.notifiers.values().iterator();

         while(var1.hasNext()) {
            NamingEventNotifier var2 = (NamingEventNotifier)var1.next();
            var2.stop();
         }

         this.notifiers = null;
      }

      if (this.eventQueue != null) {
         this.eventQueue.stop();
         this.eventQueue = null;
      }

   }

   synchronized void queueEvent(EventObject var1, Vector<? extends NamingListener> var2) {
      if (this.eventQueue == null) {
         this.eventQueue = new EventQueue();
      }

      Vector var3 = (Vector)var2.clone();
      this.eventQueue.enqueue(var1, var3);
   }
}
