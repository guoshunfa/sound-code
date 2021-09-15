package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.RuntimeOperationsException;

public class Repository {
   private final Map<String, Map<String, NamedObject>> domainTb;
   private volatile int nbElements;
   private final String domain;
   private final ReentrantReadWriteLock lock;

   private void addAllMatching(Map<String, NamedObject> var1, Set<NamedObject> var2, Repository.ObjectNamePattern var3) {
      synchronized(var1) {
         Iterator var5 = var1.values().iterator();

         while(var5.hasNext()) {
            NamedObject var6 = (NamedObject)var5.next();
            ObjectName var7 = var6.getName();
            if (var3.matchKeys(var7)) {
               var2.add(var6);
            }
         }

      }
   }

   private void addNewDomMoi(DynamicMBean var1, String var2, ObjectName var3, Repository.RegistrationContext var4) {
      HashMap var5 = new HashMap();
      String var6 = var3.getCanonicalKeyPropertyListString();
      this.addMoiToTb(var1, var3, var6, var5, var4);
      this.domainTb.put(var2, var5);
      ++this.nbElements;
   }

   private void registering(Repository.RegistrationContext var1) {
      if (var1 != null) {
         try {
            var1.registering();
         } catch (RuntimeOperationsException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new RuntimeOperationsException(var4);
         }
      }
   }

   private void unregistering(Repository.RegistrationContext var1, ObjectName var2) {
      if (var1 != null) {
         try {
            var1.unregistered();
         } catch (Exception var4) {
            JmxProperties.MBEANSERVER_LOGGER.log(Level.FINE, (String)("Unexpected exception while unregistering " + var2), (Throwable)var4);
         }

      }
   }

   private void addMoiToTb(DynamicMBean var1, ObjectName var2, String var3, Map<String, NamedObject> var4, Repository.RegistrationContext var5) {
      this.registering(var5);
      var4.put(var3, new NamedObject(var2, var1));
   }

   private NamedObject retrieveNamedObject(ObjectName var1) {
      if (var1.isPattern()) {
         return null;
      } else {
         String var2 = var1.getDomain().intern();
         if (var2.length() == 0) {
            var2 = this.domain;
         }

         Map var3 = (Map)this.domainTb.get(var2);
         return var3 == null ? null : (NamedObject)var3.get(var1.getCanonicalKeyPropertyListString());
      }
   }

   public Repository(String var1) {
      this(var1, true);
   }

   public Repository(String var1, boolean var2) {
      this.nbElements = 0;
      this.lock = new ReentrantReadWriteLock(var2);
      this.domainTb = new HashMap(5);
      if (var1 != null && var1.length() != 0) {
         this.domain = var1.intern();
      } else {
         this.domain = "DefaultDomain";
      }

      this.domainTb.put(this.domain, new HashMap());
   }

   public String[] getDomains() {
      this.lock.readLock().lock();

      ArrayList var1;
      try {
         var1 = new ArrayList(this.domainTb.size());
         Iterator var2 = this.domainTb.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            Map var4 = (Map)var3.getValue();
            if (var4 != null && var4.size() != 0) {
               var1.add(var3.getKey());
            }
         }
      } finally {
         this.lock.readLock().unlock();
      }

