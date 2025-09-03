# 开发工具建议

[Jetbrains Webstorm ](https://www.jetbrains.com.cn/webstorm/)或者 [Visual Studio Code](https://code.visualstudio.com/)

如果你使用VSCode，建议的扩展如下：

- Eslint
- Prettier
- Vue-Official
- TypeScript Importer

# 启动项目

**安装项目依赖**

```bash
pnpm install
```

**运行项目**

```bash
pnpm run dev
```

**项目打包**

```bash
pnpm run build
```

# 项目结构

```
├─assets 样式资源
│  └─iconfont 图标文件
├─components 组件
│  ├─app 和整个APP应用相关的组件
│  └─common-button 公共组件
├─layout Layout组件
├─plugin 插件（pina等）
├─router 路由
├─server api服务
│  └─api
├─store pinia
├─typings 类型
├─utils 工具
└─views 页面
```

