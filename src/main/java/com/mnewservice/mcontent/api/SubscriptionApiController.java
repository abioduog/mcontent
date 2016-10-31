package com.mnewservice.mcontent.api;

import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.manager.ServiceManager;
import com.mnewservice.mcontent.manager.SubscriptionManager;
import com.mnewservice.mcontent.util.DateUtils;
import com.mnewservice.mcontent.util.DeliveryTimeUtils;
import com.mnewservice.mcontent.util.ValidationUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@RestController
public class SubscriptionApiController extends AbstractApiController {

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    private ServiceManager serviceManager;

    private static final Logger LOG
            = Logger.getLogger(SubscriptionApiController.class);

    private static final String PARAM_MESSAGE = "MESSAGE";
    private static final String PARAM_SHORTCODE = "SHORTCODE";
    private static final String PARAM_SENDER = "SENDER";
    private static final String PARAM_MESSAGEID = "MESSAGEID";
    private static final String PARAM_OPERATOR = "OPERATOR";
    private static final String PARAM_TIMESTAMP = "TIMESTAMP";

    private static final String ERROR_SERVICE_NOT_FOUND
            = "Service was not found for the given parameters: "
            + "%s=%s, %s=%s, and %s=%s";

    @RequestMapping(method = RequestMethod.GET, value = "/subscription")
    @ResponseBody
    public String subscription(
            @RequestParam(value = PARAM_MESSAGE) String message,
            @RequestParam(value = PARAM_SHORTCODE) int shortCode,
            @RequestParam(value = PARAM_SENDER) String sender,
            @RequestParam(value = PARAM_MESSAGEID) String messageId,
            @RequestParam(value = PARAM_OPERATOR) String operator,
            @RequestParam(value = PARAM_TIMESTAMP) String timestamp) {
        Service service = null;
                Subscription subscription = null;
                        boolean returnValue;
try{
        validateRequestParams(
                message, shortCode, sender, messageId, operator, timestamp);

         service = getService(message, shortCode, operator);

         subscription
                = createSubscription(
                        service, message, shortCode, sender,
                        messageId, operator, timestamp
                );
}catch(IllegalArgumentException msg){
        System.out.println(msg + ", " + sender);
    subscriptionManager.sendServiceNotFoundMessage(shortCode, msg.getMessage(), sender);
    return RETURN_VALUE_UNSUCCESSFUL;
}

        if (isRegisteringSubscription(service, message)) {
            returnValue = subscriptionManager.registerSubscription(subscription);
        } else {
            returnValue = subscriptionManager.unRegisterSubscription(subscription);
        }

        return (returnValue) ? RETURN_VALUE_SUCCESSFUL : RETURN_VALUE_UNSUCCESSFUL;
    }

    private Service getService(String message, int shortCode, String operator) {
        Service service;
        try {
            service = getSubscribeService(message, shortCode, operator);
        } catch (IllegalArgumentException iae) {
            LOG.debug("Tried to find service for subscribing: " + iae.getMessage());
            service = null;
        }
        if (service == null) {
            LOG.debug("Trying to find service for unsubscribing");
            service = getUnsubscribeService(message, shortCode, operator);
        }
        return service;
    }

    private void validateRequestParams(String message, int shortCode,
            String sender, String messageId, String operator,
            String timestamp) throws IllegalArgumentException {
        LOG.debug(PARAM_MESSAGE + "=" + message);
        ValidationUtils.validateNotNullOrEmpty(PARAM_MESSAGE, message);

        LOG.debug(PARAM_SHORTCODE + "=" + shortCode);
        ValidationUtils.validatePositive(PARAM_SHORTCODE, shortCode);

        LOG.debug(PARAM_SENDER + "=" + sender);
        ValidationUtils.validateNumeric(PARAM_SENDER, sender);

        //We don't need to validate something that can be anything
//        LOG.debug(PARAM_MESSAGEID + "=" + messageId);
//        ValidationUtils.validateId(PARAM_MESSAGEID, messageId);

        LOG.debug(PARAM_OPERATOR + "=" + operator);
        ValidationUtils.validateNotNullOrEmpty(PARAM_OPERATOR, operator);
/*
        In reality there are multiple timestamp formats, but since we just store it as string and never use, we don't need to validate them
        EMTS, Airtel, Glo: 2016-02-05 15:21:50
        MTN: 2016-02-05_3:43:18

        LOG.debug(PARAM_TIMESTAMP + "=" + timestamp);
        ValidationUtils.validateTimestamp(
                ValidationUtils.FORMAT_MMDDYYYY_HHMM_AA,
                PARAM_TIMESTAMP,
                timestamp);
                */
    }

