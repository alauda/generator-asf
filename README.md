# generator-asf [![NPM version][npm-image]][npm-url] [![Build Status][travis-image]][travis-url] [![Coverage percentage][coveralls-image]][coveralls-url]

# 文档说明

本文档旨在指导开发人员如何通过脚手架的快速搭建 Spring Cloud 工程。

# 脚手架简介

本脚手架基于 Yeoman 实现了一套 CLI 交互，方便用户进行简单的选项填写即可快速搭建 Spring Cloud 工程。
脚手架的代码完全开源。

# 相关链接

脚手架开源代码地址：https://github.com/alauda/generator-asf

脚手架镜像地址：docker.io/dwgao/generator-asf

# 使用说明

脚手架当前只支持在 Linux 下运行，文档中的命令均为 CentOS 7.7 下的执行命令，如运行环境未其他 Linux 操作系统，需要用户使用适配对应操作系统运行环境的命令。

## 环境准备

通过运行脚手架创建工程之前需要准备环境，安装脚手架依赖的软件。

脚手架的运行环境支持 docker 和非 docker ( npm 安装后直接运行)。

### Docker 环境

1. 安装 docker，版本要求：docker>=1.13.1 。

```bash
yum install docker
```

2. 拉取镜像，镜像 tag 建议选择最新的 Release 版本。

```bash
docker pull docker.io/dwgao/generator-asf:<tag>
```

### 非 Docker 环境

1. 安装 node.js 和 npm
   
    版本要求： nodejs>=12.14.0 ；npm>=6.13.4

    安装 node.js 时会带上 npm，此处注意 yum 安装的 node.js 版本过老，不支持脚手架安装。

    node.js 安装方法：

    官网获取 node.js 软件包
```bash
wget https://nodejs.org/dist/v12.14.0/node-v12.14.0-linux-x64.tar.xz
```

软链接全局替换

```bash
ln -s <软件所在目录>/node-v12.14.0-linux-x64/bin/node /usr/bin/node
ln -s <软件所在目录>/node-v12.14.0-linux-x64/bin/npm /usr/bin/npm
```

2. 安装 Yeoman，版本要求： yo>=3.1.1 。
```bash
npm install -g yo
```
3. 安装脚手架(generator-asf)
```bash
npm install -g generator-asf
```

## 创建工程

脚手架可以创建两种应用工程，一种是常规的 Spring Cloud 微服务业务应用，另一种是基于 Spring Cloud Gateway 的微服务网关应用。

业务应用能力覆盖：熔断降级、限流、注册中心、配置中心、调用链跟踪、消息队列。

网关应用能力覆盖：熔断降级、限流、注册中心、配置中心、调用链跟踪。

### 创建方法

工程的创建方式有两种：

1. 通过 CLI 交互进行工程创建
2. 通过模板进行工程创建
使用模板创建之前需要检查模板的配置是否符合工程需求，如不符合需要自行调配模板。

### 命令说明

|创建方式|应用类型|非 docker 运行|docker 运行|
|-----|------|---------|-------------|
|CLI 交互|业务应用| `asf create-service` | `docker run --rm -it \-v <工程父目录>:/yo <镜像地址>:<镜像 tag> \` <br> `asf create-service` <br> 说明：在挂载到容器的工程父目录下创建工程|
|CLI 交互|网关应用| `asf create-gateway` | `docker run --rm -it \-v <工程父目录>:/yo <镜像地址>:<镜像 tag> \` <br> `asf create-gateway` <br> 说明：在挂载到容器的工程父目录下创建工程|
|模板创建|业务应用| `asf create-service -c <模板文件路径>` <br> 说明：在命令执行的当前目录下创建工程 | `docker run --rm -it \-v <模板文件所在目录路径>:/conf \-v <工程父目录>:/yo <镜像地址>:<镜像 tag> \` <br> `asf create-service -c /conf/<模板文件名称>` <br> 说明：模板文件所在目录路径不包含模板文件，在挂载到容器的工程父目录下创建工程|
|模板创建|网关应用| `asf create-gateway -c <模板文件路径>` <br> 说明：在命令执行的当前目录下创建工程 | `docker run --rm -it \-v <模板文件所在目录路径>:/conf \-v <工程父目录>:/yo <镜像地址>:<镜像 tag> \` <br> `asf create-gateway -c /conf/<模板文件名称>` <br> 说明：模板文件所在目录路径不包含模板文件，在挂载到容器的工程父目录下创建工程|

小技巧：上述命令中 create-service 和 create-gateway 均支持别名，分别为 cs 和 cg 。

注意：

模板创建命令会检测模板文件路径下是否存在命令中的模板文件。如不存在，脚手架将会执行 CLI 交互，并在交互操作完成后创建工程且生成模板文件；如存在，脚手架将不会执行 CLI 交互，直接按照模板文件配置创建工程。
脚手架执行工程创建时，若工程父目录下存在同名工程，该工程将会被脚手架覆盖。




## License

Apache-2.0 © [gaodawei]()


[npm-image]: https://badge.fury.io/js/generator-asf.svg
[npm-url]: https://npmjs.org/package/generator-asf
[travis-image]: https://travis-ci.com/madogao/generator-asf.svg?branch=master
[travis-url]: https://travis-ci.com/madogao/generator-asf
[daviddm-image]: https://david-dm.org/madogao/generator-asf.svg?theme=shields.io
[daviddm-url]: https://david-dm.org/madogao/generator-asf
[coveralls-image]: https://coveralls.io/repos/madogao/generator-asf/badge.svg
[coveralls-url]: https://coveralls.io/r/madogao/generator-asf
