package com.sun.jmx.remote.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.management.remote.SubjectDelegationPermission;
import javax.security.auth.Subject;

public class SubjectDelegator {
   public AccessControlContext delegatedContext(AccessControlContext var1, Subject var2, boolean var3) throws SecurityException {
      if (System.getSecurityManager() != null && var1 == null) {
         throw new SecurityException("Illegal AccessControlContext: null");
      } else {
         Collection var4 = getSubjectPrincipals(var2);
         final ArrayList var5 = new ArrayList(var4.size());
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            Principal var7 = (Principal)var6.next();
            String var8 = var7.getClass().getName() + "." + var7.getName();
            var5.add(new SubjectDelegationPermission(var8));
         }

         PrivilegedAction var9 = new PrivilegedAction<Void>() {
            public Void run() {
               Iterator var1 = var5.iterator();

               while(var1.hasNext()) {
                  Permission var2 = (Permission)var1.next();
                  AccessController.checkPermission(var2);
               }

               return null;
            }
         };
         AccessController.doPrivileged(var9, var1);
         return this.getDelegatedAcc(var2, var3);
      }
   }

   private AccessControlContext getDelegatedAcc(Subject var1, boolean var2) {
      return var2 ? JMXSubjectDomainCombiner.getDomainCombinerContext(var1) : JMXSubjectDomainCombiner.getContext(var1);
   }

   public static synchronized boolean checkRemoveCallerContext(Subject var0) {
      try {
         Iterator var1 = getSubjectPrincipals(var0).iterator();

         while(var1.hasNext()) {
            Principal var2 = (Principal)var1.next();
            String var3 = var2.getClass().getName() + "." + var2.getName();
            SubjectDelegationPermission var4 = new SubjectDelegationPermission(var3);
            AccessController.checkPermission(var4);
         }

         return true;
      } catch (SecurityException var5) {
         return false;
      }
   }

   private static Collection<Principal> getSubjectPrincipals(Subject var0) {
      if (var0.isReadOnly()) {
         return var0.getPrincipals();
      } else {
         List var1 = Arrays.asList(var0.getPrincipals().toArray(new Principal[0]));
         return Collections.unmodifiableList(var1);
      }
   }
}
