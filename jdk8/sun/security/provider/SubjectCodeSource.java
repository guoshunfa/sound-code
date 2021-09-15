package sun.security.provider;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javax.security.auth.Subject;
import sun.security.util.Debug;

class SubjectCodeSource extends CodeSource implements Serializable {
   private static final long serialVersionUID = 6039418085604715275L;
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private Subject subject;
   private LinkedList<PolicyParser.PrincipalEntry> principals;
   private static final Class<?>[] PARAMS = new Class[]{String.class};
   private static final Debug debug = Debug.getInstance("auth", "\t[Auth Access]");
   private ClassLoader sysClassLoader;

   SubjectCodeSource(Subject var1, LinkedList<PolicyParser.PrincipalEntry> var2, URL var3, Certificate[] var4) {
      super(var3, var4);
      this.subject = var1;
      this.principals = var2 == null ? new LinkedList() : new LinkedList(var2);
      this.sysClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }

   LinkedList<PolicyParser.PrincipalEntry> getPrincipals() {
      return this.principals;
   }

   Subject getSubject() {
      return this.subject;
   }

   public boolean implies(CodeSource var1) {
      LinkedList var2 = null;
      if (var1 != null && var1 instanceof SubjectCodeSource && super.implies(var1)) {
         SubjectCodeSource var3 = (SubjectCodeSource)var1;
         if (this.principals == null) {
            if (debug != null) {
               debug.println("\tSubjectCodeSource.implies: PASS 1");
            }

            return true;
         } else if (var3.getSubject() != null && var3.getSubject().getPrincipals().size() != 0) {
            ListIterator var4 = this.principals.listIterator(0);

            while(var4.hasNext()) {
               PolicyParser.PrincipalEntry var5 = (PolicyParser.PrincipalEntry)var4.next();

               Principal var8;
               try {
                  Class var6 = Class.forName(var5.principalClass, true, this.sysClassLoader);
                  if (!Principal.class.isAssignableFrom(var6)) {
                     throw new ClassCastException(var5.principalClass + " is not a Principal");
                  }

                  Constructor var11 = var6.getConstructor(PARAMS);
                  var8 = (Principal)var11.newInstance(var5.principalName);
                  if (!var8.implies(var3.getSubject())) {
                     if (debug != null) {
                        debug.println("\tSubjectCodeSource.implies: FAILURE 3");
                     }

                     return false;
                  }

                  if (debug != null) {
                     debug.println("\tSubjectCodeSource.implies: PASS 2");
                  }

                  return true;
               } catch (Exception var10) {
                  if (var2 == null) {
                     if (var3.getSubject() == null) {
                        if (debug != null) {
                           debug.println("\tSubjectCodeSource.implies: FAILURE 4");
                        }

                        return false;
                     }

                     Iterator var7 = var3.getSubject().getPrincipals().iterator();
                     var2 = new LinkedList();

                     while(var7.hasNext()) {
                        var8 = (Principal)var7.next();
                        PolicyParser.PrincipalEntry var9 = new PolicyParser.PrincipalEntry(var8.getClass().getName(), var8.getName());
                        var2.add(var9);
                     }
                  }

                  if (!this.subjectListImpliesPrincipalEntry(var2, var5)) {
                     if (debug != null) {
                        debug.println("\tSubjectCodeSource.implies: FAILURE 5");
                     }

                     return false;
                  }
               }
            }

            if (debug != null) {
               debug.println("\tSubjectCodeSource.implies: PASS 3");
            }

            return true;
         } else {
            if (debug != null) {
               debug.println("\tSubjectCodeSource.implies: FAILURE 2");
            }

            return false;
         }
      } else {
         if (debug != null) {
            debug.println("\tSubjectCodeSource.implies: FAILURE 1");
         }

         return false;
      }
   }

   private boolean subjectListImpliesPrincipalEntry(LinkedList<PolicyParser.PrincipalEntry> var1, PolicyParser.PrincipalEntry var2) {
      ListIterator var3 = var1.listIterator(0);

      PolicyParser.PrincipalEntry var4;
      do {
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (PolicyParser.PrincipalEntry)var3.next();
         } while(!var2.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") && !var2.getPrincipalClass().equals(var4.getPrincipalClass()));
      } while(!var2.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME") && !var2.getPrincipalName().equals(var4.getPrincipalName()));

      return true;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!super.equals(var1)) {
         return false;
      } else if (!(var1 instanceof SubjectCodeSource)) {
         return false;
      } else {
         SubjectCodeSource var2 = (SubjectCodeSource)var1;

         try {
            if (this.getSubject() != var2.getSubject()) {
               return false;
            }
         } catch (SecurityException var4) {
            return false;
         }

         if ((this.principals != null || var2.principals == null) && (this.principals == null || var2.principals != null)) {
            return this.principals == null || var2.principals == null || this.principals.containsAll(var2.principals) && var2.principals.containsAll(this.principals);
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String toString() {
      String var1 = super.toString();
      if (this.getSubject() != null) {
         if (debug != null) {
            final Subject var2 = this.getSubject();
            var1 = var1 + "\n" + (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  return var2.toString();
               }
            });
         } else {
            var1 = var1 + "\n" + this.getSubject().toString();
         }
      }

      PolicyParser.PrincipalEntry var3;
      if (this.principals != null) {
         for(ListIterator var4 = this.principals.listIterator(); var4.hasNext(); var1 = var1 + rb.getString("NEWLINE") + var3.getPrincipalClass() + " " + var3.getPrincipalName()) {
            var3 = (PolicyParser.PrincipalEntry)var4.next();
         }
      }

      return var1;
   }
}
