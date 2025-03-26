package org.main.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.main.behaviours.market.RespondToPriceInquiryBehaviour;

import java.util.HashMap;
import java.util.Map;

public class MarketAgent extends Agent {

    private Map<String, Double> items;
    private String name;

    @Override
    protected void setup(){
        Object[] args = getArguments();

        items = new HashMap<>();
        if(args != null && args.length > 0 && args[0] instanceof String && args[1] instanceof Map<?,?>){
            name = (String) args[0];
            items.putAll((Map<String, Double>) args[1]);
        }else{
            name = "Default";
            items.put("milk", 15.0);
            items.put("coffee", 5.0);
            items.put("rice", 3.0);
        }

        System.out.println(getLocalName() + ": " + name + " avaiable items: " + items);

        try{
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            ServiceDescription sd = new ServiceDescription();
            sd.setType("market-service");
            sd.setName(name);
            dfd.addServices(sd);

            DFService.register(this, dfd);
            System.out.println(getLocalName() + " registered in DF as market-service.");

        }catch(FIPAException e){
            e.printStackTrace();
        }

        addBehaviour(new RespondToPriceInquiryBehaviour(items));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println(getLocalName() + ": Terminated.");
    }
}
