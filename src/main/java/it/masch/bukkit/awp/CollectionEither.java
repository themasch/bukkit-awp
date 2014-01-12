package it.masch.bukkit.awp;

import java.util.Collection;

public interface CollectionEither<L> extends
        Either<L, Collection<CollectionEither<L>>>
{
}
