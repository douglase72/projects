import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { useErrorHandler } from './composables/useErrorHandler.js';
import { SaveMovieSchema } from './schemas/SaveMovieSchema.js';

export { movieCommand };

const { handleError } = useErrorHandler();

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
  .command('save')
  .description('Save a movie from TMDB')
  .argument('<file>', 'The file containing the movie to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli movie save Austin-Powers-in-Goldmember-20260205-202941.json
`)  
  .action(save);

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
    const { movie, status } = await saveMovie(parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`${status} ${movie.title} in: ${et} ms.`);    
    console.log(movie);
  } catch (error: any) {
    handleError(error);
  }
};

