package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Long extends Accessor {
   public MethodAccessor_Long() {
      super(Long.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).get_long();
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).set_long(value == null ? Const.default_value_long : (Long)value);
   }
}
