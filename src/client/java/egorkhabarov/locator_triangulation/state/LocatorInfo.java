package egorkhabarov.locator_triangulation.state;

import java.util.Map;
import java.util.UUID;

public record LocatorInfo(PlayerInfo self, Map<UUID, TargetInfo> targets) {}
