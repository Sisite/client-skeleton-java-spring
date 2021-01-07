package eu.arrowhead.client.skeleton.common;

import java.util.List;


public class WMDataObjectList {
    private List<WMDataObject> wmDataList;


    public WMDataObjectList() {}

    public WMDataObjectList(final List<WMDataObject> wmDataList) {
        this.wmDataList = wmDataList;
    }

    public List<WMDataObject> getWMDataList() {return wmDataList;}
    public void setWMDataList(List<WMDataObject> wmDataList) {
        this.wmDataList = wmDataList;
    }

}
