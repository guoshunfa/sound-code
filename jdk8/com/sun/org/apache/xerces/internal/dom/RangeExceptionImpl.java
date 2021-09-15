package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ranges.RangeException;

public class RangeExceptionImpl extends RangeException {
   static final long serialVersionUID = -9058052627467240856L;

   public RangeExceptionImpl(short code, String message) {
      super(code, message);
   }
}
