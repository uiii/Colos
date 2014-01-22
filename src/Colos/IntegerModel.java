package Colos;

import javax.swing.SpinnerModel;

/**
 * Represents model storing integer values in the specified range.
 *
 * The step between neighboring values is also set.
 */
public class IntegerModel extends AdjustableModel<Integer> {
    protected Integer min_;
    protected Integer max_;
    protected Integer precisionStep_;

    /**
     * IntegerModel's constructor
     *
     * @param min
     *    Minimal value that can be stored
     * @param max
     *    Maximal value that can be stored
     * @param precisionStep
     *    The step between neighboring values
     */
    public IntegerModel(Integer min, Integer max, Integer precisionStep) {
        precisionStep_ = precisionStep;

        min_ = Math.round(min / (float) precisionStep_) * precisionStep_;
        max_ = Math.round(max / (float) precisionStep_) * precisionStep_;
        
        value_ = min_;
    }

    /**
     * Sets value to the nearest value in the range considering the precision step
     *
     * @param value
     *     Value to set
     */
    @Override
    public void setValue(Integer value) {
        Integer oldValue_ = value_;
        value_ = Math.round(value / (float) precisionStep_) * precisionStep_;

        if(value_ > max_)
        {
            value_ = max_;
        }
        else if(value_ < min_)
        {
            value_ = min_;
        }        

        if(! oldValue_.equals(value_)) {
            fireStateChanged_();
        }
    }

    @Override
    public void adjustValue(double ratio) {
        int range = max_ - min_;
        setValue(Math.round(min_ + ((float) ratio * range)));
    }
    
    @Override
    public double ratio() {
        double range = max_ - min_;
        return (value_ - min_) / range;
    }
}
