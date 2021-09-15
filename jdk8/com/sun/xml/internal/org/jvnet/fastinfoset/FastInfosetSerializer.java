package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.io.OutputStream;
import java.util.Map;

public interface FastInfosetSerializer {
   String IGNORE_DTD_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/DTD";
   String IGNORE_COMMENTS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/comments";
   String IGNORE_PROCESSING_INSTRUCTIONS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/processingInstructions";
   String IGNORE_WHITE_SPACE_TEXT_CONTENT_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/whiteSpaceTextContent";
   String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
   String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
   String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
   int MIN_CHARACTER_CONTENT_CHUNK_SIZE = 0;
   int MAX_CHARACTER_CONTENT_CHUNK_SIZE = 32;
   int CHARACTER_CONTENT_CHUNK_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;
   int MIN_ATTRIBUTE_VALUE_SIZE = 0;
   int MAX_ATTRIBUTE_VALUE_SIZE = 32;
   int ATTRIBUTE_VALUE_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;
   String UTF_8 = "UTF-8";
   String UTF_16BE = "UTF-16BE";

   void setIgnoreDTD(boolean var1);

   boolean getIgnoreDTD();

   void setIgnoreComments(boolean var1);

   boolean getIgnoreComments();

   void setIgnoreProcesingInstructions(boolean var1);

   boolean getIgnoreProcesingInstructions();

   void setIgnoreWhiteSpaceTextContent(boolean var1);

   boolean getIgnoreWhiteSpaceTextContent();

   void setCharacterEncodingScheme(String var1);

   String getCharacterEncodingScheme();

   void setRegisteredEncodingAlgorithms(Map var1);

   Map getRegisteredEncodingAlgorithms();

   int getMinCharacterContentChunkSize();

   void setMinCharacterContentChunkSize(int var1);

   int getMaxCharacterContentChunkSize();

   void setMaxCharacterContentChunkSize(int var1);

   int getCharacterContentChunkMapMemoryLimit();

   void setCharacterContentChunkMapMemoryLimit(int var1);

   int getMinAttributeValueSize();

   void setMinAttributeValueSize(int var1);

   int getMaxAttributeValueSize();

   void setMaxAttributeValueSize(int var1);

   int getAttributeValueMapMemoryLimit();

   void setAttributeValueMapMemoryLimit(int var1);

   void setExternalVocabulary(ExternalVocabulary var1);

   void setVocabularyApplicationData(VocabularyApplicationData var1);

   VocabularyApplicationData getVocabularyApplicationData();

   void reset();

   void setOutputStream(OutputStream var1);
}
