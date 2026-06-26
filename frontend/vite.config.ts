import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'node:path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const useMock = (env.VITE_USE_MOCK ?? 'true') === 'true'
  return {
    plugins: [vue()],
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler'
        }
      }
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    server: {
      port: 5173,
      host: true,
      open: false,
      proxy: {
        '/api': {
          target: env.VITE_API_TARGET || 'http://localhost:8080',
          changeOrigin: true,
          bypass: useMock
            ? () => undefined
            : undefined
        }
      }
    },
    optimizeDeps: {
      include: ['element-plus', '@element-plus/icons-vue', 'md-editor-v3']
    }
  }
})