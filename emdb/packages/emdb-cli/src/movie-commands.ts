import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { 
  IngestSource, 
  MediaType, 
  SchedulerType, 
  type ExecuteScheduler } from '@emdb/common';

export { movieCommand };

const movieCommand = new Command('movie')
  .description('Perform CRUD operations on a movie');

movieCommand
  .command('delete')
  .description('Delete a movie in EMDB')
  .argument('<id>', 'The id of the movie to delete')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie delete 1
`)  
  .action(deleteById);  

movieCommand
  .command('find')
  .description('Find a movie in EMDB')
  .argument('<id>', 'The id of the movie to find')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie find 1
`)  
  .action(find);

movieCommand
  .command('ingest')
  .description('Ingest a movie from TMDB')
  .argument('<tmdbId>', 'The TMDB id of the movie to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie ingest 78
`)  
  .action(ingest);

movieCommand
  .command('save')
  .description('Save a movie from TMDB')
  .argument('<file>', 'The file containing the movie to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie save Austin-Powers-in-Goldmember-20260205-202941.json
`)  
  .action(save);

movieCommand
  .command('scheduler')
  .description('Execute the movie scheduler')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie scheduler
`)  
  .action(scheduler);

movieCommand
  .command('update')
  .description('Update a movie from TMDB')
  .argument('<id>', 'The id of the movie to update')
  .argument('<file>', 'The file containing the movie to update')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie update Austin-Powers-in-Goldmember-20260205-202941.json
`)  
  .action(update);

async function deleteById(id: number) {
  try {
    const { deleteMovie } = useEmdb();
    const start = performance.now();
    await deleteMovie(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Deleted ${id} in: ${et} ms.`);  
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function find(id: number) {
  try {
    const { findMovie } = useEmdb();
    const start = performance.now();
    const movie = await findMovie(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Found ${movie.title} in: ${et} ms.`);    
    console.log(movie);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.MOVIE
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function save(fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const { saveMovie } = useEmdb();
    const start = performance.now();
    const movie = await saveMovie(JSON.parse(file));
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Saved ${movie.title} in: ${et} ms.`);    
    console.log(movie);
  } catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function scheduler() {
  try {
    const { executeScheduler } = useEmdb();
    const command: ExecuteScheduler = {
      type: SchedulerType.MOVIES
    };
    const status = await executeScheduler(command);
    console.log(`Status: ${status}`);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function update(id:number, fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const { updateMovie } = useEmdb();
    const start = performance.now();
    const movie = await updateMovie(id, JSON.parse(file));
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Updated ${movie.title} in: ${et} ms.`);       
    console.log(movie);
  } catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};