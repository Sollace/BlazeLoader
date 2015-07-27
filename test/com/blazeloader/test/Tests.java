package com.blazeloader.test;



import com.blazeloader.bl.interop.ForgeMLAccess;
import com.blazeloader.util.JSArrayUtils;
import com.blazeloader.util.data.Tuple.Tuple2;
import com.blazeloader.util.reflect.Func;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.SimpleFunc;
import com.blazeloader.util.data.Option;

public class Tests {
	public static boolean PopTest() {
		Tuple2<Option, Integer[]> popped = JSArrayUtils.pop(1,2,3);
		System.out.println("[1,2,3] -> {{Some 3},[1,2]}");
		return popped.itemOne.equals(Option.some(3)) && JSArrayUtils.join(popped.itemTwo).contentEquals("1,2");
	}
	
	public static boolean interopTest() throws Throwable {
		SimpleFunc instance = Reflect.lookupStaticMethod("net.minecraftforge.fml.common.FMLCommonHandler.instance ()Lnet/minecraftforge/fml/common/FMLCommonHandler;");
		Func<?, ForgeMLAccess, Void> _exitJava = Reflect.lookupMethod(ForgeMLAccess.class, "net.minecraftforge.fml.common.FMLCommonHandler.exitJava (IZ)V");
		System.out.println("Call: net.minecraftforge.fml.common.FMLCommonHandler.instance ()Lnet/minecraftforge/fml/common/FMLCommonHandler;");
		System.out.println("Call: net.minecraftforge.fml.common.FMLCommonHandler.exitJava (IZ)V");
		_exitJava.getLambda(instance.call()).exitJava(-1, false);
		return true;
	}
	
	public static boolean isArrayTest() {
		boolean result = true;
		result &= JSArrayUtils.isArray(new int[0]) == true;
		result &= JSArrayUtils.isArray(1) == false;
		result &= JSArrayUtils.isArray("String") == false;
		return result;
	}
	
	public static boolean toSourceTest() {
		String result = JSArrayUtils.toSource(new int[] {1},2,3);
		System.out.println("[[1], 2, 3] == " + result);
		return "[[1], 2, 3]".contentEquals(result);
	}
	
	public static boolean joinTest() {
		Object[] arr = {new int[] {1},2,3};
		String result = JSArrayUtils.join(arr);
		System.out.println("1,2,3 == " + result);
		return "1,2,3".contentEquals(result);
	}
	
	/*public static boolean isArrayTest() {
		long start, end;
		int total = 1000000000;
		int[] arr = new int[0];
		start = System.currentTimeMillis();
		for (int i = 0; i < total; i++) {
			JSArrayUtils.isArray(arr);
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
		start = System.currentTimeMillis();
		for (int i = 0; i < total; i++) {
			JSArrayUtils.isArray2(arr);
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
		return true;
	}*/
}
