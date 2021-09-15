package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;

public interface ModelBuilderI<T, C, F, M> {
   Navigator<T, C, F, M> getNavigator();

   AnnotationReader<T, C, F, M> getReader();
}
