import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { IngestSource, MediaType } from '@emdb/common';

export { personCommand };

const personCommand = new Command('person')
  .description('Perform CRUD operations on a series');

personCommand
  .command('ingest')
  .description('Ingest a person from TMDB into EMDB')
  .argument('<tmdbId>', 'The TMDB id of the person to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli person ingest 3
`)  
  .action(ingest);

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.PERSON
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};