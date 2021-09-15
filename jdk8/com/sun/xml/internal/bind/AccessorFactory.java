package com.sun.xml.internal.bind;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.JAXBException;

public interface AccessorFactory {
   Accessor createFieldAccessor(Class var1, Field var2, boolean var3) throws JAXBException;

   Accessor createPropertyAccessor(Class var1, Method var2, Method var3) throws JAXBException;
}
