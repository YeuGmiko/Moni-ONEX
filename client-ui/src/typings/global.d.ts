export {}

declare global {
    export interface Window {
        $message?: import('naive-ui').MessageProviderInst
    }
}
