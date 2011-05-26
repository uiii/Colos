package Colos;

public abstract class AdjustableModel<T> extends Model<T> {

    public abstract void adjustValue(double ratio);
    public abstract double ratio();

}
