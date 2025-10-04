package egorkhabarov.locator_triangulation;

import egorkhabarov.locator_triangulation.command.LocatorDataCommand;
import egorkhabarov.locator_triangulation.command.TriangulationCommand;
import egorkhabarov.locator_triangulation.state.LocatorState;
import egorkhabarov.locator_triangulation.state.TriangulationState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorTriangulationClient implements ClientModInitializer {
    public static final String MOD_ID = "locator_triangulation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LocatorDataCommand.register();
        TriangulationCommand.register();
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            LocatorState.clearAll();
            TriangulationState.clearAll();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            LocatorState.clearAll();
            TriangulationState.clearAll();
        });
        LOGGER.info(MOD_ID + " mod loaded successfully!");
    }
}
