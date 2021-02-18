const ignoreFiles = (rootPath, props, next) => {
    if (
        !props.messageQueueEnabled &&
        rootPath.includes(`src/main/java/${props.packagePath}/stream`)
    ) {
        next();
        return true;
    }

    if (
        !props.tracingEnabled &&
        rootPath.includes(`src/main/java/${props.packagePath}/metrics`)
    ) {
        next();
        return true;
    }

    if (
        props.discoveryServerType !== "CONSUL" &&
        props.configServerType !== "CONSUL" &&
        rootPath.includes(`consul`)
    ) {
        next();
        return true;
    }
};

module.exports = ignoreFiles;
