package egorkhabarov.locator_triangulation.logic;

import egorkhabarov.locator_triangulation.model.PlayerInfo;
import java.util.Optional;

public class Triangulation {
    public record Result(double x, double z, double error, double angle) {}

    public static Optional<Result> triangulate(PlayerInfo pos1, PlayerInfo pos2) {
        if (pos1 == null || pos2 == null) return Optional.empty();

        double x1 = pos1.x(), z1 = pos1.z();
        double x2 = pos2.x(), z2 = pos2.z();
        double theta1 = Math.toRadians(pos1.yaw() + 90.0);
        double theta2 = Math.toRadians(pos2.yaw() + 90.0);
        double dx1 = Math.cos(theta1), dz1 = Math.sin(theta1);
        double dx2 = Math.cos(theta2), dz2 = Math.sin(theta2);
        double rx = x2 - x1;
        double rz = z2 - z1;
        double det = dx1 * dz2 - dz1 * dx2;
        final double EPS = 1e-7;
        double ix, iz, error;
        if (Math.abs(det) > EPS) {
            double cross_p21_d2 = rx * dz2 - rz * dx2;
            double cross_p21_d1 = rx * dz1 - rz * dx1;
            double t1 = cross_p21_d2 / det;
            double t2 = cross_p21_d1 / det;
            double r1x = x1 + t1 * dx1;
            double r1z = z1 + t1 * dz1;
            double r2x = x2 + t2 * dx2;
            double r2z = z2 + t2 * dz2;
            ix = (r1x + r2x) / 2.0;
            iz = (r1z + r2z) / 2.0;
            error = Math.hypot(r1x - r2x, r1z - r2z);
        } else {
            double dotD1 = dx1*dx1 + dz1*dz1;
            double tProj = (rx*dx1 + rz*dz1) / dotD1;
            double r1x = x1 + tProj * dx1;
            double r1z = z1 + tProj * dz1;
            double dotD2 = dx2*dx2 + dz2*dz2;
            double u = ((r1x - x2)*dx2 + (r1z - z2)*dz2) / dotD2;
            double r2x = x2 + u * dx2;
            double r2z = z2 + u * dz2;
            ix = (r1x + r2x) / 2.0;
            iz = (r1z + r2z) / 2.0;
            error = Math.hypot(r1x - r2x, r1z - r2z);
        }
        double dot = dx1*dx2 + dz1*dz2;
        dot = Math.max(-1.0, Math.min(1.0, dot));
        double angle = Math.toDegrees(Math.acos(dot));
        return Optional.of(new Result(ix, iz, error, angle));
    }

    public static Optional<Result> triangulate(PlayerInfo pos1, PlayerInfo pos2, PlayerInfo pos3) {
        Optional<Result> r12 = triangulate(pos1, pos2);
        Optional<Result> r13 = triangulate(pos1, pos3);
        Optional<Result> r23 = triangulate(pos2, pos3);

        if (r12.isEmpty() || r13.isEmpty() || r23.isEmpty()) {
            return triangulate(pos1, pos2);
        }

        double ix = (r12.get().x() + r13.get().x() + r23.get().x()) / 3.0;
        double iz = (r12.get().z() + r13.get().z() + r23.get().z()) / 3.0;
        double error = (r12.get().error() + r13.get().error() + r23.get().error()) / 3.0;
        double angle = r12.get().angle();

        return Optional.of(new Result(ix, iz, error, angle));
    }
}
