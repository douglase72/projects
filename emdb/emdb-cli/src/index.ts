import { Command } from 'commander';

import { IngestRequest } from './model/IngestRequest.js';
import { MovieService } from './service/MovieService.js';

const program = new Command();

const langMapper = new Intl.DisplayNames(['en'], { type: 'language' });
const movieService = new MovieService();

program
  .name('emdb-cli')
  .description('An EMDB command line interface')
  .version('1.0.0');

// npm start -- ingest 818
program
  .command('ingest')
  .description('Ingest a movie from TMDB into EMDB')
  .argument('<id>', 'The TMDB id of the movie to ingest')
  .action(ingest);

// npm start -- find 1
program
  .command('find')
  .description('Find a movie in EMDB')
  .argument('<id>', 'The EMDB id of the movie to find')
  .action(findById);

program.parse(process.argv); 

async function ingest(id: number) {
  try {
    const start = performance.now();
    const request: IngestRequest = { tmdbId: id };
    await movieService.ingest(request);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Ingest request for TMDB movie: ${id} took: ${et} ms.`);
  } catch (error) {
    console.error(`Error sending ingest request: ${error}`);
  }   
}

async function findById(id: number) {
  try {
    const start = performance.now();
    const movie = await movieService.findById(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Found ${movie.title} in: ${et} ms.`);
    console.log(movie);
  } catch (error) {
    console.error(`Error finding EMDB movie: ${error}`);
  }  
}
