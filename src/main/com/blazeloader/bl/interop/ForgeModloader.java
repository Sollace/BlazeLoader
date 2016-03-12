package com.blazeloader.bl.interop;

import com.blazeloader.util.reflect.Func;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.SimpleFunc;
import com.blazeloader.util.version.Versions;

public final class ForgeModloader {
	private static boolean init = false;
	private static ForgeMLAccess access;
	
	private static ForgeMLAccess getAccess() {
		if (Versions.isForgeInstalled() && !init) {
			init = true;
			/*access = new ForgeMLAccess() {
				public void exitJava(int arg0) {
					net.minecraftforge.fml.common.FMLCommonHandler.instance().exitJava(arg0);
				}
			}*/
			SimpleFunc _getInstance = Reflect.lookupStaticMethod("net.minecraftforge.fml.common.FMLCommonHandler.instance ()Lnet/minecraftforge/fml/common/FMLCommonHandler;");
			Func<Object, ForgeMLAccess, Void> _exitJava = Reflect.lookupMethod(ForgeMLAccess.class, "net.minecraftforge.fml.common.FMLCommonHandler.exitJava (IZ)V");
			if (_getInstance.valid() && _exitJava.valid()) {
				try {
					access = _exitJava.getLambda(_getInstance.call());
				} catch (Throwable e) {
					access = null;
				}
			}
		}
		return access;
	}
	
	public static void exitJVM(int exitCode) {
		if (Versions.isForgeInstalled()) {
			ForgeMLAccess access = getAccess();
			if (access != null) access.exitJava(exitCode, false);
		}
		System.exit(exitCode);
	}
}
