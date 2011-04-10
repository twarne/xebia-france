package org.springframework.webflow.samples.booking;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.payment.common.account.PaymentCard;
import org.springframework.payment.common.money.MonetaryAmount;
import org.springframework.payment.common.order.Order;
import org.springframework.payment.core.GatewayClientException;
import org.springframework.payment.core.InvalidCardException;
import org.springframework.payment.core.PaymentTransactionException;
import org.springframework.payment.creditcard.CreditCardService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import fr.xebia.ws.travel.antifraud.v1_0.AntiFraudService;
import fr.xebia.ws.travel.antifraud.v1_0.CreditCard;
import fr.xebia.ws.travel.antifraud.v1_0.CreditCardType;
import fr.xebia.ws.travel.antifraud.v1_0.SuspiciousBookingException;

@ManagedResource
@Component
public class BookingAction extends MultiAction {

    private Logger logger = LoggerFactory.getLogger(BookingAction.class);

    private CreditCardService creditCardService;

    private AntiFraudService antiFraudService;

    private boolean enableAntiFraudService;

    private Map<String, CreditCardType> creditCardTypeByName = new HashMap<String, CreditCardType>();

    @Autowired
    public BookingAction(@Qualifier("CreditCardService") CreditCardService creditCardService, AntiFraudService antiFraudService) {
        this.creditCardService = creditCardService;
        this.antiFraudService = antiFraudService;

        creditCardTypeByName.put("Visa", CreditCardType.VISA);
        creditCardTypeByName.put("MasterCard", CreditCardType.MASTER_CARD);
        creditCardTypeByName.put("American Express", CreditCardType.AMERICAN_EXPRESS);
        creditCardTypeByName.put("Discover", CreditCardType.DISCOVER);
        creditCardTypeByName.put("Diners Club", CreditCardType.DINERS_CLUB);
        creditCardTypeByName.put("Carte Blanche", CreditCardType.CARTE_BLANCHE);
        creditCardTypeByName.put("JCB", CreditCardType.JCB);

    }

    public Event submitPayment(RequestContext context, Booking booking) throws Exception {

        try {
            if (enableAntiFraudService) {
                Order order = booking.createOrder();
                fr.xebia.ws.travel.antifraud.v1_0.Booking antiFraudBooking = new fr.xebia.ws.travel.antifraud.v1_0.Booking();
                antiFraudBooking.setBeds(booking.getBeds());
                CreditCard creditCard = new CreditCard();
                PaymentCard paymentCard = order.getPaymentMethod().getPaymentCard();
                creditCard.setExpirationMonth(paymentCard.getExpiration().getMonth());
                creditCard.setExpirationYear(paymentCard.getExpiration().getYear());
                creditCard.setHolderName(paymentCard.getCardName());
                creditCard.setNumber(paymentCard.getNumber());
                creditCard.setType(creditCardTypeByName.get(paymentCard.getCardType().getType()));
                antiFraudBooking.setCreditCard(creditCard);
                antiFraudBooking.setHotel(booking.getHotel().getName());

                try {
                    antiFraudService.checkBooking(antiFraudBooking);
                } catch (SuspiciousBookingException e) {
                    return processException(context, e,
                            "Suspicious order.");

                }
            }

            creditCardService.purchase(new MonetaryAmount(booking.getTotal(), Currency.getInstance("USD")),
                    booking.createOrder(), booking.getId().toString());

            return success();

        } catch (InvalidCardException e) {
            return processException(context, e,
                    "The transaction failed using the credit card information you provided.");

        } catch (PaymentTransactionException e) {
            return processException(context, e, "You payment was not processed. Please contact customer service.");

        } catch (GatewayClientException e) {
            return processException(context, e, "Transaction failed at this time due to a payment gateway error.");

        } catch (Throwable t) {
            return processException(context, t, "Unexpected error. Please contact customer service.");
        }

    }

    private Event processException(RequestContext context, Throwable t, String message) {
        HttpServletResponse response = (HttpServletResponse) ((ServletExternalContext) ExternalContextHolder.getExternalContext())
                .getNativeResponse();
        if (!response.isCommitted()) {
            response.setHeader("exception.type", ObjectUtils.nullSafeClassName(t));
        }
        logger.error(t.getMessage(), t);
        context.getMessageContext().addMessage(new MessageBuilder().error().defaultText(message).build());
        return error();
    }

    @ManagedAttribute
    public boolean isEnableAntiFraudService() {
        return enableAntiFraudService;
    }

    @ManagedAttribute
    public void setEnableAntiFraudService(boolean enableAntiFraudService) {
        this.enableAntiFraudService = enableAntiFraudService;
    }
}
