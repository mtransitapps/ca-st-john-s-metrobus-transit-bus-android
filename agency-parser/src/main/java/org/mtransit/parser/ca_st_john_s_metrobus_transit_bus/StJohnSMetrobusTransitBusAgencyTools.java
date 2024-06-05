package org.mtransit.parser.ca_st_john_s_metrobus_transit_bus;

import static org.mtransit.commons.RegexUtils.DIGITS;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://www.metrobus.com/html-default/documents.asp
// https://www.metrobus.com/gtfs.asp
public class StJohnSMetrobusTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new StJohnSMetrobusTransitBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Metrobus Transit";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_BROWN = "A19153"; // BROWN (from old logo)

	private static final String AGENCY_COLOR = AGENCY_COLOR_BROWN;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Nullable
	@Override
	public String fixColor(@Nullable String color) {
		if ("FF0000".equalsIgnoreCase(color)) { // RED
			return null;
		}
		return super.fixColor(color);
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		final String rsnS = gRoute.getRouteShortName().trim();
		if (!CharUtils.isDigitsOnly(rsnS)) {
			if ("3A".equalsIgnoreCase(rsnS)) {
				return "8FC74A"; // same as 3
			} else if ("3B".equalsIgnoreCase(rsnS)) {
				return "8FC74A"; // same as 3
			}
			throw new MTLog.Fatal("Unexpected route color %s!", gRoute);
		}
		final int rsn = Integer.parseInt(rsnS);
		switch (rsn) {
		// @formatter:off
		case 1: return "F6863C";
		case 2: return "26A450";
		case 3: return "8FC74A";
		case 5: return "F7ACB0";
		case 6: return "933D40";
		case 9: return "691A5C";
		case 10: return "8F44AD";
		case 11: return "3E4095";
		case 12: return "00BEF2";
		case 13: return "068C83";
		case 14: return "1A5B33";
		case 15: return "C4393C";
		case 16: return "691A5C";
		case 17: return "9D6743";
		case 18: return "467C96";
		case 19: return "ED258F";
		case 20: return "FFCC2C";
		case 21: return "ADA425";
		case 22: return "D6400E";
		case 23: return "A6787A";
		case 24: return "363435";
		case 25: return "3E4095";
		case 26: return "363435";
		// case 27: return null; // TO DO
		case 30: return "EECE20";
		// @formatter:on
		default:
			throw new MTLog.Fatal("Unexpected route color %s!", gRoute);
		}
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern _DASH_ = Pattern.compile("(\\s*-\\s*)", Pattern.CASE_INSENSITIVE);
	private static final String _DASH_REPLACEMENT = " - ";

	private static final Pattern STARTS_WITH_DASH_ = Pattern.compile("(^.*( - ))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = _DASH_.matcher(tripHeadsign).replaceAll(_DASH_REPLACEMENT);
		tripHeadsign = STARTS_WITH_DASH_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = BAY_ROAD_.matcher(tripHeadsign).replaceAll(BAY_ROAD_REPLACEMENT); // after
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private String[] getIgnoredWords() {
		return new String[]{
				"AL", "CBC", "CNIB", "EMCO", "HMP", "HSC", "JB", "JJ", "MFRC", "MUN", "MVR", "NL", "RDM", "RCMP", "RNC", "YMCA",
		};
	}

	private static final Pattern NUMBER_SIGN_ = Pattern.compile("(#\\s*(\\d+))", Pattern.CASE_INSENSITIVE);
	private static final String NUMBER_SIGN_REPLACEMENT = "#$2";

	private static final Pattern BAY_ROAD_ = Pattern.compile("(b:rd)", Pattern.CASE_INSENSITIVE);
	private static final String BAY_ROAD_REPLACEMENT = "Bay Rd";

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), gStopName, getIgnoredWords());
		gStopName = _DASH_.matcher(gStopName).replaceAll(_DASH_REPLACEMENT);
		gStopName = NUMBER_SIGN_.matcher(gStopName).replaceAll(NUMBER_SIGN_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName); // before
		gStopName = BAY_ROAD_.matcher(gStopName).replaceAll(BAY_ROAD_REPLACEMENT); // after
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		//noinspection deprecation
		final String stopId = gStop.getStopId();
		if (CharUtils.isDigitsOnly(stopId)) {
			return Integer.parseInt(stopId);
		}
		final Matcher matcher = DIGITS.matcher(stopId);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}
}
