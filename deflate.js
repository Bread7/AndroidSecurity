const fs = require('fs');
const zlib = require('zlib');

// Function to deflate a .dex file
function deflateDexFile(inputFilePath, outputFilePath) {
  // Create a read stream from the input file
  const readStream = fs.createReadStream(inputFilePath);

  // Create a write stream for the deflated output
  const writeStream = fs.createWriteStream(outputFilePath);

  // Create a deflateRaw stream
  const deflateStream = zlib.createDeflateRaw();

  // Chain the streams
  readStream
    .pipe(deflateStream)    // deflate the input
    .pipe(writeStream)      // write the deflated output to a new file
    .on('finish', () => {
      console.log('Deflate process completed.');
    })
    .on('error', (error) => {
      console.error('An error occurred:', error);
    });
}

// Example usage:
const inputPath = '/Users/zj/Development/ICT2215-Project/zipper/app-release-unsigned/classes3.dex';
// const inputPath = '/Users/zj/Development/ICT2215-Project/test/classes.dex';
// const outputPath = '/Users/zj/Development/ICT2215-Project/test/classes.deflated';
const outputPath = '/Users/zj/Development/ICT2215-Project/zipper/app-release-unsigned/classes3.dex.deflated';

deflateDexFile(inputPath, outputPath);