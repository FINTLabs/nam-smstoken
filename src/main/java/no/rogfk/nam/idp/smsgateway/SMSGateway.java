package no.rogfk.nam.idp.smsgateway;

import no.rogfk.nam.idp.smsgateway.exceptions.SMSGatewaySystemException;
import no.rogfk.nam.idp.smsgateway.exceptions.SMSGatewayUnableToSendException;
import no.rogfk.nam.idp.smsgateway.exceptions.SMSGatewayUnkownResponseException;
import no.rogfk.nam.idp.utils.Tracer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class SMSGateway {

    private Config config;
    private Tracer tracer;

    public SMSGateway(Config config, Tracer tracer) {

        this.config = config;
        config.validateConfiguration();

        this.tracer = tracer;
    }

    public void sendSMS(String phoneNumber, String token) {

        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
            String data = getData(phoneNumber, token);
            tracer.trace("SMS gateway request: ", config.getGatewayURL(), data);
            URL url = new URL(config.getGatewayURL());
            URLConnection urlconn = url.openConnection();
            urlconn.setDoOutput(true);
            writer = new OutputStreamWriter(urlconn.getOutputStream());
            writer.write(data);
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
            String line;
            boolean containsSuccess = false;
            boolean containsError = false;
            while ((line = reader.readLine()) != null) {

                tracer.trace("SMS gateway output: ", line);
                if (!containsError && line.contains(config.getGatewayError())) {
                    containsError = true;
                }
                if (!containsSuccess && line.contains(config.getGatewaySuccess())) {
                    containsSuccess = true;
                }

            }

            if (containsError) {
                throw new SMSGatewayUnableToSendException("The SMS gateway could not send the SMS");
            } else if (containsSuccess) {
                tracer.trace("SMS successfully sent");
            } else {
                throw new SMSGatewayUnkownResponseException("The reponse from the gateway is uknown");
            }
        } catch (Exception e) {
            tracer.trace(e.getMessage());
            throw new SMSGatewaySystemException(e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }

        }
    }

    private String getData(String phoneNumber, String smsToken) {

        StringBuilder data = new StringBuilder().append(config.getGatewayUserParameter())
                .append("&").append(config.getGatewayPasswordParameter())
                .append("&").append(config.getGatewayDestName())
                .append("=").append(phoneNumber)
                .append("&").append(config.getGatewayMessageName())
                .append("=").append(smsToken);

        if (config.hasExtraParameter1()) {
            data.append("&").append(config.getGatewayExtraParameter1());

        }

        if (config.hasExtraParameter2()) {
            data.append("&").append(config.getGatewayExtraParameter2());
        }

        return data.toString();
    }


}
