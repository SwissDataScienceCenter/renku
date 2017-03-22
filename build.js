var $RefParser = require('json-schema-ref-parser');
var fs = require('fs');
var YAML = require('json2yaml');

if(process.argv.length < 3){
    console.log("usage: node build.js api_file.yaml");
    return;
}
if(!fs.existsSync("target/")){
    fs.mkdirSync("target/");
}

var filepath = process.argv[2];
var match = /(^.*[\\\/])*(.*)\.yaml$/.exec(filepath);
var filename = match[2];

$RefParser.bundle(filepath)
  .then(function(schema) {
          console.log("File written to: target/"+filename+".json")
          fs.writeFileSync("target/"+filename+".json", JSON.stringify(schema));
          console.log("File written to: target/"+filename+".yaml")
          fs.writeFileSync("target/"+filename+".yaml", YAML.stringify(schema));
    },  function (err) {
          console.log(err.stack);
            });
