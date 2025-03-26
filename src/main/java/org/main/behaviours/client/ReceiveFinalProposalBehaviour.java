package org.main.behaviours.client;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveFinalProposalBehaviour extends TickerBehaviour {

    public ReceiveFinalProposalBehaviour(Agent a) {
        super(a, 5000);
    }

    @Override
    protected void onTick() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId("order-proposal"),
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)
        );

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            System.out.println(myAgent.getLocalName() + " received proposal: " + msg.getContent());

            AID deliveryAgent = msg.getSender();
            ACLMessage paymentMsg = new ACLMessage(ACLMessage.INFORM);
            paymentMsg.addReceiver(deliveryAgent);
            paymentMsg.setConversationId("payment-confirmation");
            paymentMsg.setContent("Payment sent");
            myAgent.send(paymentMsg);
            System.out.println(myAgent.getLocalName() + " sent payment confirmation to " + deliveryAgent.getLocalName());
        }
    }
}