package com.sun.beans.editors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

public final class EnumEditor implements PropertyEditor {
   private final List<PropertyChangeListener> listeners = new ArrayList();
   private final Class type;
   private final String[] tags;
   private Object value;

   public EnumEditor(Class var1) {
      Object[] var2 = var1.getEnumConstants();
      if (var2 == null) {
         throw new IllegalArgumentException("Unsupported " + var1);
      } else {
         this.type = var1;
         this.tags = new String[var2.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.tags[var3] = ((Enum)var2[var3]).name();
         }

      }
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object var1) {
      if (var1 != null && !this.type.isInstance(var1)) {
         throw new IllegalArgumentException("Unsupported value: " + var1);
      } else {
         Object var2;
         PropertyChangeListener[] var3;
         synchronized(this.listeners) {
            label45: {
               var2 = this.value;
               this.value = var1;
               if (var1 == null) {
                  if (var2 != null) {
                     break label45;
                  }
               } else if (!var1.equals(var2)) {
                  break label45;
               }

               return;
            }

            int var5 = this.listeners.size();
            if (var5 == 0) {
               return;
            }

            var3 = (PropertyChangeListener[])this.listeners.toArray(new PropertyChangeListener[var5]);
         }

         PropertyChangeEvent var4 = new PropertyChangeEvent(this, (String)null, var2, var1);
         PropertyChangeListener[] var10 = var3;
         int var6 = var3.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            PropertyChangeListener var8 = var10[var7];
            var8.propertyChange(var4);
         }

      }
   }

   public String getAsText() {
      return this.value != null ? ((Enum)this.value).name() : null;
   }

   public void setAsText(String var1) {
      this.setValue(var1 != null ? Enum.valueOf(this.type, var1) : null);
   }

   public String[] getTags() {
      return (String[])this.tags.clone();
   }

   public String getJavaInitializationString() {
      String var1 = this.getAsText();
      return var1 != null ? this.type.getName() + '.' + var1 : "null";
   }

   public boolean isPaintable() {
      return false;
   }

   public void paintValue(Graphics var1, Rectangle var2) {
   }

   public boolean supportsCustomEditor() {
      return false;
   }

   public Component getCustomEditor() {
      return null;
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      synchronized(this.listeners) {
         this.listeners.add(var1);
      }
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      synchronized(this.listeners) {
         this.listeners.remove(var1);
      }
   }
}
