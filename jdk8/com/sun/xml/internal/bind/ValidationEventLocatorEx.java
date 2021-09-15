package com.sun.xml.internal.bind;

import javax.xml.bind.ValidationEventLocator;

public interface ValidationEventLocatorEx extends ValidationEventLocator {
   String getFieldName();
}
