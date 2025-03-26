package org.main.behaviours.market;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Map;
import java.util.StringJoiner;

public class RespondToPriceInquiryBehaviour extends CyclicBehaviour {

    private final Map<String, Double> items;

    public RespondToPriceInquiryBehaviour( Map<String, Double> items) {
        this.items = items;
    }

    @Override
    public void action() {

        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId("price-inquiry"),
                MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF)
        );
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            String content = msg.getContent();
            String[] orderItems = content.split(",\\s*");

            StringJoiner joiner = new StringJoiner(", ");
            for (String item : orderItems) {
                if (items.containsKey(item)) {
                    joiner.add(item + ":" + items.get(item));
                }
            }
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setConversationId("price-inquiry");
            reply.setContent(joiner.toString());
            myAgent.send(reply);
            System.out.println(myAgent.getLocalName() + " replied with proposal: " + reply.getContent());
        } else {
            block();
        }
    }
}
