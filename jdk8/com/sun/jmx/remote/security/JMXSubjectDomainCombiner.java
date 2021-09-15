package com.sun.jmx.remote.security;

import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import javax.security.auth.Subject;
import javax.security.auth.SubjectDomainCombiner;

public class JMXSubjectDomainCombiner extends SubjectDomainCombiner {
   private static final CodeSource nullCodeSource = new CodeSource((URL)null, (Certificate[])null);
   private static final ProtectionDomain pdNoPerms;

   public JMXSubjectDomainCombiner(Subject var1) {
      super(var1);
   }

   public ProtectionDomain[] combine(ProtectionDomain[] var1, ProtectionDomain[] var2) {
      ProtectionDomain[] var3;
      if (var1 != null && var1.length != 0) {
         var3 = new ProtectionDomain[var1.length + 1];

         for(int var4 = 0; var4 < var1.length; ++var4) {
            var3[var4] = var1[var4];
         }

         var3[var1.length] = pdNoPerms;
      } else {
         var3 = new ProtectionDomain[]{pdNoPerms};
      }

      return super.combine(var3, var2);
   }

   public static AccessControlContext getContext(Subject var0) {
      return new AccessControlContext(AccessController.getContext(), new JMXSubjectDomainCombiner(var0));
   }

   public static AccessControlContext getDomainCombinerContext(Subject var0) {
      return new AccessControlContext(new AccessControlContext(new ProtectionDomain[0]), new JMXSubjectDomainCombiner(var0));
   }

   static {
      pdNoPerms = new ProtectionDomain(nullCodeSource, new Permissions(), (ClassLoader)null, (Principal[])null);
   }
}
