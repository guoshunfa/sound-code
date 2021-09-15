package com.sun.xml.internal.bind.v2.runtime.reflect;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

public interface ListIterator<E> {
   boolean hasNext();

   E next() throws SAXException, JAXBException;
}
