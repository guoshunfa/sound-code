package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

final class WSDLGeneratorExtensionFacade extends WSDLGeneratorExtension {
   private final WSDLGeneratorExtension[] extensions;

   WSDLGeneratorExtensionFacade(WSDLGeneratorExtension... extensions) {
      assert extensions != null;

      this.extensions = extensions;
   }

   public void start(WSDLGenExtnContext ctxt) {
      WSDLGeneratorExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLGeneratorExtension e = var2[var4];
         e.start(ctxt);
      }

   }

   public void end(@NotNull WSDLGenExtnContext ctxt) {
      WSDLGeneratorExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLGeneratorExtension e = var2[var4];
         e.end(ctxt);
      }

   }

   public void addDefinitionsExtension(TypedXmlWriter definitions) {
      WSDLGeneratorExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLGeneratorExtension e = var2[var4];
         e.addDefinitionsExtension(definitions);
      }

   }

   public void addServiceExtension(TypedXmlWriter service) {
      WSDLGeneratorExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLGeneratorExtension e = var2[var4];
         e.addServiceExtension(service);
      }

   }

   public void addPortExtension(TypedXmlWriter port) {
      WSDLGeneratorExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLGeneratorExtension e = var2[var4];
         e.addPortExtension(port);
      }

   }

   public void addPortTypeExtension(TypedXmlWriter portType) {
      WSDLGeneratorExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLGeneratorExtension e = var2[var4];
         e.addPortTypeExtension(portType);
      }

   }

   public void addBindingExtension(TypedXmlWriter binding) {
      WSDLGeneratorExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLGeneratorExtension e = var2[var4];
         e.addBindingExtension(binding);
      }

   }

   public void addOperationExtension(TypedXmlWriter operation, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addOperationExtension(operation, method);
      }

   }

   public void addBindingOperationExtension(TypedXmlWriter operation, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addBindingOperationExtension(operation, method);
      }

   }

   public void addInputMessageExtension(TypedXmlWriter message, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addInputMessageExtension(message, method);
      }

   }

   public void addOutputMessageExtension(TypedXmlWriter message, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addOutputMessageExtension(message, method);
      }

   }

   public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addOperationInputExtension(input, method);
      }

   }

   public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addOperationOutputExtension(output, method);
      }

   }

   public void addBindingOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addBindingOperationInputExtension(input, method);
      }

   }

   public void addBindingOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
      WSDLGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLGeneratorExtension e = var3[var5];
         e.addBindingOperationOutputExtension(output, method);
      }

   }

   public void addBindingOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
      WSDLGeneratorExtension[] var4 = this.extensions;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         WSDLGeneratorExtension e = var4[var6];
         e.addBindingOperationFaultExtension(fault, method, ce);
      }

   }

   public void addFaultMessageExtension(TypedXmlWriter message, JavaMethod method, CheckedException ce) {
      WSDLGeneratorExtension[] var4 = this.extensions;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         WSDLGeneratorExtension e = var4[var6];
         e.addFaultMessageExtension(message, method, ce);
      }

   }

   public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
      WSDLGeneratorExtension[] var4 = this.extensions;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         WSDLGeneratorExtension e = var4[var6];
         e.addOperationFaultExtension(fault, method, ce);
      }

   }
}
