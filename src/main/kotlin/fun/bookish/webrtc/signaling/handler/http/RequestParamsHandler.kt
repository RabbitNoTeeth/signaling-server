package `fun`.bookish.webrtc.signaling.handler.http

import `fun`.bookish.webrtc.signaling.extend.setRequestParams
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext


/**
 * 请求参数处理器,将请求参数进行解析封装,放入到RoutingContext上下文,便于子路由获取
 */
class RequestParamsHandler : Handler<RoutingContext> {

    override fun handle(ctx: RoutingContext) {
        val method = ctx.request().method()

        if(method == HttpMethod.GET || method == HttpMethod.DELETE ||
                method == HttpMethod.PUT || method == HttpMethod.POST){
            val paramsMap = ctx.request().params()
            val params = JsonObject().apply {
                for((key,value) in paramsMap){
                    put(key,value)
                }
            }
            //将参数存入上下文,便于子路由获取
            ctx.setRequestParams(params)
        }
        ctx.next()
    }

}