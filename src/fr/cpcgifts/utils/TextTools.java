package fr.cpcgifts.utils;

public class TextTools {

	public static String escapeHtml(String src) {
		String res = src.replaceAll("&", "&amp;");
		res = res.replaceAll("<", "&lt;");
		res = res.replaceAll(">", "&gt;");
		res = res.replaceAll("€", "&euro;");

		return res;
	}

}
