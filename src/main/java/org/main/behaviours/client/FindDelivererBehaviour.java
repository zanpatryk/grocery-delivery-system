package org.main.behaviours.client;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class FindDelivererBehaviour extends TickerBehaviour {

    private final Agent agent;
    private final List<String> order;
    private int attempts = 0;
    private static  final int MAX_ATTEMPTS = 10;

    public FindDelivererBehaviour(Agent agent, List<String> order) {
        super(agent,1000);
        this.agent = agent;
        this.order = order;
    }

    @Override
    public void onTick() {
        System.out.println(agent.getLocalName() + " searching for delivery agents ...");

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("grocery-delivery");
        template.addServices(sd);

        try{
            DFAgentDescription[] results = DFService.search(agent, template);
            System.out.println(agent.getLocalName() + " found " + results.length + " delivery agents");

            if(results.length > 0){
                for ( DFAgentDescription dfd : results) {
                    AID deliveryAgent = dfd.getName();
                    ACLMessage orderMsg = new ACLMessage(ACLMessage.CFP);
                    orderMsg.addReceiver(deliveryAgent);
                    orderMsg.setConversationId("order-request");
                    orderMsg.setContent(String.join(",", order));
                    agent.send(orderMsg);
                    System.out.println(agent.getLocalName() + " sent order to " + deliveryAgent.getLocalName());
                }
                stop();
            }else{
                attempts++;
                if(attempts>= MAX_ATTEMPTS){
                    System.out.println(agent.getLocalName() + " no delivery agents found");
                    stop();
                }
            }

        }catch (FIPAException e){
            e.printStackTrace();
        }
    }

}
