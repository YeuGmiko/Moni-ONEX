import 'vue-router'

export {}

declare module 'vue-router' {
    export interface RouteMeta {
        requireAuth?: boolean
    }
    export interface RouteParams {
        id?: string
    }
}
