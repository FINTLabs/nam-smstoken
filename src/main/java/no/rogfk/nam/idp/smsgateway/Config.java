package no.rogfk.nam.idp.smsgateway;

import lombok.Data;
import no.rogfk.nam.idp.smsgateway.exceptions.SMSGatewayConfigurationException;

@Data
public class Config {
    private String gatewayURL;
    private String gatewayUserParameter;
    private String gatewayPasswordParameter;
    private String gatewayExtraParameter1;
    private String gatewayExtraParameter2;
    private String gatewayDestName;
    private String gatewayMessageName;
    private String gatewaySuccess;
    private boolean gatewaySuccessRegex;
    private String gatewayError;
    private boolean gatewayErrorRegex;

    public void validateConfiguration() {
        if (isEmpty(gatewayURL)) {
            throw new SMSGatewayConfigurationException("Missing gateway url");
        }

        if (isEmpty(gatewayUserParameter)) {
            throw new SMSGatewayConfigurationException("Missing user parameter");
        }

        if (isEmpty(gatewayPasswordParameter)) {
            throw new SMSGatewayConfigurationException("Missing password parameter");
        }

        if (isEmpty(gatewayDestName)) {
            throw new SMSGatewayConfigurationException("Missing destination parameter");
        }

        if (isEmpty(gatewayMessageName)) {
            throw new SMSGatewayConfigurationException("Missing message parameter");
        }

        if (isEmpty(gatewaySuccess)) {
            throw new SMSGatewayConfigurationException("Missing success parameter");
        }

        if (isEmpty(gatewayError)) {
            throw new SMSGatewayConfigurationException("Missing missing error parameter");
        }
    }

    public void setGatewaySuccessRegex(String gatewaySuccessRegex) {
        this.gatewaySuccessRegex = Boolean.valueOf(gatewaySuccessRegex);
    }

    public void setGatewayErrorRegex(String gatewayErrorRegex) {
        this.gatewayErrorRegex = Boolean.valueOf(gatewayErrorRegex);
    }

    boolean hasExtraParameter1() {
        return (gatewayExtraParameter1 != null && !gatewayExtraParameter1.isEmpty());
    }

    boolean hasExtraParameter2() {
        return (gatewayExtraParameter2 != null && !gatewayExtraParameter2.isEmpty());
    }

    boolean matchesGatewaySuccess(String line) {
        if (gatewaySuccessRegex) {
            return line.matches(gatewaySuccess);
        } else {
            return line.contains(gatewaySuccess);
        }
    }

    boolean matchesGatewayError(String line) {
        if (gatewayErrorRegex) {
            return line.matches(gatewayError);
        } else {
            return line.contains(gatewayError);
        }
    }

    private boolean isEmpty(String value) {
        return (value == null || value.isEmpty());
    }
}
