package `fun`.bookish.webrtc.signaling.extend


import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext


// ----------- 设置和获取请求参数 -----------
fun RoutingContext.setRequestParams(jsonObject: JsonObject):RoutingContext = this.put("REQUEST_PARAMS",jsonObject)
fun RoutingContext.getRequestParams():JsonObject = this.get<JsonObject>("REQUEST_PARAMS")





