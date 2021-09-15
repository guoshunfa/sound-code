package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Float extends Accessor {
   public MethodAccessor_Float() {
      super(Float.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).get_float();
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).set_float(value == null ? Const.default_value_float : (Float)value);
   }
}
