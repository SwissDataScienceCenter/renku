const fs = require('fs')
const path = require('path')
const $RefParser = require('json-schema-ref-parser')
const YAML = require('json2yaml')

const api_dir = path.normalize(__dirname + '/../src/api')

// Execute compile_all() if called directly (not node import)
if (require.main === module) {
    compile_all(() => {})
}

function compile_all(callback) {
    fs.readdir(api_dir, (err, items) => {
        if (err) {
            console.log(err)
            return
        }

        var counter = items.length
        var errors = []
        const cb = function() {
            --counter
            if (counter == 0) {
                process.nextTick(callback)
            }
        }

        items.forEach(filename => {
            const ext = path.extname(filename)
            if (ext === '.yaml' || ext === '.yml') {
                compile(api_dir + '/' + filename, cb)
            }
        })
    })
}

function compile(filepath, callback) {
    const filename = path.parse(filepath).name

    $RefParser.bundle(filepath)
        .then(function (schema) {
            if(!fs.existsSync("target/")) {
                fs.mkdirSync("target/")
            }
            console.log("File written to: target/" + filename + ".json")
            fs.writeFileSync("target/" + filename + ".json", JSON.stringify(schema));
            console.log("File written to: target/" + filename + ".yaml")
            fs.writeFileSync("target/" + filename + ".yaml", YAML.stringify(schema));
            process.nextTick(callback)
        }, function (err) {
            console.log(err.stack);
            process.nextTick(callback)
        })
}

module.exports = {
    compile_all: compile_all
,   compile: compile
}
