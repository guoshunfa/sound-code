package org.omg.PortableInterceptor;

public interface IORInterceptor_3_0Operations extends IORInterceptorOperations {
   void components_established(IORInfo var1);

   void adapter_manager_state_changed(int var1, short var2);

   void adapter_state_changed(ObjectReferenceTemplate[] var1, short var2);
}
