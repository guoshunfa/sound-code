package com.sun.xml.internal.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class AnyTypeAdapter extends XmlAdapter<Object, Object> {
   public Object unmarshal(Object v) {
      return v;
   }

   public Object marshal(Object v) {
      return v;
   }
}
