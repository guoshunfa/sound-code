package com.oracle.webservices.internal.api.databinding;

import java.io.File;

public interface WSDLGenerator {
   WSDLGenerator inlineSchema(boolean var1);

   WSDLGenerator property(String var1, Object var2);

   void generate(WSDLResolver var1);

   void generate(File var1, String var2);
}
