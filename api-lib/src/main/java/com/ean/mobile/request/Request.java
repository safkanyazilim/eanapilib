/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.ean.mobile.Constants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UriCreationException;

/**
 * The base class for all of the API requests that are implemented. Provides some easy-to use methods for performing
 * requests.
 */
public abstract class Request {
    private static final List<NameValuePair> BASIC_URL_PARAMETERS;

    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);

    private static final URI STANDARD_ENDPOINT;

    private static final URI SECURE_ENDPOINT;

    static {
        final String standardUriScheme = "http";
        final String standardUriHost = "api.ean.com";
        final String secureUriScheme = "https";
        final String secureUriHost = "book.api.ean.com";
        final String uriBasePath = "/ean-services/rs/hotel/v3/";

        URI standardUri = null;
        URI secureUri = null;
        try {
            standardUri = new URI(standardUriScheme, standardUriHost, uriBasePath, null, null);
            secureUri = new URI(secureUriScheme, secureUriHost, uriBasePath, null, null);
        } catch (URISyntaxException use) {
            // This exception can only be thrown if the static variables listed above are incorrectly
            // formatted, or the usage of new URI(...) is incorrect, both of which should be found out
            // long before the code is used in production, since the requests (particularly the int tests)
            // would fail.
            Log.wtf(Constants.DEBUG_TAG, "Base uri is malformed");
        }
        STANDARD_ENDPOINT = standardUri;
        SECURE_ENDPOINT = secureUri;

        //TODO: load CID, APIKey, and customerUserAgent from the classpath
        final String cid = "55505";
        final String apiKey = "cbrzfta369qwyrm9t5b8y8kf";
        final String customerUserAgent = "Android";

        final List<NameValuePair> urlParameters = Arrays.<NameValuePair>asList(
                new BasicNameValuePair("cid", cid),
                new BasicNameValuePair("apiKey", apiKey),
                new BasicNameValuePair("minorRev", "20"),
                new BasicNameValuePair("customerUserAgent", customerUserAgent)
        );
        BASIC_URL_PARAMETERS = Collections.unmodifiableList(urlParameters);
    }


    /**
     * Performs an api request at the specified path with the parameters listed.
     * @param relativePath The path on which to perform the request.
     * @param params The URI parameters to pass in the request.
     * @return The String representation of the JSON returned from the request.
     * @throws IOException If there is a network error or some other connection issue.
     */
    private static String performApiRequestForString(final String relativePath,
                                                     final List<NameValuePair> params) throws IOException {
        //TODO: refactor this to use java.net.HttpUrlConnection and javax.net.ssl.HttpsUrlConnection
        //Build the url
        final HttpRequestBase request;
        if ("res".equals(relativePath)) {
            request = new HttpPost(createFullUri(SECURE_ENDPOINT, relativePath, params));
        } else {
            request = new HttpGet(createFullUri(STANDARD_ENDPOINT, relativePath, params));
        }

        /****/
        /****/
        final String[] printbits = request.getURI().toString().split("\\?");
        System.out.print(printbits[0] + "?");
        if (printbits[0].endsWith("t")) {
            System.out.print("     ");
        } else if (printbits[0].endsWith("l")) {
            System.out.print("    ");
        }
        System.out.println(printbits[1]);
        /****/
        /****/

        Log.d(Constants.DEBUG_TAG, "request endpoint: " + request.getURI().getHost());
        final long startTime = System.currentTimeMillis();
        final HttpClient client = new EANAPIHttpClient();
        final HttpResponse response = client.execute(request);
        final StatusLine statusLine = response.getStatusLine();
        final String jsonString;
        try {
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                jsonString = EntityUtils.toString(response.getEntity());
            } else {
                jsonString = null;
                throw new IOException(statusLine.getReasonPhrase());
            }
        } finally {
            // Always close the connection.
            if (response.getEntity().isStreaming()) {
                response.getEntity().getContent().close();
            }
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            client.getConnectionManager().shutdown();
        }
        final long timeTaken = System.currentTimeMillis() - startTime;
        Log.d(Constants.DEBUG_TAG, "Took " + timeTaken + " milliseconds.");
        return jsonString;
    }

    /**
     * Performs an API request.
     * @param relativePath The relative path on which to perform the query.
     * @param params The URI parameters to attach to the URI. Used as parameters to the request.
     * @return The JSONObject that represents the content returned by the API
     * @throws IOException If there is a network issue, or the network stream cannot otherwise be read.
     * @throws JSONException If the response does not contain valid JSON
     * @throws EanWsError If the response contains an EanWsError element
     */
    protected static JSONObject performApiRequest(final String relativePath, final List<NameValuePair> params)
            throws IOException, JSONException, EanWsError {
        final JSONObject response = new JSONObject(performApiRequestForString(relativePath, params));
        if (response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }
        return response;
    }

    /**
     * Creates a full url based on the baseuri, the relative path and the uri parameters to pass.
     * @param baseUri The URI to use as the base.
     * @param relativePath The relative path from the base uri to finally request.
     * @param params The URI parameters to include in the query string.
     * @return The fully-formed URI based on the inputs.
     */
    protected static URI createFullUri(final URI baseUri,
                                       final String relativePath,
                                       final List<NameValuePair> params) {
        if (baseUri == null) {
            return null;
        }
        final URI relativeUri = relativePath == null ? baseUri : baseUri.resolve(relativePath);
        final String queryString = createQueryString(params);

        try {
            return new URI(relativeUri.getScheme(), relativeUri.getHost(), relativeUri.getPath(), queryString, null);
        } catch (URISyntaxException use) {
            throw new UriCreationException("Full URI could not be created for the request.", use);
        }
    }

    /**
     * Creates the query portion of a URI based on a list of NameValuePairs.
     * @param params The parameters to turn into a query string.
     * @return The requested string.
     */
    private static String createQueryString(final List<NameValuePair> params) {
        String queryString = null;
        if (params != null && !params.isEmpty()) {
            final StringBuilder sb = new StringBuilder(params.size() * 10);
            for (NameValuePair param : params) {
                if (param == null) {
                    continue;
                }
                sb.append(param.getName());
                sb.append("=");
                sb.append(param.getValue() == null ? "" : param.getValue());
                sb.append("&");
            }
            String potentialQueryString = sb.toString();
            if (potentialQueryString.length() == 0) {
                potentialQueryString = null;
            } else if (potentialQueryString.endsWith("&")) {
                potentialQueryString = potentialQueryString.substring(0, potentialQueryString.length() - 1);
            }
            // URLEncoder.encode cannot be used since it encodes things in a way the api does not expect.
            queryString = potentialQueryString;
        }
        return queryString;
    }

    /**
     * Gets the url parameters that will need to be present for every request.
     * @param locale The locale in which to request.
     * @param currencyCode The currency code in which to perform this request.
     * @param arrivalDate The arrival date for this request.
     * @param departureDate The departure date for this request.
     * @return The above parameters plus the cid, apikey, minor rev, and customer user agent url parameters.
     */
    public static List<NameValuePair> getBasicUrlParameters(final String locale,
                                                            final String currencyCode,
                                                            final LocalDate arrivalDate,
                                                            final LocalDate departureDate) {
        //TODO: force locale to be a java Locale object?
        final List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.addAll(BASIC_URL_PARAMETERS);
        if (locale != null) {
            params.add(new BasicNameValuePair("locale", locale));
        }
        if (currencyCode != null) {
            params.add(new BasicNameValuePair("currencyCode", currencyCode));
        }
        if (arrivalDate != null) {
            params.add(new BasicNameValuePair("arrivalDate", DATE_TIME_FORMATTER.print(arrivalDate)));
        }
        if (departureDate != null) {
            params.add(new BasicNameValuePair("departureDate", DATE_TIME_FORMATTER.print(departureDate)));
        }
        return params;
    }

    /**
     * Gets the url parameters that will need to be present for every request.
     * @param locale The locale in which to request.
     * @param currencyCode The currency code in which to perform this request.
     * @return The above parameters plus the cid, apikey, minor rev, and customer user agent url parameters.
     */
    public static List<NameValuePair> getBasicUrlParameters(final String locale,
                                                            final String currencyCode) {
        return getBasicUrlParameters(locale, currencyCode, null, null);
    }

    /**
     * Gets the url parameters that will need to be present for every request.
     * @return The above parameters plus the cid, apikey, minor rev, and customer user agent url parameters.
     */
    public static List<NameValuePair> getBasicUrlParameters() {
        return getBasicUrlParameters(null, null, null, null);
    }

    /**
     * An implementation of HTTPClient that transparently forces gzip encoding on the request and response, as well
     * as setst the Accept header to application/json.
     * Inspired by <a href="http://hc.apache.org/httpcomponents-client-ga
     * /httpclient/examples/org/apache/http/examples/client/ClientGZipContentCompression.java">
     *  apache http components</a>.
     */
    private static final class EANAPIHttpClient extends DefaultHttpClient {

        /**
         * The standard constructor for this client. Sets up the interceptors to enable the Accept-Encoding
         * and Accept headers to be set appropriately, if they are not set.
         */
        public EANAPIHttpClient() {
            this.addRequestInterceptor(new GzipRequestInterceptor());
            this.addResponseInterceptor(new GzipResponseInterceptor());
        }

        /**
         * Similar to the response interceptor below, and taken from the same place.
         */
        private static final class GzipRequestInterceptor implements HttpRequestInterceptor {

            /**
             * Implementation of
             * {@link HttpRequestInterceptor#process(org.apache.http.HttpRequest,
             * org.apache.http.protocol.HttpContext)}.
             * @param request The request that is being intercepted.
             * @param context The context into which the request is being sent.
             * @throws HttpException Can't happen in this implementation.
             * @throws IOException Can't happen in this implementation.
             */
            public void process(final HttpRequest request, final HttpContext context)
                    throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
                if (!request.containsHeader("Accept")) {
                    request.addHeader("Accept", "application/json, */*");
                }
            }
        }

        /**
         * Transparent interceptor taken from
         * <a href="http://hc.apache.org/httpcomponents-client-ga
         * /httpclient/examples/org/apache/http/examples/client/ClientGZipContentCompression.java">
         *  apache http components</a>.
         */
        private static final class GzipResponseInterceptor implements HttpResponseInterceptor {
            /**
             * Implementation of
             * {@link HttpResponseInterceptor#process(org.apache.http.HttpResponse,
             * org.apache.http.protocol.HttpContext)}.
             * @param response The response that is being intercepted.
             * @param context The context which this request/response is being interacted within.
             * @throws HttpException Can't happen in this implementation.
             * @throws IOException Can't happen in this implementation.
             */
            public void process(final HttpResponse response, final HttpContext context)
                    throws HttpException, IOException {
                // The idea here is to check the headers, and if they contain "gzip", then decompress
                // the data transparently.
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    final Header header = entity.getContentEncoding();
                    if (header != null) {
                        for (HeaderElement element : header.getElements()) {
                            if ("gzip".equalsIgnoreCase(element.getName())) {
                                response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}