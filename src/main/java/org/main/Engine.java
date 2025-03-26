package org.main;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import org.exceptions.JadePlatformInitializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Engine {

    private static final ExecutorService jadeExecutor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        final Runtime runtime = Runtime.instance();
        final Profile profile = new ProfileImpl();

        try{
            final ContainerController container = jadeExecutor.submit(() -> runtime.createMainContainer(profile)).get();

            List<String> orderItems = new ArrayList<>();
            orderItems.add("milk");
            orderItems.add("coffee");
            orderItems.add("rice");

            final AgentController clientAgent = container.createNewAgent(
                    "ClientAgent",
                    "org.main.agents.ClientAgent",
                    new Object[]{ orderItems }
            );
            clientAgent.start();

            final AgentController deliveryAgent1 = container.createNewAgent(
                    "DeliveryAgent1",
                    "org.main.agents.DeliveryAgent",
                    new Object[]{ "Wolt", 5.0 }
            );
            deliveryAgent1.start();

            final AgentController marketAgent1 = container.createNewAgent(
                    "MarketAgent1",
                    "org.main.agents.MarketAgent",
                    new Object[]{ "Biedronka", Map.of("milk", 5.0, "rice", 3.0) }
            );
            marketAgent1.start();

            final AgentController marketAgent2 = container.createNewAgent(
                    "MarketAgent2",
                    "org.main.agents.MarketAgent",
                    new Object[]{ "Lidl", Map.of( "coffee", 25.0, "rice", 3.0) }
            );
            marketAgent2.start();


        }catch (final Exception e){
            throw new JadePlatformInitializationException(e);
        }
    }
}
