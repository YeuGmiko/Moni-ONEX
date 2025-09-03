export interface ServiceConfigItem {
    baseURL: string;
    proxyPattern: string;
}
export type OtherBaseURLKey = 'demo';

export interface SimpleServiceConfig extends Pick<ServiceConfigItem, 'baseURL'> {
    other: Record<OtherBaseURLKey, string>;
}
export interface ServiceConfig extends ServiceConfigItem {
    /** Other backend service config */
    other: OtherServiceConfigItem[];
}
export interface OtherServiceConfigItem extends ServiceConfigItem {
    key: OtherBaseURLKey;
}

export function createServiceConfig(env: Env.ImportMeta) {
    const { VITE_SERVICE_BASE_URL, VITE_OTHER_SERVICE_BASE_URL } = env
    let other = {} as Record<OtherBaseURLKey, string>
    try {
        other = JSON.parse(VITE_OTHER_SERVICE_BASE_URL)
    } catch {
        console.error('VITE_OTHER_SERVICE_BASE_URL is not a valid json string')
    }
    const httpConfig: SimpleServiceConfig = {
        baseURL: VITE_SERVICE_BASE_URL,
        other
    }
    const otherHttpKeys = Object.keys(httpConfig.other) as OtherBaseURLKey[]

    const otherConfig: OtherServiceConfigItem[] = otherHttpKeys.map(key => {
        return {
            key,
            baseURL: httpConfig.other[key],
            proxyPattern: createProxyPattern(key)
        }
    })

    const config: ServiceConfig = {
        baseURL: httpConfig.baseURL,
        proxyPattern: createProxyPattern(),
        other: otherConfig
    }

    return config
}

function createProxyPattern(key?: OtherBaseURLKey) {
    if (!key) {
        return '/proxy-default'
    }
    return `/proxy-${key}`
}

export function getServiceBaseURL(env: Env.ImportMeta, isProxy: boolean) {
    const { baseURL, other } = createServiceConfig(env)

    const otherBaseURL = {} as Record<OtherBaseURLKey, string>

    other.forEach(item => {
        otherBaseURL[item.key] = isProxy ? item.proxyPattern : item.baseURL
    })

    return {
        baseURL: isProxy ? createProxyPattern() : baseURL,
        otherBaseURL
    }
}
