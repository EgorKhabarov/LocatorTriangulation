package egorkhabarov.locator_triangulation;

import egorkhabarov.locator_triangulation.command.LocatorDataCommand;
import egorkhabarov.locator_triangulation.command.TriangulationCommand;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorTriangulationClient implements ClientModInitializer {
    public static final String MOD_ID = "locator_triangulation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LocatorDataCommand.register();
        TriangulationCommand.register();
        LOGGER.info(MOD_ID + " mod loaded successfully!");
    }
}
