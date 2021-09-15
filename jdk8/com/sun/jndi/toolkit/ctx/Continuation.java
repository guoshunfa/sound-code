package com.sun.jndi.toolkit.ctx;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class Continuation extends ResolveResult {
   protected Name starter;
   protected Object followingLink = null;
   protected Hashtable<?, ?> environment = null;
   protected boolean continuing = false;
   protected Context resolvedContext = null;
   protected Name relativeResolvedName = null;
   private static final long serialVersionUID = 8162530656132624308L;

   public Continuation() {
   }

   public Continuation(Name var1, Hashtable<?, ?> var2) {
      this.starter = var1;
      this.environment = (Hashtable)((Hashtable)(var2 == null ? null : var2.clone()));
   }

   public boolean isContinue() {
      return this.continuing;
   }

   public void setSuccess() {
      this.continuing = false;
   }

   public NamingException fillInException(NamingException var1) {
      var1.setRemainingName(this.remainingName);
      var1.setResolvedObj(this.resolvedObj);
      if (this.starter != null && !this.starter.isEmpty()) {
         if (this.remainingName == null) {
            var1.setResolvedName(this.starter);
         } else {
            var1.setResolvedName(this.starter.getPrefix(this.starter.size() - this.remainingName.size()));
         }
      } else {
         var1.setResolvedName((Name)null);
      }

      if (var1 instanceof CannotProceedException) {
         CannotProceedException var2 = (CannotProceedException)var1;
         Hashtable var3 = this.environment == null ? new Hashtable(11) : (Hashtable)this.environment.clone();
         var2.setEnvironment(var3);
         var2.setAltNameCtx(this.resolvedContext);
         var2.setAltName(this.relativeResolvedName);
      }

      return var1;
   }

   public void setErrorNNS(Object var1, Name var2) {
      Name var3 = (Name)((Name)var2.clone());

      try {
         var3.add("");
      } catch (InvalidNameException var5) {
      }

      this.setErrorAux(var1, var3);
   }

   public void setErrorNNS(Object var1, String var2) {
      CompositeName var3 = new CompositeName();

      try {
         if (var2 != null && !var2.equals("")) {
            var3.add(var2);
         }

         var3.add("");
      } catch (InvalidNameException var5) {
      }

      this.setErrorAux(var1, var3);
   }

   public void setError(Object var1, Name var2) {
      if (var2 != null) {
         this.remainingName = (Name)((Name)var2.clone());
      } else {
         this.remainingName = null;
      }

      this.setErrorAux(var1, this.remainingName);
   }

   public void setError(Object var1, String var2) {
      CompositeName var3 = new CompositeName();
      if (var2 != null && !var2.equals("")) {
         try {
            var3.add(var2);
         } catch (InvalidNameException var5) {
         }
      }

      this.setErrorAux(var1, var3);
   }

   private void setErrorAux(Object var1, Name var2) {
      this.remainingName = var2;
      this.resolvedObj = var1;
      this.continuing = false;
   }

   private void setContinueAux(Object var1, Name var2, Context var3, Name var4) {
      if (var1 instanceof LinkRef) {
         this.setContinueLink(var1, var2, var3, var4);
      } else {
         this.remainingName = var4;
         this.resolvedObj = var1;
         this.relativeResolvedName = var2;
         this.resolvedContext = var3;
         this.continuing = true;
      }

   }

   public void setContinueNNS(Object var1, Name var2, Context var3) {
      new CompositeName();
      this.setContinue(var1, (Name)var2, var3, (Name)PartialCompositeContext._NNS_NAME);
   }

   public void setContinueNNS(Object var1, String var2, Context var3) {
      CompositeName var4 = new CompositeName();

      try {
         var4.add(var2);
      } catch (NamingException var6) {
      }

      this.setContinue(var1, (Name)var4, var3, (Name)PartialCompositeContext._NNS_NAME);
   }

   public void setContinue(Object var1, Name var2, Context var3) {
      this.setContinueAux(var1, var2, var3, (Name)PartialCompositeContext._EMPTY_NAME.clone());
   }

   public void setContinue(Object var1, Name var2, Context var3, Name var4) {
      if (var4 != null) {
         this.remainingName = (Name)((Name)var4.clone());
      } else {
         this.remainingName = new CompositeName();
      }

      this.setContinueAux(var1, var2, var3, this.remainingName);
   }

   public void setContinue(Object var1, String var2, Context var3, String var4) {
      CompositeName var5 = new CompositeName();
      if (!var2.equals("")) {
         try {
            var5.add(var2);
         } catch (NamingException var9) {
         }
      }

      CompositeName var6 = new CompositeName();
      if (!var4.equals("")) {
         try {
            var6.add(var4);
         } catch (NamingException var8) {
         }
      }

      this.setContinueAux(var1, var5, var3, var6);
   }

   /** @deprecated */
   @Deprecated
   public void setContinue(Object var1, Object var2) {
      this.setContinue(var1, (Name)null, (Context)var2);
   }

   private void setContinueLink(Object var1, Name var2, Context var3, Name var4) {
      this.followingLink = var1;
      this.remainingName = var4;
      this.resolvedObj = var3;
      this.relativeResolvedName = PartialCompositeContext._EMPTY_NAME;
      this.resolvedContext = var3;
      this.continuing = true;
   }

   public String toString() {
      return this.remainingName != null ? this.starter.toString() + "; remainingName: '" + this.remainingName + "'" : this.starter.toString();
   }

   public String toString(boolean var1) {
      return var1 && this.resolvedObj != null ? this.toString() + "; resolvedObj: " + this.resolvedObj + "; relativeResolvedName: " + this.relativeResolvedName + "; resolvedContext: " + this.resolvedContext : this.toString();
   }
}
