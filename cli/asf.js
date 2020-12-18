#!/usr/bin/env node

const path = require("path");
const Environment = require("yeoman-environment");

let env = Environment.createEnv();
env.lookup({ packagePaths: [path.join(__dirname, "..")] });
const [, , action, ...args] = process.argv;

args.push("--no-insight");
const argv = require("minimist")(args);
const actions = {
    cs: "create-service",
    cg: "create-gateway",
    version: "-v"
};

env.run([`asf:${actions[action] || action}`, ...args], {
    help: argv.h || argv.help,
    ...argv
});
