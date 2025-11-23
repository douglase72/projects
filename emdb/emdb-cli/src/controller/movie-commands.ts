import { Command } from 'commander';
import { promises as fs } from 'fs';

import { MovieService } from '../service/MovieService.js';

const movieCommand = new Command('movie')
  .description('Perform CRUD operations on movies');

movieCommand
  .command('ingest')
  .description('Ingest a movie from TMDB into EMDB')
  .argument('<id>', 'The TMDB id of the movie to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie ingest 818
`)  
  .action(ingest);

movieCommand
  .command('create')
  .description('Create a movie from TMDB in EMDB')
  .argument('<file>', 'The file containing the movie to create')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie create /home/erdouglass/projects/emdb/emdb-cli/movies/Austin-Powers-in-Goldmember-20251123-124317.json
`)  
  .action(create);

movieCommand
  .command('find')
  .description('Find a movie in EMDB')
  .argument('<id>', 'The id of the movie to find')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie find 1
`)  
  .action(findById);

movieCommand
  .command('update')
  .description('Update a movie in EMDB')
  .argument('<id>', 'The id of the movie to update')
  .argument('<file>', 'The file containing the movie update')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie update 1 /home/erdouglass/projects/emdb/emdb-cli/movies/goldmember.json
`)
  .action(update);

movieCommand
  .command('delete')
  .description('Delete a movie in EMDB')
  .argument('<id>', 'The id of the movie to delete')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie delete 1
`)  
  .action(deleteById);  
  
async function ingest(id: number) {
  try {
    const movieService = new MovieService();
    const start = performance.now();
    await movieService.ingest({ tmdbId: id });
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Ingest request for TMDB movie: ${id} took: ${et} ms.`);
  } catch (error) {
    console.error(`Error sending movie ingest request: ${error}`);
  }   
}

async function create(fileName: string) {
  try {
    const movieService = new MovieService();
    const file = await fs.readFile(fileName, 'utf-8');
    const start = performance.now();
    const movie = await movieService.create(JSON.parse(file));
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Created: ${movie.title} in: ${et} ms.`);
    console.log(movie);
  } catch (error) {
    console.error(`Error creating movie: ${error}`);
  }    
}

async function findById(id: number) {
  try {
    const movieService = new MovieService();
    const start = performance.now();
    const movie = await movieService.findById(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Found: ${movie.title} in: ${et} ms.`);
    console.log(movie);
  } catch (error) {
    console.error(`Error finding movie: ${error}`);
  }  
}

async function update(id: number, fileName: string) {
  try {
    const movieService = new MovieService();
    const file = await fs.readFile(fileName, 'utf-8');
    const start = performance.now();
    const movie = await movieService.update(id, JSON.parse(file));
       const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Updated: ${movie.title} in: ${et} ms.`);
    console.log(movie);
  } catch (error) {
    console.error(`Error updating movie: ${error}`);
  }
}

async function deleteById(id: number) {
  try {
    const movieService = new MovieService();
    const start = performance.now();
    await movieService.deleteById(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Deleted: ${id} in: ${et} ms.`);
  } catch (error) {
    console.error(`Error deleting movie: ${error}`);
  }    
}

export { movieCommand };

