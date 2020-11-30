"use strict";
const Generator = require("yeoman-generator");
const chalk = require("chalk");
const prompts = require("./prompts");
const camelcase = require("camelcase");
const decamelize = require("decamelize");
const walk = require("walk");
const path = require("path");
const tree = require("tree-node-cli");
const fs = require("fs");
const alaudaSay = require("../../lib/alauda-say");
const base64 = require("base64-utf8");
const ignoreFiles = require("./ignore-files");

module.exports = class extends Generator {
    constructor(args, opts) {
        // Calling the super constructor is important so our generator is correctly set up
        super(args, opts);

        this.option("config", {
            alias: "c",
            desc: "配置文件地址",
            type: String
        });

        this.getDockerConfigJson = this.getDockerConfigJson.bind(this);
        this._writeTpl = this._writeTpl.bind(this);
        this._translateProps = this._translateProps.bind(this);
    }

    async prompting() {
        this.log(alaudaSay(`欢迎使用 Spring Cloud For Alauda 脚手架。`));
        const configFile = this.options.config;
        if (configFile && fs.existsSync(configFile)) {
            const data = fs.readFileSync(configFile, "utf8");
            this.props = JSON.parse(data);
        } else {
            this.props = await this.prompt({
                type: "confirm",
                name: "bootstrap",
                message: "确认开始创建一个微服务网关工程吗？",
                default: true
            });

            if (this.props.bootstrap) {
                prompts.forEach(o => {
                    if (typeof o.default !== "undefined") {
                        this.props[o.name] = o.default;
                    }
                });
                this.props = Object.assign(
                    {},
                    this.props,
                    await this.prompt(prompts)
                );
            }
        }
    }

    _translateProps() {
        Object.keys(this.props).forEach(key => {
            this.props[decamelize(key).toUpperCase()] = this.props[key];
        });
    }

    getDockerConfigJson() {
        this.props.dockerImage = "";
        const {
            dockerUrl,
            dockerPort,
            dockerUsername,
            dockerPassword,
            projectName
        } = this.props;
        if (!dockerUrl || !dockerPort) return "";
        const authsKey = `${dockerUrl}:${dockerPort}`;
        this.props.dockerImage = `${authsKey}/${projectName}:latest`;
        if (!dockerUsername || !dockerPassword) return "";
        const auth = base64.encode(`${dockerUsername}:${dockerPassword}`);
        return base64.encode(
            JSON.stringify({
                auths: {
                    [authsKey]: {
                        username: dockerUsername,
                        password: dockerPassword,
                        email: "",
                        auth
                    }
                }
            })
        );
    }

    _writeTpl(dirName) {
        let { packageName, projectName, messageQueuePassword } = this.props;

        this.props.upperProjectName = camelcase(projectName, {
            pascalCase: true
        });
        this.props.packagePath = packageName.toLowerCase().replace(/\./g, "/");
        this.props.dockerConfigJson = this.getDockerConfigJson();
        this.props.messageQueuePasswordBase64 = base64.encode(
            messageQueuePassword
        );

        this._translateProps();

        let templateDir = this.templatePath(dirName);
        let me = this;
        return new Promise(function(resolve) {
            let walker = walk.walk(templateDir);

            walker.on("file", function(roots, stat, next) {
                let rootPath = path
                    .relative(templateDir, roots)
                    .replace(/\${PACKAGE_PATH}/g, me.props.packagePath);
                let destRoot = `${projectName}/${rootPath}`;
                let destName = stat.name;

                if (ignoreFiles(rootPath, me.props, next)) return;

                Object.keys(me.props).forEach(key => {
                    destRoot = destRoot.replace(
                        "${" + key + "}",
                        me.props[key]
                    );
                    destName = destName.replace(
                        "${" + key + "}",
                        me.props[key]
                    );
                });

                me.fs.copyTpl(
                    me.templatePath(`${roots}/${stat.name}`),
                    me.destinationPath(`${destRoot}/${destName}`),
                    me.props
                );
                next();
            });

            walker.on("end", function() {
                resolve();
            });
        });
    }

    writing() {
        if (!this.props.bootstrap) return;
        return this._writeTpl("gateway");
    }

    end() {
        if (!this.props.bootstrap) return;
        if (this.options.config) {
            fs.writeFileSync(
                this.options.config,
                JSON.stringify(this.props, null, 4)
            );
        }

        this.log("\n");
        this.log(chalk.green("您已成功完成工程创建。工程结构如下："));
        let treeString = tree(this.props.projectName);
        this.log(chalk.yellow(treeString));
        this.log(
            chalk.green(
                "脚手架已为您完成所有依赖的添加，您可能还需要根据实际开发环境完成配置文件修改。"
            )
        );
        if (this.props.configServerEnabled) {
            let configPath = chalk.hex("#34AFE4")(
                `【${this.props.projectName}/src/main/resources/config/application.yml】`
            );
            let kv = chalk.hex("#34AFE4")(
                `【config/${this.props.projectName}/data】`
            );
            if (this.props.configServerType === "K8S") {
                configPath = chalk.hex("#34AFE4")(
                    `【${this.props.projectName}/src/main/resources/config/configmap.yml】`
                );
                let namespace = chalk.hex("#34AFE4")(
                    `【${this.props.k8sNamespace}】`
                );
                let configmap = chalk.hex("#34AFE4")(
                    `【${this.props.projectName}】`
                );
                this.log(
                    `${chalk.yellow("请将配置文件")}${configPath}${chalk.yellow(
                        "提交至 Kubernetes 集群的 "
                    )}${namespace}${chalk.yellow(
                        "命名空间下"
                    )}${configmap}${chalk.yellow("ConfigMap 中")}`
                );
            } else {
                this.log(
                    `${chalk.yellow("请将配置文件")}${configPath}${chalk.yellow(
                        "提交至 Consul 的 "
                    )}${kv}${chalk.yellow("Key/Value 配置中")}`
                );
            }
        }

        this.log(
            chalk.green(
                "祝您开发顺利！如对脚手架有任何建议，您可以通过提 github issues(https://github.com/madogao/generator-asf) 或发送邮件到 suggest@alauda.io 进行反馈，我们会及时回复并处理。"
            )
        );
        chalk.reset();
    }
};
