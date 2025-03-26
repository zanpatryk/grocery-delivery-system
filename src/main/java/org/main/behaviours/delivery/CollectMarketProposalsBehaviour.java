package org.main.behaviours.delivery;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.main.agents.DeliveryAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectMarketProposalsBehaviour extends SimpleBehaviour {

    private final List<String> order;
    private final Map<AID, Map<String, Double>> proposals = new HashMap<>();
    private long startTime;
    private final long timeout = 10000;
    private boolean done = false;
    private final DeliveryAgent agent;

    public CollectMarketProposalsBehaviour(Agent a, List<String> order) {
        super(a);
        this.agent = (DeliveryAgent) a;
        this.order = order;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId("price-inquiry"),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );

        while (System.currentTimeMillis() - startTime < timeout) {
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println(myAgent.getLocalName() + " received message from "
                        + msg.getSender().getLocalName() + " with content: " + msg.getContent());
                processMessage(msg);
            } else {
                block(500);
            }
        }

        done = true;
        processProposals();
    }

    @Override
    public boolean done() {
        return done;
    }

    private void processMessage(ACLMessage msg) {
        Map<String, Double> proposal = new HashMap<>();
        String content = msg.getContent();
        if (content != null && !content.isEmpty()) {
            String[] itemsArr = content.split(",\\s*");
            for (String itemEntry : itemsArr) {
                String[] parts = itemEntry.split(":");
                if (parts.length == 2) {
                    try {
                        String itemName = parts[0];
                        double price = Double.parseDouble(parts[1]);
                        proposal.put(itemName, price);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        proposals.put(msg.getSender(), proposal);
        System.out.println(myAgent.getLocalName() + " stored proposal from " + msg.getSender().getLocalName() + ": " + proposal);
    }

    private void processProposals(){
        System.out.println(myAgent.getLocalName() + " processing proposals...");

        List<String> missingItems = new ArrayList<>(order);
        List<MarketSelection> selections = new ArrayList<>();

        while(!missingItems.isEmpty()){
            int bestCount = 0;
            double bestPrice = Double.MAX_VALUE;
            AID bestMarket = null;
            List<String> bestItems = new ArrayList<>();

            for(Map.Entry<AID, Map<String,Double>> entry : proposals.entrySet()){
                Map<String, Double> available = entry.getValue();
                List<String> offered = missingItems.stream()
                        .filter(available::containsKey)
                        .collect(Collectors.toList());
                int count = offered.size();
                if(count == 0) continue;

                double totalPrice = offered.stream().mapToDouble(available::get).sum();

                if( count > bestCount || (count==bestCount && totalPrice < bestPrice)){
                    bestCount = count;
                    bestPrice = totalPrice;
                    bestMarket = entry.getKey();
                    bestItems = offered;
                }
            }
            if(bestMarket == null || bestItems.isEmpty()){
                System.out.println(myAgent.getLocalName() + " cannot fulfill the complete order.");
                break;
            }else{
                selections.add(new MarketSelection(bestMarket, bestItems, bestPrice));
                missingItems.removeAll(bestItems);
            }
        }

        if(!missingItems.isEmpty()){
            System.out.println(myAgent.getLocalName() + " final proposal: Incomplete order, missing items: " + missingItems);
        }else{
            double deliveryFee = agent.getDeliveryFee();
            double totalCost = selections.stream().mapToDouble(s -> s.price).sum();

            StringBuilder proposalMsg = new StringBuilder();
            for(MarketSelection sel : selections) {
                proposalMsg.append(sel.market.getLocalName())
                        .append(" supplies: ")
                        .append(String.join(", ", sel.items))
                        .append(" (Cost: ")
                        .append(sel.price)
                        .append("); ");
            }
            proposalMsg.append("Delivery fee: ").append(deliveryFee)
                    .append("; Total cost: ").append(totalCost);
            System.out.println(myAgent.getLocalName() + " final proposal: " + proposalMsg);
            ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
            reply.addReceiver(new AID("ClientAgent", AID.ISLOCALNAME));
            reply.setConversationId("order-proposal");
            reply.setContent(proposalMsg.toString());
            myAgent.send(reply);
            System.out.println(myAgent.getLocalName() + " sent proposal.");
        }
    }

    private static class MarketSelection {
        AID market;
        List<String> items;
        double price;

        public MarketSelection(AID market, List<String> items, double price) {
            this.market = market;
            this.items = items;
            this.price = price;
        }
    }
}
