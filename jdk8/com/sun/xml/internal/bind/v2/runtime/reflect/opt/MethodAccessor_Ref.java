package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Ref extends Accessor {
   public MethodAccessor_Ref() {
      super(Ref.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).get_ref();
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).set_ref((Ref)value);
   }
}
