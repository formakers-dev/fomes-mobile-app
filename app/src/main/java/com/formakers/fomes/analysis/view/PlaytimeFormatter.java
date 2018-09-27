package com.formakers.fomes.analysis.view;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.Map;

public class PlaytimeFormatter implements IValueFormatter, IAxisValueFormatter {

    protected DecimalFormat defaultFormat;

    public PlaytimeFormatter() {
        defaultFormat = new DecimalFormat("###,###,##0.0");
    }

    /**
     * Allow a custom decimalformat
     *
     * @param format
     */
    public PlaytimeFormatter(DecimalFormat format) {
        this.defaultFormat = format;
    }


    // IAxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return value > 0 ? defaultFormat.format(value) + "시간" : "정보가 부족해요ㅠㅠ";
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return value > 0 ? defaultFormat.format(value) + "시간" : "정보가 부족해요ㅠㅠ";
    }
}
