package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSequenceSeries_HeadTest {

    @Test
    public void test() {
        IntSeries s = new IntSequenceSeries(1, 4).head(2);
        new IntSeriesAsserts(s).expectData(1, 2);
    }

    @Test
    public void test_Zero() {
        IntSeries s = new IntSequenceSeries(1, 4).head(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        IntSeries s = new IntSequenceSeries(1, 4).head(4);
        new IntSeriesAsserts(s).expectData(1, 2, 3);
    }

    @Test
    public void test_Negative() {
        IntSeries s = new IntSequenceSeries(1, 4).head(-2);
        new IntSeriesAsserts(s).expectData(3);
    }
}