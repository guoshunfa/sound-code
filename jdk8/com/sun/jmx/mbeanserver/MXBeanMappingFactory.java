package com.sun.jmx.mbeanserver;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;

public abstract class MXBeanMappingFactory {
   public static final MXBeanMappingFactory DEFAULT = new DefaultMXBeanMappingFactory();

   protected MXBeanMappingFactory() {
   }

   public abstract MXBeanMapping mappingForType(Type var1, MXBeanMappingFactory var2) throws OpenDataException;
}
