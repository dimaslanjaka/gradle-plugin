package com.dimaslanjaka.gradle.plugin.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.win32.StdCallLibrary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


@Component
@Qualifier("windowsSetSystemTime")
public class WindowsSetSystemTime {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static boolean SetLocalTime(SYSTEMTIME st) {
		return Kernel32.instance.SetLocalTime(st);
	}

	public static boolean SetLocalTime(short wYear, short wMonth, short wDay, short wHour, short wMinute, short wSecond) {
		SYSTEMTIME st = new SYSTEMTIME();
		st.wYear = wYear;
		st.wMonth = wMonth;
		st.wDay = wDay;
		st.wHour = wHour;
		st.wMinute = wMinute;
		st.wSecond = wSecond;
		return SetLocalTime(st);
	}

	public static boolean setTime() {
		//System.out.println("START SYNC " + windowsSetSystemTime);

		//https://i.stack.imgur.com/ff5o2.png
		return SetLocalTime((short) 2017, (short) 10, (short) 29, (short) 11, (short) 35, (short) 0);
	}

	public static void main(String[] args) {
		com.dimaslanjaka.kotlin.Helper.println(setTime());
	}

	/**
	 * Kernel32 DLL Interface. kernel32.dll uses the __stdcall calling
	 * convention (check the function declaration for "WINAPI" or "PASCAL"), so
	 * extend StdCallLibrary Most C libraries will just extend
	 * com.sun.jna.Library,
	 */
	public interface Kernel32 extends StdCallLibrary {

		Kernel32 instance = Native.load/*loadLibrary*/("kernel32.dll", Kernel32.class);

		boolean SetLocalTime(SYSTEMTIME st);
	}
}