package it.polimi.tiw.documents.utils;

public class EmailValidator {
	public static boolean isValid(String email) {
		if (email.length() > 320 || email.contains(" ")) return false;

		String[] parts = email.split("@");
		
		if (parts.length != 2 || parts[0].length() > 64 || parts[1].length() > 255) return false;
		
		String local = parts[0];
		String domain = parts[1];
		
		if (!hasProperDotUsage(local)) return false;
		
		String[] DNSLabels = domain.split(".");
		
		for (String label : DNSLabels) {
			if (label.length() > 63 || !hasProperHyphenUsage(label)) return false;
		}
 
		return true;
	}

	private static boolean hasProperDotUsage(String local) {
		for(int i = 0; i < local.length(); i++) {
			if (local.charAt(i) != '.') continue;
			
			if (i == 0 || i == local.length() - 1) return false;
			
			if (local.charAt(i + 1) == '.') return false;
		}
		
		return true;
	}
	
	private static boolean hasProperHyphenUsage(String label) {
		for(int i = 0; i < label.length(); i++) {
			if (label.charAt(i) != '-') continue;
			
			if (i == 0 || i == label.length() - 1) return false;
		}
		
		return true;
	}
}
