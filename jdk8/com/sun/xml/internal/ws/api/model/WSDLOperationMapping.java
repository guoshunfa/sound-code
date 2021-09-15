package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.namespace.QName;

public interface WSDLOperationMapping {
   WSDLBoundOperation getWSDLBoundOperation();

   JavaMethod getJavaMethod();

   QName getOperationName();
}
