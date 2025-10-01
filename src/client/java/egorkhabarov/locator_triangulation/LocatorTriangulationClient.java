package egorkhabarov.locator_triangulation;

import egorkhabarov.locator_triangulation.command.LocatorDataCommand;
import net.fabricmc.api.ClientModInitializer;

public class LocatorTriangulationClient implements ClientModInitializer {
    public static final String MOD_ID = "locator_triangulation";

    @Override
    public void onInitializeClient() {
        LocatorDataCommand.register();
    }
}
