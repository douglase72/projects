import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { IngestSource, MediaType } from '@emdb/common';

export { seriesCommand };

const seriesCommand = new Command('series')
  .description('Perform CRUD operations on a series');

seriesCommand
  .command('ingest')
  .description('Ingest a series from TMDB into EMDB')
  .argument('<tmdbId>', 'The TMDB id of the series to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli series ingest 1396
`)  
  .action(ingest);

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.SERIES
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};