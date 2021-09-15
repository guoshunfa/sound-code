package com.sun.xml.internal.ws.addressing.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class MissingAddressingHeaderException extends WebServiceException {
   private final QName name;
   private final transient Packet packet;

   public MissingAddressingHeaderException(@NotNull QName name) {
      this(name, (Packet)null);
   }

   public MissingAddressingHeaderException(@NotNull QName name, @Nullable Packet p) {
      super(AddressingMessages.MISSING_HEADER_EXCEPTION(name));
      this.name = name;
      this.packet = p;
   }

   public QName getMissingHeaderQName() {
      return this.name;
   }

   public Packet getPacket() {
      return this.packet;
   }
}
