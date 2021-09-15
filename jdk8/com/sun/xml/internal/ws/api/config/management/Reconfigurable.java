package com.sun.xml.internal.ws.api.config.management;

import javax.xml.ws.WebServiceException;

public interface Reconfigurable {
   void reconfigure() throws WebServiceException;
}
