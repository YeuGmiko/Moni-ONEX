import { fileURLToPath, URL } from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import Components from 'unplugin-vue-components/vite'
import { createViteProxy } from './build/config'

import UnoCss from 'unocss/vite'
import {NaiveUiResolver} from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig(configEnv => {
  const viteEnv = loadEnv(configEnv.mode, process.cwd()) as unknown as Env.ImportMeta
  const enableProxy = configEnv.command === 'serve' && !configEnv.isPreview

  return {
    plugins: [
      vue(),
      vueDevTools(),
      UnoCss(),
      Components({
        resolvers: [NaiveUiResolver()]
      })
    ],
    resolve: {
      alias: {
        '~': fileURLToPath(new URL('/', import.meta.url)),
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        '@types': fileURLToPath(new URL('./src/typings', import.meta.url)),
      }
    },
    server: {
      host: '0.0.0.0',
      port:  3000,
      open: true,
      proxy: createViteProxy(viteEnv, enableProxy),
      cors: true
    }
  }
})
