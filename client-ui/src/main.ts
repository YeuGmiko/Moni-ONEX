import { createApp } from 'vue'

import App from '@/App.vue'
import {setupPinia} from '@/plugin/pinia'
// import {setupOhVueIcons} from '@/plugin/ohVueIcons'

import '@/assets'
import {setupRouter} from '@/router'
import {setupOhVueIcons} from '@/plugin'


async function setupApp() {
    const app = createApp(App)

    setupPinia(app)
    setupOhVueIcons()
    setupRouter(app)

    app.mount('#app')
}

setupApp()
