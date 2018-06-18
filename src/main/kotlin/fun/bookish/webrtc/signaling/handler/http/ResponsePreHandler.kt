package `fun`.bookish.webrtc.signaling.handler.http

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

/**
 * response前置处理器
 */
class ResponsePreHandler: Handler<RoutingContext> {

    override fun handle(ctx: RoutingContext) {
        ctx.response().putHeader("Content-Type", "application/json;charset=UTF-8")
        ctx.response().isChunked = true
        ctx.next()
    }

}