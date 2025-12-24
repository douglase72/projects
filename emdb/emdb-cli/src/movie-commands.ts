import { Command } from 'commander';
import { promises as fs } from 'fs';

import { monitorJob } from './util/JobMonitor.js';
import { MovieService } from './service/MovieService.js';

export { movieCommand };

const movieCommand = new Command('movie')
  .description('Perform CRUD operations on a movie');

movieCommand
  .command('create')
  .description('Create a movie from TMDB in EMDB')
  .argument('<file>', 'The file containing the movie to create')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie create goldmember.json
`)  
  .action(create);

movieCommand
  .command('cron')
  .description('Execute the movie cron job now')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie cron
`)  
  .action(cron);

movieCommand
  .command('ingest')
  .description('Ingest a movie from TMDB into EMDB')
  .argument('<tmdbId>', 'The TMDB id of the movie to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie ingest 335984
`)  
  .action(ingest);

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
  $ emdb-cli movie update 1 goldmember.json
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

async function cron() {
  try {
    const movieService = new MovieService();
    await movieService.cron(); 
    const start = new Date();
    console.log(`Cron job started at ${start.toISOString()}.`);
  } catch (error) {
    console.error(`Error sending movie cron request: ${error}`);
  }   
}

async function ingest(tmdbId: number) {
  try {
    const movieService = new MovieService();
    const jobId = await movieService.ingest({ tmdbId: tmdbId });
    const start = performance.now();
    await monitorJob(jobId);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Job: ${jobId} finished in ${et} ms.`);
  } catch (error) {
    console.error(`${error}`);
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
    console.dir(movie, { depth: null, colors: true }); 
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