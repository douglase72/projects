import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { useErrorHandler } from './composables/useErrorHandler.js';
import { type IngestMedia, IngestSource, MediaType } from '@emdb/common';
import { SaveMovieSchema } from './schemas/SaveMovieSchema.js';

export { movieCommand };

const { handleError } = useErrorHandler();

const movieCommand = new Command('movie')
  .description('Perform CRUD operations on a movie');

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
  .command('find')
  .description('Find a movie in EMDB')
  .argument('<id>', 'The id of the movie to find')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie find 1
`)  
  .action(find);

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command: IngestMedia = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.MOVIE
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  } catch (error: any) {
    handleError(error);
  }
};

async function save(fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const parsed = SaveMovieSchema.safeParse(JSON.parse(file));
    if (!parsed.success) {
      console.error(`Validation failed for file: ${fileName}`);
      console.error(JSON.stringify(parsed.error.format(), null, 2));
      process.exit(1);
    }
    const { saveMovie } = useEmdb();
    const start = performance.now();
    const { status, movie } = await saveMovie(parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });

    if (status === 201) {
      console.log(`Created movie: ${movie?.title} in ${et} ms.`);
      console.log(movie);
    } else if (status === 200) {
      console.log(`Updated movie: ${movie?.title} in ${et} ms.`);
      console.log(movie);
    } else if (status === 204) {
      console.log(`No changes needed for ${parsed.data.title}. Skipped in ${et} ms.`);
    } else {
      console.log(`Saved with status ${status} in ${et} ms.`);
    }
  } catch (error: any) {
    handleError(error);
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
  } catch (error: any) {
    handleError(error);
  }
};

