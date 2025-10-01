package egorkhabarov.locator_triangulation.state;

import java.util.UUID;

/**
 * @param yaw Абсолютный yaw [-180,180)
 */
public record TargetInfo(UUID uuid, String name, double yaw, double distance) {}
