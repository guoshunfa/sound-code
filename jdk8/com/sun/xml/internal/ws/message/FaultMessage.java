package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.FilterMessageImpl;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.namespace.QName;

public class FaultMessage extends FilterMessageImpl {
   @Nullable
   private final QName detailEntryName;

   public FaultMessage(Message delegate, @Nullable QName detailEntryName) {
      super(delegate);
      this.detailEntryName = detailEntryName;
   }

   @Nullable
   public QName getFirstDetailEntryName() {
      return this.detailEntryName;
   }
}
