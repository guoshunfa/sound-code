package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.jws.WebParam;

public class WrapperParameter extends ParameterImpl {
   protected final List<ParameterImpl> wrapperChildren = new ArrayList();

   public WrapperParameter(JavaMethodImpl parent, TypeInfo typeRef, WebParam.Mode mode, int index) {
      super(parent, typeRef, mode, index);
      typeRef.properties().put(WrapperParameter.class.getName(), this);
   }

   /** @deprecated */
   public boolean isWrapperStyle() {
      return true;
   }

   public List<ParameterImpl> getWrapperChildren() {
      return this.wrapperChildren;
   }

   public void addWrapperChild(ParameterImpl wrapperChild) {
      this.wrapperChildren.add(wrapperChild);
      wrapperChild.wrapper = this;

      assert wrapperChild.getBinding() == ParameterBinding.BODY;

   }

   public void clear() {
      this.wrapperChildren.clear();
   }

   void fillTypes(List<TypeInfo> types) {
      super.fillTypes(types);
      if (WrapperComposite.class.equals(this.getTypeInfo().type)) {
         Iterator var2 = this.wrapperChildren.iterator();

         while(var2.hasNext()) {
            ParameterImpl p = (ParameterImpl)var2.next();
            p.fillTypes(types);
         }
      }

   }
}
