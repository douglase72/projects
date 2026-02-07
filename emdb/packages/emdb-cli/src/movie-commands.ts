import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { IngestSource, MediaType } from '@emdb/common';

export { movieCommand };

const movieCommand = new Command('movie')
  .description('Perform CRUD operations on a movie');

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
  .description('Ingest a movie from TMDB into EMDB')
  .argument('<tmdbId>', 'The TMDB id of the movie to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie ingest 78
`)  
  .action(ingest);

movieCommand
  .command('save')
  .description('Save a movie from TMDB to EMDB')
  .argument('<file>', 'The file containing the movie to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie save Austin-Powers-in-Goldmember-20260205-202941.json
`)  
  .action(save);

async function find(id: number) {
  try {
    const { findMovie } = useEmdb();
    const movie = await findMovie(id);
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
    const movie = await saveMovie(JSON.parse(file));
    console.log(movie);
  } catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};