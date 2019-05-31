package com.github.androidserver.service;

import fi.iki.elonen.NanoHTTPD.Response;

public interface Media<T> {

    /**
     *
     * @param pageIndex default 0
     * @param pageSize default 20
     * @return
     */
    Response getListResponse(int pageIndex, int pageSize);

    /**
     * get media file by id
     * @param id
     * @return
     */
    T get(int id);

    Response delete(String... ids);

}
