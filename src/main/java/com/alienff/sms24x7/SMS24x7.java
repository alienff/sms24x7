package com.alienff.sms24x7;

import java.io.IOException;

/**
 * <p>
 *     Simple api implementation for <a href="http://sms24x7.ru/">SMS 24x7 service</a>.
 *     Look for <a href="https://outbox.sms24x7.ru/api_manual/">API documentation</a> on provider's website.
 * </p>
 *
 * Created 2/16/13 9:24 PM
 * @author mike
 *
 * @see <a href="http://sms24x7.ru/">http://sms24x7.ru/</a>
 */
public interface SMS24x7 {
    /**
     * Single method to send
     * @param email       Username from sms24x7 service
     * @param password    Password from sms24x7 service
     * @param from        Sender name. Less or equal to 11 latin symbols (GSM_0338 exactly)
     * @param to          Recipient phone number in international format. E.g. 79991234567
     * @param text        Text of the message
     * @return Server response body
     * @throws IOException if something bad happened with internet connection
     */
    String send(String email, String password, String from, String to, String text) throws IOException;
}
