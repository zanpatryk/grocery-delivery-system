package org.main.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.main.behaviours.delivery.ReceivePaymentBehaviour;
import org.main.behaviours.delivery.WaitForOrderBehaviour;

public class DeliveryAgent extends Agent {
    private String deliveryService;
    private double deliveryFee;


    @Override
    protected void setup() {

        Object[] args = getArguments();

        if (args != null && args.length >= 2) {
            deliveryService = (String) args[0];
            deliveryFee = (double) args[1];
        } else {
            deliveryService = "Invalid";
            deliveryFee = 0.0;
        }

        System.out.println(getLocalName() + " started with service " + deliveryService + ", fee: " + deliveryFee);

        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("grocery-delivery");
            sd.setName(deliveryService);
            dfd.addServices(sd);
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new WaitForOrderBehaviour(this));
        addBehaviour(new ReceivePaymentBehaviour(this, 3000));
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

    public double getDeliveryFee() {
        return deliveryFee;
    }
}
