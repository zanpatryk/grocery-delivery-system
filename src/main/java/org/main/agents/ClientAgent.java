package org.main.agents;

import jade.core.Agent;
import org.main.behaviours.client.FindDelivererBehaviour;
import org.main.behaviours.client.ReceiveFinalProposalBehaviour;

import java.util.ArrayList;
import java.util.List;

public class ClientAgent extends Agent {

    private List<String> order;

    @Override
    protected void setup(){
        order = initializeOrderItems(getArguments());
        System.out.println("Agent " + getLocalName() + " made order: " + order);
        addBehaviour( new FindDelivererBehaviour(this, order));
        addBehaviour(new ReceiveFinalProposalBehaviour(this));
    }

    private List<String> initializeOrderItems(Object[] args){
        if(args[0] != null && args[0] instanceof List<?> ){
            return new ArrayList<>((List<String>) args[0]);
        }
        return List.of("milk", "coffee", "rice");
    }

    @Override
    public void doDelete() {
        System.out.println(getLocalName() + ": Terminated.");
    }
}
