package Colos;

/**
 * Represents model which can be adjusted by ratio.
 *
 * It means this model can store a range of values.
 * Adjusting the current value will select it from this range
 * according the the specified ratio.
 */
public abstract class AdjustableModel<T> extends Model<T> {
    /**
     * Set current value from range according to the ratio
     *
     * @param ratio
     *     Ratio
     */
    public abstract void adjustValue(double ratio);

    /**
     * Get ratio in the range of the current value of model
     *
     * @return
     *     Ratio
     */
    public abstract double ratio();

}
