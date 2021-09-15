package com.sun.xml.internal.ws.addressing.model;

import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class InvalidAddressingHeaderException extends WebServiceException {
   private QName problemHeader;
   private QName subsubcode;

   public InvalidAddressingHeaderException(QName problemHeader, QName subsubcode) {
      super(AddressingMessages.INVALID_ADDRESSING_HEADER_EXCEPTION(problemHeader, subsubcode));
      this.problemHeader = problemHeader;
      this.subsubcode = subsubcode;
   }

   public QName getProblemHeader() {
      return this.problemHeader;
   }

   public QName getSubsubcode() {
      return this.subsubcode;
   }
}
