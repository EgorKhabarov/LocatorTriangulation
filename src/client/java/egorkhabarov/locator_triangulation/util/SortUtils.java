package egorkhabarov.locator_triangulation.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SortUtils {
    public static <T> List<T> sortByName(Collection<T> set, Function<T, String> nameGetter) {
        return set.stream()
            .filter(Objects::nonNull)
            .sorted(
                Comparator.comparingInt((T t) -> {
                    String name = nameGetter.apply(t);
                    return name != null ? name.length() : 0;
                })
                .thenComparing(t -> {
                    String name = nameGetter.apply(t);
                    return name != null ? name : "";
                })
            )
            .toList();
    }
}
