package com.sun.security.auth.module;

import com.sun.security.auth.UnixNumericGroupPrincipal;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import java.util.LinkedList;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;

@Exported
public class UnixLoginModule implements LoginModule {
   private Subject subject;
   private CallbackHandler callbackHandler;
   private Map<String, ?> sharedState;
   private Map<String, ?> options;
   private boolean debug = true;
   private UnixSystem ss;
   private boolean succeeded = false;
   private boolean commitSucceeded = false;
   private UnixPrincipal userPrincipal;
   private UnixNumericUserPrincipal UIDPrincipal;
   private UnixNumericGroupPrincipal GIDPrincipal;
   private LinkedList<UnixNumericGroupPrincipal> supplementaryGroups = new LinkedList();

   public void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4) {
      this.subject = var1;
      this.callbackHandler = var2;
      this.sharedState = var3;
      this.options = var4;
      this.debug = "true".equalsIgnoreCase((String)var4.get("debug"));
   }

   public boolean login() throws LoginException {
      Object var1 = null;
      this.ss = new UnixSystem();
      if (this.ss == null) {
         this.succeeded = false;
         throw new FailedLoginException("Failed in attempt to import the underlying system identity information");
      } else {
         this.userPrincipal = new UnixPrincipal(this.ss.getUsername());
         this.UIDPrincipal = new UnixNumericUserPrincipal(this.ss.getUid());
         this.GIDPrincipal = new UnixNumericGroupPrincipal(this.ss.getGid(), true);
         int var2;
         long[] var4;
         if (this.ss.getGroups() != null && this.ss.getGroups().length > 0) {
            var4 = this.ss.getGroups();

            for(var2 = 0; var2 < var4.length; ++var2) {
               UnixNumericGroupPrincipal var3 = new UnixNumericGroupPrincipal(var4[var2], false);
               if (!var3.getName().equals(this.GIDPrincipal.getName())) {
                  this.supplementaryGroups.add(var3);
               }
            }
         }

         if (this.debug) {
            System.out.println("\t\t[UnixLoginModule]: succeeded importing info: ");
            System.out.println("\t\t\tuid = " + this.ss.getUid());
            System.out.println("\t\t\tgid = " + this.ss.getGid());
            var4 = this.ss.getGroups();

            for(var2 = 0; var2 < var4.length; ++var2) {
               System.out.println("\t\t\tsupp gid = " + var4[var2]);
            }
         }

         this.succeeded = true;
         return true;
      }
   }

   public boolean commit() throws LoginException {
      if (!this.succeeded) {
         if (this.debug) {
            System.out.println("\t\t[UnixLoginModule]: did not add any Principals to Subject because own authentication failed.");
         }

         return false;
      } else if (this.subject.isReadOnly()) {
         throw new LoginException("commit Failed: Subject is Readonly");
      } else {
         if (!this.subject.getPrincipals().contains(this.userPrincipal)) {
            this.subject.getPrincipals().add(this.userPrincipal);
         }

         if (!this.subject.getPrincipals().contains(this.UIDPrincipal)) {
            this.subject.getPrincipals().add(this.UIDPrincipal);
         }

         if (!this.subject.getPrincipals().contains(this.GIDPrincipal)) {
            this.subject.getPrincipals().add(this.GIDPrincipal);
         }

         for(int var1 = 0; var1 < this.supplementaryGroups.size(); ++var1) {
            if (!this.subject.getPrincipals().contains(this.supplementaryGroups.get(var1))) {
               this.subject.getPrincipals().add(this.supplementaryGroups.get(var1));
            }
         }

         if (this.debug) {
            System.out.println("\t\t[UnixLoginModule]: added UnixPrincipal,");
            System.out.println("\t\t\t\tUnixNumericUserPrincipal,");
            System.out.println("\t\t\t\tUnixNumericGroupPrincipal(s),");
            System.out.println("\t\t\t to Subject");
         }

         this.commitSucceeded = true;
         return true;
      }
   }

   public boolean abort() throws LoginException {
      if (this.debug) {
         System.out.println("\t\t[UnixLoginModule]: aborted authentication attempt");
      }

      if (!this.succeeded) {
         return false;
      } else {
         if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.ss = null;
            this.userPrincipal = null;
            this.UIDPrincipal = null;
            this.GIDPrincipal = null;
            this.supplementaryGroups = new LinkedList();
         } else {
            this.logout();
         }

         return true;
      }
   }

   public boolean logout() throws LoginException {
      if (this.subject.isReadOnly()) {
         throw new LoginException("logout Failed: Subject is Readonly");
      } else {
         this.subject.getPrincipals().remove(this.userPrincipal);
         this.subject.getPrincipals().remove(this.UIDPrincipal);
         this.subject.getPrincipals().remove(this.GIDPrincipal);

         for(int var1 = 0; var1 < this.supplementaryGroups.size(); ++var1) {
            this.subject.getPrincipals().remove(this.supplementaryGroups.get(var1));
         }

         this.ss = null;
         this.succeeded = false;
         this.commitSucceeded = false;
         this.userPrincipal = null;
         this.UIDPrincipal = null;
         this.GIDPrincipal = null;
         this.supplementaryGroups = new LinkedList();
         if (this.debug) {
            System.out.println("\t\t[UnixLoginModule]: logged out Subject");
         }

         return true;
      }
   }
}