      return (String[])var1.toArray(new String[var1.size()]);
   }

   public void addMBean(DynamicMBean var1, ObjectName var2, Repository.RegistrationContext var3) throws InstanceAlreadyExistsException {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "addMBean", "name = " + var2);
      }

      String var4 = var2.getDomain().intern();
      boolean var5 = false;
      if (var4.length() == 0) {
         var2 = Util.newObjectName(this.domain + var2.toString());
      }

      if (var4 == this.domain) {
         var5 = true;
         var4 = this.domain;
      } else {
         var5 = false;
      }

      if (var2.isPattern()) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Repository: cannot add mbean for pattern name " + var2.toString()));
      } else {
         this.lock.writeLock().lock();

         try {
            if (!var5 && var4.equals("JMImplementation") && this.domainTb.containsKey("JMImplementation")) {
               throw new RuntimeOperationsException(new IllegalArgumentException("Repository: domain name cannot be JMImplementation"));
            }

            Map var6 = (Map)this.domainTb.get(var4);
            if (var6 != null) {
               String var7 = var2.getCanonicalKeyPropertyListString();
               NamedObject var8 = (NamedObject)var6.get(var7);
               if (var8 != null) {
                  throw new InstanceAlreadyExistsException(var2.toString());
               }

               ++this.nbElements;
               this.addMoiToTb(var1, var2, var7, var6, var3);
               return;
            }

            this.addNewDomMoi(var1, var4, var2, var3);
         } finally {
            this.lock.writeLock().unlock();
         }

      }
   }

   public boolean contains(ObjectName var1) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "contains", " name = " + var1);
      }

      this.lock.readLock().lock();

      boolean var2;
      try {
         var2 = this.retrieveNamedObject(var1) != null;
      } finally {
         this.lock.readLock().unlock();
      }

      return var2;
   }

   public DynamicMBean retrieve(ObjectName var1) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "retrieve", "name = " + var1);
      }

      this.lock.readLock().lock();

      DynamicMBean var3;
      try {
         NamedObject var2 = this.retrieveNamedObject(var1);
         if (var2 == null) {
            var3 = null;
            return var3;
         }

         var3 = var2.getObject();
      } finally {
         this.lock.readLock().unlock();
      }

      return var3;
   }

   public Set<NamedObject> query(ObjectName var1, QueryExp var2) {
      HashSet var3 = new HashSet();
      ObjectName var4;
      if (var1 != null && var1.getCanonicalName().length() != 0 && !var1.equals(ObjectName.WILDCARD)) {
         var4 = var1;
      } else {
         var4 = ObjectName.WILDCARD;
      }

      this.lock.readLock().lock();

      try {
         if (!var4.isPattern()) {
            NamedObject var17 = this.retrieveNamedObject(var4);
            if (var17 != null) {
               var3.add(var17);
            }

            HashSet var19 = var3;
            return var19;
         } else if (var4 != ObjectName.WILDCARD) {
            String var16 = var4.getCanonicalKeyPropertyListString();
            boolean var18 = var16.length() == 0;
            Repository.ObjectNamePattern var7 = var18 ? null : new Repository.ObjectNamePattern(var4);
            Map var8;
            HashSet var9;
            if (var4.getDomain().length() == 0) {
               var8 = (Map)this.domainTb.get(this.domain);
               if (var18) {
                  var3.addAll(var8.values());
               } else {
                  this.addAllMatching(var8, var3, var7);
               }

               var9 = var3;
               return var9;
            } else if (var4.isDomainPattern()) {
               String var20 = var4.getDomain();
               Iterator var22 = this.domainTb.keySet().iterator();

               while(var22.hasNext()) {
                  String var10 = (String)var22.next();
                  if (Util.wildmatch(var10, var20)) {
                     Map var11 = (Map)this.domainTb.get(var10);
                     if (var18) {
                        var3.addAll(var11.values());
                     } else {
                        this.addAllMatching(var11, var3, var7);
                     }
                  }
               }

               var9 = var3;
               return var9;
            } else {
               var8 = (Map)this.domainTb.get(var4.getDomain());
               if (var8 == null) {
                  Set var21 = Collections.emptySet();
                  return var21;
               } else {
                  if (var18) {
                     var3.addAll(var8.values());
                  } else {
                     this.addAllMatching(var8, var3, var7);
                  }

                  var9 = var3;
                  return var9;
               }
            }
         } else {
            Iterator var5 = this.domainTb.values().iterator();

            while(var5.hasNext()) {
               Map var6 = (Map)var5.next();
               var3.addAll(var6.values());
            }

            HashSet var15 = var3;
            return var15;
         }
      } finally {
         this.lock.readLock().unlock();
      }
   }

   public void remove(ObjectName var1, Repository.RegistrationContext var2) throws InstanceNotFoundException {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "remove", "name = " + var1);
      }

      String var3 = var1.getDomain().intern();
      if (var3.length() == 0) {
         var3 = this.domain;
      }

      this.lock.writeLock().lock();

      try {
         Map var4 = (Map)this.domainTb.get(var3);
         if (var4 == null) {
            throw new InstanceNotFoundException(var1.toString());
         }

         if (var4.remove(var1.getCanonicalKeyPropertyListString()) == null) {
            throw new InstanceNotFoundException(var1.toString());
         }

         --this.nbElements;
         if (var4.isEmpty()) {
            this.domainTb.remove(var3);
            if (var3 == this.domain) {
               this.domainTb.put(this.domain, new HashMap());
            }
         }

         this.unregistering(var2, var1);
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public Integer getCount() {
      return this.nbElements;
   }

   public String getDefaultDomain() {
      return this.domain;
   }

   private static final class ObjectNamePattern {
      private final String[] keys;
      private final String[] values;
      private final String properties;
      private final boolean isPropertyListPattern;
      private final boolean isPropertyValuePattern;
      public final ObjectName pattern;

      public ObjectNamePattern(ObjectName var1) {
         this(var1.isPropertyListPattern(), var1.isPropertyValuePattern(), var1.getCanonicalKeyPropertyListString(), var1.getKeyPropertyList(), var1);
      }

      ObjectNamePattern(boolean var1, boolean var2, String var3, Map<String, String> var4, ObjectName var5) {
         this.isPropertyListPattern = var1;
         this.isPropertyValuePattern = var2;
         this.properties = var3;
         int var6 = var4.size();
         this.keys = new String[var6];
         this.values = new String[var6];
         int var7 = 0;

         for(Iterator var8 = var4.entrySet().iterator(); var8.hasNext(); ++var7) {
            Map.Entry var9 = (Map.Entry)var8.next();
            this.keys[var7] = (String)var9.getKey();
            this.values[var7] = (String)var9.getValue();
         }

         this.pattern = var5;
      }

      public boolean matchKeys(ObjectName var1) {
         if (this.isPropertyValuePattern && !this.isPropertyListPattern && var1.getKeyPropertyList().size() != this.keys.length) {
            return false;
         } else {
            String var3;
            if (!this.isPropertyValuePattern && !this.isPropertyListPattern) {
               String var4 = var1.getCanonicalKeyPropertyListString();
               var3 = this.properties;
               return var4.equals(var3);
            } else {
               for(int var2 = this.keys.length - 1; var2 >= 0; --var2) {
                  var3 = var1.getKeyProperty(this.keys[var2]);
                  if (var3 == null) {
                     return false;
                  }

                  if (this.isPropertyValuePattern && this.pattern.isPropertyValuePattern(this.keys[var2])) {
                     if (!Util.wildmatch(var3, this.values[var2])) {
                        return false;
                     }
                  } else if (!var3.equals(this.values[var2])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public interface RegistrationContext {
      void registering();

      void unregistered();
   }
}
