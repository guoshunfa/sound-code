package com.sun.xml.internal.ws.spi.db;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import java.util.Map;

public interface DatabindingProvider {
   boolean isFor(String var1);

   void init(Map<String, Object> var1);

   Databinding create(DatabindingConfig var1);

   WSDLGenerator wsdlGen(DatabindingConfig var1);
}
