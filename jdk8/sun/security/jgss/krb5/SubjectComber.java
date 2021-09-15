package sun.security.jgss.krb5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;

class SubjectComber {
   private static final boolean DEBUG;

   private SubjectComber() {
   }

   static <T> T find(Subject var0, String var1, String var2, Class<T> var3) {
      return var3.cast(findAux(var0, var1, var2, var3, true));
   }

   static <T> List<T> findMany(Subject var0, String var1, String var2, Class<T> var3) {
      return (List)findAux(var0, var1, var2, var3, false);
   }

   private static <T> Object findAux(Subject var0, String var1, String var2, Class<T> var3, boolean var4) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var5 = var4 ? null : new ArrayList();
         Iterator var6;
         if (var3 == KeyTab.class) {
            var6 = var0.getPrivateCredentials(KeyTab.class).iterator();

            while(true) {
               KeyTab var7;
               while(true) {
                  if (!var6.hasNext()) {
                     return var5;
                  }

                  var7 = (KeyTab)var6.next();
                  if (var1 == null || !var7.isBound()) {
                     break;
                  }

                  KerberosPrincipal var8 = var7.getPrincipal();
                  if (var8 != null) {
                     if (!var1.equals(var8.getName())) {
                        continue;
                     }
                     break;
                  } else {
                     boolean var9 = false;
                     Iterator var10 = var0.getPrincipals(KerberosPrincipal.class).iterator();

                     while(var10.hasNext()) {
                        KerberosPrincipal var11 = (KerberosPrincipal)var10.next();
                        if (var11.getName().equals(var1)) {
                           var9 = true;
                           break;
                        }
                     }

                     if (var9) {
                        break;
                     }
                  }
               }

               if (DEBUG) {
                  System.out.println("Found " + var3.getSimpleName() + " " + var7);
               }

               if (var4) {
                  return var7;
               }

               var5.add(var3.cast(var7));
            }
         } else if (var3 == KerberosKey.class) {
            var6 = var0.getPrivateCredentials(KerberosKey.class).iterator();

            while(true) {
               KerberosKey var16;
               String var17;
               do {
                  if (!var6.hasNext()) {
                     return var5;
                  }

                  var16 = (KerberosKey)var6.next();
                  var17 = var16.getPrincipal().getName();
               } while(var1 != null && !var1.equals(var17));

               if (DEBUG) {
                  System.out.println("Found " + var3.getSimpleName() + " for " + var17);
               }

               if (var4) {
                  return var16;
               }

               var5.add(var3.cast(var16));
            }
         } else if (var3 == KerberosTicket.class) {
            Set var15 = var0.getPrivateCredentials();
            synchronized(var15) {
               Iterator var18 = var15.iterator();

               while(true) {
                  KerberosTicket var20;
                  do {
                     while(true) {
                        Object var19;
                        do {
                           if (!var18.hasNext()) {
                              return var5;
                           }

                           var19 = var18.next();
                        } while(!(var19 instanceof KerberosTicket));

                        var20 = (KerberosTicket)var19;
                        if (DEBUG) {
                           System.out.println("Found ticket for " + var20.getClient() + " to go to " + var20.getServer() + " expiring on " + var20.getEndTime());
                        }

                        if (!var20.isCurrent()) {
                           break;
                        }

                        if ((var1 == null || var20.getServer().getName().equals(var1)) && (var2 == null || var2.equals(var20.getClient().getName()))) {
                           if (var4) {
                              return var20;
                           }

                           if (var2 == null) {
                              var2 = var20.getClient().getName();
                           }

                           if (var1 == null) {
                              var1 = var20.getServer().getName();
                           }

                           var5.add(var3.cast(var20));
                        }
                     }
                  } while(var0.isReadOnly());

                  var18.remove();

                  try {
                     var20.destroy();
                     if (DEBUG) {
                        System.out.println("Removed and destroyed the expired Ticket \n" + var20);
                     }
                  } catch (DestroyFailedException var13) {
                     if (DEBUG) {
                        System.out.println("Expired ticket not detroyed successfully. " + var13);
                     }
                  }
               }
            }
         } else {
            return var5;
         }
      }
   }

   static {
      DEBUG = Krb5Util.DEBUG;
   }
}
