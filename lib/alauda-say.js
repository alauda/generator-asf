const chalk = require("chalk");

const alaudaSay = words => {
    let template = "\n";
    template += `      ${chalk.yellow(
        "╭──────────────────────────────╮"
    )}               \n`;
    template += `      ${chalk.yellow(
        "│ @@@@@@@@@@@@@@@@@@@@@@@@@@@@ │"
    )}               \n`;
    template += `      ${chalk.yellow(
        "│ ############################ │"
    )}    ${chalk.hex("#34AFE4")(" /ˉˉˉˉˉˉ\\")}  \n`;
    template += `      ${chalk.yellow(
        "╰──────────────────────────────╯"
    )}    ${chalk.hex("#34AFE4")("/        \\")} \n`;
    template += `                                       ${chalk.yellow(
        "\\"
    )} ${chalk.hex("#34AFE4")("<  ●       |")}\n`;
    template += `                                          ${chalk.hex(
        "#34AFE4"
    )("|         |")}\n`;
    template += `                                          ${chalk.hex(
        "#34AFE4"
    )(" |        |")}\n`;
    template += `                                          ${chalk.hex(
        "#34AFE4"
    )("  \\       |")}\n`;
    template += `                                          ${chalk.hex(
        "#34AFE4"
    )("   ˉˉ\\    |")}\n`;
    template += `                                          ${chalk.hex(
        "#34AFE4"
    )("      ˉˉˉ\\|")}\n`;

    let line1 = words.substr(0, 24).padEnd(24, " ");
    let line2 = words.substr(24).padEnd(24, " ");

    template = template.replace("@@@@@@@@@@@@@@@@@@@@@@@@@@@@", line1);
    template = template.replace("############################", line2);
    return template;
};

module.exports = alaudaSay;
