package org.springframework.webflow.samples.booking;

import java.util.Currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.payment.common.money.MonetaryAmount;
import org.springframework.payment.core.GatewayClientException;
import org.springframework.payment.core.InvalidCardException;
import org.springframework.payment.core.PaymentTransactionException;
import org.springframework.payment.creditcard.CreditCardService;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Component
public class BookingAction extends MultiAction {

    private Logger logger = LoggerFactory.getLogger(BookingAction.class);

    private CreditCardService creditCardService;

    @Autowired
    public BookingAction(@Qualifier("CreditCardService") CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    public Event submitPayment(RequestContext context, Booking booking) throws Exception {

        try {
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
        logger.error(t.getMessage(), t);
        context.getMessageContext().addMessage(new MessageBuilder().error().defaultText(message).build());
        return error();
    }

}
