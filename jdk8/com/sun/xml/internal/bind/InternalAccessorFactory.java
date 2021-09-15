package com.sun.xml.internal.bind;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import javax.xml.bind.JAXBException;

public interface InternalAccessorFactory extends AccessorFactory {
   Accessor createFieldAccessor(Class var1, Field var2, boolean var3, boolean var4) throws JAXBException;
}
