package com.github.androidserver.utils;

import com.github.androidserver.Constants;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ParameterUtil {

    public static List<String> getIds(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> params = session.getParameters();
        return params.get(Constants.Key.ID);
    }

    public static int getPageSize(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> pageSizes = parameters.get(Constants.Key.PAGE_SIZE);
        int pageSize = 0;
        if (pageSizes != null && !pageSizes.isEmpty()) {
            int reqSize = Integer.valueOf(pageSizes.get(0));
            if (reqSize >= Constants.Code.MAX_SIZE) {
                pageSize = Constants.Code.MAX_SIZE;
            } else {
                pageSize = reqSize;
            }
        }
        return pageSize;
    }

    public static int getPageIndex(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> pageIndexs = parameters.get(Constants.Key.PAGE_INDEX);
        int pageIndex = 0;
        if (pageIndexs != null && !pageIndexs.isEmpty()) {
            int reqIndex = Integer.valueOf(pageIndexs.get(0));
            if (reqIndex < 0) {
                pageIndex = 0;
            } else {
                pageIndex = reqIndex;
            }
        }
        return pageIndex;
    }

}
