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
    private String gatewayError;

    public void validateConfiguration() {

        if (gatewayURL == null || gatewayURL.isEmpty()) {
            throw new SMSGatewayConfigurationException("Missing gateway url");
        }


        if (gatewayUserParameter == null || gatewayUserParameter.isEmpty()) {
            throw new SMSGatewayConfigurationException("Missing user parameter");
        }

        if (gatewayPasswordParameter == null || gatewayPasswordParameter.isEmpty()) {
            throw new SMSGatewayConfigurationException("Missing password parameter");

        }

        if (gatewayDestName == null || gatewayDestName.isEmpty()) {
            throw new SMSGatewayConfigurationException("Missing destination parameter");
        }

        if (gatewayMessageName == null || gatewayMessageName.isEmpty()) {
            throw new SMSGatewayConfigurationException("Missing message parameter");
        }

        if (gatewaySuccess == null || gatewaySuccess.isEmpty()) {
            throw new SMSGatewayConfigurationException("Missing success parameter");
        }
        if (gatewayError == null || gatewayError.isEmpty()) {
            throw new SMSGatewayConfigurationException("Missing missing error parameter");
        }
    }

    public boolean hasExtraParameter1() {
        return (gatewayExtraParameter1 != null && !gatewayExtraParameter1.isEmpty());
    }

    public boolean hasExtraParameter2() {
        return (gatewayExtraParameter2 != null && !gatewayExtraParameter2.isEmpty());
    }
}
