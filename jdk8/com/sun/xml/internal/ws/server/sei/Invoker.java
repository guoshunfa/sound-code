package com.sun.xml.internal.ws.server.sei;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Invoker {
   public abstract Object invoke(@NotNull Packet var1, @NotNull Method var2, @NotNull Object... var3) throws InvocationTargetException, IllegalAccessException;
}
