package no.rogfk.nam.idp.utils;


import no.rogfk.nam.idp.SMSTokenConstants;

public class Token {


    public static String getToken(String charsToken, String lengthToken) {
        charsToken = validateTokenCharacters(charsToken);
        int length = validateTokenLength(lengthToken);

        String token = generateToken(charsToken, length);

        return token;
    }

    public static boolean validateToken(String responseToken, String sentToken) {

        if (responseToken == null || responseToken.length() == 0) {
            return false;
        }

        if (!responseToken.equals(sentToken)) {
            return false;
        }

        return true;
    }

    private static String generateToken(String charsToken, int length) {
        String token = "";
        for (int i = 1; i < length; i++) {
            int index = (int) (charsToken.length() * Math.random());
            token = token + charsToken.charAt(index);
        }
        return token;
    }

    private static int validateTokenLength(String lengthToken) {
        int lenght;

        if (lengthToken == null || lengthToken.length() == 0) {
            lenght = SMSTokenConstants.DEFAULT_TOKEN_LENGTH + 1;
        } else {
            lenght = Integer.valueOf(lengthToken).intValue() + 1;
        }

        return lenght;
    }

    private static String validateTokenCharacters(String charsToken) {
        if (charsToken == null || charsToken.length() == 0)
            charsToken = SMSTokenConstants.DEFAULT_TOKEN_CHARACTERS;
        return charsToken;
    }
}
