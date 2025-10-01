package egorkhabarov.locator_triangulation.state;

/**
 * @param yaw Абсолютный yaw [-180,180)
 */
public record PlayerInfo(double x, double y, double z, double yaw) {}
