package com.sun.beans.finder;

import com.sun.beans.WeakCache;
import com.sun.beans.editors.BooleanEditor;
import com.sun.beans.editors.ByteEditor;
import com.sun.beans.editors.DoubleEditor;
import com.sun.beans.editors.EnumEditor;
import com.sun.beans.editors.FloatEditor;
import com.sun.beans.editors.IntegerEditor;
import com.sun.beans.editors.LongEditor;
import com.sun.beans.editors.ShortEditor;
import java.beans.PropertyEditor;

public final class PropertyEditorFinder extends InstanceFinder<PropertyEditor> {
   private static final String DEFAULT = "sun.beans.editors";
   private static final String DEFAULT_NEW = "com.sun.beans.editors";
   private final WeakCache<Class<?>, Class<?>> registry = new WeakCache();

   public PropertyEditorFinder() {
      super(PropertyEditor.class, false, "Editor", "sun.beans.editors");
      this.registry.put(Byte.TYPE, ByteEditor.class);
      this.registry.put(Short.TYPE, ShortEditor.class);
      this.registry.put(Integer.TYPE, IntegerEditor.class);
      this.registry.put(Long.TYPE, LongEditor.class);
      this.registry.put(Boolean.TYPE, BooleanEditor.class);
      this.registry.put(Float.TYPE, FloatEditor.class);
      this.registry.put(Double.TYPE, DoubleEditor.class);
   }

   public void register(Class<?> var1, Class<?> var2) {
      synchronized(this.registry) {
         this.registry.put(var1, var2);
      }
   }

   public PropertyEditor find(Class<?> var1) {
      Class var2;
      synchronized(this.registry) {
         var2 = (Class)this.registry.get(var1);
      }

      Object var3 = (PropertyEditor)this.instantiate(var2, (String)null);
      if (var3 == null) {
         var3 = (PropertyEditor)super.find(var1);
         if (var3 == null && null != var1.getEnumConstants()) {
            var3 = new EnumEditor(var1);
         }
      }

      return (PropertyEditor)var3;
   }

   protected PropertyEditor instantiate(Class<?> var1, String var2, String var3) {
      return (PropertyEditor)super.instantiate(var1, "sun.beans.editors".equals(var2) ? "com.sun.beans.editors" : var2, var3);
   }
}
