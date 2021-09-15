package com.sun.xml.internal.messaging.saaj.soap;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.transform.Source;

public interface Envelope extends SOAPEnvelope {
   Source getContent();

   void output(OutputStream var1) throws IOException;

   void output(OutputStream var1, boolean var2) throws IOException;
}
