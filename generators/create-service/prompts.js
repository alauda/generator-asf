const chalk = require("chalk");

const serviceType = "微服务";

const prompts = [
    {
        type: "input",
        name: "projectName",
        message: "请输入您的工程名称：",
        validate: input => {
            if (!input) return chalk.red("工程名称不能为空");
            if (!/^[a-z]+[a-z0-9-_]*[a-z0-9]+$/.test(input)) {
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
            if (!/^[a-z]+[a-z0-9.]*[a-z0-9]+$/.test(input)) {
                return chalk.red(
                    "Maven Group Id 只能包含小写英文、数字或点（.），且必须以英文字母开头，英文字母或数字结尾"
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
            if (!/^[a-z]+[a-z0-9.]*[a-z0-9]+$/.test(input)) {
                return chalk.red(
                    "包名只能包含小写英文、数字或点（.），且必须以英文字母开头，英文字母或数字结尾"
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
        type: "confirm",
        name: "discoveryEnabled",
        message: props => `您的${serviceType(props)}是否需要支持注册发现？`,
        default: true
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
            { value: "CONSUL", name: "Consul" },
            { value: "K8S", name: "Kubernetes" }
        ],
        default: "CONSUL"
    },
    {
        type: "input",
        name: "k8sNamespace",
        message: "请选择要部署 ConfigMap 的命名空间名称：",
        when: props =>
            props.configServerEnabled && props.configServerType === "K8S",
        default: "default",
        validate: input => {
            if (!input) return chalk.red("命名空间名称不能为空");
            if (!/^[a-z]+[a-z0-9-]*[a-z0-9]+$/.test(input)) {
                return chalk.red(
                    "工程名称只能包含字母、数字短线（-），且必须以英文字母开头，英文字母或数字结尾"
                );
            }

            return true;
        }
    },
    {
        type: "confirm",
        name: "tracingEnabled",
        message: props =>
            `您的${serviceType(props)}是否需要调用链跟踪（Jagger）？`,
        default: true
    },
    {
        type: "confirm",
        name: "messageQueueEnabled",
        message: props => `您的${serviceType(props)}是否需要支持消息队列？`,
        default: true
    },
    {
        type: "checkbox",
        name: "messageQueueType",
        message: props =>
            `请选择您的${serviceType(props)}要支持的消息队列类型：`,
        when: props => props.messageQueueEnabled,
        choices: [
            { value: "RABBIT_MQ", name: "RabbitMQ" },
            { value: "KAFKA", name: "Kafka" }
        ],
        default: ["RABBIT_MQ"]
    },
    {
        type: "confirm",
        name: "configNow",
        message: props =>
            `是否现在开始配置您的${serviceType(
                props
            )}（Consul、Jagger、MQ等）？`,
        default: true
    },
    {
        type: "input",
        name: "consulHost",
        default: "",
        message: "请输入您的 Consul 访问地址（域名或IP）：",
        when: props => props.configNow && props.discoveryEnabled,
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
        when: props => props.configNow && props.discoveryEnabled,
        validate: input => {
            if (!input) return true;
            if (!/^[0-9]+$/.test(input)) {
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
        message: "请输入您的 Consul Server Token：",
        when: props => props.configNow && props.discoveryEnabled
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
                return chalk.red("Jagger 端口号只能为0~65536的数字");
            }

            if (Number(input) <= 0 || Number(input) > 65536) {
                return chalk.red("Jagger 端口号只能为0~65536的数字");
            }

            return true;
        }
    },
    {
        type: "input",
        name: "messageQueueHost",
        default: "",
        when: props => props.configNow && props.messageQueueEnabled,
        message: props =>
            `请输入您的 ${
                props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ" : "Kafka"
            } 访问地址（域名或IP）：`,
        validate: (input, props) => {
            if (!input) return true;
            let mqType =
                props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ" : "Kafka";
            if (!/^[a-z0-9-_.]+$/.test(input)) {
                return chalk.red(
                    `${mqType} 地址只能包含小写英文、数字、短线（-）或者下划线（_）`
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "messageQueuePort",
        default: "",
        when: props => props.configNow && props.messageQueueEnabled,
        message: props =>
            `请输入您的 ${
                props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ" : "Kafka"
            } 端口号：`,
        validate: (input, props) => {
            if (!input) return true;
            let mqType =
                props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ" : "Kafka";
            if (!/^[0-9]+$/.test(input)) {
                return chalk.red(`${mqType} 端口号只能为0~65536的数字`);
            }

            if (Number(input) <= 0 || Number(input) > 65536) {
                return chalk.red(`${mqType} 端口号只能为0~65536的数字`);
            }

            return true;
        }
    },
    {
        type: "input",
        name: "messageQueueUsername",
        default: "",
        when: props =>
            props.configNow &&
            props.messageQueueEnabled &&
            props.messageQueueType === "RABBIT_MQ",
        message: props =>
            `请输入您的 ${
                props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ" : "Kafka"
            } 用户名：`,
        validate: (input, props) => {
            if (!input) return true;
            let mqType =
                props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ" : "Kafka";
            if (!/^[a-z]+[a-z0-9-_]*[a-z0-9]+$/.test(input)) {
                return chalk.red(
                    `${mqType} 用户名只能包含字母、数字短线（-）或者下划线（_），且必须以英文字母开头，英文字母或数字结尾`
                );
            }

            return true;
        }
    },
    {
        type: "input",
        name: "messageQueuePassword",
        default: "",
        when: props =>
            props.configNow &&
            props.messageQueueEnabled &&
            props.messageQueueType === "RABBIT_MQ",
        message: props =>
            `请输入您的 ${
                props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ" : "Kafka"
            } 密码：`,
        validate: (input, _) => {
            if (!input) return true;
            // Let mqType = props.messageQueueType === "RABBIT_MQ" ? "RabbitMQ":"Kafka";
            // if(!/^[a-zA-Z]+[a-zA-Z0-9-_]*[a-zA-Z0-9]+$/.test(input)){
            //     return chalk.red(`${mqType} 密码只能包含字母、数字短线（-）或者下划线（_），且必须以英文字母开头，英文字母或数字结尾`);
            // }
            return true;
        }
    }
];

module.exports = prompts;
