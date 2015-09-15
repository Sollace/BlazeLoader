package com.blazeloader.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

import com.blazeloader.bl.main.BlazeLoaderAPI;
import com.google.common.collect.Lists;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderEnvironment.EnvironmentType;
import com.mumfrey.liteloader.launch.LoaderProperties;

public abstract class Start {
	
	private static final LoaderEnvironment env = new DebugEnvironment(EnvironmentType.CLIENT);
	
	public static void main(String[] args) {
		System.out.println("Starting System Debug...");
		BlazeLoaderAPI api = new BlazeLoaderAPI();
		api.init(env, (LoaderProperties)env);
		
		ArrayList<Method> tests = Lists.newArrayList(Tests.class.getDeclaredMethods());
		tests.sort(new Comparator<Method>() {
			public int compare(Method arg0, Method arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		Method[] meths = tests.toArray(new Method[tests.size()]);
		System.out.println("Running " + meths.length + " Tests...");
		for (int i = 0; i < meths.length; i++) {
			boolean o;
			try {
				o = (boolean)meths[i].invoke(null);
				System.out.println("Test (" + i + "): " + meths[i].getName() + " " + (o ? "Passed" : "Failed"));
			} catch (Throwable e) {
				System.out.println("Test (" + i + "): " + meths[i].getName() + " failed to execute.");
				e.printStackTrace();
			}
		}
		System.out.println("Tests completed. Exiting...");
	}
}
