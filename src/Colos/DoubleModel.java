package Colos;

public class DoubleModel extends Model<Double> {
    protected Double min_;
    protected Double max_;
    protected Double precisionStep_;

    public DoubleModel(Double min, Double max, Double precisionStep) {
        precisionStep_ = precisionStep;

        min_ = Math.round(min_ / precisionStep_) * precisionStep_;
        max_ = Math.round(max_ / precisionStep_) * precisionStep_;
        
        value_ = min_;
    }

    @Override
    public void setValue(Double value) {
        value_ = Math.round(value / precisionStep_) * precisionStep_;

        if(value_ > max_)
        {
            value_ = max_;
        }
        else if(value_ < min_)
        {
            value_ = min_;
        }        
    }

    @Override
    public void adjustValue(double ratio) {
        double range = max_ - min_;
        setValue(min_ + (ratio * range));
    }

    @Override
    public double ratio() {
        double range = max_ - min_;
        return (value_ - min_) / range;
    }
}
