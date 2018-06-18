package `fun`.bookish.webrtc.signaling.handler.http

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

/**
 * 跨域过滤器
 */
class MyCorsHandler: Handler<RoutingContext> {

    override fun handle(ctx: RoutingContext) {

        ctx.request().getHeader("Origin")?.let {
            ctx.response().putHeader("Access-Control-Allow-Origin",it)
        }

        ctx.request().getHeader("Access-Control-Request-Headers")?.let {
            ctx.response().putHeader("Access-Control-Allow-Headers",it)
        }

        ctx.response()
                .putHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE")
                .putHeader("Access-Control-Allow-Credentials","true")
                .putHeader("Access-Control-Expose-Headers","JSESSIONID")    //设置允许浏览器读取自定义的响应头

        ctx.next()

    }

}