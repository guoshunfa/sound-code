package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.jws.WebParam;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

public class ParameterImpl implements Parameter {
   private ParameterBinding binding;
   private ParameterBinding outBinding;
   private String partName;
   private final int index;
   private final WebParam.Mode mode;
   /** @deprecated */
   private TypeReference typeReference;
   private TypeInfo typeInfo;
   private QName name;
   private final JavaMethodImpl parent;
   WrapperParameter wrapper;
   TypeInfo itemTypeInfo;

   public ParameterImpl(JavaMethodImpl parent, TypeInfo type, WebParam.Mode mode, int index) {
      assert type != null;

      this.typeInfo = type;
      this.name = type.tagName;
      this.mode = mode;
      this.index = index;
      this.parent = parent;
   }

   public AbstractSEIModelImpl getOwner() {
      return this.parent.owner;
   }

   public JavaMethod getParent() {
      return this.parent;
   }

   public QName getName() {
      return this.name;
   }

   public XMLBridge getXMLBridge() {
      return this.getOwner().getXMLBridge(this.typeInfo);
   }

   public XMLBridge getInlinedRepeatedElementBridge() {
      TypeInfo itemType = this.getItemType();
      if (itemType != null) {
         XMLBridge xb = this.getOwner().getXMLBridge(itemType);
         if (xb != null) {
            return new RepeatedElementBridge(this.typeInfo, xb);
         }
      }

      return null;
   }

   public TypeInfo getItemType() {
      if (this.itemTypeInfo != null) {
         return this.itemTypeInfo;
      } else if (!this.parent.getBinding().isRpcLit() && this.wrapper != null) {
         if (!WrapperComposite.class.equals(this.wrapper.getTypeInfo().type)) {
            return null;
         } else if (!this.getBinding().isBody()) {
            return null;
         } else {
            this.itemTypeInfo = this.typeInfo.getItemType();
            return this.itemTypeInfo;
         }
      } else {
         return null;
      }
   }

   /** @deprecated */
   public Bridge getBridge() {
      return this.getOwner().getBridge(this.typeReference);
   }

   /** @deprecated */
   protected Bridge getBridge(TypeReference typeRef) {
      return this.getOwner().getBridge(typeRef);
   }

   /** @deprecated */
   public TypeReference getTypeReference() {
      return this.typeReference;
   }

   public TypeInfo getTypeInfo() {
      return this.typeInfo;
   }

   /** @deprecated */
   void setTypeReference(TypeReference type) {
      this.typeReference = type;
      this.name = type.tagName;
   }

   public WebParam.Mode getMode() {
      return this.mode;
   }

   public int getIndex() {
      return this.index;
   }

   public boolean isWrapperStyle() {
      return false;
   }

   public boolean isReturnValue() {
      return this.index == -1;
   }

   public ParameterBinding getBinding() {
      return this.binding == null ? ParameterBinding.BODY : this.binding;
   }

   public void setBinding(ParameterBinding binding) {
      this.binding = binding;
   }

   public void setInBinding(ParameterBinding binding) {
      this.binding = binding;
   }

   public void setOutBinding(ParameterBinding binding) {
      this.outBinding = binding;
   }

   public ParameterBinding getInBinding() {
      return this.binding;
   }

   public ParameterBinding getOutBinding() {
      return this.outBinding == null ? this.binding : this.outBinding;
   }

   public boolean isIN() {
      return this.mode == WebParam.Mode.IN;
   }

   public boolean isOUT() {
      return this.mode == WebParam.Mode.OUT;
   }

   public boolean isINOUT() {
      return this.mode == WebParam.Mode.INOUT;
   }

   public boolean isResponse() {
      return this.index == -1;
   }

   public Object getHolderValue(Object obj) {
      return obj != null && obj instanceof Holder ? ((Holder)obj).value : obj;
   }

   public String getPartName() {
      return this.partName == null ? this.name.getLocalPart() : this.partName;
   }

   public void setPartName(String partName) {
      this.partName = partName;
   }

   void fillTypes(List<TypeInfo> types) {
      TypeInfo itemType = this.getItemType();
      types.add(itemType != null ? itemType : this.getTypeInfo());
   }
}
