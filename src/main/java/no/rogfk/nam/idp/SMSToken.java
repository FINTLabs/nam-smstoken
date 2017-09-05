package no.rogfk.nam.idp;

import com.novell.nidp.NIDPConstants;
import com.novell.nidp.NIDPError;
import com.novell.nidp.NIDPPrincipal;
import com.novell.nidp.authentication.AuthnConstants;
import com.novell.nidp.authentication.local.LocalAuthenticationClass;
import com.novell.nidp.common.authority.UserAuthority;
import lombok.extern.slf4j.Slf4j;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;


@Slf4j
public class SMSToken extends LocalAuthenticationClass {

    private String sessionUser;
    private String smsToken;
    private boolean smsTokenSent;

    String phoneAttr = getProperty("phoneAttr");  // the attribute name to retrieve the number - default mobile
    String charsToken = getProperty("charsToken");  // characters allowed in token - default ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890
    String lengthToken = getProperty("lengthToken");  // length token - default 6
    String gwURL = getProperty("gwURL"); // gateway URL - mandatory
    String gwUserParameter = getProperty("gwUserParameter"); // username to auth to gw
    String gwPasswdParameter = getProperty("gwPasswdParameter");  // password to auth to gw
    String gwExtraParameter = getProperty("gwExtraParameter"); // extra param for gw
    String gwExtraParameter2 = getProperty("gwExtraParameter2"); // extra param2 for gw
    String gwDestName = getProperty("gwDestName"); // name of the Destination parameter for gw
    String gwMessageName = getProperty("gwMessageName"); // name of the message parameter for gw
    String gwSuccess = getProperty("gwSuccess"); // success if gw response contains this value
    String gwError = getProperty("gwError"); // error if gw response contains this value
    String debug = getProperty("debug"); // if this is present, debug is enabled


    public SMSToken(Properties properties, ArrayList<UserAuthority> arrayList) {
        super(properties, arrayList);
        sessionUser = "true";
        smsTokenSent = false;

    }


    public String getType() {
        return AuthnConstants.OTHER;
    }

    @Override
    public int doAuthenticate() {
        log.info("SMSToken: doAuthenticate()");

        NIDPPrincipal nIDPPrincipal = getUserPrincipal();

        if (nIDPPrincipal == null) {
            setUserErrorMsg("No Authenticated User Found");
            log.info("No Authenticated User Found");
            NIDPError nIDPError = new NIDPError(getUserErrorMsg(), getUserErrorMsg(), Locale.US);
            showError(nIDPError);
            return NOT_AUTHENTICATED;
        }

        log.info("Found principal {}", nIDPPrincipal.getUserIdentifier());

        setPrincipal(nIDPPrincipal);

        if (!smsTokenSent) {
            log.info("Sending SMS Token");


            log.info("Mobile: {}", getMobileNumber());
            log.info("Token: {}", generateToken());

            smsToken = generateToken();

            log.info("Showing SMS Token input page");
            sendSMS(getMobileNumber());
            smsTokenSent = true;
            m_Request.setAttribute(NIDPConstants.ATTR_URL, (getReturnURL() != null ? getReturnURL() : m_Request.getRequestURL().toString()));
            showJSP("token");
            return SHOW_JSP;
        }


        if (smsTokenSent) {
            log.info("Validating SMS Token");
            String vToken = m_Request.getParameter("Response");
            if (vToken == null || vToken.length() == 0) {
                NIDPError nIDPError = new NIDPError(getUserErrorMsg(), getUserErrorMsg(), Locale.US);
                showError(nIDPError);
                return NOT_AUTHENTICATED;
            }
            if (vToken.equals(smsToken)) {
                log.info("SMSToken Authentication Success");
                return AUTHENTICATED;
            }
            m_Request.setAttribute(NIDPConstants.ATTR_LOGIN_ERROR, "SMS Login Failed");
            log.info("Token validation failed");
            return NOT_AUTHENTICATED;
        }

        return NOT_AUTHENTICATED;
    }

