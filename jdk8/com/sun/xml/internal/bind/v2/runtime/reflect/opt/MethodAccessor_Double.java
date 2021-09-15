package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Double extends Accessor {
   public MethodAccessor_Double() {
      super(Double.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).get_double();
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).set_double(value == null ? Const.default_value_double : (Double)value);
   }
}
