declare namespace Env {
    export interface ImportMetaEnv {
        [key: string]: any
        BASE_URL: string
        MODE: string
        DEV: boolean
        PROD: boolean
        SSR: boolean
    }
    export interface ImportMeta extends ImportMetaEnv {
        readonly VITE_HTTP_PROXY: string;
        readonly VITE_SERVICE_TOKEN_BEARER: string;
    }
}
