package org.main.behaviours.delivery;

import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class FindMarketBehaviour extends OneShotBehaviour {
    private final List<String> order;

    public FindMarketBehaviour(List<String> order) {
        this.order = order;
    }

    @Override
    public void action() {
        System.out.println(myAgent.getLocalName() + " searching for MarketAgents...");

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("market-service");
        template.addServices(sd);

        try {
            DFAgentDescription[] results = DFService.search(myAgent, template);
            System.out.println(myAgent.getLocalName() + " found " + results.length + " MarketAgents");

            if(results.length > 0) {
                for (DFAgentDescription dfd : results) {
                    ACLMessage query = new ACLMessage(ACLMessage.QUERY_IF);
                    query.addReceiver(dfd.getName());
                    query.setConversationId("price-inquiry");
                    query.setContent(String.join(", ", order));
                    myAgent.send(query);
                    System.out.println(myAgent.getLocalName() + " sent price request to " + dfd.getName().getLocalName());
                }
                myAgent.addBehaviour(new CollectMarketProposalsBehaviour(myAgent, order));
            } else {
                System.out.println(myAgent.getLocalName() + " no MarketAgents found.");
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