    private Subscription createSubscription(Service service, String message,
            int shortCode, String sender, String messageId, String operator,
            String timestamp) {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(createSubscriber(sender));
        subscription.setService(service);
        subscription.setPeriods(new ArrayList<>());
        subscription.getPeriods().add(
                createSubscriptionPeriod(
                        service, message, shortCode, operator,
                        timestamp, messageId, sender)
        );

        return subscription;
    }

    private Service getSubscribeService(String subscribeMessage,
            int shortCode, String operator) throws IllegalArgumentException {
        Service service
                = serviceManager.getService(
                        subscribeMessage, shortCode, operator
                );

        if (service == null) {
            String msg = String.format(
                    ERROR_SERVICE_NOT_FOUND,
                    PARAM_MESSAGE,
                    subscribeMessage,
                    PARAM_SHORTCODE,
                    shortCode,
                    PARAM_OPERATOR,
                    operator);
            throw new IllegalArgumentException(msg);
        }

        return service;
    }

    private Service getUnsubscribeService(String unSubscribeMessage,
            int shortCode, String operator) {
        Service service
                = serviceManager.getUnsubscribeService(
                        unSubscribeMessage, shortCode, operator
                );

        if (service == null) {
            String msg = String.format(
                    ERROR_SERVICE_NOT_FOUND,
                    PARAM_MESSAGE,
                    unSubscribeMessage,
                    PARAM_SHORTCODE,
                    shortCode,
                    PARAM_OPERATOR,
                    operator);
            throw new IllegalArgumentException(msg);
        }

        return service;
    }

    private SubscriptionPeriod createSubscriptionPeriod(
            Service service, String message, int shortCode, String operator,
            String timestamp, String messageId, String sender) {
        SubscriptionPeriod period = new SubscriptionPeriod();
        period.setStart(getDeliveryStartDate(service.getDeliveryTime()));
        period.setEnd(
                DateUtils.addDays(
                        period.getStart(),
                        service.getSubscriptionPeriod()
                )
        );
        period.setMessage(message);
        period.setShortCode(shortCode);
        period.setOperator(operator);
        period.setOriginalTimeStamp(timestamp);
        period.setMessageId(messageId);
        period.setSender(sender);
        return period;
    }

    private Subscriber createSubscriber(String sender) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber(sender);

        Subscriber subscriber = new Subscriber();
        subscriber.setPhone(phoneNumber);

        return subscriber;
    }

    // Subscription starts from today if before delivery time
    // or tomorrow if after delivery time.
    private Date getDeliveryStartDate(DeliveryTime deliveryTime) {
        Calendar currentTime = DateUtils.getCurrentCalendar();
        Calendar todaysRunTime = getTodaysRunTime(deliveryTime);

        // TODO: should we use some kind of threshold in comparison?
        if (currentTime.compareTo(todaysRunTime) < 0) {
            return DateUtils.getCurrentCalendarAtMidnight().getTime();
        } else {
            // today's execution has been passed, next delivery will be tomorrow..
            Calendar cal = DateUtils.getCurrentCalendarAtMidnight();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            return cal.getTime();
        }
    }

    private Calendar getTodaysRunTime(DeliveryTime deliveryTime) {
        int[] parsedDeliveryTime
                = DeliveryTimeUtils.parseDeliveryTimeAsIntArray(deliveryTime);
        Calendar todaysRunTime = DateUtils.getCurrentCalendarAtMidnight();
        todaysRunTime.set(Calendar.HOUR_OF_DAY, parsedDeliveryTime[0]);
        todaysRunTime.set(Calendar.MINUTE, parsedDeliveryTime[1]);
        todaysRunTime.set(Calendar.SECOND, parsedDeliveryTime[2]);
        return todaysRunTime;
    }

    private boolean isRegisteringSubscription(Service service, String message) {
        return service.getKeyword().equalsIgnoreCase(message);
    }
}
