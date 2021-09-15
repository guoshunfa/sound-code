package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.message.ContentType;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.ws.soap.MTOMFeature;

public interface MessageWritable {
   ContentType getContentType();

   ContentType writeTo(OutputStream var1) throws IOException;

   void setMTOMConfiguration(MTOMFeature var1);
}
