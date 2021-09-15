package com.oracle.webservices.internal.impl.internalspi.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import java.io.IOException;
import java.io.InputStream;

public interface StreamDecoder {
   Message decode(InputStream var1, String var2, AttachmentSet var3, SOAPVersion var4) throws IOException;
}
