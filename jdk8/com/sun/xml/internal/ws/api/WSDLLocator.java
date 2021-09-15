package com.sun.xml.internal.ws.api;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.Service;

public abstract class WSDLLocator {
   public abstract URL locateWSDL(Class<Service> var1, String var2) throws MalformedURLException;
}
