package it.masch.bukkit.awp;

public interface Either<L, R>
{

    public boolean isLeft();

    public boolean isRight();

    public L left();

    public R right();
}
