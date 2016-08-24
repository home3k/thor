/*
 * Copyright 2014-2030 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server.server;

import java.text.AttributedCharacterIterator;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * @author dujingwei
 * @date 2016年4月14日
 */
public class SimpleDateFormatThreadSafe extends SimpleDateFormat {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3653345891943921268L;

	ThreadLocal<SimpleDateFormat> localSimpleDateFormat;

    public SimpleDateFormatThreadSafe() {
        super();
        localSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat();
            }
        };
    }

    public SimpleDateFormatThreadSafe(final String pattern) {
        super(pattern);
        localSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(pattern);
            }
        };
    }

    public SimpleDateFormatThreadSafe(final String pattern, final DateFormatSymbols formatSymbols) {
        super(pattern, formatSymbols);
        localSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(pattern, formatSymbols);
            }
        };
    }

    public SimpleDateFormatThreadSafe(final String pattern, final Locale locale) {
        super(pattern, locale);
        localSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(pattern, locale);
            }
        };
    }

    public Object parseObject(String source) throws ParseException {
        return localSimpleDateFormat.get().parseObject(source);
    }

    public String toString() {
        return localSimpleDateFormat.get().toString();
    }

    public Date parse(String source) throws ParseException {
        return localSimpleDateFormat.get().parse(source);
    }

    public Object parseObject(String source, ParsePosition pos) {
        return localSimpleDateFormat.get().parseObject(source, pos);
    }

    public void setCalendar(Calendar newCalendar) {
        localSimpleDateFormat.get().setCalendar(newCalendar);
    }

    public Calendar getCalendar() {
        return localSimpleDateFormat.get().getCalendar();
    }

    public void setNumberFormat(NumberFormat newNumberFormat) {
        localSimpleDateFormat.get().setNumberFormat(newNumberFormat);
    }

    public NumberFormat getNumberFormat() {
        return localSimpleDateFormat.get().getNumberFormat();
    }

    public void setTimeZone(TimeZone zone) {
        localSimpleDateFormat.get().setTimeZone(zone);
    }

    public TimeZone getTimeZone() {
        return localSimpleDateFormat.get().getTimeZone();
    }

    public void setLenient(boolean lenient) {
        localSimpleDateFormat.get().setLenient(lenient);
    }

    public boolean isLenient() {
        return localSimpleDateFormat.get().isLenient();
    }

    public void set2DigitYearStart(Date startDate) {
        localSimpleDateFormat.get().set2DigitYearStart(startDate);
    }

    public Date get2DigitYearStart() {
        return localSimpleDateFormat.get().get2DigitYearStart();
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        return localSimpleDateFormat.get().format(date, toAppendTo, pos);
    }

    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return localSimpleDateFormat.get().formatToCharacterIterator(obj);
    }

    public Date parse(String text, ParsePosition pos) {
        return localSimpleDateFormat.get().parse(text, pos);
    }

    public String toPattern() {
        return localSimpleDateFormat.get().toPattern();
    }

    public String toLocalizedPattern() {
        return localSimpleDateFormat.get().toLocalizedPattern();
    }

    public void applyPattern(String pattern) {
        localSimpleDateFormat.get().applyPattern(pattern);
    }

    public void applyLocalizedPattern(String pattern) {
        localSimpleDateFormat.get().applyLocalizedPattern(pattern);
    }

    public DateFormatSymbols getDateFormatSymbols() {
        return localSimpleDateFormat.get().getDateFormatSymbols();
    }

    public void setDateFormatSymbols(DateFormatSymbols newFormatSymbols) {
        localSimpleDateFormat.get().setDateFormatSymbols(newFormatSymbols);
    }

    /**
     * 非常重要
     */
    public Object clone() {
        return localSimpleDateFormat.get().clone();
    }

    public int hashCode() {
        return localSimpleDateFormat.get().hashCode();
    }

    public boolean equals(Object obj) {
        return localSimpleDateFormat.get().equals(obj);
    }


}
