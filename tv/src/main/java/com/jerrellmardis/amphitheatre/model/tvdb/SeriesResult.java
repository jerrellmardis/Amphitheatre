package com.jerrellmardis.amphitheatre.model.tvdb;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Data", strict = false)
public class SeriesResult {

    @ElementList(name = "Series", inline = true, required = false)
    private List<Series> mSeries;

    public List<Series> getSeries() {
        return mSeries;
    }
}
