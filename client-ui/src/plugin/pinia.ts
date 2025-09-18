import { createPinia } from 'pinia'
import type {App} from 'vue'

export function setupPinia(app: App) {
    app.use(createPinia())
}