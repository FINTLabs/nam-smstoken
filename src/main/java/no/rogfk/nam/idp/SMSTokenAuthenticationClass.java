package no.rogfk.nam.idp;

import com.novell.nidp.NIDPConstants;
import com.novell.nidp.NIDPError;
import com.novell.nidp.NIDPPrincipal;
import com.novell.nidp.authentication.AuthnConstants;
import com.novell.nidp.authentication.local.LocalAuthenticationClass;
import com.novell.nidp.common.authority.UserAuthority;
import lombok.extern.slf4j.Slf4j;
import no.rogfk.nam.idp.exceptions.MissingMobileNumberException;
import no.rogfk.nam.idp.exceptions.MobileNumberException;
import no.rogfk.nam.idp.smsgateway.Config;
import no.rogfk.nam.idp.smsgateway.SMSGateway;
import no.rogfk.nam.idp.smsgateway.exceptions.SMSGatewayException;
import no.rogfk.nam.idp.utils.Mobile;
import no.rogfk.nam.idp.utils.Token;
import no.rogfk.nam.idp.utils.Tracer;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;


@Slf4j
public class SMSTokenAuthenticationClass extends LocalAuthenticationClass {

    private boolean allowSessionUser;

    protected boolean smsTokenSent;
    protected String smsToken;

    private String phoneAttribute;
    private String charsToken;
    private String lengthToken;
    private String missingMobileMessage;
    private Tracer tracer;
    private SMSGateway smsGateway;


    public SMSTokenAuthenticationClass(Properties properties, ArrayList<UserAuthority> arrayList) {
        super(properties, arrayList);
        smsTokenSent = false;

        allowSessionUser = Boolean.valueOf(getProperty(SMSTokenConstants.PROP_NAME_ALLOW_SESSION_USER));
        phoneAttribute = getProperty(SMSTokenConstants.PROP_NAME_MOBILE_PHONE_ATTRIBUTE);
        charsToken = getProperty(SMSTokenConstants.PROP_NAME_TOKEN_CHARACTERS);
        lengthToken = getProperty(SMSTokenConstants.PROP_NAME_TOKEN_LENGTH);
        missingMobileMessage = getProperty(SMSTokenConstants.PROP_NAME_MISSING_MOBILE_MESSAGE);

        tracer = new Tracer(Boolean.valueOf(getProperty(SMSTokenConstants.PROP_NAME_TRACE)));

        Config smsConfig = new Config();
        smsConfig.setGatewayDestName(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_DESTINATION_NAME));
        smsConfig.setGatewayError(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_ERROR));
        smsConfig.setGatewaySuccess(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_SUCCESS));
        smsConfig.setGatewayMessageName(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_MESSAGE_NAME));
        smsConfig.setGatewayURL(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_URL));
        smsConfig.setGatewayPasswordParameter(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_PASSWORD_PARAMETER));
        smsConfig.setGatewayUserParameter(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_USER_PARAMETER));
        smsConfig.setGatewayExtraParameter1(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_EXTRA_PARAMETER1));
        smsConfig.setGatewayExtraParameter2(getProperty(SMSTokenConstants.PROP_NAME_GATEWAY_EXTRA_PARAMETER2));

        smsGateway = new SMSGateway(smsConfig, tracer);

        tracer.trace("Greetings from Rogaland fylkeskommune, IKT- og arkivavdelingen!");
    }

    SMSTokenAuthenticationClass(Properties properties, SMSGateway smsGateway) {
        super(properties, new ArrayList<>());
        tracer = new Tracer(true);
        this.smsGateway = smsGateway;
    }

    protected void init() {
    }


    public String getType() {
        return AuthnConstants.OTHER;
    }

    @Override
    public int doAuthenticate() {
        tracer.trace("SMSTokenAuthenticationClass: doAuthenticate()");

        if (validAuthentication()) {
            if (smsTokenSent) {
                return runTokenValidation();
            } else {
                return runInitialPhase();
            }
        }

        return NOT_AUTHENTICATED;
    }

    private int runTokenValidation() {
        tracer.trace("Validating SMS Token");
        String token = m_Request.getParameter("Response");

        if (Token.validateToken(token, smsToken)) {
            tracer.trace("SMS Token is valid");
            tracer.trace("SMSTokenAuthenticationClass Authentication Success");
            return AUTHENTICATED;
        } else {
            return showLoginError("Validating SMS Token failed");
        }
    }

    private int runInitialPhase() {
        tracer.trace("Sending SMS Token");

        smsToken = Token.getToken(charsToken, lengthToken);
        tracer.trace("Token: ", smsToken);

        try {
            log.info("Showing SMS Token input page");
            String[] mobilePhoneAttribute = Mobile.validateMobileNumberAttribute(phoneAttribute);


            String mobile = Mobile.getMobile(getPrincipalAttributes(mobilePhoneAttribute));
            tracer.trace("Mobile:", mobile);

            smsGateway.sendSMS(mobile, smsToken);

            return showInitialTokenJSP();

        } catch (MissingMobileNumberException e) {
            tracer.trace(e.getMessage());
            return showLoginError(String.format("%s. %s.", e.getMessage(), missingMobileMessage));
        } catch (MobileNumberException | SMSGatewayException e) {
            tracer.trace(e.getMessage());
            return showLoginError(e.getMessage());
        }
    }

    private boolean validAuthentication() {

        NIDPPrincipal nidpPrincipal = getUserPrincipal();

        if (nidpPrincipal == null) {
            setUserErrorMsg("No Authenticated User Found");
            tracer.trace("No Authenticated User Found");
            try {
                NIDPError nIDPError = new NIDPError(getUserErrorMsg(), getUserErrorMsg(), Locale.US);
                showError(nIDPError);
            } catch (NullPointerException ignore) { // added to be able to run unit test
            }

            return false;
        }

        setPrincipal(nidpPrincipal);

        return true;
    }

    private int showInitialTokenJSP() {
        smsTokenSent = true;
        m_Request.setAttribute(NIDPConstants.ATTR_URL, (getReturnURL() != null ? getReturnURL() : m_Request.getRequestURL().toString()));
        showJSP("token");
        return SHOW_JSP;
    }

    private NIDPPrincipal getUserPrincipal() {
        NIDPPrincipal idpPrincipal = getContractUser();

        if (idpPrincipal == null) {
            idpPrincipal = getSessionUser();
        }

        return idpPrincipal;
    }

    private NIDPPrincipal getContractUser() {
        NIDPPrincipal contractUser = (NIDPPrincipal) m_Properties.get("Principal");

        if (contractUser != null) {
            tracer.trace("Found contract authenticated user: ", contractUser.getUserIdentifier());
        }

        return contractUser;
    }

    private NIDPPrincipal getSessionUser() {

        if (allowSessionUser && m_Session.isAuthenticated()) {
            NIDPPrincipal[] idpPrincipalList = m_Session.getSubject().getPrincipals();
            if (idpPrincipalList.length == 1) {
                NIDPPrincipal sessionPricipal = idpPrincipalList[0];
                tracer.trace("Found session authenticated user: ", sessionPricipal.getUserIdentifier());
                return sessionPricipal;
            }
        }

        return null;
    }

    private int showLoginError(String message) {
        m_Request.setAttribute(NIDPConstants.ATTR_URL, (getReturnURL() != null ? getReturnURL() : m_Request.getRequestURL().toString()));
        m_Request.setAttribute(NIDPConstants.ATTR_LOGIN_ERROR, message);
        showJSP("token");
        tracer.trace(message);
        return SHOW_JSP;

    }


}
