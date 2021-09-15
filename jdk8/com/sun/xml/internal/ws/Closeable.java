package com.sun.xml.internal.ws;

import javax.xml.ws.WebServiceException;

public interface Closeable extends java.io.Closeable {
   void close() throws WebServiceException;
}
