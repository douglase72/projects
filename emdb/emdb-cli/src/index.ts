import { Command } from 'commander';
import { promises as fs } from 'fs';

import { MovieService } from './service/MovieService.js';

const program = new Command();

const langMapper = new Intl.DisplayNames(['en'], { type: 'language' });
const movieService = new MovieService();

program
  .name('emdb-cli')
  .description('An EMDB command line interface')
  .version('1.0.0');

// npm start -- create-movie ./movies/goldmember.json
program
  .command('create-movie')
  .description('Create a movie in EMDB')
  .argument('<file>', 'The file containing the movie')
  .action(createMovie);

// npm start -- find-movie 1
program
  .command('find-movie')
  .description('Find a movie in EMDB')
  .argument('<id>', 'The EMDB id of the movie to find')
  .action(findMovie);

// npm start -- update-movie 3 ./movies/goldmember-update.json
program
  .command('update-movie')
  .description('Update a movie in EMDB')
  .argument('<id>', 'The EMDB id of the movie to update')
  .argument('<file>', 'The file containing the movie update')
  .action(updateMovie);

// npm start -- delete-movie 2
program
  .command('delete-movie')
  .description('Delete a movie in EMDB')
  .argument('<id>', 'The EMDB id of the movie to delete')
  .action(deleteMovie);

program.parse(process.argv); 

async function createMovie(movieFileName: string) {
  console.log(`Creating movie from ${movieFileName} ...`);

  try {
    const movieFile = await fs.readFile(movieFileName, 'utf-8');
    const movie = await movieService.create(JSON.parse(movieFile));
    console.log(movie);
  } catch (error) {
    console.error(`Error creating EMDB movie: ${error}`);
  }    
}

async function findMovie(id: number) {
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

async function updateMovie(id: number, movieFileName: string) {
  console.log(`Updating movie from ${movieFileName} ...`);
  try {
    const movieFile = await fs.readFile(movieFileName, 'utf-8');
    const movie = await movieService.update(id, JSON.parse(movieFile));
    console.log(movie);
  } catch (error) {
    console.error(`Error updating EMDB movie: ${error}`);
  }  
}

async function deleteMovie(id: number) {
  console.log(`Deleting EMDB movie: ${id} ...`);
  try {
    await movieService.deleteById(id);
  } catch (error) {
    console.error(`Error deleting EMDB movie: ${error}`);
  }  
}


