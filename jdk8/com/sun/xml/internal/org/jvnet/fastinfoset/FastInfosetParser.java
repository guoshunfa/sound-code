package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.util.Map;

public interface FastInfosetParser {
   String STRING_INTERNING_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/string-interning";
   String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
   String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
   String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
   String FORCE_STREAM_CLOSE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/force-stream-close";

   void setStringInterning(boolean var1);

   boolean getStringInterning();

   void setBufferSize(int var1);

   int getBufferSize();

   void setRegisteredEncodingAlgorithms(Map var1);

   Map getRegisteredEncodingAlgorithms();

   void setExternalVocabularies(Map var1);

   /** @deprecated */
   Map getExternalVocabularies();

   void setParseFragments(boolean var1);

   boolean getParseFragments();

   void setForceStreamClose(boolean var1);

   boolean getForceStreamClose();
}
