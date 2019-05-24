var exec = require('cordova/exec');

// you need to add module when you have multiple functions
module.exports.add = function(arg0, success, error) {
    console.log("Add method inside PrinterPlugin.js file");
    exec(success, error, 'PrinterPlugin', 'add', [arg0]);
}

module.exports.substract = function(arg0, success, error) {
    exec(success, error, 'PrinterPlugin', 'substract', [arg0]);
}

module.exports.print = function (arg0, success, error) {
    console.log("Print method inside PrinterPlugin.js file");
    exec(success, error, 'PrinterPlugin', 'print', [arg0]);
}
