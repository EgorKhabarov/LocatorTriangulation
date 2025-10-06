package egorkhabarov.locator_triangulation.state;

import java.util.UUID;

/**
 * @param yaw Absolute yaw [-180,180)
 */
public record TargetInfo(UUID uuid, String name, Integer color, double yaw, double distance) {}
