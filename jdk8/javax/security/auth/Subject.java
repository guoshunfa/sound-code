package javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DomainCombiner;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import sun.security.util.ResourcesMgr;

public final class Subject implements Serializable {
   private static final long serialVersionUID = -8308522755600156056L;
   Set<Principal> principals;
   transient Set<Object> pubCredentials;
   transient Set<Object> privCredentials;
   private volatile boolean readOnly = false;
   private static final int PRINCIPAL_SET = 1;
   private static final int PUB_CREDENTIAL_SET = 2;
   private static final int PRIV_CREDENTIAL_SET = 3;
   private static final ProtectionDomain[] NULL_PD_ARRAY = new ProtectionDomain[0];

   public Subject() {
      this.principals = Collections.synchronizedSet(new Subject.SecureSet(this, 1));
      this.pubCredentials = Collections.synchronizedSet(new Subject.SecureSet(this, 2));
      this.privCredentials = Collections.synchronizedSet(new Subject.SecureSet(this, 3));
   }

   public Subject(boolean var1, Set<? extends Principal> var2, Set<?> var3, Set<?> var4) {
      if (var2 != null && var3 != null && var4 != null) {
         this.principals = Collections.synchronizedSet(new Subject.SecureSet(this, 1, var2));
         this.pubCredentials = Collections.synchronizedSet(new Subject.SecureSet(this, 2, var3));
         this.privCredentials = Collections.synchronizedSet(new Subject.SecureSet(this, 3, var4));
         this.readOnly = var1;
      } else {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.input.s."));
      }
   }

