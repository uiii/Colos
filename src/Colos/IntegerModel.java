package Colos;

public class IntegerModel extends Model<Integer> {
    protected Integer min_;
    protected Integer max_;
    protected Integer precisionStep_;

    public IntegerModel(Integer min, Integer max, Integer precisionStep) {
        precisionStep_ = precisionStep;

        min_ = Math.round(min / (float) precisionStep_) * precisionStep_;
        max_ = Math.round(max / (float) precisionStep_) * precisionStep_;
        
        value_ = min_;
    }

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
