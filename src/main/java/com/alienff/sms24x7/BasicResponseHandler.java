package com.alienff.sms24x7;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * <p>
 *     Almost copy of apache {@link org.apache.http.impl.client.BasicResponseHandler} except ability to explicitly set response encoding
 * </p>
 *
 * Created 3/14/13 9:15 PM
 * @author mike
 */
public class BasicResponseHandler implements ResponseHandler<String> {
    private final String encoding;

    /**
     * Force response encoding to utf-8
     */
    public BasicResponseHandler() {
        this.encoding = "utf-8";
    }

    /**
     * @param encoding    Force response encoding
     */
    public BasicResponseHandler(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Returns the response body as a String if the response was successful (a
     * 2xx status code). If no response body exists, this returns null. If the
     * response was unsuccessful (>= 300 status code), throws an
     * {@link org.apache.http.client.HttpResponseException}.
     */
    public String handleResponse(final HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                                            statusLine.getReasonPhrase());
        }
        return entity == null ? null : EntityUtils.toString(entity, encoding);
    }

}
