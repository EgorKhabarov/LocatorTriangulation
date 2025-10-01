package egorkhabarov.locator_triangulation.state;

import java.util.List;

public class LocatorInfo {
    public final PlayerInfo self;
    public final List<TargetInfo> targets;

    public LocatorInfo(PlayerInfo self, List<TargetInfo> targets) {
        this.self = self;
        this.targets = targets;
    }
}
