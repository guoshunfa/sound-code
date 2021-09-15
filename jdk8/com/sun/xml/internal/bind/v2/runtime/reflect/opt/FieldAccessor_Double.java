package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Double extends Accessor {
   public FieldAccessor_Double() {
      super(Double.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).f_double;
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).f_double = value == null ? Const.default_value_double : (Double)value;
   }
}
