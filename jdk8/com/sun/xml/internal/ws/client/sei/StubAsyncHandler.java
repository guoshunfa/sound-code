package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import java.util.Iterator;
import java.util.List;
import javax.jws.soap.SOAPBinding;

public class StubAsyncHandler extends StubHandler {
   private final Class asyncBeanClass;

   public StubAsyncHandler(JavaMethodImpl jm, JavaMethodImpl sync, MessageContextFactory mcf) {
      super(sync, mcf);
      List<ParameterImpl> rp = sync.getResponseParameters();
      int size = 0;
      Iterator var6 = rp.iterator();

      while(var6.hasNext()) {
         ParameterImpl param = (ParameterImpl)var6.next();
         if (param.isWrapperStyle()) {
            WrapperParameter wrapParam = (WrapperParameter)param;
            size += wrapParam.getWrapperChildren().size();
            if (sync.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT) {
               size += 2;
            }
         } else {
            ++size;
         }
      }

      Class tempWrap = null;
      if (size > 1) {
         rp = jm.getResponseParameters();
         Iterator var13 = rp.iterator();

         label57:
         do {
            ParameterImpl param;
            do {
               if (!var13.hasNext()) {
                  break label57;
               }

               param = (ParameterImpl)var13.next();
               if (param.isWrapperStyle()) {
                  WrapperParameter wrapParam = (WrapperParameter)param;
                  if (sync.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT) {
                     tempWrap = (Class)wrapParam.getTypeInfo().type;
                     break label57;
                  }

                  Iterator var10 = wrapParam.getWrapperChildren().iterator();

                  while(var10.hasNext()) {
                     ParameterImpl p = (ParameterImpl)var10.next();
                     if (p.getIndex() == -1) {
                        tempWrap = (Class)p.getTypeInfo().type;
                        continue label57;
                     }
                  }
                  continue label57;
               }
            } while(param.getIndex() != -1);

            tempWrap = (Class)param.getTypeInfo().type;
            break;
         } while(tempWrap == null);
      }

      this.asyncBeanClass = tempWrap;
      switch(size) {
      case 0:
         this.responseBuilder = this.buildResponseBuilder(sync, ValueSetterFactory.NONE);
         break;
      case 1:
         this.responseBuilder = this.buildResponseBuilder(sync, ValueSetterFactory.SINGLE);
         break;
      default:
         this.responseBuilder = this.buildResponseBuilder(sync, new ValueSetterFactory.AsyncBeanValueSetterFactory(this.asyncBeanClass));
      }

   }

   protected void initArgs(Object[] args) throws Exception {
      if (this.asyncBeanClass != null) {
         args[0] = this.asyncBeanClass.newInstance();
      }

   }

   ValueGetterFactory getValueGetterFactory() {
      return ValueGetterFactory.ASYNC;
   }
}
