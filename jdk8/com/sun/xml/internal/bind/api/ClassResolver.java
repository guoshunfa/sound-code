package com.sun.xml.internal.bind.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract class ClassResolver {
   @Nullable
   public abstract Class<?> resolveElementName(@NotNull String var1, @NotNull String var2) throws Exception;
}
