package io.quarkiverse.quinoa;

import static io.quarkiverse.quinoa.QuinoaRecorder.isIgnored;
import static io.quarkiverse.quinoa.QuinoaRecorder.next;
import static io.quarkiverse.quinoa.QuinoaRecorder.resolvePath;
import static io.quarkiverse.quinoa.QuinoaRecorder.shouldHandleMethod;

import java.util.List;
import java.util.Objects;

import org.jboss.logging.Logger;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

class QuinoaSPARoutingHandler implements Handler<RoutingContext> {
    private static final Logger LOG = Logger.getLogger(QuinoaSPARoutingHandler.class);
    private final ClassLoader currentClassLoader;
    private final List<String> ignoredPathPrefixes;

    public QuinoaSPARoutingHandler(List<String> ignoredPathPrefixes) {
        this.ignoredPathPrefixes = ignoredPathPrefixes;
        currentClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void handle(RoutingContext ctx) {
        if (!shouldHandleMethod(ctx)) {
            next(currentClassLoader, ctx);
            return;
        }
        String path = resolvePath(ctx);
        if (!Objects.equals(path, "/") && !isIgnored(path, ignoredPathPrefixes)) {
            LOG.debugf("Quinoa is re-routing SPA request '%s' to '/'", ctx.normalizedPath());
            ctx.reroute(ctx.mountPoint() != null ? ctx.mountPoint() : "/");
        } else {
            next(currentClassLoader, ctx);
        }
    }
}
