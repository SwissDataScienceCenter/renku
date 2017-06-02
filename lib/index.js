const path = require('path')
const chokidar = require('chokidar')
const express = require('express')

const build = require('./build')

// package dir
const root_dir = path.normalize(__dirname + '/..')

// Watch source file and recompile on change
const watcher = chokidar.watch(path.normalize(root_dir + '/src'))
var compile_counter = 0

watcher.on('all', (event, path) => {
    console.log(event, path)
    add_compile()
})

function add_compile() {
    if (compile_counter == 0) {
        compile_counter = 1
        compile_loop()
    } else if (compile_counter == 1) {
        compile_counter = 2
    }
}

function compile_loop() {
    if (compile_counter > 0) {
        build.compile_all(() => {
            --compile_counter
            compile_loop()
        })
    }
}

// Serve current directory
const app = express()

app.use('/rest-api-definitions', express.static(root_dir))

app.listen(3000, function () {
    console.log('Example app listening on port 3000!')
})
