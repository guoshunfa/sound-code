package com.apple.laf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;

public class ClientPropertyApplicator<T extends JComponent, N> implements PropertyChangeListener {
   private final Map<String, ClientPropertyApplicator.Property<N>> properties = new HashMap();

   public ClientPropertyApplicator(ClientPropertyApplicator.Property<N>... var1) {
      ClientPropertyApplicator.Property[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ClientPropertyApplicator.Property var5 = var2[var4];
         this.properties.put(var5.name, var5);
      }

   }

   void applyProperty(N var1, String var2, Object var3) {
      ClientPropertyApplicator.Property var4 = (ClientPropertyApplicator.Property)this.properties.get(var2);
      if (var4 != null) {
         var4.applyProperty(var1, var3);
      }

   }

   public void attachAndApplyClientProperties(T var1) {
      var1.addPropertyChangeListener(this);
      Object var2 = this.convertJComponentToTarget(var1);
      if (var2 != null) {
         Set var3 = this.properties.keySet();
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            Object var6 = var1.getClientProperty(var5);
            if (var6 != null) {
               this.applyProperty(var2, var5, var6);
            }
         }

      }
   }

   public void removeFrom(T var1) {
      var1.removePropertyChangeListener(this);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      Object var2 = this.convertJComponentToTarget((JComponent)var1.getSource());
      if (var2 != null) {
         this.applyProperty(var2, var1.getPropertyName(), var1.getNewValue());
      }
   }

   public N convertJComponentToTarget(T var1) {
      return var1;
   }

   public abstract static class Property<X> {
      final String name;

      public Property(String var1) {
         this.name = var1;
      }

      public abstract void applyProperty(X var1, Object var2);
   }
}
