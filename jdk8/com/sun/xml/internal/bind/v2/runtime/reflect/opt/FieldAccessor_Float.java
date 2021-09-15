package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Float extends Accessor {
   public FieldAccessor_Float() {
      super(Float.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).f_float;
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).f_float = value == null ? Const.default_value_float : (Float)value;
   }
}
