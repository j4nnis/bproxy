package org.ipinfodb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * A simple Java client for the IpInfoDb API.
 * <br>
 * License: Free for use in any way.
 * <br>
 * <br>
 * This example is dependent on the following libraries:
 * <pre>
 * commons-logging-1.1.1.jar
 * httpclient-4.2.jar
 * httpcore-4.2.jar
 * jackson-annotations-2.1.0.jar
 * jackson-core-2.1.0.jar
 * jackson-databind-2.1.0.jar
 * </pre>
 * <br>
 *
 * Created with IntelliJ IDEA.
 * User: MosheElisha
 */
public class IpInfoDbClient {

    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        // Add a handler to handle unknown properties (in case the API adds new properties to the response)
        MAPPER.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext context, JsonParser jp, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                // Do not fail - just log
                String className = (beanOrClass instanceof Class) ? ((Class) beanOrClass).getName() : beanOrClass.getClass().getName();
                System.out.println("Unknown property while de-serializing: " + className + "." + propertyName);
                context.getParser().skipChildren();
                return true;
            }
        });
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 3) {
            System.out.println("Usage: org.ipinfodb.IpInfoDbClient MODE API_KEY [IP_ADDRESS]\n"
                    + "MODE can be either 'ip-country' or 'ip-city'.\n"
                    + "If you don't have an API_KEY yet, you can get one for free by registering at http://www.ipinfodb.com/register.php."
            );
            return;
        }
        String mode = args[0];
        String apiKey = args[1];

        String url = "http://api.ipinfodb.com/v3/" + mode + "/?format=json&key=" + apiKey;
        if (args.length > 2) {
            String ip = args[2];
            url += "&ip=" + ip;
        }

        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = HTTP_CLIENT.execute(request, new BasicHttpContext());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("IpInfoDb response is " + response.getStatusLine());
            }

            String responseBody = EntityUtils.toString(response.getEntity());
            IpCityResponse ipCityResponse = MAPPER.readValue(responseBody, IpCityResponse.class);
            if ("OK".equals(ipCityResponse.getStatusCode())) {
                System.out.println(ipCityResponse.getCountryCode() + ", " + ipCityResponse.getRegionName() + ", " + ipCityResponse.getCityName());
            } else {
                System.out.println("API status message is '" + ipCityResponse.getStatusMessage() + "'");
            }
        } finally {
            HTTP_CLIENT.getConnectionManager().shutdown();
        }
    }

    /**
     * <pre>
     * Example request:
     * http://api.ipinfodb.com/v3/ip-city/?format=json&key=API_KEY&ip=IP_ADDRESS
     *
     * Example response:
     * {
     * 	"statusCode" : "OK",
     * 	"statusMessage" : "",
     * 	"ipAddress" : "74.125.45.100",
     * 	"countryCode" : "US",
     * 	"countryName" : "UNITED STATES",
     * 	"regionName" : "CALIFORNIA",
     * 	"cityName" : "MOUNTAIN VIEW",
     * 	"zipCode" : "94043",
     * 	"latitude" : "37.3861",
     * 	"longitude" : "-122.084",
     * 	"timeZone" : "-07:00"
     * }
     * </pre>
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class IpCityResponse {

        private String statusCode;
        private String statusMessage;
        private String ipAddress;
        private String countryCode;
        private String countryName;
        private String regionName;
        private String cityName;
        private String zipCode;
        private String latitude;
        private String longitude;
        private String timeZone;

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        @Override
        public String toString() {
            return "";
        }
    }

}
