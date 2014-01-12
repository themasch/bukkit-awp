package it.masch.bukkit.awp;

import java.util.Map;

public interface MapEither<L, K> extends Either<L, Map<K, MapEither<L, K>>>
{
}
