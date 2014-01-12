package it.masch.bukkit.awp;

public final class DefaultEither<L, R> implements Either<L, R>
{

    protected L left;
    protected R right;

    protected DefaultEither()
    {
    }

    public static <L, R> Either<L, R> Left(L left)
    {
        DefaultEither<L, R> ret = new DefaultEither<L, R>();
        ret.left = left;
        return ret;
    }

    public static <L, R> Either<L, R> Right(R right)
    {
        DefaultEither<L, R> ret = new DefaultEither<L, R>();
        ret.right = right;
        return ret;
    }

    public boolean isLeft()
    {
        return left != null;
    }

    public boolean isRight()
    {
        return right != null;
    }

    public L left()
    {
        return left;
    }

    public R right()
    {
        return right;
    }

}
