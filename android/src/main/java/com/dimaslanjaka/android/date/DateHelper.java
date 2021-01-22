package com.dimaslanjaka.android.date;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by kosh20111 on 10/7/2015.
 * <p>
 * Helper Class to deal with time and dates
 */
public class DateHelper {

	/**
	 * @return hh:mm a || dd MMM hh:mm a
	 */
	public static String prettifyDate(long timestamp) {
		SimpleDateFormat dateFormat;
		if (DateUtils.isToday(timestamp)) {
			dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
		} else {
			dateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
		}
		return dateFormat.format(timestamp);
	}

	/**
	 * @return hh:mm a || dd MMM hh:mm a
	 */
	public static String prettifyDate(String timestamp) {
		SimpleDateFormat dateFormat;
		if (DateUtils.isToday(Long.parseLong(timestamp))) {
			dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
		} else {
			dateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
		}
		return dateFormat.format(Long.parseLong(timestamp));
	}

	/**
	 * @return dd/MM/yyyy
	 */
	public static long getDateOnly(String date) {
		SimpleDateFormat sample = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		try {
			return sample.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @return dd/MM/yyyy
	 */
	public static String getDateOnly(long time) {
		return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(time);
	}

	/**
	 * @return dd/MM/yyyy, hh:mm a
	 */
	public static String getDateAndTime(long time) {
		SimpleDateFormat sample = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault());
		return sample.format(new Date(time));
	}

	/**
	 * @return dd/MM/yyyy, hh:mm a
	 */
	public static String getDateAndTime(String time) {
		SimpleDateFormat sample = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault());
		return sample.format(time);
	}

	/**
	 * @return hh:mm a
	 */
	public static String getTimeOnly(long time) {
		SimpleDateFormat sample = new SimpleDateFormat("hh:mm a", Locale.getDefault());
		return sample.format(time);
	}

	/**
	 * @return today's date in format (dd/MM/yyyy HH:mm:ss)
	 */
	public static String getTodayWithTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
		return dateFormat.format(new Date());
	}

	/**
	 * @return today's date in format (dd/MM/yyyy)
	 */
	public static String getToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		return dateFormat.format(new Date());
	}

	/**
	 * @return tomorrows's date in format (dd/MM/yyyy)
	 */
	public static String getTomorrow() {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(getToday()));
			calendar.add(Calendar.DATE, 1);
			Date tomorrow = calendar.getTime();
			return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(tomorrow);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param old     ( must be dd/MM/yyyy, hh:mm a )
	 * @param newDate ( must be dd/MM/yyyy, hh:mm a )
	 * @return number of days
	 */
	public static Long getDaysBetweenTwoDate(String old, String newDate, DateFormats dateFormats) {
		SimpleDateFormat myFormat = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
		try {
			Date date1 = myFormat.parse(old);
			Date date2 = myFormat.parse(newDate);
			long diff = date1.getTime() - date2.getTime();
			return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param old     ( must be dd/MM/yyyy, hh:mm a )
	 * @param newDate ( must be dd/MM/yyyy, hh:mm a )
	 * @return number of hours
	 */
	public static Long getHoursBetweenTwoDate(String old, String newDate, DateFormats dateFormats) {
		SimpleDateFormat myFormat = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
		try {
			Date date1 = myFormat.parse(old);
			Date date2 = myFormat.parse(newDate);
			long diff = date1.getTime() - date2.getTime();
			return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Long getMinutesBetweenTwoDates(String old, String newDate, DateFormats dateFormats) {
		SimpleDateFormat myFormat = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
		try {
			Date date1 = myFormat.parse(old);
			Date date2 = myFormat.parse(newDate);
			long diff = date1.getTime() - date2.getTime();
			return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * it will loop through {@link DateFormats} possible formats and returns the parsed date if any match.
	 */
	public static long parseAnyDate(String date) {
		long time = 0;
		for (DateFormats formats : DateFormats.values()) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(formats.getDateFormat(), Locale.getDefault());
				time = format.parse(date).getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return time;
	}

	public static long parseDate(String date, DateFormats dateFormats) {
		SimpleDateFormat format = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
		try {
			return format.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static String getDesiredFormat(DateFormats formats) {
		SimpleDateFormat format = new SimpleDateFormat(formats.getDateFormat(), Locale.getDefault());
		return format.format(new Date());
	}

	public static String getDesiredFormat(DateFormats formats, long date) {
		SimpleDateFormat format = new SimpleDateFormat(formats.getDateFormat(), Locale.getDefault());
		return format.format(date);
	}

	public static DatePickerDialog datePickerDialog(final Context context, final EditText date, final boolean withTime, final DateFormats
					dateFormats) {
		return new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar newDate = Calendar.getInstance();
				newDate.set(year, monthOfYear, dayOfMonth);
				date.setText(getDesiredFormat(dateFormats, newDate.getTimeInMillis()));
				if (withTime) {
					timePickerDialog(context, date, true).show();
				}
			}
		}, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
						Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
	}

	public static TimePickerDialog timePickerDialog(final Context context, final EditText date, final boolean withAppend) {
		return new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
			// FIXME: 02/12/15 timePicker for android 4.++ < 5.0
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
				if (view.isShown()) {
					Calendar datetime = Calendar.getInstance();
					datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
					datetime.set(Calendar.MINUTE, minutes);
					if (withAppend) {
						date.setText(String.format("%s, %s", date.getText().toString(), getDesiredFormat(DateFormats.HHMMA, datetime
										.getTimeInMillis())));
					} else {
						date.setText(getDesiredFormat(DateFormats.HHMMA, datetime.getTimeInMillis()));
					}
				}
			}
		}, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
						Calendar.getInstance().get(Calendar.MINUTE), true);
	}

	public static String getDateFromDays(int numOfDays) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, numOfDays);
		return getDesiredFormat(DateFormats.D_DDMMyy_N, cal.getTimeInMillis());
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
		private String dateFormat;

		DateFormats(String dateFormat) {
			this.dateFormat = dateFormat;
		}

		public String getDateFormat() {
			return dateFormat;
		}
	}

}