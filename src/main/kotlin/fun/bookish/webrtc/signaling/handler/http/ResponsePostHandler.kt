package `fun`.bookish.webrtc.signaling.handler.http

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

/**
 * response结果处理器,所有未被权限处理器拦截的访问的结果,都可以在此处获取,用以生成操作日志
 */
class ResponsePostHandler: Handler<RoutingContext> {

    override fun handle(ctx: RoutingContext) {
        ctx.response().end()
    }

}