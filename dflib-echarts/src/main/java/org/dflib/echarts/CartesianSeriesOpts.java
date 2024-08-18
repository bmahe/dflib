package org.dflib.echarts;

/**
 * @since 1.0.0-M22
 */
public abstract class CartesianSeriesOpts<SO extends CartesianSeriesOpts<SO>> extends SeriesOpts<SO> {

    protected Label label;
    protected Integer xAxisIndex;
    protected Integer yAxisIndex;

    /**
     * @since 1.0.0-M22
     */
    public SO label(LabelPosition position) {
        this.label = Label.of(position);
        return (SO) this;
    }

    /**
     * @since 1.0.0-M22
     */
    public SO label(Label label) {
        this.label = label;
        return (SO) this;
    }

    /**
     * Sets an index of X axis to use for this Series. There can be one or more X axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     *
     * @since 1.0.0-M22
     */
    public SO xAxisIndex(int index) {
        this.xAxisIndex = index;
        return (SO) this;
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     *
     * @since 1.0.0-M22
     */
    public SO yAxisIndex(int index) {
        this.yAxisIndex = index;
        return (SO) this;
    }
}