    private NIDPPrincipal getUserPrincipal() {
        NIDPPrincipal nIDPPrincipal = (NIDPPrincipal) m_Properties.get("Principal");

        if (nIDPPrincipal == null) {

            if (sessionUser != null) {
                if (m_Session.isAuthenticated()) {
                    NIDPPrincipal[] arrnIDPPrincipal = m_Session.getSubject().getPrincipals();
                    if (arrnIDPPrincipal.length == 1) {
                        nIDPPrincipal = arrnIDPPrincipal[0];
                        log.info("Found Session Authenticated User: {}", nIDPPrincipal.getUserIdentifier());
                    }
                }
                if (nIDPPrincipal == null) {
                    log.info("No Session Authenticated User Found");
                }
            }
        } else {
            log.info("Found Contract Authenticated User: {}" + nIDPPrincipal.getUserIdentifier());
        }
        return nIDPPrincipal;
    }

    private String generateToken() {
        if (charsToken == null || charsToken.length() == 0)
            charsToken = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        if (lengthToken == null || lengthToken.length() == 0)
            lengthToken = "6";
        int lengthInt = Integer.valueOf(lengthToken).intValue() + 1;
        int i;
        String token = "";
        for (i = 1; i < lengthInt; i++) {
            int index = (int) (charsToken.length() * Math.random());
            token = token + charsToken.charAt(index);
        }
        return token;
    }

    private String getMobileNumber() {
        if (phoneAttr == null || phoneAttr.length() == 0)
            phoneAttr = "mobile";

        String[] qAttr = {phoneAttr};
        String number = null;
        Attributes vAttr = getPrincipalAttributes(qAttr);
        int vCount = vAttr.size();
        if (vCount != 1) {
            setErrorMsg(NIDPConstants.ATTR_LOGIN_ERROR, "Phone number error");
            log.info("Phone number error");
        }
        if (vCount == 1) {
            Attribute vAttr2 = vAttr.get(phoneAttr);
            String vAttr3 = vAttr2.toString();
            int subCount = phoneAttr.length() + 2;
            number = vAttr3.substring(subCount);
        }
        return number;
    }

    private int sendSMS(String phoneNumber) {
        int result = 0;
        if (gwSuccess != null)
            result = 2;
        int resultSuccess = 0;
        int resultError = 0;
        try {
            String data = gwUserParameter + "&" + gwPasswdParameter + "&" + gwDestName + "=" + phoneNumber + "&" + gwMessageName + "=" + smsToken + "&" + gwExtraParameter + "&" + gwExtraParameter2;
            log.info("SMS gateway request: {}?{} ", gwURL, data);
            URL url = new URL(gwURL);
            URLConnection urlconn = url.openConnection();
            urlconn.setDoOutput(true);
            OutputStreamWriter swr = new OutputStreamWriter(urlconn.getOutputStream());
            swr.write(data);
            swr.flush();
            BufferedReader brd = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
            String line;
            while ((line = brd.readLine()) != null) {
                log.info("SMS gateway output: {}", line);
                if (gwError != null && line.indexOf(gwError) >= 0)
                    resultError = 1;

                if (gwSuccess != null && line.indexOf(gwSuccess) >= 0)
                    resultSuccess = 1;
            }
            swr.close();
            brd.close();
        } catch (Exception e) {
            log.info("SMS gateway exception: {}", e);
            result = 3;
        }

        if (resultError == 1)
            result = 1;
        if (resultSuccess == 1)
            result = 0;

        if (result == 1)
            m_Request.setAttribute(NIDPConstants.ATTR_LOGIN_ERROR, "SMS Gateway Error (Error Found)");
        if (result == 2)
            m_Request.setAttribute(NIDPConstants.ATTR_LOGIN_ERROR, "SMS Gateway Error (Success Not Found)");
        if (result == 3)
            m_Request.setAttribute(NIDPConstants.ATTR_LOGIN_ERROR, "Method Configuration Error");
        return result;
    }
}
