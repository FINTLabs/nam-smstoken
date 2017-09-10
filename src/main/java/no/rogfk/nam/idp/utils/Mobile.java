package no.rogfk.nam.idp.utils;

import no.rogfk.nam.idp.SMSTokenConstants;
import no.rogfk.nam.idp.exceptions.InvalidMobileNumberException;
import no.rogfk.nam.idp.exceptions.MissingMobileNumberException;
import no.rogfk.nam.idp.exceptions.MultipleMobileNumberException;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class Mobile {

    public static String getMobile(Attributes attributes) {

        int attributeCount = attributes.size();

        validateMobileNumber(attributeCount);
        NamingEnumeration<String> iDs = attributes.getIDs();
        String mobileAttributeName = iDs.nextElement();
        Attribute mobileAttribute = attributes.get(mobileAttributeName);
        String mobile = mobileAttribute.toString();
        int subCount = mobileAttributeName.length() + 2;

        String mobileValue = mobile.substring(subCount);
        if (mobileValue == null || mobileValue.isEmpty()) {
            throw new InvalidMobileNumberException("Mobile number is empty");
        }

        return mobileValue;
    }

    public static String[] validateMobileNumberAttribute(String phoneAttribute) {
        if (phoneAttribute == null || phoneAttribute.length() == 0)
            phoneAttribute = SMSTokenConstants.DEFAULT_MOBILE_ATTRIBUTE;

        return new String[]{phoneAttribute};
    }

    private static void validateMobileNumber(int attributeCount) {
        if (attributeCount < 1) {
            throw new MissingMobileNumberException("Missing mobile number");
        }
        if (attributeCount > 1) {
            throw new MultipleMobileNumberException("Found multiple mobile numbers");
        }
    }
}
