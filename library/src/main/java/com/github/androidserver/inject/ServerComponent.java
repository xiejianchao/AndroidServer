package com.github.androidserver.inject;

import com.github.androidserver.AndroidHttpServer;
import com.github.androidserver.Server;
import com.github.androidserver.manager.RequestManager;
import com.github.androidserver.service.MediaServiceImpl;

import dagger.Component;

@ServerScope
@Component(modules = {ApplicationModule.class})
public interface ServerComponent {
    void inject(Server server);
    void inject(RequestManager manager);
    void inject(MediaServiceImpl service);
    void inject(AndroidHttpServer server);
}
