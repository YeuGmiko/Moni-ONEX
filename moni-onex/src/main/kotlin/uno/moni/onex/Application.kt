package uno.moni.onex

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import uno.moni.onex.core.core.util.IpUtils

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
class Application

fun main(args: Array<String>) {
    /* run application */
    val context = runApplication<Application>(*args)
    /* print swagger url path */
    val contextPath = context.environment.getProperty("server.servlet.context-path")
    val hostIp = IpUtils.getHostIp()
    val port: String? = context.environment.getProperty("server.port")
    val swaggerPath: String? = context.environment.getProperty("springdoc.swagger-ui.path")
    println("Swagger访问地址：http://${hostIp}:${port}${contextPath}${swaggerPath}")
}