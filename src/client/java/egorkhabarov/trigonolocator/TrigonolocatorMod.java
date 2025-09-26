package egorkhabarov.trigonolocator;

import egorkhabarov.trigonolocator.command.LocatorDataCommand;
import net.fabricmc.api.ClientModInitializer;

public class TrigonolocatorMod implements ClientModInitializer {
    public static final String MOD_ID = "trigonolocator";

    @Override
    public void onInitializeClient() {
        LocatorDataCommand.register();
    }
}
