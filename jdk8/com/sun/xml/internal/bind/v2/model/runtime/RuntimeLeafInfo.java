package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

public interface RuntimeLeafInfo extends LeafInfo<Type, Class>, RuntimeNonElement {
   <V> Transducer<V> getTransducer();

   Class getClazz();

   QName[] getTypeNames();
}
