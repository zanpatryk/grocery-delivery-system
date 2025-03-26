package org.main.behaviours.delivery;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceivePaymentBehaviour extends TickerBehaviour {

    public ReceivePaymentBehaviour(jade.core.Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {

        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId("payment-confirmation"),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            System.out.println(myAgent.getLocalName() + " received payment confirmation from " + msg.getSender().getLocalName());
        }
    }
}
