package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public interface CharacterEscapeHandler {
   void escape(char[] var1, int var2, int var3, boolean var4, Writer var5) throws IOException;
}
