package egorkhabarov.trigonolocator.util;

import egorkhabarov.trigonolocator.locator.LocatorInfo;
import egorkhabarov.trigonolocator.locator.LocatorInfo.TargetInfo;

import java.util.Optional;

public class Triangulation {
    public static class Result {
        public final double x;
        public final double z;
        public final double error; // отклонение пересечения

        public Result(double x, double z, double error) {
            this.x = x;
            this.z = z;
            this.error = error;
        }
    }

    public static Optional<Result> triangulate(LocatorInfo a, LocatorInfo b, String playerName) {
        if (a == null || b == null) return Optional.empty();

        TargetInfo ta = findByName(a, playerName);
        TargetInfo tb = findByName(b, playerName);
        if (ta == null || tb == null) return Optional.empty();

        double x1 = a.self.x, z1 = a.self.z;
        double x2 = b.self.x, z2 = b.self.z;

        double theta1 = Math.toRadians(ta.yaw + 90.0);
        double theta2 = Math.toRadians(tb.yaw + 90.0);
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
            // почти параллельные
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

        return Optional.of(new Result(ix, iz, error));
    }

    private static TargetInfo findByName(LocatorInfo info, String name) {
        if (info == null || info.targets == null) return null;
        for (TargetInfo t : info.targets) {
            if (t.name != null && t.name.equalsIgnoreCase(name)) return t;
        }
        return null;
    }
}
