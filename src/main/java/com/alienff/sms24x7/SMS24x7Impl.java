package com.alienff.sms24x7;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.SM;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.alienff.sms24x7.Util.encode;

/**
 * <p>
 *     Simple api implementation for <a href="http://sms24x7.ru/">SMS 24x7 service</a>.
 *     Not thread safe.
 * </p>
 *
 * Created 2/16/13 1:13 AM
 * @author mike
 */
public class SMS24x7Impl implements SMS24x7 {
    private static final Logger log = LoggerFactory.getLogger(SMS24x7Impl.class);
    private static final String API_URL = "https://api.sms24x7.ru/";

    private final HttpClient HTTP_CLIENT;

    /**
     * http://sms24x7.ru/ uses utf-8 for its responses but do not send proper http headers. That's why we specify encoding explicitly.
     */
    private static final BasicResponseHandler HANDLER = new BasicResponseHandler("utf-8");

    private String sid;

    {
        final PoolingClientConnectionManager ccm = new PoolingClientConnectionManager();
        ccm.setMaxTotal(1);
        HTTP_CLIENT = new DefaultHttpClient(ccm);
    }

    /**
     * Standalone version for {@link SMS24x7Impl#send} method
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        log.debug("Starting SMS24X7");
        if (args != null && args.length == 5) {
            final SMS24x7Impl sms = new SMS24x7Impl();
            sms.send(args[0], args[1], args[2], args[3], args[4]);
        } else {
            log.warn("Usage: SendSMS login password from to message_text");
        }
    }

    /**
     * Send SMS immediately. Use on-fly authentication. I.e. credentials will be send in the same request as sms text. Sends only one request.
     * @param email       Username from sms24x7 service
     * @param password    Password from sms24x7 service
     * @param from        Sender name. Less or equal to 11 latin symbols (GSM_0338 exactly)
     * @param to          Recipient phone number in international format. E.g. 79991234567
     * @param text        Text of the message
     * @return Server response body
     * @throws IOException if something bad happened with internet connection
     */
    @Override
    public String send(String email, String password, String from, String to, String text) throws IOException {
        log.info("Sending sms from " + from + " to " + to + " using on-fly authentication");
        final HttpGet get;
        final String result;

        email = encode(email);
        password = encode(password);
        text = encode(text);
        to = encode(to);
        from = encode(from);
        get = new HttpGet(API_URL + "?method=push_msg&email=" + email + "&password=" + password + "&text=" + text + "&phone=" + to + "&sender_name=" + from);
        result = HTTP_CLIENT.execute(get, HANDLER);
        log.trace(result);
        return result;
    }

    @Override
    public String login(String email, String password) throws IOException {
        log.debug("Logging to sms24x7 service");
        final HttpGet get;
        final HttpContext context;
        final CookieStore cookieStore;
        final String result;

        email = encode(email);
        password = encode(password);
        get = new HttpGet(API_URL + "?method=login&email=" + email + "&password=" + password);
        context = new BasicHttpContext();
        cookieStore = new BasicCookieStore();

        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        result = HTTP_CLIENT.execute(get, HANDLER, context);
        sid = cookieStore.getCookies().get(0).getValue();
        log.debug("Cookie got: " + Util.maskCookie(sid));
        log.trace(result);
        return result;
    }

    @Override
    public String send(String from, String to, String text) throws IOException {
        log.info("Sending message using previously logged in session");
        final HttpGet get;
        final String result;

        text = encode(text);
        to = encode(to);
        from = encode(from);
        get = new HttpGet(API_URL + "?method=push_msg&text=" + text + "&phone=" + to + "&sender_name=" + from);
        get.setHeader(SM.COOKIE, "sid=" + sid);

        result = HTTP_CLIENT.execute(get, HANDLER);
        log.trace(result);
        return result;
    }

    @Override
    public String logout() throws IOException {
        log.debug("Logging out from sms24x7 service");
        final HttpGet get = new HttpGet(API_URL + "?method=logout");
        final String result;
        get.setHeader(SM.COOKIE, "sid=" + sid);
        sid = null;
        result = HTTP_CLIENT.execute(get, HANDLER);
        log.trace(result);
        return result;
    }
}
