const chalk = require("chalk");

const serviceType = _ => "网关";

const prompts = [
    {
        type: "input",
        name: "projectName",
        message: "请输入您的工程名称：",
        validate: input => {
            if (!input) return chalk.red("工程名称不能为空");
            if (!/^[a-zA-Z]+[a-zA-Z0-9-_]*[a-zA-Z0-9]+$/.test(input)) {
                return chalk.red(
                    "工程名称只能包含字母、数字短线（-）或者下划线（_），且必须以英文字母开头，英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "projectDescribe",
        message: "请输入您的工程描述："
    },
    {
        type: "input",
        name: "mavenGroupId",
        message: "请输入 Maven Group Id：",
        validate: input => {
            if (!input) return chalk.red("Maven Group Id 不能为空");
            if (
                !/^(?:[a-z_]+(?:\d*[a-zA-Z_]*)*)(?:\.[a-z_]+(?:\d*[a-zA-Z_]*)*)*$/.test(
                    input
                )
            ) {
                return chalk.red(
                    "Maven Group Id 只能包含小写英文、数字或点（.），每个 Id 名必须以英文字母开头，英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "mavenArtifactId",
        message: "请输入 Maven Artifact Id：",
        validate: input => {
            if (!input) return chalk.red("Maven Artifact Id 不能为空");
            if (!/^[a-z]+[a-z0-9-_]*[a-z0-9]+$/.test(input)) {
                return chalk.red(
                    "Maven Artifact Id 只能包含小写英文、数字、短线（-）或者下划线（_），且必须以英文字母开头，英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "packageName",
        message: "请输入包名：",
        validate: input => {
            if (!input) return chalk.red("包名不能为空");
            if (
                !/^(?:[a-z_]+(?:\d*[a-zA-Z_]*)*)(?:\.[a-z_]+(?:\d*[a-zA-Z_]*)*)*$/.test(
                    input
                )
            ) {
                return chalk.red(
                    "包名只能包含小写英文、数字或点（.），每个包名必须以英文字母开头，英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "k8sNamespace",
        message:
            "请输入网关要部署的命名空间名称(如不部署在 Kubernetes 可跳过)：",
        // When: props =>
        //     props.configServerEnabled && props.configServerType === "K8S",
        // default: "default",
        validate: input => {
            // If (!input) return chalk.red("命名空间名称不能为空");
            if (!input) return true;
            if (!/^[a-z]+[a-z0-9-]*[a-z0-9]+$/.test(input)) {
                return chalk.red(
                    "命名空间名称只能包含字母、数字短线（-），且必须以英文字母开头，英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "confirm",
        name: "circuitBreakerEnabled",
        message: props => `您的${serviceType(props)}是否需要支持熔断降级？`,
        default: true
    },
    {
        type: "confirm",
        name: "rateLimitEnabled",
        message: props => `您的${serviceType(props)}是否需要支持限流？`,
        default: true
    },
    {
        type: "list",
        name: "discoveryServerType",
        message: "请选择您的注册中心类型：",
        choices: [
            { value: "K8S", name: "Kubernetes(推荐)" },
            { value: "CONSUL", name: "Consul" }
        ],
        default: "K8S"
    },
    {
        type: "confirm",
        name: "configServerEnabled",
        message: props => `您的${serviceType(props)}是否需要支持配置中心？`,
        default: true
    },
    {
        type: "list",
        name: "configServerType",
        message: "请选择您的配置中心类型：",
        when: props => props.configServerEnabled,
        choices: [
            { value: "K8S", name: "Kubernetes(推荐)" },
            { value: "CONSUL", name: "Consul" }
        ],
        default: "K8S"
    },
    {
        type: "confirm",
        name: "tracingEnabled",
        message: props =>
            `您的${serviceType(props)}是否需要调用链跟踪（Jaeger）？`,
        default: true
    },
    {
        type: "confirm",
        name: "configNow",
        message: props =>
            `是否现在开始配置您的${serviceType(props)}(注册中心、调用链等)？`,
        default: true
    },
    {
        type: "input",
        name: "consulHost",
        default: "",
        message: "请输入您的 Consul 访问地址（域名或IP）：",
        when: props =>
            props.configNow &&
            (props.discoveryServerType === "CONSUL" ||
                props.configServerType === "CONSUL"),
        validate: input => {
            if (!input) return true;
            if (!/^[a-z0-9-_.]+$/.test(input)) {
                return chalk.red(
                    "Consul 地址只能包含小写英文、数字、短线（-）或者下划线（_）"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "consulPort",
        default: "",
        message: "请输入您的 Consul 端口号：",
        when: props =>
            props.configNow &&
            (props.discoveryServerType === "CONSUL" ||
                props.configServerType === "CONSUL"),
        validate: input => {
            if (!input) return true;
            if (!/^[0-9]{0,5}$/.test(input)) {
                return chalk.red("Consul 端口号只能为0~65536的数字");
            }

            if (Number(input) <= 0 || Number(input) > 65536) {
                return chalk.red("Consul 端口号只能为0~65536的数字");
            }

            return true;
        }
    },
    {
        type: "input",
        name: "consulToken",
        default: "",
        message:
            "请输入您的 Consul Server Token(如 Consul 未开启 ACL 可直接回车跳过这步)：",
        when: props =>
            props.configNow &&
            (props.discoveryServerType === "CONSUL" ||
                props.configServerType === "CONSUL")
    },
    {
        type: "input",
        name: "jaegerHost",
        default: "",
        message: "请输入您的 Jaeger 访问地址（域名或IP）：",
        when: props => props.configNow && props.tracingEnabled,
        validate: input => {
            if (!input) return true;
            if (!/^[a-z0-9-_.]+$/.test(input)) {
                return chalk.red(
                    "Jaeger 地址只能包含小写英文、数字、短线（-）或者下划线（_）"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "jaegerPort",
        default: "",
        message: "请输入您的 Jaeger 端口号：",
        when: props => props.configNow && props.tracingEnabled,
        validate: input => {
            if (!input) return true;
            if (!/^[0-9]+$/.test(input)) {
                return chalk.red("Jaeger 端口号只能为0~65536的数字");
            }

            if (Number(input) <= 0 || Number(input) > 65536) {
                return chalk.red("Jaeger 端口号只能为0~65536的数字");
            }

            return true;
        }
    },
    {
        type: "input",
        name: "dockerUrl",
        default: "",
        when: props => props.configNow,
        message: _ => "请输入您的镜像仓库地址：",
        validate: (input, _) => {
            if (!input) return true;
            if (!/^[a-zA-Z0-9]+[a-zA-Z0-9-.]*[a-zA-Z0-9]+$/.test(input)) {
                return chalk.red(
                    "镜像仓库地址只能包含字母、数字或者.，且必须以英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "dockerPort",
        default: "",
        when: props => props.configNow,
        message: _ => "请输入您的镜像仓库端口：",
        validate: (input, _) => {
            if (!input) return true;
            if (!/^[0-9]{0,5}$/.test(input)) {
                return chalk.red("端口号只能为0~65536的数字");
            }

            return true;
        }
    },
    {
        type: "input",
        name: "dockerUsername",
        default: "",
        when: props => props.configNow,
        message: _ => "请输入您的镜像仓库用户名：",
        validate: (input, _) => {
            if (!input) return true;
            if (!/^[a-zA-Z]+[a-zA-Z0-9-_]*[a-zA-Z0-9]+$/.test(input)) {
                return chalk.red(
                    "用户名只能包含字母、数字短线（-）或者下划线（_），且必须以英文字母开头，英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "dockerPassword",
        default: "",
        when: props => props.configNow,
        message: _ => "请输入您的镜像仓库密码：",
        validate: (input, _) => {
            if (!input) return true;
            // If (!input) {
            //     return chalk.red("请输入镜像仓库密码");
            // }

            return true;
        }
    }
];

module.exports = prompts;
