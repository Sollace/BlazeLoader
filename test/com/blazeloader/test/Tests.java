package com.blazeloader.test;

import com.blazeloader.bl.interop.ForgeMLAccess;
import com.blazeloader.util.reflect.Func;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.SimpleFunc;

public class Tests {
	public static boolean interopTest() throws Throwable {
		SimpleFunc instance = Reflect.lookupStaticMethod("net.minecraftforge.fml.common.FMLCommonHandler.instance ()Lnet/minecraftforge/fml/common/FMLCommonHandler;");
		Func<?, ForgeMLAccess, Void> _exitJava = Reflect.lookupMethod(ForgeMLAccess.class, "net.minecraftforge.fml.common.FMLCommonHandler.exitJava (IZ)V");
		System.out.println("Call: net.minecraftforge.fml.common.FMLCommonHandler.instance ()Lnet/minecraftforge/fml/common/FMLCommonHandler;");
		System.out.println("Call: net.minecraftforge.fml.common.FMLCommonHandler.exitJava (IZ)V");
		_exitJava.getLambda(instance.call()).exitJava(-1, false);
		return true;
	}
}