   public void setReadOnly() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(Subject.AuthPermissionHolder.SET_READ_ONLY_PERMISSION);
      }

      this.readOnly = true;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public static Subject getSubject(final AccessControlContext var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(Subject.AuthPermissionHolder.GET_SUBJECT_PERMISSION);
      }

      if (var0 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.AccessControlContext.provided"));
      } else {
         return (Subject)AccessController.doPrivileged(new PrivilegedAction<Subject>() {
            public Subject run() {
               DomainCombiner var1 = var0.getDomainCombiner();
               if (!(var1 instanceof SubjectDomainCombiner)) {
                  return null;
               } else {
                  SubjectDomainCombiner var2 = (SubjectDomainCombiner)var1;
                  return var2.getSubject();
               }
            }
         });
      }
   }

   public static <T> T doAs(Subject var0, PrivilegedAction<T> var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(Subject.AuthPermissionHolder.DO_AS_PERMISSION);
      }

      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
      } else {
         AccessControlContext var3 = AccessController.getContext();
         return AccessController.doPrivileged(var1, createContext(var0, var3));
      }
   }

   public static <T> T doAs(Subject var0, PrivilegedExceptionAction<T> var1) throws PrivilegedActionException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(Subject.AuthPermissionHolder.DO_AS_PERMISSION);
      }

      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
      } else {
         AccessControlContext var3 = AccessController.getContext();
         return AccessController.doPrivileged(var1, createContext(var0, var3));
      }
   }

   public static <T> T doAsPrivileged(Subject var0, PrivilegedAction<T> var1, AccessControlContext var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(Subject.AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION);
      }

      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
      } else {
         AccessControlContext var4 = var2 == null ? new AccessControlContext(NULL_PD_ARRAY) : var2;
         return AccessController.doPrivileged(var1, createContext(var0, var4));
      }
   }

   public static <T> T doAsPrivileged(Subject var0, PrivilegedExceptionAction<T> var1, AccessControlContext var2) throws PrivilegedActionException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(Subject.AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION);
      }

      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
      } else {
         AccessControlContext var4 = var2 == null ? new AccessControlContext(NULL_PD_ARRAY) : var2;
         return AccessController.doPrivileged(var1, createContext(var0, var4));
      }
   }

   private static AccessControlContext createContext(final Subject var0, final AccessControlContext var1) {
      return (AccessControlContext)AccessController.doPrivileged(new PrivilegedAction<AccessControlContext>() {
         public AccessControlContext run() {
            return var0 == null ? new AccessControlContext(var1, (DomainCombiner)null) : new AccessControlContext(var1, new SubjectDomainCombiner(var0));
         }
      });
   }

   public Set<Principal> getPrincipals() {
      return this.principals;
   }

   public <T extends Principal> Set<T> getPrincipals(Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
      } else {
         return new Subject.ClassSet(1, var1);
      }
   }

   public Set<Object> getPublicCredentials() {
      return this.pubCredentials;
   }

   public Set<Object> getPrivateCredentials() {
      return this.privCredentials;
   }

   public <T> Set<T> getPublicCredentials(Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
      } else {
         return new Subject.ClassSet(2, var1);
      }
   }

   public <T> Set<T> getPrivateCredentials(Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
      } else {
         return new Subject.ClassSet(3, var1);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (var1 instanceof Subject) {
         Subject var2 = (Subject)var1;
         HashSet var3;
         synchronized(var2.principals) {
            var3 = new HashSet(var2.principals);
         }

         if (!this.principals.equals(var3)) {
            return false;
         } else {
            HashSet var4;
            synchronized(var2.pubCredentials) {
               var4 = new HashSet(var2.pubCredentials);
            }

            if (!this.pubCredentials.equals(var4)) {
               return false;
            } else {
               HashSet var5;
               synchronized(var2.privCredentials) {
                  var5 = new HashSet(var2.privCredentials);
               }

               return this.privCredentials.equals(var5);
            }
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return this.toString(true);
   }

   String toString(boolean var1) {
      String var2 = ResourcesMgr.getString("Subject.");
      String var3 = "";
      Iterator var5;
      synchronized(this.principals) {
         var5 = this.principals.iterator();

         while(true) {
            if (!var5.hasNext()) {
               break;
            }

            Principal var6 = (Principal)var5.next();
            var3 = var3 + ResourcesMgr.getString(".Principal.") + var6.toString() + ResourcesMgr.getString("NEWLINE");
         }
      }

      Object var14;
      synchronized(this.pubCredentials) {
         var5 = this.pubCredentials.iterator();

         while(true) {
            if (!var5.hasNext()) {
               break;
            }

            var14 = var5.next();
            var3 = var3 + ResourcesMgr.getString(".Public.Credential.") + var14.toString() + ResourcesMgr.getString("NEWLINE");
         }
      }

      if (var1) {
         synchronized(this.privCredentials) {
            var5 = this.privCredentials.iterator();

            while(var5.hasNext()) {
               try {
                  var14 = var5.next();
                  var3 = var3 + ResourcesMgr.getString(".Private.Credential.") + var14.toString() + ResourcesMgr.getString("NEWLINE");
               } catch (SecurityException var10) {
                  var3 = var3 + ResourcesMgr.getString(".Private.Credential.inaccessible.");
                  break;
               }
            }
         }
      }

      return var2 + var3;
   }

   public int hashCode() {
      int var1 = 0;
      Iterator var3;
      synchronized(this.principals) {
         var3 = this.principals.iterator();

         while(true) {
            if (!var3.hasNext()) {
               break;
            }

            Principal var4 = (Principal)var3.next();
            var1 ^= var4.hashCode();
         }
      }

      synchronized(this.pubCredentials) {
         for(var3 = this.pubCredentials.iterator(); var3.hasNext(); var1 ^= this.getCredHashCode(var3.next())) {
         }

         return var1;
      }
   }

   private int getCredHashCode(Object var1) {
      try {
         return var1.hashCode();
      } catch (IllegalStateException var3) {
         return var1.getClass().toString().hashCode();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      synchronized(this.principals) {
         var1.defaultWriteObject();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.readOnly = var2.get("readOnly", false);
      Set var3 = (Set)var2.get("principals", (Object)null);
      if (var3 == null) {
         throw new NullPointerException(ResourcesMgr.getString("invalid.null.input.s."));
      } else {
         try {
            this.principals = Collections.synchronizedSet(new Subject.SecureSet(this, 1, var3));
         } catch (NullPointerException var5) {
            this.principals = Collections.synchronizedSet(new Subject.SecureSet(this, 1));
         }

         this.pubCredentials = Collections.synchronizedSet(new Subject.SecureSet(this, 2));
         this.privCredentials = Collections.synchronizedSet(new Subject.SecureSet(this, 3));
      }
   }

   static class AuthPermissionHolder {
      static final AuthPermission DO_AS_PERMISSION = new AuthPermission("doAs");
      static final AuthPermission DO_AS_PRIVILEGED_PERMISSION = new AuthPermission("doAsPrivileged");
      static final AuthPermission SET_READ_ONLY_PERMISSION = new AuthPermission("setReadOnly");
      static final AuthPermission GET_SUBJECT_PERMISSION = new AuthPermission("getSubject");
      static final AuthPermission MODIFY_PRINCIPALS_PERMISSION = new AuthPermission("modifyPrincipals");
      static final AuthPermission MODIFY_PUBLIC_CREDENTIALS_PERMISSION = new AuthPermission("modifyPublicCredentials");
      static final AuthPermission MODIFY_PRIVATE_CREDENTIALS_PERMISSION = new AuthPermission("modifyPrivateCredentials");
   }

   private class ClassSet<T> extends AbstractSet<T> {
      private int which;
      private Class<T> c;
      private Set<T> set;

      ClassSet(int var2, Class<T> var3) {
         this.which = var2;
         this.c = var3;
         this.set = new HashSet();
         switch(var2) {
         case 1:
            synchronized(Subject.this.principals) {
               this.populateSet();
               break;
            }
         case 2:
            synchronized(Subject.this.pubCredentials) {
               this.populateSet();
               break;
            }
         default:
            synchronized(Subject.this.privCredentials) {
               this.populateSet();
            }
         }

      }

      private void populateSet() {
         final Iterator var1;
         switch(this.which) {
         case 1:
            var1 = Subject.this.principals.iterator();
            break;
         case 2:
            var1 = Subject.this.pubCredentials.iterator();
            break;
         default:
            var1 = Subject.this.privCredentials.iterator();
         }

         while(var1.hasNext()) {
            Object var2;
            if (this.which == 3) {
               var2 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                  public Object run() {
                     return var1.next();
                  }
               });
            } else {
               var2 = var1.next();
            }

            if (this.c.isAssignableFrom(var2.getClass())) {
               if (this.which != 3) {
                  this.set.add(var2);
               } else {
                  SecurityManager var3 = System.getSecurityManager();
                  if (var3 != null) {
                     var3.checkPermission(new PrivateCredentialPermission(var2.getClass().getName(), Subject.this.getPrincipals()));
                  }

                  this.set.add(var2);
               }
            }
         }

      }

      public int size() {
         return this.set.size();
      }

      public Iterator<T> iterator() {
         return this.set.iterator();
      }

      public boolean add(T var1) {
         if (!var1.getClass().isAssignableFrom(this.c)) {
            MessageFormat var2 = new MessageFormat(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.class"));
            Object[] var3 = new Object[]{this.c.toString()};
            throw new SecurityException(var2.format(var3));
         } else {
            return this.set.add(var1);
         }
      }
   }

   private static class SecureSet<E> extends AbstractSet<E> implements Serializable {
      private static final long serialVersionUID = 7911754171111800359L;
      private static final ObjectStreamField[] serialPersistentFields;
      Subject subject;
      LinkedList<E> elements;
      private int which;

      SecureSet(Subject var1, int var2) {
         this.subject = var1;
         this.which = var2;
         this.elements = new LinkedList();
      }

      SecureSet(Subject var1, int var2, Set<? extends E> var3) {
         this.subject = var1;
         this.which = var2;
         this.elements = new LinkedList(var3);
      }

      public int size() {
         return this.elements.size();
      }

      public Iterator<E> iterator() {
         final LinkedList var1 = this.elements;
         return new Iterator<E>() {
            ListIterator<E> i = var1.listIterator(0);

            public boolean hasNext() {
               return this.i.hasNext();
            }

            public E next() {
               if (SecureSet.this.which != 3) {
                  return this.i.next();
               } else {
                  SecurityManager var1x = System.getSecurityManager();
                  if (var1x != null) {
                     try {
                        var1x.checkPermission(new PrivateCredentialPermission(var1.get(this.i.nextIndex()).getClass().getName(), SecureSet.this.subject.getPrincipals()));
                     } catch (SecurityException var3) {
                        this.i.next();
                        throw var3;
                     }
                  }

                  return this.i.next();
               }
            }

            public void remove() {
               if (SecureSet.this.subject.isReadOnly()) {
                  throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only"));
               } else {
                  SecurityManager var1x = System.getSecurityManager();
                  if (var1x != null) {
                     switch(SecureSet.this.which) {
                     case 1:
                        var1x.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
                        break;
                     case 2:
                        var1x.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
                        break;
                     default:
                        var1x.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
                     }
                  }

                  this.i.remove();
               }
            }
         };
      }

      public boolean add(E var1) {
         if (this.subject.isReadOnly()) {
            throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only"));
         } else {
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               switch(this.which) {
               case 1:
                  var2.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
                  break;
               case 2:
                  var2.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
                  break;
               default:
                  var2.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
               }
            }

            switch(this.which) {
            case 1:
               if (!(var1 instanceof Principal)) {
                  throw new SecurityException(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set"));
               }
            default:
               return !this.elements.contains(var1) ? this.elements.add(var1) : false;
            }
         }
      }

      public boolean remove(Object var1) {
         final Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            Object var3;
            if (this.which != 3) {
               var3 = var2.next();
            } else {
               var3 = AccessController.doPrivileged(new PrivilegedAction<E>() {
                  public E run() {
                     return var2.next();
                  }
               });
            }

            if (var3 == null) {
               if (var1 == null) {
                  var2.remove();
                  return true;
               }
            } else if (var3.equals(var1)) {
               var2.remove();
               return true;
            }
         }

         return false;
      }

      public boolean contains(Object var1) {
         final Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            Object var3;
            if (this.which != 3) {
               var3 = var2.next();
            } else {
               SecurityManager var4 = System.getSecurityManager();
               if (var4 != null) {
                  var4.checkPermission(new PrivateCredentialPermission(var1.getClass().getName(), this.subject.getPrincipals()));
               }

               var3 = AccessController.doPrivileged(new PrivilegedAction<E>() {
                  public E run() {
                     return var2.next();
                  }
               });
            }

            if (var3 == null) {
               if (var1 == null) {
                  return true;
               }
            } else if (var3.equals(var1)) {
               return true;
            }
         }

         return false;
      }

      public boolean removeAll(Collection<?> var1) {
         Objects.requireNonNull(var1);
         boolean var2 = false;
         final Iterator var3 = this.iterator();

         while(true) {
            while(var3.hasNext()) {
               Object var4;
               if (this.which != 3) {
                  var4 = var3.next();
               } else {
                  var4 = AccessController.doPrivileged(new PrivilegedAction<E>() {
                     public E run() {
                        return var3.next();
                     }
                  });
               }

               Iterator var5 = var1.iterator();

               while(var5.hasNext()) {
                  Object var6 = var5.next();
                  if (var4 == null) {
                     if (var6 == null) {
                        var3.remove();
                        var2 = true;
                        break;
                     }
                  } else if (var4.equals(var6)) {
                     var3.remove();
                     var2 = true;
                     break;
                  }
               }
            }

            return var2;
         }
      }

      public boolean retainAll(Collection<?> var1) {
         Objects.requireNonNull(var1);
         boolean var2 = false;
         boolean var3 = false;
         final Iterator var4 = this.iterator();

         while(var4.hasNext()) {
            var3 = false;
            Object var5;
            if (this.which != 3) {
               var5 = var4.next();
            } else {
               var5 = AccessController.doPrivileged(new PrivilegedAction<E>() {
                  public E run() {
                     return var4.next();
                  }
               });
            }

            Iterator var6 = var1.iterator();

            while(var6.hasNext()) {
               Object var7 = var6.next();
               if (var5 == null) {
                  if (var7 == null) {
                     var3 = true;
                     break;
                  }
               } else if (var5.equals(var7)) {
                  var3 = true;
                  break;
               }
            }

            if (!var3) {
               var4.remove();
               var3 = false;
               var2 = true;
            }
         }

         return var2;
      }

      public void clear() {
         for(final Iterator var1 = this.iterator(); var1.hasNext(); var1.remove()) {
            Object var2;
            if (this.which != 3) {
               var2 = var1.next();
            } else {
               var2 = AccessController.doPrivileged(new PrivilegedAction<E>() {
                  public E run() {
                     return var1.next();
                  }
               });
            }
         }

      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         if (this.which == 3) {
            Iterator var2 = this.iterator();

            while(var2.hasNext()) {
               var2.next();
            }
         }

         ObjectOutputStream.PutField var3 = var1.putFields();
         var3.put("this$0", this.subject);
         var3.put("elements", this.elements);
         var3.put("which", this.which);
         var1.writeFields();
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.subject = (Subject)var2.get("this$0", (Object)null);
         this.which = var2.get("which", (int)0);
         LinkedList var3 = (LinkedList)var2.get("elements", (Object)null);
         if (var3.getClass() != LinkedList.class) {
            this.elements = new LinkedList(var3);
         } else {
            this.elements = var3;
         }

      }

      static {
         serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("this$0", Subject.class), new ObjectStreamField("elements", LinkedList.class), new ObjectStreamField("which", Integer.TYPE)};
      }
   }
}
