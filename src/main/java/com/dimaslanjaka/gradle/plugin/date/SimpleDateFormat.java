package com.dimaslanjaka.gradle.plugin.date;

import org.jetbrains.annotations.Contract;

import java.util.Date;
import java.util.Locale;

import static com.dimaslanjaka.gradle.plugin.Utils.println;

public class SimpleDateFormat extends java.text.SimpleDateFormat {
	static final long serialVersionUID = 4774881970558854345L;
	static final int currentSerialVersion = 1;
	@SuppressWarnings({"all"})
	private int serialVersionOnStream = currentSerialVersion;

	public SimpleDateFormat(String pattern) {
		super(pattern, Locale.getDefault(Locale.Category.FORMAT));
	}

	@SuppressWarnings({"unused"})
	public SimpleDateFormat(String dateString, Locale aDefault) {
		super(dateString, aDefault);
	}

	public static void main(String[] args) {
		for (DateFormats df : DateFormats.values()) {
			//DateFormats df2 = new DateFormats(String.valueOf(df));
			println(df.getDateFormat());
		}
	}

	public Date parse(String dateString) {
		try {
			return super.parse(dateString);
		} catch (Exception e) {
			for (DateFormats df : DateFormats.values()) {
				try {
					//String stringSigns = dateString.replaceAll("[0-9]+", "");
					//String[] signsArray = stringSigns.split("");
					//println(signsArray);
					//SimpleDateFormat format = new SimpleDateFormat(df.dateFormat);
					//String ds = format.format(new Date());
					//Date date = format.parse("2009-12-31");
					/*
					if (test.parse(dateString) != null){
						return test.parse(dateString);
					}*/
				} catch (Exception ee) {
					ee.printStackTrace();
				}
				//println("df " + df.dateFormat, dateString);
				//println(df.getDateFormat());
			}
		}
		return null;
	}

	public enum DateFormats {
		D_YYMMDD("yy-MM-dd"), D_DDMMyy("dd-MM-yy"),
		D_YYMMDD_N("yy-MMM-dd"), D_DDMMyy_N("dd-MMM-yy"),
		D_YYMMDDHHMMA_N("yy-MMM-dd, hh:mma"), D_DDMMyyHHMMA_N("dd-MMM-yy, hh:mma"),
		S_YYMMDD("yy/MM/dd"), S_DDMMyy("dd/MM/yy"),
		S_YYMMDDHHMMA("yy/MM/dd, hh:mma"), S_DDMMyyHHMMA("dd/MM/yy, hh:mma"),
		S_YYMMDDHHMMA_N("yy/MMM/dd, hh:mma"), S_DDMMyyHHMMA_N("dd/MMM/yy, hh:mma"),
		D_YYYYMMDD("yyyy-MM-dd"), D_DDMMYYYY("dd-MM-yyyy"),
		D_YYYYMMDDHHMMA("yyyy-MM-dd, hh:mma"), D_DDMMYYYYHHMMA("dd-MM-yyyy, hh:mma"),
		D_YYYYMMDD_N("yyyy-MMM-dd"), D_DDMMYYYY_N("dd-MMM-yyyy"),
		D_YYYYMMDDHHMMA_N("yyyy-MMM-dd, hh:mma"), D_DDMMYYYYHHMMA_N("dd-MMM-yyyy, hh:mma"),
		S_YYYYMMDD("yyyy/MM/dd"), S_DDMMYYYY("dd/MM/yyyy"),
		S_YYYYMMDDHHMMA("yyyy/MM/dd, hh:mma"), S_DDMMYYYYHHMMA("dd/MM/yyyy, hh:mma"),
		S_YYYYMMDDHHMMA_N("yyyy/MMM/dd, hh:mma"), S_DDMMYYYYHHMMA_N("dd/MMM/yyyy, hh:mma"),
		D_YYMMDDHHMMSSA_N("yy-MMM-dd, hh:mm:ssa"), D_DDMMyyHHMMSSA_N("dd-MMM-yy, hh:mm:ssa"),
		S_YYMMDDHHMMSSA("yy/MM/dd, hh:mm:ssa"), S_DDMMyyHHMMSSA("dd/MM/yy, hh:mm:ssa"),
		S_YYMMDDHHMMSSA_N("yy/MMM/dd, hh:mm:ssa"), S_DDMMyyHHMMSSA_N("dd/MMM/yy, hh:mm:ssa"),
		D_YYYYMMDDHHMMSSA("yyyy-MM-dd, hh:mm:ssa"), D_DDMMYYYYHHMMSSA("dd-MM-yyyy, hh:mm:ssa"),
		D_YYYYMMDDHHMMSSA_N("yyyy-MMM-dd, hh:mm:ssa"), D_DDMMYYYYHHMMSSA_N("dd-MMM-yyyy, hh:mm:ssa"),
		S_YYYYMMDDHHMMSSA("yyyy/MM/dd, hh:mm:ssa"), S_DDMMYYYYHHMMSSA("dd/MM/yyyy, hh:mm:ssa"),
		S_YYYYMMDDHHMMSSA_N("yyyy/MMM/dd, hh:mm:ssa"), S_DDMMYYYYHHMMSSA_N("dd/MMM/yyyy, hh:mm:ssa"),
		HHMMA("hh:mma"), HHMM("hh:mm"), HHMMSSA("hh:mm:ssa"), HHMMSS("hh:mm:ss");
		@SuppressWarnings({"all"})
		private String dateFormat;

		@Contract(pure = true)
		DateFormats(String dateFormat) {
			this.dateFormat = dateFormat;
		}

		public String getDateFormat() {
			return dateFormat;
		}

		public void setDateFormat(String dateFormat) {
			this.dateFormat = dateFormat;
		}
	}
}
