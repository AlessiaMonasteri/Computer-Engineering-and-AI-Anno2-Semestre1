package AlessiaMonasteri.BagECommerce.tools;

import AlessiaMonasteri.BagECommerce.entities.Order;
import AlessiaMonasteri.BagECommerce.entities.OrderItem;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import AlessiaMonasteri.BagECommerce.entities.User;

import java.util.List;

@Component
public class MailgunSender {

    private final String domainName;
    private final String apiKey;
    private final String sender;
    private final String testReceiver;

    public MailgunSender(
            @Value("${mailgun.domain}") String domainName,
            @Value("${mailgun.apiKey}") String apiKey,
            @Value("${mailgun.sender}") String sender,
            @Value("${mailgun.test.receiver}") String testReceiver
    ) {
        this.domainName = domainName;
        this.apiKey = apiKey;
        this.sender = sender;
        this.testReceiver = testReceiver;
    }

    public void sendRegistrationEmail(User recipient) {

        HttpResponse<JsonNode> response = Unirest.post(
                        "https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", this.sender)
                .queryString("to", this.testReceiver)
                .queryString("subject", "Welcome to the platform")
                .queryString(
                        "text",
                        "Hello " + recipient.getName() + ", registration was successful!"
                )
                .asJson();

        System.out.println(response.getBody());
    }

    public void sendOrderEmail(User user, Order order, List<OrderItem> items, double total) {

        // per costruire dinamicamente la lista dei prodotti ordinati
        StringBuilder productsList = new StringBuilder();

        // cicla su ogni prodotto restituendo quantità, prezzo unitario e prezzo totale
        for (OrderItem oi : items) {
            String model = oi.getProduct().getModel();
            int qty = oi.getQuantity();
            double unitPrice = oi.getUnitPrice();
            double lineTotal = unitPrice * qty;

            productsList.append("- ")
                    .append(model)
                    .append(" | Quantity: ").append(qty)
                    .append(" | Price: € ")
                    .append(String.format(java.util.Locale.US, "%.2f", unitPrice))
                    .append(" | Subtotal: € ")
                    .append(String.format(java.util.Locale.US, "%.2f", lineTotal))
                    .append("\n");
        }

        // Struttura email che include la lista di prodotti
        String text =
                "Hello " + user.getName() + ",\n\n" +
                        "Your order has been placed successfully!\n\n" +
                        "Order Id: " + order.getId() + "\n" +
                        "Date: " + order.getOrderDate() + "\n\n" +
                        "Ordered products:\n" +
                        productsList +
                        "\nTotal order: € " +
                        String.format(java.util.Locale.US, "%.2f", total) +
                        "\n\nThank you for choosing AM Bags E-Commerce.";

        HttpResponse<JsonNode> response = Unirest.post(
                        "https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", this.sender)
                .queryString("to", this.testReceiver)
                .queryString("subject", "AM Bags E-Commerce Order Confirmation")
                .queryString("text", text)
                .asJson();

        System.out.println(response.getBody());
    }
}

