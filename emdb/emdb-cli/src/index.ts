import { Command } from 'commander';
import { promises as fs } from 'fs';

import { IngestRequest } from './model/IngestRequest.js';
import { MovieService } from './service/MovieService.js';
import { MovieScraperClient } from './client/MovieScraperClient.js';

const program = new Command();

const langMapper = new Intl.DisplayNames(['en'], { type: 'language' });
const movieService = new MovieService();
const movieScraper = new MovieScraperClient();

program
  .name('emdb-cli')
  .description('An EMDB command line interface')
  .version('1.0.0');

// npm start -- ingest-movie 818
program
  .command('ingest-movie')
  .description('Ingest a movie from TMDB into EMDB')
  .argument('<id>', 'The TMDB id of the movie to ingest')
  .action(ingest);

// npm start -- create-movie ./movies/goldmember.json
program
  .command('create-movie')
  .description('Create a movie in EMDB')
  .argument('<file>', 'The file containing the movie')
  .action(create);

// npm start -- find-movie 1
program
  .command('find-movie')
  .description('Find a movie in EMDB')
  .argument('<id>', 'The EMDB id of the movie to find')
  .action(findById);

// npm start -- update-movie 3 ./movies/goldmember-update.json
program
  .command('update-movie')
  .description('Update a movie in EMDB')
  .argument('<id>', 'The EMDB id of the movie to update')
  .argument('<file>', 'The file containing the movie update')
  .action(update);

// npm start -- delete-movie 2
program
  .command('delete-movie')
  .description('Delete a movie in EMDB')
  .argument('<id>', 'The EMDB id of the movie to delete')
  .action(deleteById);

program.parse(process.argv); 

async function create(movieFileName: string) {
  console.log(`Creating movie from ${movieFileName} ...`);

  try {
    const movieFile = await fs.readFile(movieFileName, 'utf-8');
    const movie = await movieService.create(JSON.parse(movieFile));
    console.log(movie);
  } catch (error) {
    console.error(`Error creating EMDB movie: ${error}`);
  }    
}

async function findById(id: number) {
  console.log(`Finding EMDB movie: ${id} ...`);
  try {
    const movie = await movieService.findById(id);
    if (movie.originalLanguage) {
      movie.originalLanguage = langMapper.of(movie.originalLanguage) ?? null;;
    }
    console.log(movie);
  } catch (error) {
    console.error(`Error finding EMDB movie: ${error}`);
  }  
}

async function ingest(id: number) {
  try {
    const start = performance.now();
    const request: IngestRequest = { tmdbId: id };
    const response = await movieScraper.ingest(request);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Ingest request for TMDB movie: ${id} took: ${et} ms.`);
  } catch (error) {
    console.error(`Error sending ingest request: ${error}`);
  }   
}

async function update(id: number, movieFileName: string) {
  console.log(`Updating movie from ${movieFileName} ...`);
  try {
    const movieFile = await fs.readFile(movieFileName, 'utf-8');
    const movie = await movieService.update(id, JSON.parse(movieFile));
    console.log(movie);
  } catch (error) {
    console.error(`Error updating EMDB movie: ${error}`);
  }  
}

async function deleteById(id: number) {
  console.log(`Deleting EMDB movie: ${id} ...`);
  try {
    await movieService.deleteById(id);
  } catch (error) {
    console.error(`Error deleting EMDB movie: ${error}`);
  }  
}


