import type {ProxyOptions} from "vite";
import {createServiceConfig, ServiceConfigItem} from "../../src/utils/sercice";

/**
 * 设置http请求代理
 *
 * @param env 当前环境变量ENV
 * @param enable 表达为真，则启动请求代理
 */
export function createViteProxy(env: Env.ImportMeta, enable: boolean) {
    const isEnableHttpProxy = enable && env.VITE_HTTP_PROXY === 'Y'
    if (!isEnableHttpProxy) return undefined
    const { baseURL, proxyPattern, other } = createServiceConfig(env);
    const proxy: Record<string, ProxyOptions> = createProxyItem({ baseURL, proxyPattern });
    other.forEach(item => {
        Object.assign(proxy, createProxyItem(item));
    });

    return proxy;
}

function createProxyItem(item: ServiceConfigItem) {
    const proxy: Record<string, ProxyOptions> = {};

    proxy[item.proxyPattern] = {
        target: item.baseURL,
        changeOrigin: true,
        rewrite: path => path.replace(new RegExp(`^${item.proxyPattern}`), '')
    };

    return proxy;
}
