const fs = require('fs');
const data = fs.readFileSync('temp_index.txt', 'utf8');
fs.writeFileSync('src/views/home/index.vue', data, 'utf8');
console.log('Written');
