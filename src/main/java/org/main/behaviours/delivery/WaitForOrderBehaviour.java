package org.main.behaviours.delivery;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.main.agents.DeliveryAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitForOrderBehaviour extends CyclicBehaviour {

    private final DeliveryAgent deliveryAgent;

    public WaitForOrderBehaviour(DeliveryAgent agent) {
        super(agent);
        this.deliveryAgent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.CFP && msg.getConversationId().equals("order-request")) {
                System.out.println(myAgent.getLocalName() + " received order: " + msg.getContent());

                List<String> order = new ArrayList<>(Arrays.asList(msg.getContent().split(",")));
                System.out.println("MESSAGE - CONTENT: "+ order);

                myAgent.addBehaviour(new FindMarketBehaviour(order));
            }
        } else {
            block();
        }
    }
}
