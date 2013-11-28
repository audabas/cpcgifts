package fr.cpcgifts.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TextTools {

	public static String escapeHtml(String src) {
		String res = src.replaceAll("&", "&amp;");
		res = res.replaceAll("<", "&lt;");
		res = res.replaceAll(">", "&gt;");
		res = res.replaceAll("€", "&euro;");

		return res;
	}
	
	public static String urlEncode(String src) {
		String res = new String();
		
		try {
			res = URLEncoder.encode(src, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		res = res.replaceAll("\\+", "%20");

		return res;
	}

}
